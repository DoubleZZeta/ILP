from flask import Flask, render_template, request, jsonify
from pathlib import Path
import json
import sys
import pandas as pd
from datetime import datetime
import io
from flask import send_file

# Add parent directory to path to allow imports from analysis module
repo_root = Path(__file__).parent.parent
sys.path.insert(0, str(repo_root))

# Import analysis functions
from analysis.analysis import (
    load_deliver_df,
    k_means,
    calc_distance,
    plot_elbow_graph,
    plot_service_point_bar_chart,
    plot_drone_servicepoint_stacked_chart,
    plot_day_week_chart,
    plot_time_chart,
    data_summary,
)

app = Flask(__name__, template_folder=str(repo_root / 'dashboard' / 'templates'), static_folder=str(repo_root / 'dashboard' / 'static'))

# Global data and helper functions
def get_kpis():
    """Calculate KPI metrics."""
    df = load_deliver_df()
    total_deliveries = len(df)
    unique_drones = df['droneId'].nunique() if 'droneId' in df.columns else 0
    unique_service_points = df['servicePointName'].nunique() if 'servicePointName' in df.columns else 0
    
    # Calculate avg distance (rough approximation using coordinates)
    if len(df) > 1 and 'deliveryPointLng' in df.columns and 'deliveryPointLat' in df.columns:
        distances = []
        for i in range(len(df) - 1):
            x1 = df.iloc[i]['deliveryPointLng']
            y1 = df.iloc[i]['deliveryPointLat']
            x2 = df.iloc[i + 1]['deliveryPointLng']
            y2 = df.iloc[i + 1]['deliveryPointLat']
            distances.append(calc_distance(x1, y1, x2, y2))
        avg_distance = sum(distances) / len(distances) if distances else 0
    else:
        avg_distance = 0
    
    return {
        'total_deliveries': total_deliveries,
        'unique_drones': unique_drones,
        'unique_service_points': unique_service_points,
        'avg_distance': round(avg_distance, 2),
    }


def create_elbow_plot(max_k=8):
    # Deprecated: use server-side matplotlib elbow image at `/analysis/plot/elbow.png`.
    return jsonify({'error': 'deprecated'}), 410


@app.route('/map')
def map_view():
    """Render a Leaflet map page that shows deliveries, active service points and suggested new service points."""
    return render_template('map.html')


@app.route('/api/map_data')
def api_map_data():
    """Return JSON containing deliveries, active service points (approximate coordinates), centroids (k-means), and optional no-fly polygons.

    Query params:
      - k: number of clusters to compute (optional)
    """
    try:
        k = int(request.args.get('k', 3))
    except Exception:
        k = 3

    # Load fresh dataset for each request so the map reflects CSV changes instantly.
    df = load_deliver_df()

    # Cap k to dataset size
    k = max(1, min(k, len(df)))

    # Deliveries
    deliveries = []
    for _, row in df.iterrows():
        deliveries.append({
            'lng': float(row['deliveryPointLng']),
            'lat': float(row['deliveryPointLat']),
            'servicePointName': row.get('servicePointName', ''),
            'droneId': str(row.get('droneId', '')),
        })

    # Active/used service points (approximate location as mean of deliveries for that service point)
    service_points = []
    if 'servicePointName' in df.columns:
        grp = df.groupby('servicePointName')
        for name, g in grp:
            try:
                lng = float(g['deliveryPointLng'].mean())
                lat = float(g['deliveryPointLat'].mean())
                service_points.append({'name': name, 'lng': lng, 'lat': lat})
            except Exception:
                continue

    # Compute centroids (suggested new service points)
    centroids_df, clusters = k_means(df, k=k)
    centroids = []
    for _, r in centroids_df.iterrows():
        centroids.append({'lng': float(r['deliveryPointLng']), 'lat': float(r['deliveryPointLat'])})

    # Optional: load no-fly polygons if present as geojson in repo root (no_fly_zones.geojson)
    resp = jsonify({
        'deliveries': deliveries,
        'service_points': service_points,
        'centroids': centroids,
    })
    # Ensure clients always fetch fresh data when CSV changes
    resp.headers['Cache-Control'] = 'no-store, no-cache, must-revalidate, max-age=0'
    return resp


def create_day_of_week_plot():
    """Generate Plotly bar chart for day of week."""
    # removed: replaced by server-side matplotlib figures in analysis module
    return jsonify({'error': 'deprecated'}), 410


def create_time_of_day_plot():
    """Generate Plotly bar chart for time of day."""
    # removed: replaced by server-side matplotlib figures in analysis module
    return jsonify({'error': 'deprecated'}), 410


def create_scatter_plot(lng_col='deliveryPointLng', lat_col='deliveryPointLat'):
    """Generate Plotly scatter plot for delivery locations."""
    # removed: scatter served by matplotlib image endpoint
    return jsonify({'error': 'deprecated'}), 410


# Routes
@app.route('/')
def index():
    """Homepage with KPIs."""
    kpis = get_kpis()
    return render_template('index.html', kpis=kpis)


@app.route('/analysis')
def analysis():
    """Analysis page with all plots."""
    # Simpler approach: serve matplotlib-generated PNGs produced by analysis.py
    # The template will request the images from the `/analysis/plot/<name>.png` endpoints.
    return render_template('analysis.html')


@app.route('/analysis/plot/<plot_name>.png')
def analysis_plot(plot_name):
    """Generate requested matplotlib plot on the fly and return PNG.

    Supported names: elbow, service_point, drone_stacked, day_week, time_of_day, scatter
    """
    df = load_deliver_df()
    import tempfile
    from flask import send_file

    mapping = {
        'elbow': 'plot_elbow_graph',
        'service_point': 'plot_service_point_bar_chart',
        'drone_stacked': 'plot_drone_servicepoint_stacked_chart',
        'day_week': 'plot_day_week_chart',
        'time_of_day': 'plot_time_chart',
    }

    # scatter will be produced via a quick matplotlib scatter if requested
    try:
        from matplotlib import pyplot as plt

        if plot_name == 'scatter':
            fig, ax = plt.subplots(figsize=(8,5))
            if 'deliveryPointLng' in df.columns and 'deliveryPointLat' in df.columns:
                ax.scatter(df['deliveryPointLng'], df['deliveryPointLat'], s=10, c='blue', alpha=0.6)
                ax.set_xlabel('Longitude')
                ax.set_ylabel('Latitude')
                ax.set_title('Delivery Locations')
            else:
                ax.text(0.5, 0.5, 'No coordinate columns', ha='center')
            fig.tight_layout()
        elif plot_name in mapping:
            func_name = mapping[plot_name]
            module = __import__('analysis.analysis', fromlist=[func_name])
            func = getattr(module, func_name)
            if plot_name == 'elbow':
                fig = func(df, max_k=8)
            else:
                fig = func(df)
        else:
            return jsonify({'error': 'unknown plot name'}), 404

        buf = io.BytesIO()
        fig.savefig(buf, format='png', dpi=150)
        buf.seek(0)
        plt.close(fig)
        resp = send_file(buf, mimetype='image/png')
        resp.headers['Cache-Control'] = 'no-store, no-cache, must-revalidate, max-age=0'
        return resp
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/api/raw_data')
def api_raw_data():
    """Return a small sample of raw data for debugging (parameter `n`)."""
    try:
        n = int(request.args.get('n', 10))
    except Exception:
        n = 10
    n = max(1, min(n, 1000))
    df = load_deliver_df()
    resp = jsonify({'head': df.head(n).to_dict(orient='records')})
    resp.headers['Cache-Control'] = 'no-store, no-cache, must-revalidate, max-age=0'
    return resp


@app.route('/api/data_summary')
def api_data_summary():
    """Return a summary of the dataset (dtypes, missing values, sample rows)."""
    return jsonify(data_summary())



@app.route('/data')
def data():
    """Raw data table page."""
    df = load_deliver_df()
    return render_template('data.html', data=df.to_html(classes='table table-striped', index=False))


@app.route('/about')
def about():
    """About page."""
    return render_template('about.html')


if __name__ == '__main__':
    # Bind explicitly to the loopback interface and disable the reloader
    # to avoid issues with background execution and port collisions.
    app.run(host='127.0.0.1', port=5001, debug=False, use_reloader=False)
