# Import common packages 
import os
import math
import pandas as pd
import matplotlib
# Use a non-interactive backend for server-side image generation
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
from pathlib import Path


def calc_distance(x1,y1,x2,y2):
    x_diff = x1 - x2
    y_diff = y1 - y2
    return math.sqrt(x_diff**2 + y_diff**2)


def k_means(df,k=3):
    position_df = df[['deliveryPointLng','deliveryPointLat']].copy(deep = True)
    
    # Handle case where k is larger than dataset size
    # If there are no points, return empty results
    if len(position_df) == 0:
        empty_centroids = pd.DataFrame(columns=['deliveryPointLng', 'deliveryPointLat'])
        return empty_centroids, []

    k = min(k, len(position_df))
    if k < 1:
        k = 1
    
    MAX_ITER = 1000
    centroids_df = position_df.sample(n=k, random_state=42)
    
    for iter in range(MAX_ITER):

        clusters = [[] for i in range(k)]
            
        for i in range(len(position_df)):
            distance_to_centroid = float('inf')
            assigned_to = -1
            for j in range(k):
                # Get cooridnates of the point to be assigned to a cluster
                x1 = position_df.iloc[i]['deliveryPointLng']
                y1 = position_df.iloc[i]['deliveryPointLat']
                # Get cooridnates of the center of a cluster
                x2 = centroids_df.iloc[j]['deliveryPointLng']
                y2 = centroids_df.iloc[j]['deliveryPointLat']
                # Calculate the distance to the center of clusters
                if (calc_distance(x1,y1,x2,y2) < distance_to_centroid):
                    distance_to_centroid = calc_distance(x1,y1,x2,y2)
                    assigned_to = j
            # Assigning to clusters
            clusters[assigned_to].append(i)

        new_centroids = []
        for i in range(k):
            if len(clusters[i]) != 0:
                # Get the stored data frame row ids
                cluster_points = position_df.iloc[clusters[i]]

                # Get the new centroid using the mean
                new_lng = cluster_points['deliveryPointLng'].mean()
                new_lat = cluster_points['deliveryPointLat'].mean()
            else:
                # If cluster is empty, re-sample a random point
                rand_row = position_df.sample(n=1).iloc[0]
                new_lng = rand_row['deliveryPointLng']
                new_lat = rand_row['deliveryPointLat']
            new_centroids.append((new_lng, new_lat))

            # Convert back to DataFrame for next iteration
            new_centroids_df = pd.DataFrame(new_centroids, columns=['deliveryPointLng', 'deliveryPointLat'])
            
        # Stop if centroids didn’t move (rough convergence check)
        if new_centroids_df.equals(centroids_df):
            break

        centroids_df = new_centroids_df

    return centroids_df, clusters


def plot_elbow_graph(df,max_k=8,filename="elbow.png"):
    position_df = df[['deliveryPointLng','deliveryPointLat']].copy(deep = True)

    # Adjust max_k to not exceed dataset size
    max_k = min(max_k, len(position_df))

    se = []
    k_values = list(range(1, max_k + 1))
    for k in k_values:
        centroids_df, clusters = k_means(position_df,k)
        square_error = 0
        for i in range(k):
            for j in clusters[i]:
                x1 = position_df.iloc[j]['deliveryPointLng']
                y1 = position_df.iloc[j]['deliveryPointLat']
                x2 = centroids_df.iloc[i]['deliveryPointLng']
                y2 = centroids_df.iloc[i]['deliveryPointLat']
                square_error += calc_distance(x1,y1,x2,y2)**2
        se.append(square_error)

    fig, ax = plt.subplots(figsize=(8,4))
    ax.plot(k_values, se, marker='o')
    ax.set_xlabel("Number of clusters k")
    ax.set_ylabel("Sum of squared distances (SSE)")
    ax.set_title("Elbow plot for k-means")
    ax.set_xticks(k_values)
    fig.tight_layout()
    return fig
    

def plot_service_point_bar_chart(deliver_df):
    service_points = deliver_df['servicePointName'].unique()
    counts = []
    for i in range(len(service_points)):
        counts.append(len(deliver_df[deliver_df['servicePointName'] == service_points[i]]))

    fig, ax = plt.subplots(figsize=(8,4))
    ax.bar(service_points, counts)
    ax.set_xlabel("Service point")
    ax.set_ylabel("Number of deliveries")
    ax.set_title("Deliveries per service point")
    plt.setp(ax.get_xticklabels(), rotation=45, ha="right")
    fig.tight_layout()
    return fig


def plot_drone_servicepoint_stacked_chart(df):
    deliver_df = df.copy(deep = True)
    counts = (deliver_df.groupby(['droneId', 'servicePointName']).size().unstack(fill_value=0))
    drone_ids = counts.index
    service_points = counts.columns

    fig, ax = plt.subplots(figsize=(10,6))

    # stack the next service point on top
    bottom = np.zeros(len(drone_ids))
    x = np.arange(len(drone_ids))
    for sp in service_points:
        values = counts[sp].values
        ax.bar(x, values, bottom=bottom, label=sp)
        bottom += values

    ax.set_xticks(x)
    ax.set_xticklabels(drone_ids.astype(str))
    ax.set_xlabel("Drone ID")
    ax.set_ylabel("Number of deliveries")
    ax.set_title("Deliveries per drone (stacked by service point)")
    ax.legend(title="Service point")
    fig.tight_layout()
    return fig


def plot_day_week_chart(df):
    deliver_df = df.copy(deep = True)
    deliver_df['deliveryDate'] = pd.to_datetime(deliver_df['deliveryDate'], errors='coerce')
    deliver_df['deliveryDayOfWeek'] = deliver_df['deliveryDate'].dt.day_name()

    weekday_order = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
    counts = deliver_df.groupby(['deliveryDayOfWeek']).size().reindex(weekday_order, fill_value=0)

    fig, ax = plt.subplots(figsize=(8,4))
    ax.bar(weekday_order, counts.values)
    ax.set_xlabel("Day of week")
    ax.set_ylabel("Number of deliveries")
    ax.set_title("Deliveries per weekday")
    plt.setp(ax.get_xticklabels(), rotation=45, ha="right")
    fig.tight_layout()
    return fig


def plot_time_chart(df):
    deliver_df = df.copy(deep = True)
    # parse robustly, accept HH:MM or HH:MM:SS
    deliver_df['deliveryHour'] = pd.to_datetime(deliver_df.get('deliveryTime', None), errors='coerce').dt.hour
    deliver_df['deliveryHour'] = deliver_df['deliveryHour'].fillna(-1).astype(int)

    hour_order = list(range(24))
    counts = deliver_df[deliver_df['deliveryHour'] >= 0].groupby('deliveryHour').size().reindex(hour_order, fill_value=0)

    fig, ax = plt.subplots(figsize=(10,4))
    ax.bar(hour_order, counts.values)
    ax.set_xlabel("Hour of day")
    ax.set_ylabel("Number of deliveries")
    ax.set_title("Deliveries per day")
    ax.set_xticks(hour_order)
    fig.tight_layout()
    return fig


# Read the delivery logs (main function)
# Resolve path to repository root (one directory up from `analysis/`)
def load_deliver_df():
    base_dir = Path(__file__).resolve().parents[1]
    deliver_log_filepath = base_dir / 'delivery_logs.csv'
    return pd.read_csv(str(deliver_log_filepath))

def get_kpis(df=None):
    """Calculate KPI metrics from delivery data."""
    if df is None:
        df = load_deliver_df()
    
    total_deliveries = len(df)
    unique_drones = df['droneId'].nunique() if 'droneId' in df.columns else 0
    unique_service_points = df['servicePointName'].nunique() if 'servicePointName' in df.columns else 0

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


def plot_scatter(df):
    """Generate a scatter plot of delivery locations."""
    fig, ax = plt.subplots(figsize=(8, 5))
    if 'deliveryPointLng' in df.columns and 'deliveryPointLat' in df.columns:
        ax.scatter(df['deliveryPointLng'], df['deliveryPointLat'], s=10, c='blue', alpha=0.6)
        ax.set_xlabel('Longitude')
        ax.set_ylabel('Latitude')
        ax.set_title('Delivery Locations')
    else:
        ax.text(0.5, 0.5, 'No coordinate columns', ha='center')
    fig.tight_layout()
    return fig


def get_service_points(df):
    """Extract service points with canonical coordinates.
    
    Prefers explicit servicePointLng/Lat if present; otherwise falls back
    to mean of delivery coordinates.
    
    Returns:
        list of dict: [{'name': str, 'lng': float, 'lat': float}, ...]
    """
    service_points = []
    if 'servicePointName' not in df.columns:
        return service_points
    
    grp = df.groupby('servicePointName')
    for name, g in grp:
        try:
            if ('servicePointLng' in df.columns and 'servicePointLat' in df.columns 
                and g['servicePointLng'].notna().any() and g['servicePointLat'].notna().any()):
                # Use first non-null canonical coordinates
                lng = float(g['servicePointLng'].dropna().iloc[0])
                lat = float(g['servicePointLat'].dropna().iloc[0])
            else:
                # Fallback to mean of delivery points
                lng = float(g['deliveryPointLng'].mean())
                lat = float(g['deliveryPointLat'].mean())
            service_points.append({'name': name, 'lng': lng, 'lat': lat})
        except Exception:
            continue
    
    return service_points


def data_summary(df=None):
    """Return a small summary for the given dataframe or the current CSV if None."""
    if df is None:
        df = load_deliver_df()
    summary = {
        'rows': len(df),
        'columns': df.shape[1],
        'dtypes': {c: str(t) for c, t in df.dtypes.items()},
        'missing': {c: int(df[c].isna().sum()) for c in df.columns},
        'head': df.head(5).to_dict(orient='records')
    }
    return summary

