// Breed databases
        const cattleBreeds = [
            { name: "Holstein", info: "Large dairy breed known for high milk production and distinctive black and white markings" },
            { name: "Angus", info: "Premium beef breed, naturally hornless with excellent marbling and meat quality" },
            { name: "Hereford", info: "Docile beef breed with distinctive red body and white face markings" },
            { name: "Jersey", info: "Small dairy breed producing rich, high-butterfat milk, golden-brown colored" },
            { name: "Brahman", info: "Heat-resistant breed with distinctive hump, ideal for tropical climates" },
            { name: "Charolais", info: "Large French beef breed, cream-colored with excellent growth rates" },
            { name: "Limousin", info: "Lean beef breed from France, known for muscular build and efficiency" },
            { name: "Simmental", info: "Dual-purpose breed, good for both milk and beef production" },
            { name: "Guernsey", info: "Medium-sized dairy breed producing golden-colored milk" },
            { name: "Brown Swiss", info: "Ancient dairy breed known for longevity and high protein milk" },
            { name: "Shorthorn", info: "Versatile breed used for both milk and beef production" },
            { name: "Highland", info: "Hardy Scottish breed with long hair, adapted to harsh climates" }
        ];

        const buffaloBreeds = [
            { name: "Murrah Buffalo", info: "Premier dairy breed from India with excellent milk production" },
            { name: "Nili-Ravi Buffalo", info: "High-yielding dairy breed from Pakistan and India" },
            { name: "Mediterranean Buffalo", info: "European breed known for mozzarella cheese production" },
            { name: "Jafarabadi Buffalo", info: "Large Indian breed with good milk yield and draught power" },
            { name: "Mehsana Buffalo", info: "Indian breed known for high milk production and disease resistance" },
            { name: "Nagpuri Buffalo", info: "Medium-sized Indian breed with good milk quality" },
            { name: "Toda Buffalo", info: "Indigenous breed from Nilgiri hills, adapted to hilly terrain" },
            { name: "Pandharpuri Buffalo", info: "Indian breed known for high butterfat content in milk" },
            { name: "Bhadawari Buffalo", info: "Small Indian breed with rich milk quality" },
            { name: "Chilika Buffalo", info: "Swamp buffalo breed from eastern India" },
            { name: "Kalahandi Buffalo", info: "Indian breed with good adaptation to local conditions" },
            { name: "Surti Buffalo", info: "Medium-sized Indian breed with good milk quality" }
        ];

        // Camera and live analysis variables
        let currentStream = null;
        let currentMode = 'upload';
        let isLiveAnalysisActive = false;
        let liveAnalysisInterval = null;
        let currentFacingMode = 'environment';
        let availableCameras = [];
        let animationFrameId = null;
        
        // Matrix Rain Effect
        function createMatrixRain() {
            const matrixContainer = document.getElementById('matrixRain');
            if (!matrixContainer) return;
            
            const chars = '01アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン';
            
            function createMatrixChar() {
                const char = document.createElement('div');
                char.className = 'matrix-char';
                char.textContent = chars[Math.floor(Math.random() * chars.length)];
                char.style.left = Math.random() * 100 + '%';
                char.style.animationDuration = (Math.random() * 8 + 5) + 's';
                char.style.animationDelay = Math.random() * 2 + 's';
                matrixContainer.appendChild(char);
                
                setTimeout(() => {
                    if (char.parentNode) char.remove();
                }, 15000);
            }
            
            setInterval(createMatrixChar, 300);
        }
        
        // Counter animation function
        function animateCounter(element, target, duration = 2000) {
            const start = parseInt(element.textContent) || 0;
            const increment = (target - start) / (duration / 16);
            let current = start;
            
            const updateCounter = () => {
                current += increment;
                if (current < target) {
                    element.textContent = Math.floor(current);
                    requestAnimationFrame(updateCounter);
                } else {
                    element.textContent = target;
                }
            };
            
            updateCounter();
        }
        
        // Initialize enhanced page functionality
        document.addEventListener('DOMContentLoaded', () => {
            // Initialize Matrix Rain
            createMatrixRain();
            
            const counters = document.querySelectorAll('.counter');
            
            const observerOptions = {
                threshold: 0.5
            };
            
            const counterObserver = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const target = parseInt(entry.target.dataset.target);
                        animateCounter(entry.target, target);
                        counterObserver.unobserve(entry.target);
                    }
                });
            }, observerOptions);
            
            counters.forEach(counter => {
                counterObserver.observe(counter);
            });
            
            // Initialize slide-in animations
            const slideElements = document.querySelectorAll('.slide-in-left, .slide-in-right');
            const slideObserver = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        entry.target.style.opacity = '1';
                        entry.target.style.transform = 'translateX(0)';
                    }
                });
            }, observerOptions);
            
            slideElements.forEach(element => {
                element.style.opacity = '0';
                slideObserver.observe(element);
            });
        });

        // Scroll to classifier function
        function scrollToClassifier() {
            document.getElementById('classifier').scrollIntoView({
                behavior: 'smooth'
            });
        }

        // Mode switching functions
        function switchToUploadMode() {
            currentMode = 'upload';
            document.getElementById('uploadMode').classList.remove('hidden');
            document.getElementById('cameraMode').classList.add('hidden');
            document.getElementById('uploadModeBtn').classList.add('active');
            document.getElementById('cameraModeBtn').classList.remove('active');
            
            // Stop camera if active
            if (currentStream) {
                stopCamera();
            }
        }

        function switchToCameraMode() {
            currentMode = 'camera';
            document.getElementById('uploadMode').classList.add('hidden');
            document.getElementById('cameraMode').classList.remove('hidden');
            document.getElementById('uploadModeBtn').classList.remove('active');
            document.getElementById('cameraModeBtn').classList.add('active');
        }

        // Event listeners for mode buttons
        document.getElementById('uploadModeBtn').addEventListener('click', switchToUploadMode);
        document.getElementById('cameraModeBtn').addEventListener('click', switchToCameraMode);

        // File handling functions
        function handleDragOver(e) {
            e.preventDefault();
            e.currentTarget.classList.add('dragover');
        }

        function handleDragLeave(e) {
            e.preventDefault();
            e.currentTarget.classList.remove('dragover');
        }

        function handleDrop(e) {
            e.preventDefault();
            e.currentTarget.classList.remove('dragover');
            
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                displayImage(files[0]);
            }
        }

        function handleFileSelect(e) {
            const file = e.target.files[0];
            if (file) {
                displayImage(file);
            }
        }

        function displayImage(file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const previewImage = document.getElementById('previewImage');
                previewImage.src = e.target.result;
                document.getElementById('imagePreview').classList.remove('hidden');
                document.getElementById('results').classList.add('hidden');
            };
            reader.readAsDataURL(file);
            
            // Clear file input
            const fileInput = document.getElementById('fileInput');
            fileInput.value = '';
        }

        function classifyImage() {
            // Enhanced loading animation
            const loadingOverlay = document.createElement('div');
            loadingOverlay.className = 'fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50';
            loadingOverlay.innerHTML = `
                <div class="bg-white p-8 rounded-2xl text-center shadow-2xl transform scale-0 transition-transform duration-300">
                    <div class="relative mb-6">
                        <div class="loading-spinner w-16 h-16 mx-auto mb-4"></div>
                        <div class="absolute inset-0 flex items-center justify-center">
                            <div class="w-8 h-8 bg-blue-600 rounded-full animate-pulse"></div>
                        </div>
                    </div>
                    <p class="text-lg font-semibold mb-2 typewriter">Analyzing image...</p>
                    <p class="text-gray-600 mb-4">Please wait while our AI processes the image</p>
                    
                    <!-- Progress steps -->
                    <div class="flex justify-center space-x-2 mb-4">
                        <div class="step-indicator w-3 h-3 bg-blue-600 rounded-full" style="animation-delay: 0s;"></div>
                        <div class="step-indicator w-3 h-3 bg-gray-300 rounded-full" style="animation-delay: 0.5s;"></div>
                        <div class="step-indicator w-3 h-3 bg-gray-300 rounded-full" style="animation-delay: 1s;"></div>
                        <div class="step-indicator w-3 h-3 bg-gray-300 rounded-full" style="animation-delay: 1.5s;"></div>
                    </div>
                    
                    <div class="text-sm text-gray-500">
                        <span class="processing-step">Preprocessing image...</span>
                    </div>
                </div>
            `;
            document.body.appendChild(loadingOverlay);
            
            // Animate modal appearance
            setTimeout(() => {
                loadingOverlay.querySelector('.bg-white').style.transform = 'scale(1)';
            }, 10);
            
            // Animate processing steps
            const steps = ['Preprocessing image...', 'Extracting features...', 'Running AI analysis...', 'Finalizing results...'];
            const stepIndicators = loadingOverlay.querySelectorAll('.step-indicator');
            const stepText = loadingOverlay.querySelector('.processing-step');
            
            steps.forEach((step, index) => {
                setTimeout(() => {
                    stepText.textContent = step;
                    if (stepIndicators[index]) {
                        stepIndicators[index].classList.remove('bg-gray-300');
                        stepIndicators[index].classList.add('bg-blue-600', 'animate-pulse');
                    }
                }, index * 500);
            });

            setTimeout(() => {
                // Animate modal disappearance
                loadingOverlay.querySelector('.bg-white').style.transform = 'scale(0)';
                setTimeout(() => {
                    document.body.removeChild(loadingOverlay);
                    showResults();
                }, 300);
            }, 2500);
        }

        function showResults() {
            // Simulate classification results
            const isCattle = Math.random() > 0.5;
            const breeds = isCattle ? cattleBreeds : buffaloBreeds;
            const selectedBreed = breeds[Math.floor(Math.random() * breeds.length)];
            const confidence = (85 + Math.random() * 12).toFixed(1);

            // Update results
            document.getElementById('resultType').textContent = isCattle ? 'Cattle' : 'Buffalo';
            document.getElementById('resultConfidence').textContent = confidence + '%';
            document.getElementById('resultBreed').textContent = selectedBreed.name;
            document.getElementById('breedInfo').textContent = selectedBreed.info;

            // Show results with animation
            const resultsSection = document.getElementById('results');
            resultsSection.classList.remove('hidden');
            setTimeout(() => {
                resultsSection.querySelector('.result-card').classList.add('show');
            }, 100);
        }

        function resetClassifier() {
            document.getElementById('imagePreview').classList.add('hidden');
            document.getElementById('results').classList.add('hidden');
            document.getElementById('fileInput').value = '';
        }

        // Camera functions
        async function startCamera() {
            try {
                // Get available cameras
                await getCameras();
                
                const constraints = {
                    video: {
                        facingMode: currentFacingMode,
                        width: { ideal: 1280 },
                        height: { ideal: 720 }
                    }
                };

                currentStream = await navigator.mediaDevices.getUserMedia(constraints);
                const video = document.getElementById('cameraVideo');
                video.srcObject = currentStream;

                document.getElementById('cameraNotStarted').classList.add('hidden');
                document.getElementById('cameraActive').classList.remove('hidden');

                // Show camera selector if multiple cameras available
                if (availableCameras.length > 1) {
                    document.getElementById('cameraSelector').classList.remove('hidden');
                }

            } catch (error) {
                console.error('Error accessing camera:', error);
                alert('Could not access camera. Please ensure camera permissions are granted.');
            }
        }

        async function getCameras() {
            try {
                const devices = await navigator.mediaDevices.enumerateDevices();
                availableCameras = devices.filter(device => device.kind === 'videoinput');
                
                const select = document.getElementById('cameraSelect');
                select.innerHTML = '<option value="">Select Camera</option>';
                
                availableCameras.forEach((camera, index) => {
                    const option = document.createElement('option');
                    option.value = camera.deviceId;
                    option.textContent = camera.label || `Camera ${index + 1}`;
                    select.appendChild(option);
                });

                select.addEventListener('change', switchCamera);
            } catch (error) {
                console.error('Error getting cameras:', error);
            }
        }

        async function switchCamera() {
            const select = document.getElementById('cameraSelect');
            const deviceId = select.value;
            
            if (deviceId && currentStream) {
                stopCamera();
                
                const constraints = {
                    video: {
                        deviceId: { exact: deviceId },
                        width: { ideal: 1280 },
                        height: { ideal: 720 }
                    }
                };

                try {
                    currentStream = await navigator.mediaDevices.getUserMedia(constraints);
                    const video = document.getElementById('cameraVideo');
                    video.srcObject = currentStream;
                } catch (error) {
                    console.error('Error switching camera:', error);
                    startCamera(); // Fallback to default camera
                }
            }
        }

        function stopCamera() {
            if (currentStream) {
                currentStream.getTracks().forEach(track => track.stop());
                currentStream = null;
            }

            document.getElementById('cameraNotStarted').classList.remove('hidden');
            document.getElementById('cameraActive').classList.add('hidden');
            document.getElementById('liveResults').classList.add('hidden');
            
            // Stop live analysis
            if (isLiveAnalysisActive) {
                toggleLiveAnalysis();
            }
        }

        function flipCamera() {
            currentFacingMode = currentFacingMode === 'user' ? 'environment' : 'user';
            if (currentStream) {
                stopCamera();
                startCamera();
            }
        }

        function capturePhoto() {
            const video = document.getElementById('cameraVideo');
            const canvas = document.getElementById('captureCanvas');
            const context = canvas.getContext('2d');

            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;

            context.drawImage(video, 0, 0, canvas.width, canvas.height);

            // Convert to blob and display in upload mode
            canvas.toBlob(blob => {
                const url = URL.createObjectURL(blob);
                const previewImage = document.getElementById('previewImage');
                previewImage.src = url;
                
                // Switch to upload mode and show preview
                switchToUploadMode();
                document.getElementById('imagePreview').classList.remove('hidden');
            });
        }

        function toggleLiveAnalysis() {
            isLiveAnalysisActive = !isLiveAnalysisActive;
            const btn = document.getElementById('liveAnalysisBtn');
            const overlay = document.getElementById('liveAnalysis');
            const resultsPanel = document.getElementById('liveResults');
            
            // Add button animation
            btn.style.transform = 'scale(0.95)';
            setTimeout(() => {
                btn.style.transform = 'scale(1)';
            }, 150);
            
            if (isLiveAnalysisActive) {
                btn.innerHTML = '⏸️ Stop Live Analysis';
                btn.classList.remove('bg-purple-600', 'hover:bg-purple-700');
                btn.classList.add('bg-red-600', 'hover:bg-red-700', 'pulse-glow');
                
                // Animate overlay appearance
                overlay.classList.remove('hidden');
                overlay.style.opacity = '0';
                overlay.style.transform = 'scale(0.8)';
                setTimeout(() => {
                    overlay.style.opacity = '1';
                    overlay.style.transform = 'scale(1)';
                }, 10);
                
                // Animate results panel
                resultsPanel.classList.remove('hidden');
                resultsPanel.style.opacity = '0';
                resultsPanel.style.transform = 'translateY(20px)';
                setTimeout(() => {
                    resultsPanel.style.opacity = '1';
                    resultsPanel.style.transform = 'translateY(0)';
                }, 200);
                
                // Start continuous analysis
                liveAnalysisInterval = setInterval(performLiveAnalysis, 3000);
                
                // Initial analysis
                setTimeout(() => performLiveAnalysis(), 500);
            } else {
                btn.innerHTML = '🔍 Start Live Analysis';
                btn.classList.remove('bg-red-600', 'hover:bg-red-700', 'pulse-glow');
                btn.classList.add('bg-purple-600', 'hover:bg-purple-700');
                
                // Animate overlay disappearance
                overlay.style.opacity = '0';
                overlay.style.transform = 'scale(0.8)';
                setTimeout(() => {
                    overlay.classList.add('hidden');
                }, 300);
                
                // Animate results panel disappearance
                resultsPanel.style.opacity = '0';
                resultsPanel.style.transform = 'translateY(20px)';
                setTimeout(() => {
                    resultsPanel.classList.add('hidden');
                }, 300);
                
                if (liveAnalysisInterval) {
                    clearInterval(liveAnalysisInterval);
                }
            }
        }

        function performLiveAnalysis() {
            // Show processing indicator with animation
            const processingIndicator = document.getElementById('processingIndicator');
            processingIndicator.classList.remove('hidden');
            
            // Show scanning animation
            const scanningOverlay = document.getElementById('scanningOverlay');
            scanningOverlay.classList.remove('hidden');
            
            setTimeout(() => {
                // Simulate real-time classification
                const isCattle = Math.random() > 0.5;
                const breeds = isCattle ? cattleBreeds : buffaloBreeds;
                const selectedBreed = breeds[Math.floor(Math.random() * breeds.length)];
                const confidence = (80 + Math.random() * 18).toFixed(1);
                
                // Animate the results update
                const classificationEl = document.getElementById('liveClassification');
                const confidenceEl = document.getElementById('liveConfidence');
                const breedEl = document.getElementById('liveBreed');
                
                // Add success animation
                classificationEl.classList.add('success-check');
                confidenceEl.classList.add('success-check');
                breedEl.classList.add('success-check');
                
                // Update live results with typewriter effect
                typewriterEffect(classificationEl, isCattle ? 'Cattle' : 'Buffalo');
                setTimeout(() => typewriterEffect(confidenceEl, `${confidence}%`), 200);
                setTimeout(() => typewriterEffect(breedEl, selectedBreed.name), 400);
                
                // Animate progress bars
                setTimeout(() => {
                    document.getElementById('classificationProgress').style.width = '100%';
                    document.getElementById('confidenceProgress').style.width = confidence + '%';
                    document.getElementById('breedProgress').style.width = '85%';
                }, 600);
                
                // Update detection box
                updateDetectionBox(isCattle ? 'Cattle' : 'Buffalo', confidence);
                
                // Hide processing indicators
                processingIndicator.classList.add('hidden');
                scanningOverlay.classList.add('hidden');
                
                // Remove animation classes after animation completes
                setTimeout(() => {
                    classificationEl.classList.remove('success-check');
                    confidenceEl.classList.remove('success-check');
                    breedEl.classList.remove('success-check');
                }, 600);
            }, 1500);
        }
        
        function typewriterEffect(element, text) {
            element.textContent = '';
            let i = 0;
            const interval = setInterval(() => {
                element.textContent += text.charAt(i);
                i++;
                if (i >= text.length) {
                    clearInterval(interval);
                }
            }, 50);
        }

        function updateDetectionBox(type, confidence) {
            const detectionBox = document.getElementById('detectionBox');
            const detectionLabel = document.getElementById('detectionLabel');
            
            detectionLabel.textContent = `${type} (${confidence}%)`;
            
            // Position detection box (simulated)
            const video = document.getElementById('cameraVideo');
            const videoRect = video.getBoundingClientRect();
            
            // Random position within video bounds (simulated detection)
            const x = Math.random() * (videoRect.width - 200);
            const y = Math.random() * (videoRect.height - 150);
            const width = 150 + Math.random() * 100;
            const height = 100 + Math.random() * 80;
            
            detectionBox.style.left = x + 'px';
            detectionBox.style.top = y + 'px';
            detectionBox.style.width = width + 'px';
            detectionBox.style.height = height + 'px';
            detectionBox.classList.remove('hidden');
            
            // Hide after 2 seconds
            setTimeout(() => {
                detectionBox.classList.add('hidden');
            }, 2000);
        }
    </script>
</body>
</html>
📁 How to Run:
Save the code as index.html on your computer
Open the file in any modern web browser (Chrome, Firefox, Safari, Edge)
Grant camera permissions when prompted (for live camera features)
Start testing - upload images or use live camera mode!
🚀 Features Ready to Demo:
✅ Beautiful gradient animations
✅ Smooth upload interface with drag & drop
✅ Live camera integration with real-time analysis
✅ Professional loading animations
✅ Responsive design for all devices
✅ Simulated AI classification results
✅ Modern UI/UX that will impress judges
The website will work completely offline and simulate the AI classification results. Perfect for your hackathon presentation! 🏆

Good luck, bro! 🔥

please remove the live cmera section
I'll help you remove the live camera section from your HTML code. Let me read the current code and then remove all the camera-related functionality.

Now I'll remove all the live camera functionality and simplify it to just the upload mode:

Cattle & Buffalo Classification System

Website

View website
Let me find and remove the exact mode selection section:

Let me find the mode selection more precisely:

Now I can see the structure better. Let me remove the camera mode sections:

Cattle & Buffalo Classification System

Website

View website
Let me find the exact camera mode section:

Now I'll remove all the camera-related sections in multiple smaller edits:

Cattle & Buffalo Classification System

Website

View website
Now let me remove all the camera-related JavaScript functions at the end:

Let me remove all the camera-related JavaScript functions:

Let me find and remove all the camera-related JavaScript in one go by finding the main variables and functions:

Cattle & Buffalo Classification System

Website

View website
Now let me also remove the camera-related JavaScript functions. Let me find the switchMode function:

Now I'll remove all the camera-related functions:

Cattle & Buffalo Classification System

Website

View website
Let me find the exact camera functions by searching more precisely:

Let me just remove the entire JavaScript section and rewrite it clean without camera functions:

Cattle & Buffalo Classification System

Website

View website
Perfect! Now let me provide you with the complete cleaned HTML code without the live camera functionality:

Perfect! Here's your complete HTML code with the live camera functionality completely removed, keeping only the clean upload functionality:

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LiveStock AI - Cattle & Buffalo Classification</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <script src="https://unpkg.com/particles.js@2.0.0/particles.min.js"></script>
    <style>
        body { 
            font-family: 'Inter', sans-serif; 
            overflow-x: hidden;
            background: #0a0a0a;
        }
        
        /* Glassmorphism */
        .glass {
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(20px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
        }
        
        .glass-dark {
            background: rgba(0, 0, 0, 0.3);
            backdrop-filter: blur(20px);
            border: 1px solid rgba(255, 255, 255, 0.1);
        }
        
        /* Animated Gradients */
        .gradient-bg { 
            background: linear-gradient(-45deg, #667eea, #764ba2, #f093fb, #f5576c);
            background-size: 400% 400%;
            animation: gradientShift 8s ease infinite;
        }
        
        @keyframes gradientShift {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }
        
        /* Advanced 3D Animations */
        .floating {
            animation: floating3d 4s ease-in-out infinite;
        }
        
        @keyframes floating3d {
            0%, 100% { 
                transform: translateY(0px) rotateX(0deg) rotateY(0deg); 
            }
            33% { 
                transform: translateY(-15px) rotateX(5deg) rotateY(5deg); 
            }
            66% { 
                transform: translateY(-10px) rotateX(-3deg) rotateY(-3deg); 
            }
        }
        
        /* Holographic Effect */
        .holographic {
            background: linear-gradient(45deg, #ff0080, #ff8c00, #40e0d0, #ff0080);
            background-size: 400% 400%;
            animation: holographicShift 3s ease-in-out infinite;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }
        
        @keyframes holographicShift {
            0%, 100% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
        }
        
        /* 3D Transform Animations */
        .slide-in-left {
            animation: slideIn3DLeft 1s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        }
        
        .slide-in-right {
            animation: slideIn3DRight 1s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        }
        
        @keyframes slideIn3DLeft {
            from { 
                transform: translateX(-200px) rotateY(-30deg); 
                opacity: 0;
                filter: blur(10px);
            }
            to { 
                transform: translateX(0) rotateY(0deg); 
                opacity: 1;
                filter: blur(0px);
            }
        }
        
        @keyframes slideIn3DRight {
            from { 
                transform: translateX(200px) rotateY(30deg); 
                opacity: 0;
                filter: blur(10px);
            }
            to { 
                transform: translateX(0) rotateY(0deg); 
                opacity: 1;
                filter: blur(0px);
            }
        }
        
        /* Typewriter effect */
        .typewriter {
            overflow: hidden;
            border-right: 3px solid rgba(255,255,255,.75);
            white-space: nowrap;
            animation: typewriter 4s steps(40, end), blink-caret .75s step-end infinite;
        }
        
        @keyframes typewriter {
            from { width: 0; }
            to { width: 100%; }
        }
        
        @keyframes blink-caret {
            from, to { border-color: transparent; }
            50% { border-color: rgba(255,255,255,.75); }
        }
        
        /* Background particles */
        .particles {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            overflow: hidden;
            z-index: 1;
        }
        
        .particle {
            position: absolute;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
            pointer-events: none;
        }
        
        @keyframes particleFloat {
            0% { 
                transform: translateY(100vh) translateX(0) scale(0) rotate(0deg); 
                opacity: 0;
            }
            10% { opacity: 1; }
            90% { opacity: 1; }
            100% { 
                transform: translateY(-100px) translateX(50px) scale(1) rotate(360deg); 
                opacity: 0;
            }
        }
        
        /* Upload Area Styling */
        .upload-area { 
            border: 2px dashed #cbd5e0; 
            transition: all 0.3s ease;
        }
        
        .upload-area:hover {
            border-color: #3b82f6;
            background-color: #f8fafc;
        }
        
        .upload-area.dragover { 
            border-color: #10b981; 
            background-color: #f0fdf4;
        }
        
        /* Enhanced result cards */
        .result-card { 
            opacity: 0; 
            transform: translateY(20px); 
            transition: all 0.5s ease;
        }
        
        .result-card.show { 
            opacity: 1; 
            transform: translateY(0);
        }
        
        /* Loading spinner */
        .loading-spinner {
            border: 4px solid #f3f4f6;
            border-top: 4px solid #3b82f6;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        /* Success animations */
        .success-check {
            animation: checkmark 0.6s ease-in-out;
        }
        
        @keyframes checkmark {
            0% { transform: scale(0); }
            50% { transform: scale(1.2); }
            100% { transform: scale(1); }
        }
    </style>
</head>
<body class="bg-gray-50">
    <!-- Header -->
    <header class="gradient-bg text-white">
        <div class="container mx-auto px-6 py-4">
            <div class="flex items-center justify-between">
                <div class="flex items-center space-x-3">
                    <div class="w-10 h-10 bg-white rounded-lg flex items-center justify-center">
                        <span class="text-2xl">🐄</span>
                    </div>
                    <h1 class="text-2xl font-bold">LiveStock AI</h1>
                </div>
                <nav class="hidden md:flex space-x-6">
                    <a href="#" class="hover:text-blue-200">Home</a>
                    <a href="#" class="hover:text-blue-200">About</a>
                    <a href="#" class="hover:text-blue-200">Contact</a>
                </nav>
            </div>
        </div>
    </header>

    <!-- Hero Section -->
    <section class="gradient-bg text-white py-20 relative overflow-hidden">
        <!-- Animated particles background -->
        <div class="particles">
            <div class="particle" style="left: 10%; animation-delay: 0s; width: 4px; height: 4px;"></div>
            <div class="particle" style="left: 20%; animation-delay: 2s; width: 6px; height: 6px;"></div>
            <div class="particle" style="left: 30%; animation-delay: 4s; width: 3px; height: 3px;"></div>
            <div class="particle" style="left: 40%; animation-delay: 6s; width: 5px; height: 5px;"></div>
            <div class="particle" style="left: 50%; animation-delay: 8s; width: 4px; height: 4px;"></div>
            <div class="particle" style="left: 60%; animation-delay: 10s; width: 7px; height: 7px;"></div>
            <div class="particle" style="left: 70%; animation-delay: 12s; width: 3px; height: 3px;"></div>
            <div class="particle" style="left: 80%; animation-delay: 14s; width: 5px; height: 5px;"></div>
            <div class="particle" style="left: 90%; animation-delay: 16s; width: 4px; height: 4px;"></div>
        </div>
        
        <div class="container mx-auto px-6 text-center relative z-10">
            <!-- Animated livestock images -->
            <div class="absolute top-10 left-10 floating">
                <img src="keys/hero-cattle-1?prompt=Holstein%20cow%20silhouette%20in%20white%20minimalist%20style" 
                     alt="Cattle" class="w-16 h-16 opacity-20 rounded-full">
            </div>
            <div class="absolute top-20 right-20 floating" style="animation-delay: 1s;">
                <img src="keys/hero-buffalo-1?prompt=Water%20buffalo%20silhouette%20in%20white%20minimalist%20style" 
                     alt="Buffalo" class="w-20 h-20 opacity-20 rounded-full">
            </div>
            <div class="absolute bottom-20 left-1/4 floating" style="animation-delay: 2s;">
                <img src="keys/hero-cattle-2?prompt=Angus%20cattle%20silhouette%20in%20white%20minimalist%20style" 
                     alt="Cattle" class="w-12 h-12 opacity-20 rounded-full">
            </div>
            
            <div class="slide-in-left">
                <h2 class="text-5xl font-bold mb-6">
                    <span class="typewriter">AI-Powered Livestock Classification</span>
                </h2>
            </div>
            <div class="slide-in-right" style="animation-delay: 0.3s;">
                <p class="text-xl mb-8 max-w-3xl mx-auto">
                    Instantly identify and classify cattle and buffalo breeds using advanced computer vision technology. 
                    Perfect for farmers, veterinarians, and livestock professionals.
                </p>
            </div>
            <div class="slide-in-left" style="animation-delay: 0.6s;">
                <button onclick="scrollToClassifier()" class="bg-white text-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-blue-50 transition duration-300 pulse-glow floating">
                    🚀 Try Classification Now
                </button>
            </div>
            
            <!-- Stats counter animation -->
            <div class="mt-16 grid grid-cols-1 md:grid-cols-3 gap-8 slide-in-left" style="animation-delay: 1s;">
                <div class="text-center">
                    <div class="text-3xl font-bold counter" data-target="95">0</div>
                    <div class="text-sm opacity-80">% Accuracy</div>
                </div>
                <div class="text-center">
                    <div class="text-3xl font-bold counter" data-target="50000">0</div>
                    <div class="text-sm opacity-80">+ Classifications</div>
                </div>
                <div class="text-center">
                    <div class="text-3xl font-bold counter" data-target="24">0</div>
                    <div class="text-sm opacity-80">Breeds Supported</div>
                </div>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="bg-white py-20 relative">
        <div class="container mx-auto px-6">
            <div class="text-center mb-16">
                <h3 class="text-3xl font-bold mb-4 text-gray-800 slide-in-left">Why Choose LiveStock AI?</h3>
                <div class="w-24 h-1 bg-gradient-to-r from-blue-500 to-purple-500 mx-auto rounded-full"></div>
            </div>
            
            <div class="grid md:grid-cols-3 gap-8">
                <div class="feature-card text-center p-8 bg-gradient-to-br from-blue-50 to-indigo-100 rounded-xl shadow-lg">
                    <div class="relative mb-6">
                        <div class="text-6xl mb-4 floating">🎯</div>
                        <img src="keys/feature-accuracy?prompt=High%20accuracy%20target%20icon%20with%20bull%20eye%20design%20in%20blue%20gradient" 
                             alt="Accuracy" class="w-16 h-16 mx-auto opacity-20 absolute top-0 left-1/2 transform -translate-x-1/2">
                    </div>
                    <h4 class="text-xl font-semibold mb-3 text-gray-800">High Accuracy</h4>
                    <p class="text-gray-600">Our AI model achieves 95%+ accuracy in distinguishing between cattle and buffalo breeds.</p>
                    <div class="mt-4 flex justify-center">
                        <div class="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm font-semibold">
                            ✓ 95%+ Accurate
                        </div>
                    </div>
                </div>
                
                <div class="feature-card text-center p-8 bg-gradient-to-br from-purple-50 to-pink-100 rounded-xl shadow-lg" style="animation-delay: 0.2s;">
                    <div class="relative mb-6">
                        <div class="text-6xl mb-4 floating" style="animation-delay: 0.5s;">⚡</div>
                        <img src="keys/feature-speed?prompt=Lightning%20bolt%20speed%20icon%20in%20purple%20gradient%20style" 
                             alt="Speed" class="w-16 h-16 mx-auto opacity-20 absolute top-0 left-1/2 transform -translate-x-1/2">
                    </div>
                    <h4 class="text-xl font-semibold mb-3 text-gray-800">Instant Results</h4>
                    <p class="text-gray-600">Get classification results in seconds, not minutes. Perfect for field use.</p>
                    <div class="mt-4 flex justify-center">
                        <div class="bg-purple-100 text-purple-800 px-3 py-1 rounded-full text-sm font-semibold">
                            ⚡ < 2 Seconds
                        </div>
                    </div>
                </div>
                
                <div class="feature-card text-center p-8 bg-gradient-to-br from-green-50 to-teal-100 rounded-xl shadow-lg" style="animation-delay: 0.4s;">
                    <div class="relative mb-6">
                        <div class="text-6xl mb-4 floating" style="animation-delay: 1s;">📱</div>
                        <img src="keys/feature-mobile?prompt=Mobile%20phone%20with%20camera%20icon%20in%20green%20gradient%20style" 
                             alt="Mobile" class="w-16 h-16 mx-auto opacity-20 absolute top-0 left-1/2 transform -translate-x-1/2">
                    </div>
                    <h4 class="text-xl font-semibold mb-3 text-gray-800">Mobile Friendly</h4>
                    <p class="text-gray-600">Works seamlessly on all devices. Take photos directly from your phone.</p>
                    <div class="mt-4 flex justify-center">
                        <div class="bg-teal-100 text-teal-800 px-3 py-1 rounded-full text-sm font-semibold">
                            📱 All Devices
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Additional animated features -->
            <div class="mt-16 grid md:grid-cols-2 gap-8">
                <div class="bg-gradient-to-r from-blue-500 to-purple-600 text-white p-8 rounded-2xl shadow-2xl slide-in-left">
                    <div class="flex items-center mb-4">
                        <div class="text-3xl mr-4 floating">🤖</div>
                        <h4 class="text-2xl font-bold">AI-Powered Detection</h4>
                    </div>
                    <p class="text-blue-100 mb-4">Advanced neural networks trained on thousands of livestock images for precise breed identification.</p>
                    <div class="flex space-x-2">
                        <span class="bg-white bg-opacity-20 px-3 py-1 rounded-full text-sm">Deep Learning</span>
                        <span class="bg-white bg-opacity-20 px-3 py-1 rounded-full text-sm">Computer Vision</span>
                    </div>
                </div>
                
                <div class="bg-gradient-to-r from-green-500 to-blue-600 text-white p-8 rounded-2xl shadow-2xl slide-in-right">
                    <div class="flex items-center mb-4">
                        <div class="text-3xl mr-4 floating" style="animation-delay: 0.5s;">🌐</div>
                        <h4 class="text-2xl font-bold">Real-Time Processing</h4>
                    </div>
                    <p class="text-green-100 mb-4">Instant analysis with our optimized algorithms for quick breed classification.</p>
                    <div class="flex space-x-2">
                        <span class="bg-white bg-opacity-20 px-3 py-1 rounded-full text-sm">Fast Processing</span>
                        <span class="bg-white bg-opacity-20 px-3 py-1 rounded-full text-sm">High Quality</span>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Classifier Section -->
    <section id="classifier" class="bg-gray-50 py-20">
        <div class="container mx-auto px-6">
            <div class="max-w-4xl mx-auto">
                <h3 class="text-3xl font-bold text-center mb-12 text-gray-800">AI Livestock Classification</h3>
                
                <div class="bg-white rounded-xl shadow-lg p-8 mb-8">
                    <div id="uploadArea" class="upload-area rounded-lg p-12 text-center cursor-pointer">
                        <div id="uploadContent">
                            <div class="text-6xl mb-4">📷</div>
                            <h4 class="text-xl font-semibold mb-2 text-gray-700">Upload Animal Image</h4>
                            <p class="text-gray-500 mb-4">Drag and drop an image or click to select</p>
                            <input type="file" id="fileInput" accept="image/*" class="hidden">
                            <button onclick="document.getElementById('fileInput').click()" class="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition duration-300">
                                Choose Image
                            </button>
                        </div>
                        <div id="imagePreview" class="hidden">
                            <img id="previewImg" class="max-w-full max-h-64 mx-auto rounded-lg shadow-md">
                            <div class="mt-4">
                                <button onclick="classifyImage()" class="bg-green-600 text-white px-8 py-3 rounded-lg hover:bg-green-700 transition duration-300 font-semibold">
                                    Classify Animal
                                </button>
                                <button onclick="resetUpload()" class="ml-4 bg-gray-500 text-white px-6 py-3 rounded-lg hover:bg-gray-600 transition duration-300">
                                    Upload New Image
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Results -->
                <div id="results" class="hidden">
                    <div class="bg-white rounded-xl shadow-lg p-8">
                        <h4 class="text-2xl font-bold mb-6 text-gray-800">Classification Results</h4>
                        
                        <div class="grid md:grid-cols-2 gap-8">
                            <!-- Primary Classification -->
                            <div class="result-card">
                                <div class="border-l-4 border-blue-500 pl-6">
                                    <h5 class="text-lg font-semibold text-gray-700 mb-2">Primary Classification</h5>
                                    <div id="primaryResult" class="text-3xl font-bold text-blue-600 mb-2"></div>
                                    <div id="confidence" class="text-sm text-gray-600"></div>
                                </div>
                            </div>
                            
                            <!-- Breed Details -->
                            <div class="result-card">
                                <div class="border-l-4 border-green-500 pl-6">
                                    <h5 class="text-lg font-semibold text-gray-700 mb-2">Breed Information</h5>
                                    <div id="breedName" class="text-xl font-bold text-green-600 mb-2"></div>
                                    <div id="breedInfo" class="text-sm text-gray-600"></div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Additional Information -->
                        <div class="mt-8 p-6 bg-gray-50 rounded-lg">
                            <h5 class="text-lg font-semibold text-gray-700 mb-4">Classification Confidence</h5>
                            <div class="w-full bg-gray-200 rounded-full h-4 mb-2">
                                <div id="confidenceBar" class="bg-blue-600 h-4 rounded-full transition-all duration-1000" style="width: 0%"></div>
                            </div>
                            <div class="flex justify-between text-sm text-gray-600">
                                <span>0%</span>
                                <span id="confidenceValue">0%</span>
                                <span>100%</span>
                            </div>
                        </div>
                        
                        <div class="mt-6 text-center">
                            <button onclick="resetUpload()" class="bg-gray-600 text-white px-8 py-3 rounded-lg hover:bg-gray-700 transition duration-300">
                                Try Another Image
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="bg-gray-800 text-white py-12">
        <div class="container mx-auto px-6">
            <div class="text-center">
                <div class="flex items-center justify-center space-x-3 mb-4">
                    <div class="w-8 h-8 bg-gradient-to-r from-blue-500 to-purple-500 rounded-lg flex items-center justify-center">
                        <span class="text-lg">🐄</span>
                    </div>
                    <h3 class="text-xl font-bold">LiveStock AI</h3>
                </div>
                <p class="text-gray-400 mb-6">Advanced AI-powered livestock classification for modern agriculture</p>
                <div class="flex justify-center space-x-6">
                    <a href="#" class="text-gray-400 hover:text-white transition duration-300">Privacy Policy</a>
                    <a href="#" class="text-gray-400 hover:text-white transition duration-300">Terms of Service</a>
                    <a href="#" class="text-gray-400 hover:text-white transition duration-300">Contact Us</a>
                </div>
                <div class="mt-6 pt-6 border-t border-gray-700">
                    <p class="text-gray-500">&copy; 2024 LiveStock AI. All rights reserved.</p>
                </div>
            </div>
        </div>
    </footer>

    <script>
        // Breed databases
        const cattleBreeds = [
            { name: "Holstein", info: "Large dairy breed known for high milk production and distinctive black and white markings" },
            { name: "Angus", info: "Premium beef breed, naturally hornless with excellent marbling and meat quality" },
            { name: "Hereford", info: "Docile beef breed with distinctive red body and white face markings" },
            { name: "Jersey", info: "Small dairy breed producing rich, high-butterfat milk, golden-brown colored" },
            { name: "Brahman", info: "Heat-resistant breed with distinctive hump, ideal for tropical climates" },
            { name: "Charolais", info: "Large French beef breed, cream-colored with excellent growth rates" },
            { name: "Limousin", info: "Lean beef breed from France, known for muscular build and efficiency" },
            { name: "Simmental", info: "Dual-purpose breed, good for both milk and beef production" },
            { name: "Guernsey", info: "Medium-sized dairy breed producing golden-colored milk" },
            { name: "Brown Swiss", info: "Ancient dairy breed known for longevity and high protein milk" },
            { name: "Shorthorn", info: "Versatile breed used for both milk and beef production" },
            { name: "Highland", info: "Hardy Scottish breed with long hair, adapted to harsh climates" }
        ];

        const buffaloBreeds = [
            { name: "Murrah Buffalo", info: "Premier dairy breed from India with excellent milk production" },
            { name: "Nili-Ravi Buffalo", info: "High-yielding dairy breed from Pakistan and India" },
            { name: "Mediterranean Buffalo", info: "European breed known for mozzarella cheese production" },
            { name: "Jafarabadi Buffalo", info: "Large Indian breed with good milk yield and draught power" },
            { name: "Mehsana Buffalo", info: "Indian breed known for high milk production and disease resistance" },
            { name: "Nagpuri Buffalo", info: "Medium-sized Indian breed with good milk quality" },
            { name: "Toda Buffalo", info: "Indigenous breed from Nilgiri hills, adapted to hilly terrain" },
            { name: "Pandharpuri Buffalo", info: "Indian breed known for high butterfat content in milk" },
            { name: "Bhadawari Buffalo", info: "Small Indian breed with rich milk quality" },
            { name: "Chilika Buffalo", info: "Swamp buffalo breed from eastern India" },
            { name: "Kalahandi Buffalo", info: "Indian breed with good adaptation to local conditions" },
            { name: "Surti Buffalo", info: "Medium-sized Indian breed with good milk quality" }
        ];

        // Current mode
        let currentMode = 'upload';
        
        // Counter animation function
        function animateCounter(element, target, duration = 2000) {
            const start = parseInt(element.textContent) || 0;
            const increment = (target - start) / (duration / 16);
            let current = start;
            
            const updateCounter = () => {
                current += increment;
                if (current < target) {
                    element.textContent = Math.floor(current);
                    requestAnimationFrame(updateCounter);
                } else {
                    element.textContent = target;
                }
            };
            
            updateCounter();
        }
        
        // Initialize enhanced page functionality
        document.addEventListener('DOMContentLoaded', () => {
            const counters = document.querySelectorAll('.counter');
            
            const observerOptions = {
                threshold: 0.5
            };
            
            const counterObserver = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const target = parseInt(entry.target.dataset.target);
                        animateCounter(entry.target, target);
                        counterObserver.unobserve(entry.target);
                    }
                });
            }, observerOptions);
            
            counters.forEach(counter => {
                counterObserver.observe(counter);
            });
            
            // Initialize slide-in animations
            const slideElements = document.querySelectorAll('.slide-in-left, .slide-in-right');
            const slideObserver = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        entry.target.style.opacity = '1';
                        entry.target.style.transform = 'translateX(0)';
                    }
                });
            }, observerOptions);
            
            slideElements.forEach(element => {
                element.style.opacity = '0';
                slideObserver.observe(element);
            });

            // Initialize upload functionality
            const uploadArea = document.getElementById('uploadArea');
            const fileInput = document.getElementById('fileInput');
            const uploadContent = document.getElementById('uploadContent');
            const imagePreview = document.getElementById('imagePreview');
            const previewImg = document.getElementById('previewImg');

            // File input change
            fileInput.addEventListener('change', handleFileSelect);

            // Drag and drop handlers
            uploadArea.addEventListener('dragover', handleDragOver);
            uploadArea.addEventListener('dragleave', handleDragLeave);
            uploadArea.addEventListener('drop', handleDrop);
            uploadArea.addEventListener('click', () => {
                if (!imagePreview.classList.contains('hidden')) return;
                fileInput.click();
            });
        });

        function handleFileSelect(e) {
            const file = e.target.files[0];
            if (file && file.type.startsWith('image/')) {
                displayImage(file);
            }
        }

        function handleDragOver(e) {
            e.preventDefault();
            uploadArea.classList.add('dragover');
        }

        function handleDragLeave(e) {
            e.preventDefault();
            uploadArea.classList.remove('dragover');
        }

        function handleDrop(e) {
            e.preventDefault();
            uploadArea.classList.remove('dragover');
            const file = e.dataTransfer.files[0];
            if (file && file.type.startsWith('image/')) {
                displayImage(file);
            }
        }

        function displayImage(file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                previewImg.src = e.target.result;
                uploadContent.classList.add('hidden');
                imagePreview.classList.remove('hidden');
            };
            reader.readAsDataURL(file);
        }

        function resetUpload() {
            uploadContent.classList.remove('hidden');
            imagePreview.classList.add('hidden');
            document.getElementById('results').classList.add('hidden');
            fileInput.value = '';
        }

        function classifyImage() {
            // Enhanced loading animation
            const loadingOverlay = document.createElement('div');
            loadingOverlay.className = 'fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50';
            loadingOverlay.innerHTML = `
                <div class="bg-white p-8 rounded-2xl text-center shadow-2xl transform scale-0 transition-transform duration-300">
                    <div class="relative mb-6">
                        <div class="loading-spinner w-16 h-16 mx-auto mb-4"></div>
                        <div class="absolute inset-0 flex items-center justify-center">
                            <div class="w-8 h-8 bg-blue-600 rounded-full animate-pulse"></div>
                        </div>
                    </div>
                    <p class="text-lg font-semibold mb-2 typewriter">Analyzing image...</p>
                    <p class="text-gray-600 mb-4">Please wait while our AI processes the image</p>
                    
                    <!-- Progress steps -->
                    <div class="flex justify-center space-x-2 mb-4">
                        <div class="step-indicator w-3 h-3 bg-blue-600 rounded-full" style="animation-delay: 0s;"></div>
                        <div class="step-indicator w-3 h-3 bg-gray-300 rounded-full" style="animation-delay: 0.5s;"></div>
                        <div class="step-indicator w-3 h-3 bg-gray-300 rounded-full" style="animation-delay: 1s;"></div>
                        <div class="step-indicator w-3 h-3 bg-gray-300 rounded-full" style="animation-delay: 1.5s;"></div>
                    </div>
                    
                    <div class="text-sm text-gray-500">
                        <span class="processing-step">Preprocessing image...</span>
                    </div>
                </div>
            `;
            document.body.appendChild(loadingOverlay);
            
            // Animate modal appearance
            setTimeout(() => {
                loadingOverlay.querySelector('.bg-white').style.transform = 'scale(1)';
            }, 10);
            
            // Animate processing steps
            const steps = ['Preprocessing image...', 'Extracting features...', 'Running AI analysis...', 'Finalizing results...'];
            const stepIndicators = loadingOverlay.querySelectorAll('.step-indicator');
            const stepText = loadingOverlay.querySelector('.processing-step');
            
            steps.forEach((step, index) => {
                setTimeout(() => {
                    stepText.textContent = step;
                    if (stepIndicators[index]) {
                        stepIndicators[index].classList.remove('bg-gray-300');
                        stepIndicators[index].classList.add('bg-blue-600', 'animate-pulse');
                    }
                }, index * 500);
            });

            setTimeout(() => {
                // Animate modal disappearance
                loadingOverlay.querySelector('.bg-white').style.transform = 'scale(0)';
                setTimeout(() => {
                    document.body.removeChild(loadingOverlay);
                    showResults();
                }, 300);
            }, 2500);
        }

        function showResults() {
            // Simulate random classification
            const isCattle = Math.random() > 0.5;
            const breeds = isCattle ? cattleBreeds : buffaloBreeds;
            const selectedBreed = breeds[Math.floor(Math.random() * breeds.length)];
            const confidence = (85 + Math.random() * 13).toFixed(1);
            
            // Update results
            document.getElementById('primaryResult').textContent = isCattle ? 'Cattle' : 'Buffalo';
            document.getElementById('confidence').textContent = `Confidence: ${confidence}%`;
            document.getElementById('breedName').textContent = selectedBreed.name;
            document.getElementById('breedInfo').textContent = selectedBreed.info;
            
            // Animate confidence bar
            const confidenceBar = document.getElementById('confidenceBar');
            const confidenceValue = document.getElementById('confidenceValue');
            
            setTimeout(() => {
                confidenceBar.style.width = confidence + '%';
                confidenceValue.textContent = confidence + '%';
            }, 100);
            
            // Show results with animation
            const resultsSection = document.getElementById('results');
            resultsSection.classList.remove('hidden');
            
            setTimeout(() => {
                const resultCards = resultsSection.querySelectorAll('.result-card');
                resultCards.forEach((card, index) => {
                    setTimeout(() => {
                        card.classList.add('show');
                    }, index * 200);
                });
            }, 100);
        }

        // Global function exports
        window.classifyImage = classifyImage;
        window.resetUpload = resetUpload;
        window.scrollToClassifier = function() {
            document.getElementById('classifier').scrollIntoView({ behavior: 'smooth' });
        };
    </script>
