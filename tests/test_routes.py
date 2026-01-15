"""
Tests for Flask routes and API endpoints
"""
import pytest
from dashboard import create_app


@pytest.fixture
def app():
    """Create and configure a test instance of the app"""
    app = create_app({'TESTING': True})
    return app


@pytest.fixture
def client(app):
    """A test client for the app"""
    return app.test_client()


class TestHTMLRoutes:
    """Test HTML page routes"""
    
    def test_homepage(self, client):
        """Test homepage returns 200"""
        response = client.get('/')
        assert response.status_code == 200
        assert b'Drone Deliveries Dashboard' in response.data or b'dashboard' in response.data.lower()
    
    def test_map_page(self, client):
        """Test map page returns 200"""
        response = client.get('/map')
        assert response.status_code == 200
    
    def test_analysis_page(self, client):
        """Test analysis page returns 200"""
        response = client.get('/analysis')
        assert response.status_code == 200
    
    def test_data_page(self, client):
        """Test data page returns 200"""
        response = client.get('/data')
        assert response.status_code == 200
    
    def test_about_page(self, client):
        """Test about page returns 200"""
        response = client.get('/about')
        assert response.status_code == 200


class TestAPIRoutes:
    """Test JSON API endpoints"""
    
    def test_map_data_api(self, client):
        """Test /api/map_data returns JSON with expected keys"""
        response = client.get('/api/map_data?k=3')
        assert response.status_code == 200
        assert response.content_type == 'application/json'
        data = response.get_json()
        assert 'deliveries' in data
        assert 'service_points' in data
        assert 'centroids' in data
        assert 'Cache-Control' in response.headers
    
    def test_raw_data_api(self, client):
        """Test /api/raw_data returns JSON"""
        response = client.get('/api/raw_data?n=5')
        assert response.status_code == 200
        assert response.content_type == 'application/json'
        data = response.get_json()
        assert isinstance(data, dict)
        assert 'head' in data
        assert isinstance(data['head'], list)
        assert len(data['head']) <= 5
    
    def test_data_summary_api(self, client):
        """Test /api/data_summary returns JSON"""
        response = client.get('/api/data_summary')
        assert response.status_code == 200
        assert response.content_type == 'application/json'


class TestPlotRoutes:
    """Test analysis plot image endpoints"""
    
    @pytest.mark.parametrize('plot_name', [
        'scatter', 'elbow', 'service_point', 
        'drone_stacked', 'day_week', 'time_of_day'
    ])
    def test_plot_endpoints(self, client, plot_name):
        """Test all plot endpoints return PNG images"""
        response = client.get(f'/analysis/plot/{plot_name}.png')
        assert response.status_code == 200
        assert response.content_type == 'image/png'
        assert 'Cache-Control' in response.headers
