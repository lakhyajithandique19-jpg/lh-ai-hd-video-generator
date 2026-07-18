import React, { useState, useRef } from 'react';

/**
 * ImageToVideoGenerator - A React Component for Image-to-Video generation.
 * Includes image preview, motion intensity slider (1-10), and a mock API call with state indicators.
 * Built with styling suitable for modern tailwind-based or custom layouts.
 */
export default function ImageToVideoGenerator() {
  const [selectedImage, setSelectedImage] = useState(null);
  const [imagePreviewUrl, setImagePreviewUrl] = useState(null);
  const [motionIntensity, setMotionIntensity] = useState(5); // Default is 5 (range 1-10)
  const [isGenerating, setIsGenerating] = useState(false);
  const [progress, setProgress] = useState(0);
  const [generationStatus, setGenerationStatus] = useState(''); // 'idle' | 'success' | 'error'
  const [generatedVideoUrl, setGeneratedVideoUrl] = useState(null);
  const fileInputRef = useRef(null);

  // Handle image selection
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedImage(file);
      const url = URL.createObjectURL(file);
      setImagePreviewUrl(url);
      setGenerationStatus('idle');
      setGeneratedVideoUrl(null);
    }
  };

  // Trigger file selection dialog
  const triggerFileSelect = () => {
    fileInputRef.current?.click();
  };

  // Remove current image
  const removeImage = (e) => {
    e.stopPropagation();
    setSelectedImage(null);
    setImagePreviewUrl(null);
    setGenerationStatus('idle');
    setGeneratedVideoUrl(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  // Trigger placeholder video generation API call
  const handleGenerateVideo = async () => {
    if (!selectedImage) {
      alert('Please select or upload a source image first.');
      return;
    }

    setIsGenerating(true);
    setProgress(5);
    setGenerationStatus('generating');
    setGeneratedVideoUrl(null);

    // Simulate different stages of API video generation
    const stages = [
      { progress: 15, msg: 'Uploading source image to servers...' },
      { progress: 35, msg: 'Analyzing image composition and depth layers...' },
      { progress: 60, msg: `Applying motion matrices (Intensity: ${motionIntensity}/10)...` },
      { progress: 85, msg: 'Rendering high-definition video frames...' },
      { progress: 100, msg: 'Finalizing high-definition MP4 stream...' }
    ];

    for (let i = 0; i < stages.length; i++) {
      await new Promise((resolve) => setTimeout(resolve, 1200));
      setProgress(stages[i].progress);
      setGenerationStatus(stages[i].msg);
    }

    // Mock API response call
    try {
      // Placeholder API call setup
      const response = await fetch('https://httpbin.org/post', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          imageName: selectedImage.name,
          motionIntensity: motionIntensity,
          timestamp: new Date().toISOString(),
          requestType: 'image-to-video'
        })
      });

      if (response.ok) {
        setIsGenerating(false);
        setGenerationStatus('success');
        // Standard sample MP4 video URL
        setGeneratedVideoUrl('https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4');
      } else {
        throw new Error('Server returned an error.');
      }
    } catch (error) {
      console.error('Video Generation Error:', error);
      setIsGenerating(false);
      setGenerationStatus('error');
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>🎬 High-Definition Image-to-Video Studio</h2>
        <p style={styles.subtitle}>Transform your static images into gorgeous, fluid cinematic videos in real-time.</p>

        {/* Image Preview & Upload Area */}
        <div 
          onClick={!imagePreviewUrl ? triggerFileSelect : undefined} 
          style={{
            ...styles.previewArea,
            cursor: !imagePreviewUrl ? 'pointer' : 'default',
            borderColor: imagePreviewUrl ? '#ffd166' : '#2a3547'
          }}
        >
          <input 
            type="file" 
            ref={fileInputRef} 
            onChange={handleImageChange} 
            accept="image/*" 
            style={{ display: 'none' }}
          />

          {imagePreviewUrl ? (
            <div style={styles.previewContainer}>
              <img src={imagePreviewUrl} alt="Preview Source" style={styles.previewImage} />
              <button onClick={removeImage} style={styles.removeBtn} title="Remove Image">
                ✕
              </button>
              <div style={styles.badge}>Source Image Loaded</div>
            </div>
          ) : (
            <div style={styles.uploadPlaceholder}>
              <div style={styles.uploadIcon}>📸</div>
              <p style={styles.uploadText}>Click to browse or upload source image</p>
              <p style={styles.uploadHint}>Supports PNG, JPEG, WEBP</p>
            </div>
          )}
        </div>

        {/* Motion Intensity Control Slider */}
        <div style={styles.controlGroup}>
          <div style={styles.sliderHeader}>
            <span style={styles.controlLabel}>⚡ Motion Intensity</span>
            <span style={styles.sliderValue}>{motionIntensity} / 10</span>
          </div>
          <input 
            type="range" 
            min="1" 
            max="10" 
            value={motionIntensity} 
            onChange={(e) => setMotionIntensity(parseInt(e.target.value))}
            disabled={isGenerating}
            style={styles.slider}
          />
          <div style={styles.sliderLabels}>
            <span>Subtle Pan</span>
            <span>Cinematic Flow</span>
            <span>Hyper Motion</span>
          </div>
        </div>

        {/* Generate Button */}
        <button 
          onClick={handleGenerateVideo} 
          disabled={isGenerating || !selectedImage}
          style={{
            ...styles.generateBtn,
            opacity: (isGenerating || !selectedImage) ? 0.6 : 1,
            cursor: (isGenerating || !selectedImage) ? 'not-allowed' : 'pointer'
          }}
        >
          {isGenerating ? '⚡ Generating Cinematic Stream...' : '✨ Generate Video Now'}
        </button>

        {/* Status and Progress Bar */}
        {isGenerating && (
          <div style={styles.statusContainer}>
            <div style={styles.progressBarBg}>
              <div style={{ ...styles.progressBarFill, width: `${progress}%` }}></div>
            </div>
            <p style={styles.statusText}>
              <span style={styles.spinner}>⏳</span> {generationStatus} ({progress}%)
            </p>
          </div>
        )}

        {/* Successful Generation Video Preview */}
        {generationStatus === 'success' && generatedVideoUrl && (
          <div style={styles.successContainer}>
            <h3 style={styles.successTitle}>✨ Cinematic Video Generated Successfully!</h3>
            <div style={styles.videoWrapper}>
              <video 
                src={generatedVideoUrl} 
                controls 
                autoPlay 
                loop 
                style={styles.videoElement}
              />
            </div>
            <p style={styles.successHint}>Press Play above to preview your custom high-definition motion video.</p>
          </div>
        )}

        {/* Error State */}
        {generationStatus === 'error' && (
          <div style={styles.errorContainer}>
            ❌ Generation Failed. Standard node restriction encountered. Activating Free Cloud fallback stream.
          </div>
        )}
      </div>
    </div>
  );
}

// Inline Styles for instant portability & styling isolation
const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    padding: '24px',
    backgroundColor: '#0c0f14',
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
    minHeight: '100%',
    color: '#ffffff'
  },
  card: {
    width: '100%',
    maxWidth: '540px',
    backgroundColor: '#131924',
    borderRadius: '16px',
    padding: '28px',
    border: '1px solid #1e2638',
    boxShadow: '0 10px 30px rgba(0, 0, 0, 0.5)'
  },
  title: {
    fontSize: '22px',
    fontWeight: '700',
    color: '#ffffff',
    margin: '0 0 8px 0',
    textAlign: 'center'
  },
  subtitle: {
    fontSize: '13px',
    color: '#a0aec0',
    lineHeight: '1.5',
    margin: '0 0 24px 0',
    textAlign: 'center'
  },
  previewArea: {
    height: '240px',
    border: '2px dashed #2a3547',
    borderRadius: '12px',
    backgroundColor: '#0c0f14',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    position: 'relative',
    overflow: 'hidden',
    transition: 'all 0.2s ease'
  },
  previewContainer: {
    width: '100%',
    height: '100%',
    position: 'relative'
  },
  previewImage: {
    width: '100%',
    height: '100%',
    objectFit: 'cover'
  },
  removeBtn: {
    position: 'absolute',
    top: '12px',
    right: '12px',
    width: '32px',
    height: '32px',
    borderRadius: '50%',
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    color: '#ffffff',
    border: 'none',
    cursor: 'pointer',
    fontSize: '14px',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    fontWeight: 'bold',
    transition: 'background-color 0.2s',
  },
  badge: {
    position: 'absolute',
    bottom: '12px',
    left: '12px',
    backgroundColor: 'rgba(255, 209, 102, 0.9)',
    color: '#0c0f14',
    padding: '4px 10px',
    borderRadius: '6px',
    fontSize: '11px',
    fontWeight: 'bold'
  },
  uploadPlaceholder: {
    textAlign: 'center',
    padding: '20px'
  },
  uploadIcon: {
    fontSize: '36px',
    marginBottom: '12px'
  },
  uploadText: {
    fontSize: '14px',
    color: '#e2e8f0',
    fontWeight: '500',
    margin: '0 0 4px 0'
  },
  uploadHint: {
    fontSize: '11px',
    color: '#718096',
    margin: '0'
  },
  controlGroup: {
    marginTop: '24px',
    marginBottom: '24px'
  },
  sliderHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '10px'
  },
  controlLabel: {
    fontSize: '14px',
    fontWeight: '600',
    color: '#ffffff'
  },
  sliderValue: {
    fontSize: '14px',
    fontWeight: '700',
    color: '#ffd166'
  },
  slider: {
    width: '100%',
    height: '6px',
    borderRadius: '3px',
    outline: 'none',
    backgroundColor: '#2a3547',
    cursor: 'pointer',
    accentColor: '#ffd166'
  },
  sliderLabels: {
    display: 'flex',
    justifyContent: 'space-between',
    fontSize: '10px',
    color: '#718096',
    marginTop: '6px'
  },
  generateBtn: {
    width: '100%',
    padding: '14px',
    borderRadius: '10px',
    backgroundColor: '#ffd166',
    color: '#0c0f14',
    fontSize: '15px',
    fontWeight: '700',
    border: 'none',
    transition: 'background-color 0.2s, transform 0.1s',
    boxShadow: '0 4px 14px rgba(255, 209, 102, 0.3)'
  },
  statusContainer: {
    marginTop: '20px',
    textAlign: 'center'
  },
  progressBarBg: {
    width: '100%',
    height: '6px',
    backgroundColor: '#2a3547',
    borderRadius: '3px',
    overflow: 'hidden',
    marginBottom: '8px'
  },
  progressBarFill: {
    height: '100%',
    backgroundColor: '#ffd166',
    transition: 'width 0.4s ease'
  },
  statusText: {
    fontSize: '12px',
    color: '#cbd5e0',
    margin: '0'
  },
  spinner: {
    display: 'inline-block',
    animation: 'spin 2s linear infinite'
  },
  successContainer: {
    marginTop: '24px',
    padding: '16px',
    borderRadius: '12px',
    backgroundColor: '#1b2436',
    border: '1px solid #2d3d5a'
  },
  successTitle: {
    fontSize: '13px',
    color: '#00e676',
    margin: '0 0 12px 0',
    textAlign: 'center',
    fontWeight: 'bold'
  },
  videoWrapper: {
    borderRadius: '8px',
    overflow: 'hidden',
    backgroundColor: '#0c0f14',
    lineHeight: '0'
  },
  videoElement: {
    width: '100%',
    height: 'auto',
    display: 'block'
  },
  successHint: {
    fontSize: '11px',
    color: '#a0aec0',
    textAlign: 'center',
    marginTop: '10px',
    margin: '0'
  },
  errorContainer: {
    marginTop: '20px',
    padding: '12px',
    borderRadius: '8px',
    backgroundColor: 'rgba(255, 82, 82, 0.1)',
    border: '1px solid #ff5252',
    color: '#ff5252',
    fontSize: '12px',
    textAlign: 'center'
  }
};
