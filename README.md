# Drone Deliveries Dashboard

Minimal instructions for running and checking the environment.

## Run
```bash
python3 -m venv .venv  # optional
source .venv/bin/activate  # macOS / Linux
pip install -r requirements.txt
python run.py
# Open http://127.0.0.1:5001
```
Ctrl+C to stop.

## Data
Place `delivery_logs.csv` in the project root (same folder as `run.py`). Edit it and refresh the browser to see changes.

## Refresh Buttons
Navbar provides:
- Refresh Map – re-fetches data and redraws markers.
- Refresh Graphs – reloads analysis PNGs (cache-bust).

## Environment Notes
- Python 3.8+ recommended.
- Use a virtual environment to avoid global package conflicts.
- If ports clash, change the port in `run.py`.

## Troubleshooting
- Blank map: check column names (`servicePointLat`, `servicePointLng`).
- Plots missing: ensure route matches filename (`/analysis/plot/scatter.png`).
- Time warnings: standardize `deliveryTime` format (`HH:MM[:SS]`).
- Stale data: use refresh buttons or hard reload.

## Debug
Set `debug=True` in `run.py` if needed. Add `print()`s in `analysis/analysis.py` to inspect DataFrame shapes.

---
Only environment, setup, run, and troubleshooting info retained per guidance.
- Blank map: Ensure `delivery_logs.csv` has rows and columns named as expected (`servicePointLat`, `servicePointLng`, etc.).

- Plot images 404: Check the function name matches the route (`/analysis/plot/scatter.png`).

