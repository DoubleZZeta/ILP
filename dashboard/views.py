from flask import Blueprint, render_template, request, jsonify
import io
from flask import send_file
from analysis import (
    load_deliver_df,
    k_means,
    plot_elbow_graph,
    plot_service_point_bar_chart,
    plot_drone_servicepoint_stacked_chart,
    plot_day_week_chart,
    plot_time_chart,
    plot_scatter,
    get_kpis,
    get_service_points,
    data_summary,
)

bp = Blueprint('main', __name__, template_folder='templates', static_folder='static')


def no_cache_json(data):
    """Return JSON response with no-cache headers."""
    resp = jsonify(data)
    resp.headers['Cache-Control'] = 'no-store, no-cache, must-revalidate, max-age=0'
    return resp


def no_cache_file(file_obj, mimetype):
    """Return file response with no-cache headers."""
    resp = send_file(file_obj, mimetype=mimetype)
    resp.headers['Cache-Control'] = 'no-store, no-cache, must-revalidate, max-age=0'
    return resp


@bp.route('/')
def index():
    kpis = get_kpis()
    return render_template('index.html', kpis=kpis)


@bp.route('/map')
def map_view():
    return render_template('map.html')


@bp.route('/api/map_data')
def api_map_data():
    try:
        k = int(request.args.get('k', 3))
    except Exception:
        k = 3

    df = load_deliver_df()
    k = max(1, min(k, len(df)))

    deliveries = []
    for _, row in df.iterrows():
        deliveries.append({
            'lng': float(row['deliveryPointLng']),
            'lat': float(row['deliveryPointLat']),
            'servicePointName': row.get('servicePointName', ''),
            'droneId': str(row.get('droneId', '')),
        })

    service_points = get_service_points(df)

    centroids_df, clusters = k_means(df, k=k)
    centroids = []
    for _, r in centroids_df.iterrows():
        centroids.append({'lng': float(r['deliveryPointLng']), 'lat': float(r['deliveryPointLat'])})

    return no_cache_json({
        'deliveries': deliveries,
        'service_points': service_points,
        'centroids': centroids,
    })


@bp.route('/analysis')
def analysis_view():
    return render_template('analysis.html')


@bp.route('/analysis/plot/<plot_name>.png')
def analysis_plot(plot_name):
    df = load_deliver_df()
    # Map names directly to plotting callables already imported above
    mapping = {
         'elbow': plot_elbow_graph,
         'service_point': plot_service_point_bar_chart,
         'drone_stacked': plot_drone_servicepoint_stacked_chart,
         'day_week': plot_day_week_chart,
         'time_of_day': plot_time_chart,
         'scatter': plot_scatter,
    }
    try:
        if plot_name in mapping:
            func = mapping[plot_name]
            if plot_name == 'elbow':
                fig = func(df, max_k=8)
            else:
                fig = func(df)
        else:
            return jsonify({'error': 'unknown plot name'}), 404

        buf = io.BytesIO()
        fig.savefig(buf, format='png', dpi=150)
        buf.seek(0)
        from matplotlib import pyplot as plt
        plt.close(fig)
        return no_cache_file(buf, mimetype='image/png')
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@bp.route('/api/raw_data')
def api_raw_data():
    try:
        n = int(request.args.get('n', 10))
    except Exception:
        n = 10
    n = max(1, min(n, 1000))
    df = load_deliver_df()
    return no_cache_json({'head': df.head(n).to_dict(orient='records')})


@bp.route('/api/data_summary')
def api_data_summary():
    return jsonify(data_summary())


@bp.route('/data')
def data():
    df = load_deliver_df()
    return render_template('data.html', data=df.to_html(classes='table table-striped', index=False))


@bp.route('/about')
def about():
    return render_template('about.html')
