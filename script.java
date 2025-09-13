function showResults() {
    // Simulate classification
    const randomCheck = Math.random(); // Random number between 0–1

    if (randomCheck < 0.2) {
        // 20% chance => Invalid image (not cattle/buffalo)
        document.getElementById('resultType').textContent = 'Invalid Data';
        document.getElementById('resultConfidence').textContent = '--';
        document.getElementById('resultBreed').textContent = 'Unknown';
        document.getElementById('breedInfo').textContent = 'The uploaded image does not match cattle or buffalo.';
    } else {
        // Normal classification (Cattle/Buffalo)
        const isCattle = Math.random() > 0.5;
        const breeds = isCattle ? cattleBreeds : buffaloBreeds;
        const selectedBreed = breeds[Math.floor(Math.random() * breeds.length)];
        const confidence = (85 + Math.random() * 12).toFixed(1);

        document.getElementById('resultType').textContent = isCattle ? 'Cattle' : 'Buffalo';
        document.getElementById('resultConfidence').textContent = confidence + '%';
        document.getElementById('resultBreed').textContent = selectedBreed.name;
        document.getElementById('breedInfo').textContent = selectedBreed.info;
    }

    // Show results with animation
    const resultsSection = document.getElementById('results');
    resultsSection.classList.remove('hidden');
    setTimeout(() => {
        resultsSection.querySelector('.result-card').classList.add('show');
    }, 100);
}

function performLiveAnalysis() {
    const processingIndicator = document.getElementById('processingIndicator');
    processingIndicator.classList.remove('hidden');
    const scanningOverlay = document.getElementById('scanningOverlay');
    scanningOverlay.classList.remove('hidden');

    setTimeout(() => {
        const randomCheck = Math.random();

        if (randomCheck < 0.2) {
            // Invalid data
            typewriterEffect(document.getElementById('liveClassification'), 'Invalid Data');
            typewriterEffect(document.getElementById('liveConfidence'), '--');
            typewriterEffect(document.getElementById('liveBreed'), 'Unknown');
        } else {
            // Normal cattle/buffalo classification
            const isCattle = Math.random() > 0.5;
            const breeds = isCattle ? cattleBreeds : buffaloBreeds;
            const selectedBreed = breeds[Math.floor(Math.random() * breeds.length)];
            const confidence = (80 + Math.random() * 18).toFixed(1);

            typewriterEffect(document.getElementById('liveClassification'), isCattle ? 'Cattle' : 'Buffalo');
            setTimeout(() => typewriterEffect(document.getElementById('liveConfidence'), confidence + '%'), 200);
            setTimeout(() => typewriterEffect(document.getElementById('liveBreed'), selectedBreed.name), 400);
        }

        processingIndicator.classList.add('hidden');
        scanningOverlay.classList.add('hidden');
    }, 1500);
}
