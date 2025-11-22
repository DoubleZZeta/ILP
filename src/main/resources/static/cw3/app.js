// --- 1. Initialise the map ---
const map = L.map('map').setView([55.944, -3.189], 16);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);

// layer where we’ll draw routes
let routesLayer = L.geoJSON(null).addTo(map);

// --- 2. DOM elements ---
const queriesInput = document.getElementById('queriesInput');
const planBtn = document.getElementById('planRoute');
const loadSampleBtn = document.getElementById('loadSample');
const statusEl = document.getElementById('status');

// --- 3. Optional: a small sample MedDispatch list ---
const sampleQueries = [
    {
        "id": 1,
        "date": "2025-11-17",
        "time": "14:30",
        "requirements": {
            "capacity": 1,
            "cooling": false,
            "heating": false,
            "maxCost": 20
        },
        "delivery": {
            "lng": -3.189,
            "lat": 55.94266
        }
    }
];

loadSampleBtn.addEventListener('click', () => {
    queriesInput.value = JSON.stringify(sampleQueries, null, 2);
});

// --- 4. Call your backend and draw GeoJSON ---
planBtn.addEventListener('click', async () => {
    statusEl.textContent = '';

    let queries;
    try {
        queries = JSON.parse(queriesInput.value);
        if (!Array.isArray(queries)) {
            throw new Error('JSON must be an array of MedDispatch records');
        }
    } catch (err) {
        statusEl.textContent = 'Invalid JSON: ' + err.message;
        return;
    }

    try {
        statusEl.textContent = 'Calling backend...';

        const response = await fetch('/api/v1/calcDeliveryPathAsGeoJson', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(queries)
        });

        if (!response.ok) {
            const text = await response.text();
            throw new Error(`HTTP ${response.status}: ${text}`);
        }

        const geojson = await response.json();

        statusEl.textContent = 'Got route. Rendering...';

        // Clear old routes
        routesLayer.clearLayers();

        // Add new GeoJSON
        routesLayer.addData(geojson);

        // Fit map to routes if not empty
        try {
            map.fitBounds(routesLayer.getBounds(), { padding: [20, 20] });
        } catch (_) {
            // ignore if no bounds
        }

        statusEl.textContent = 'Done.';
    } catch (err) {
        console.error(err);
        statusEl.textContent = 'Error: ' + err.message;
    }
});
