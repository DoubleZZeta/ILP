# Import common packages 
import os
import math
import pandas as pd
import matplotlib
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np


def calc_distance(x1,y1,x2,y2):
    x_diff = x1 - x2
    y_diff = y1 - y2
    return math.sqrt(x_diff**2 + y_diff**2)


def k_means(df,k=3):
    position_df = df[['deliveryPointLng','deliveryPointLat']].copy(deep = True)
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
        
    plt.figure()
    plt.plot(k_values, se, marker='o')
    plt.xlabel("Number of clusters k")
    plt.ylabel("Sum of squared distances (SSE)")
    plt.title("Elbow plot for k-means")
    plt.xticks(k_values)
    plt.tight_layout()
    plt.savefig(filename, dpi=150)
    plt.close()
    

def plot_service_point_bar_chart(deliver_df,filename = "service_point.png"):
    service_points = deliver_df['servicePointName'].unique()
    counts = []
    for i in range(len(service_points)):
        counts.append(len(deliver_df[deliver_df['servicePointName'] == service_points[i]]))
        
    plt.figure()
    plt.bar(service_points, counts)
    plt.xlabel("Service point")
    plt.ylabel("Number of deliveries")
    plt.title("Deliveries per service point")
    plt.xticks(rotation=45, ha="right")
    plt.tight_layout()
    plt.savefig(filename,dpi=150)
    plt.close()    


def plot_drone_servicepoint_stacked_chart(df,filename="drone_servicepoints.png"):
    deliver_df = df.copy(deep = True)
    counts = (deliver_df.groupby(['droneId', 'servicePointName']).size().unstack(fill_value=0))
    drone_ids = counts.index               
    service_points = counts.columns       
    x = np.arange(len(drone_ids))         

    plt.figure()

    # stack the next service point on top
    bottom = np.zeros(len(drone_ids))
    for sp in service_points:
        values = counts[sp].values
        plt.bar(x, values, bottom=bottom, label=sp)
        bottom += values                   

    plt.xticks(x, drone_ids.astype(str))
    plt.xlabel("Drone ID")
    plt.ylabel("Number of deliveries")
    plt.title("Deliveries per drone (stacked by service point)")
    plt.legend(title="Service point")
    plt.tight_layout()
    plt.savefig(filename, dpi=150)
    plt.close()


def plot_day_week_chart(df,filename="day_of_week.png"):
    deliver_df = df.copy(deep = True)
    deliver_df['deliveryDate'] = pd.to_datetime(deliver_df['deliveryDate'])
    deliver_df['deliveryDayOfWeek'] = deliver_df['deliveryDate'].dt.day_name()
    
    weekday_order = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
        
    counts = deliver_df.groupby(['deliveryDayOfWeek']).size().reindex(weekday_order, fill_value=0)
    
    plt.figure()
    plt.bar(weekday_order, counts.values)
    plt.xlabel("Day of week")
    plt.ylabel("Number of deliveries")
    plt.title("Deliveries per weekday")
    plt.xticks(rotation=45, ha="right")
    plt.tight_layout()
    plt.savefig(filename, dpi=150)
    plt.close()


def plot_time_chart(df,filename="time.png"):
    deliver_df = df.copy(deep = True)
    deliver_df["deliveryTime"] = pd.to_datetime(deliver_df["deliveryTime"]).dt.time
    deliver_df["deliveryHour"] = pd.to_datetime(deliver_df["deliveryTime"], format="%H:%M:%S").dt.hour
    
    hour_order = [i for i in range(24)]
    
    counts = deliver_df.groupby(['deliveryHour']).size().reindex(hour_order, fill_value=0)
    
    plt.figure()
    plt.bar(hour_order, counts.values)
    plt.xlabel("Hour of day")
    plt.ylabel("Number of deliveries")
    plt.title("Deliveries per day")
    plt.xticks(hour_order)
    plt.yticks(range(0,max(counts.values)))
    plt.tight_layout()
    plt.savefig(filename, dpi=150)
    plt.close()


# Read the delivery logs (main function)
deliver_log_filepath = os.path.join(os.getcwd(), 'delivery_logs.csv') # need to change the path
deliver_df = pd.read_csv(deliver_log_filepath)

    