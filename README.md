# Drone Deliveries Dashboard

Minimal instructions for running and checking the environment.

## Run
MacOS / Linux:
```bash
python3 -m venv .venv  # optional
source .venv/bin/activate  # macOS / Linux
pip install -r requirements.txt
python run.py
# Open http://127.0.0.1:5001
```
Ctrl+C to stop.

Windows (PowerShell):
```powershell
python -m venv .venv  # optional
.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python run.py
# Open http://127.0.0.1:5001
```
Ctrl+C to stop.


Notes:
- Virtual environment is optional. You can install directly with `pip install -r requirements.txt` and run `python run.py`.
- If PowerShell execution policy blocks activation, use CMD:
	```cmd
	.venv\Scripts\activate.bat
	pip install -r requirements.txt
	python run.py
	```

**Verify installation:**
```bash
python3 -c "import flask,pandas,numpy,matplotlib,seaborn; print('All dependencies OK')"
```

## Data
Place `delivery_logs.csv` in the project root (same folder as `run.py`). Edit it and refresh the browser to see changes.

**Required CSV columns:** `deliveryId`, `droneId`, `servicePointLat`, `servicePointLng`, `deliveryPointLat`, `deliveryPointLng`, `deliveryDate`, `deliveryTime`, `actualCost`.

**Verify CSV is readable:**
```bash
python3 -c "import pandas as pd; print(pd.read_csv('delivery_logs.csv').head())"
```

<!-- Refresh buttons intentionally omitted to keep README run-only per instructor guidance. -->

## Environment Notes
- **Python version:** 3.8+ required. Tested on Python 3.10–3.13.
- Use a virtual environment to avoid global package conflicts.
- If ports clash, change the port in `run.py`.

## Troubleshooting
- Blank map: check column names (`servicePointLat`, `servicePointLng`).
- Plots missing: ensure route matches filename (`/analysis/plot/scatter.png`).
- Time warnings: standardize `deliveryTime` format (`HH:MM[:SS]`).
- Stale data: use refresh buttons or hard reload.

---


