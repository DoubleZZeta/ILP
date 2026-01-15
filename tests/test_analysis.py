"""
Tests for analysis module functions
"""
import pytest
import pandas as pd
import numpy as np
from analysis import load_deliver_df, k_means, calc_distance, get_kpis


class TestDataLoading:
    """Test data loading functions"""
    
    def test_load_deliver_df_returns_dataframe(self):
        """Test that load_deliver_df returns a pandas DataFrame"""
        df = load_deliver_df()
        assert isinstance(df, pd.DataFrame)
        assert len(df) > 0
    
    def test_load_deliver_df_has_required_columns(self):
        """Test that loaded DataFrame has expected columns"""
        df = load_deliver_df()
        required_cols = ['deliveryId', 'droneId', 'servicePointLng', 'servicePointLat']
        for col in required_cols:
            assert col in df.columns, f"Missing required column: {col}"


class TestKMeans:
    """Test k-means clustering"""
    
    def test_k_means_returns_expected_structure(self):
        """Test k_means returns DataFrame and clusters"""
        df = load_deliver_df()
        centroids_df, clusters = k_means(df, k=3)
        
        assert isinstance(centroids_df, pd.DataFrame)
        assert isinstance(clusters, list)
        assert len(centroids_df) == 3
        assert len(clusters) == 3  # 3 clusters
    
    def test_k_means_handles_large_k(self):
        """Test k_means caps k when k > dataset size"""
        df = load_deliver_df()
        n_samples = len(df)
        centroids_df, clusters = k_means(df, k=n_samples + 100)
        
        # Should cap to number of samples
        assert len(centroids_df) <= n_samples
    
    def test_k_means_with_k_1(self):
        """Test k_means works with k=1"""
        df = load_deliver_df()
        centroids_df, clusters = k_means(df, k=1)
        
        assert len(centroids_df) == 1
        assert len(clusters) == 1


class TestDistanceCalculation:
    """Test distance calculation utilities"""
    
    def test_calc_distance_returns_float(self):
        """Test calc_distance returns numeric value"""
        # Edinburgh to London approx
        distance = calc_distance(55.9533, -3.1883, 51.5074, -0.1278)
        assert isinstance(distance, (int, float))
        assert distance > 0
    
    def test_calc_distance_same_point(self):
        """Test distance between same point is zero"""
        distance = calc_distance(51.5, -0.1, 51.5, -0.1)
        assert distance == 0


class TestKPIs:
    """Test KPI calculation"""
    
    def test_get_kpis_returns_dict(self):
        """Test get_kpis returns dictionary with expected keys"""
        df = load_deliver_df()
        kpis = get_kpis(df)
        
        assert isinstance(kpis, dict)
        expected_keys = ['total_deliveries', 'unique_drones', 'unique_service_points', 'avg_distance']
        for key in expected_keys:
            assert key in kpis, f"Missing KPI: {key}"
    
    def test_get_kpis_values_are_numeric(self):
        """Test all KPI values are numeric"""
        df = load_deliver_df()
        kpis = get_kpis(df)
        
        for key, value in kpis.items():
            assert isinstance(value, (int, float)), f"{key} should be numeric"
            assert value >= 0, f"{key} should be non-negative"
