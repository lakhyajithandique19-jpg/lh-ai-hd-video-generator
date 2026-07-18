const express = require('express');
const cors = require('cors');
require('dotenv').config();
const fetch = require('node-fetch');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json({ limit: '10mb' }));

// Simple in-memory Cache with TTL (Time To Live)
const cache = new Map();
const CACHE_TTL_MS = 10 * 60 * 1000; // 10 minutes cache

function getCached(key) {
  const cached = cache.get(key);
  if (!cached) return null;
  if (Date.now() > cached.expiry) {
    cache.delete(key);
    return null;
  }
  return cached.value;
}

function setCached(key, value) {
  cache.set(key, {
    value,
    expiry: Date.now() + CACHE_TTL_MS
  });
}

// Request Queue implementation
class RequestQueue {
  constructor(concurrency = 3) {
    this.concurrency = concurrency;
    this.running = 0;
    this.queue = [];
  }

  push(task) {
    return new Promise((resolve, reject) => {
      this.queue.push({ task, resolve, reject });
      this.next();
    });
  }

  next() {
    if (this.running >= this.concurrency || this.queue.length === 0) {
      return;
    }
    this.running++;
    const { task, resolve, reject } = this.queue.shift();
    task()
      .then(resolve)
      .catch(reject)
      .finally(() => {
        this.running--;
        this.next();
      });
  }
}

const aiQueue = new RequestQueue(5);

// Helper function to retry with exponential backoff and timeout handling
async function fetchWithRetryAndTimeout(url, options, retries = 2, delayMs = 1000, timeoutMs = 25000) {
  for (let attempt = 0; attempt <= retries; attempt++) {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), timeoutMs);
    const fetchOptions = { ...options, signal: controller.signal };

    try {
      const response = await fetch(url, fetchOptions);
      clearTimeout(timeoutId);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status} - ${await response.text().catch(() => 'Unknown Error')}`);
      }
      return response;
    } catch (error) {
      clearTimeout(timeoutId);
      const isTimeout = error.name === 'AbortError';
      console.warn(`[Attempt ${attempt + 1}] Error fetching ${url}: ${isTimeout ? 'Timeout' : error.message}`);
      
      if (attempt === retries) {
        throw error;
      }
      await new Promise(res => setTimeout(res, delayMs * Math.pow(2, attempt)));
    }
  }
}

// ---------------------------------------------------------
// PROVIDER CHAIN FOR TEXT GENERATION (LLM)
// 1. Gemini -> 2. OpenRouter -> 3. Hugging Face
// ---------------------------------------------------------

async function tryGemini(prompt) {
  const apiKey = process.env.GEMINI_API_KEY;
  if (!apiKey || apiKey === 'MY_GEMINI_API_KEY') {
    throw new Error('Gemini API key not configured on backend.');
  }

  const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`;
  const response = await fetchWithRetryAndTimeout(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      contents: [{ parts: [{ text: prompt }] }]
    })
  });

  const data = await response.json();
  if (data.candidates && data.candidates[0] && data.candidates[0].content && data.candidates[0].content.parts[0]) {
    return data.candidates[0].content.parts[0].text;
  }
  throw new Error('Invalid response structure from Gemini API');
}

async function tryOpenRouter(prompt) {
  const apiKey = process.env.OPENROUTER_API_KEY;
  if (!apiKey || apiKey === 'MY_OPENROUTER_API_KEY') {
    throw new Error('OpenRouter API key not configured on backend.');
  }

  const url = 'https://openrouter.ai/api/v1/chat/completions';
  const response = await fetchWithRetryAndTimeout(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${apiKey}`,
      'HTTP-Referer': 'https://aistudio.com',
      'X-Title': 'VideoRift Platform'
    },
    body: JSON.stringify({
      model: 'meta-llama/llama-3-8b-instruct:free',
      messages: [{ role: 'user', content: prompt }]
    })
  });

  const data = await response.json();
  if (data.choices && data.choices[0] && data.choices[0].message) {
    return data.choices[0].message.content;
  }
  throw new Error('Invalid response structure from OpenRouter API');
}

async function tryHuggingFace(prompt) {
  const apiKey = process.env.HUGGINGFACE_API_KEY;
  if (!apiKey || apiKey === 'MY_HUGGINGFACE_API_KEY') {
    throw new Error('Hugging Face API key not configured on backend.');
  }

  const url = 'https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2';
  const response = await fetchWithRetryAndTimeout(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${apiKey}`
    },
    body: JSON.stringify({ inputs: prompt })
  });

  const data = await response.json();
  if (Array.isArray(data) && data[0] && data[0].generated_text) {
    // Remove prompt from generated text if HF returns it
    let text = data[0].generated_text;
    if (text.startsWith(prompt)) {
      text = text.substring(prompt.length).trim();
    }
    return text;
  }
  throw new Error('Invalid response structure from Hugging Face Inference API');
}

// ---------------------------------------------------------
// IMAGE AND VIDEO GENERATION (Replicate)
// ---------------------------------------------------------

async function generateImageViaReplicate(prompt) {
  const apiKey = process.env.REPLICATE_API_KEY;
  if (!apiKey || apiKey === 'MY_REPLICATE_API_KEY') {
    throw new Error('Replicate API key not configured on backend.');
  }

  // Use FLUX schnell model (extremely fast and high quality)
  const url = 'https://api.replicate.com/v1/predictions';
  const response = await fetchWithRetryAndTimeout(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Token ${apiKey}`
    },
    body: JSON.stringify({
      version: '31e35c80d05e8141db9911cb4ab13f796987e91559867909c0fa46ff6f43e597', // FLUX.1 Schnell
      input: {
        prompt: prompt,
        aspect_ratio: '1:1',
        output_format: 'webp',
        output_quality: 90
      }
    })
  });

  const prediction = await response.json();
  return await pollReplicatePrediction(prediction.id, apiKey);
}

async function generateVideoViaReplicate(prompt, inputImageBase64) {
  const apiKey = process.env.REPLICATE_API_KEY;
  if (!apiKey || apiKey === 'MY_REPLICATE_API_KEY') {
    throw new Error('Replicate API key not configured on backend.');
  }

  const isImageToVideo = !!inputImageBase64;
  let version, input;

  if (isImageToVideo) {
    // Luma Dream Machine or Stable Video Diffusion
    version = "3f2e4d049d139fe07e78d910f5451ba1450a11cb1e791839218d6a89c3ca3ee7"; // stable-video-diffusion
    input = {
      video_length: "14_frames_with_svd",
      sizing_strategy: "maintain_aspect_ratio",
      input_image: inputImageBase64.startsWith('data:') ? inputImageBase64 : `data:image/jpeg;base64,${inputImageBase64}`
    };
  } else {
    // Text-to-Video using Luma or Minimax / Hotshot
    version = "b0ef8549e491c95b4528be63a43bfef50e194883ef4a4fe487053e1fdf9db107"; // luma dream-machine prompt-to-video or similar text-to-video
    input = {
      prompt: prompt,
      aspect_ratio: "16:9"
    };
  }

  const url = 'https://api.replicate.com/v1/predictions';
  const response = await fetchWithRetryAndTimeout(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Token ${apiKey}`
    },
    body: JSON.stringify({ version, input })
  });

  const prediction = await response.json();
  return await pollReplicatePrediction(prediction.id, apiKey);
}

async function pollReplicatePrediction(predictionId, apiKey) {
  const url = `https://api.replicate.com/v1/predictions/${predictionId}`;
  const maxAttempts = 20; // Up to 60s
  
  for (let i = 0; i < maxAttempts; i++) {
    const response = await fetch(url, {
      headers: { 'Authorization': `Token ${apiKey}` }
    });
    if (!response.ok) {
      throw new Error(`Failed to poll Replicate task: ${response.statusText}`);
    }
    const result = await response.json();
    if (result.status === 'succeeded') {
      const output = result.output;
      if (Array.isArray(output)) return output[0];
      return output;
    }
    if (result.status === 'failed' || result.status === 'canceled') {
      throw new Error(`Replicate generation ${result.status}: ${result.error || 'Unknown error'}`);
    }
    await new Promise(res => setTimeout(res, 3000));
  }
  throw new Error('Replicate prediction polling timed out.');
}

// ---------------------------------------------------------
// EXPRESS REST API ENDPOINTS
// ---------------------------------------------------------

// Endpoint to enhance text or generate cinematic scripts
app.post('/api/generate/text', async (req, res) => {
  const { prompt } = req.body;
  if (!prompt) {
    return res.status(400).json({ error: 'Prompt is required' });
  }

  // 1. Check Cache
  const cachedResult = getCached(prompt);
  if (cachedResult) {
    console.log(`[Cache Hit] Serving cached text response for: ${prompt.substring(0, 30)}...`);
    return res.json({ text: cachedResult.text, provider: cachedResult.provider, cached: true });
  }

  console.log(`[Queue] Queueing text generation task...`);
  
  try {
    const result = await aiQueue.push(async () => {
      // 2. Try Providers in Chain
      let text = '';
      let provider = '';

      // Try Gemini
      try {
        console.log('[Chain 1/3] Attempting Gemini API...');
        text = await tryGemini(prompt);
        provider = 'Gemini';
      } catch (geminiError) {
        console.warn(`[Chain 1/3] Gemini failed: ${geminiError.message}. Switching to OpenRouter...`);
        
        // Try OpenRouter
        try {
          console.log('[Chain 2/3] Attempting OpenRouter API...');
          text = await tryOpenRouter(prompt);
          provider = 'OpenRouter';
        } catch (orError) {
          console.warn(`[Chain 2/3] OpenRouter failed: ${orError.message}. Switching to Hugging Face...`);
          
          // Try Hugging Face
          try {
            console.log('[Chain 3/3] Attempting Hugging Face Inference API...');
            text = await tryHuggingFace(prompt);
            provider = 'Hugging Face';
          } catch (hfError) {
            console.error('[Chain 3/3] All text generation providers failed!');
            throw new Error(`All providers failed. Gemini: ${geminiError.message} | OpenRouter: ${orError.message} | Hugging Face: ${hfError.message}`);
          }
        }
      }

      return { text, provider };
    });

    // 3. Cache Success Response
    setCached(prompt, { text: result.text, provider: result.provider });
    return res.json({ text: result.text, provider: result.provider, cached: false });

  } catch (error) {
    console.error('[Text Generation Error]', error.message);
    return res.status(500).json({ error: error.message });
  }
});

// Endpoint to generate professional images
app.post('/api/generate/image', async (req, res) => {
  const { prompt } = req.body;
  if (!prompt) {
    return res.status(400).json({ error: 'Prompt is required' });
  }

  // Check Cache
  const cachedUrl = getCached(`img_${prompt}`);
  if (cachedUrl) {
    console.log('[Cache Hit] Serving cached image response.');
    return res.json({ imageUrl: cachedUrl, provider: 'Replicate', cached: true });
  }

  try {
    const imageUrl = await aiQueue.push(() => generateImageViaReplicate(prompt));
    setCached(`img_${prompt}`, imageUrl);
    return res.json({ imageUrl, provider: 'Replicate', cached: false });
  } catch (error) {
    console.error('[Image Generation Error]', error.message);
    return res.status(500).json({ error: error.message });
  }
});

// Endpoint to generate cinematic video sequences
app.post('/api/generate/video', async (req, res) => {
  const { prompt, inputImage } = req.body;
  if (!prompt && !inputImage) {
    return res.status(400).json({ error: 'Prompt or inputImage is required' });
  }

  try {
    const videoUrl = await aiQueue.push(() => generateVideoViaReplicate(prompt, inputImage));
    return res.json({ videoUrl, provider: 'Replicate' });
  } catch (error) {
    console.error('[Video Generation Error]', error.message);
    return res.status(500).json({ error: error.message });
  }
});

// Health check and provider status check
app.get('/api/status', (req, res) => {
  res.json({
    status: 'ONLINE',
    providers: {
      gemini: !!process.env.GEMINI_API_KEY,
      openrouter: !!process.env.OPENROUTER_API_KEY,
      replicate: !!process.env.REPLICATE_API_KEY,
      huggingface: !!process.env.HUGGINGFACE_API_KEY
    },
    cacheSize: cache.size,
    queueActive: aiQueue.running,
    queueWaiting: aiQueue.queue.length
  });
});

app.listen(PORT, () => {
  console.log(`🚀 Multi-provider AI Platform Server is running securely on http://localhost:${PORT}`);
});
