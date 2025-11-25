# Drone Deliveries Dashboard

Lightweight Flask dashboard for exploring drone delivery logs (server-side plotting + Leaflet map).

**Quick start**

- Requirements: Python 3.8+ and packages in `requirements.txt`.
- Install:

```bash
cd /path/to/ILP_CW1
python3 -m venv .venv  # optional
source .venv/bin/activate  # macOS / Linux
pip install -r requirements.txt
```

- Run the app:

```bash
cd /Users/zhangao/Desktop/s2487866
python3 dashboard/flask_app.py
# Open http://127.0.0.1:5001
```

**Important files**

- `delivery_logs.csv` — data source at repo root (loaded on every request).
- `analysis/analysis.py` — data loading (`load_deliver_df()`), clustering (`k_means`) and server-side plotting functions (return Matplotlib `Figure`).
- `dashboard/flask_app.py` — Flask app: routes, JSON APIs, and the on-demand PNG endpoints for plots.
- `dashboard/templates/` — Jinja2 templates for pages: `index.html`, `map.html`, `analysis.html`, `data.html`, `about.html`.
- `dashboard/static/style.css` — site styling and the quick-link button classes.

**Routes & APIs**

- `GET /` — Homepage with KPIs (server-rendered).
- `GET /map` — Leaflet map page (client JS calls `/api/map_data`).
- `GET /analysis` — Analysis page which shows server-generated PNGs.
- `GET /data` — Raw data table page.
- `GET /about` — About page.
- `GET /api/map_data?k=3` — Returns JSON: `deliveries`, `service_points`, `centroids` (k-means results). `Cache-Control` prevents browser caching.
- `GET /api/raw_data?n=10` — Returns sample rows.
- `GET /api/data_summary` — Returns dataset summary (dtypes, missing, sample rows).
- `GET /analysis/plot/<name>.png` — Returns server-generated PNG for `elbow`, `service_point`, `drone_stacked`, `day_week`, `time_of_day`, or `scatter`.

**How it works (short)**

- Server-side: Flask endpoints call `analysis` functions. CSV is reloaded per request so changing `delivery_logs.csv` updates the site immediately.
- Map: `map.html` uses Leaflet; client JS fetches `/api/map_data` and renders markers and centroids.
- Analysis: templates include `<img src="/analysis/plot/<name>.png">`; Flask produces Matplotlib `Figure`s, writes PNG into memory, and serves it.

**Troubleshooting & developer notes**

- If you see `UserWarning: Could not infer format` for `deliveryTime`, the plotting code falls back to dateutil. To remove the warning, ensure `deliveryTime` uses consistent `HH:MM` or `HH:MM:SS` or update `plot_time_chart` parsing logic.
- k-means handles `k > n` by capping `k` to `n`, but if the dataset is empty some functions may fail — add a guard in `k_means` to return empty results for `n == 0`.

**Next improvements (recommended)**

- Add unit tests for `k_means` and endpoints (`/api/map_data`, `/analysis/plot/*`).
- Add a short GitHub Actions CI to run tests and linting.
- Add a README section with example screenshots or a quick demo GIF.

If you want, I can add tests and CI config, or update the parsing / k-means guards now.# Getting Started #
I hate myself 


## Plotting functions (server-side)

This project uses server-side Matplotlib to generate PNG plots on demand. The analysis helpers in `analysis/analysis.py` return `matplotlib.figure.Figure` objects. That makes it simple to embed images in the Flask app without writing files.

Quick notes for working with the plotting helpers:

- Each plotting function returns a `Figure` object (for example `fig = plot_service_point_bar_chart(df)`).
- To return an image from a Flask route, write the figure to an in-memory buffer and send it:

```python
import io
from flask import send_file

buf = io.BytesIO()
fig.savefig(buf, format='png', dpi=150)
buf.seek(0)
return send_file(buf, mimetype='image/png')
```

- If you prefer the helper to save to disk, call `fig.savefig('out.png')` instead.

These helpers call `load_deliver_df()` when you pass `None` or when the caller requests fresh data, so the server reflects changes to `delivery_logs.csv` immediately.

