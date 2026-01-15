#!/usr/bin/env python3
"""
Comprehensive Test Suite for ILP CW1 REST API endpoints.
Tests all endpoints with valid data, invalid data, edge cases, and error conditions.
Run this script to verify your implementation meets all assignment requirements.
"""

import requests
import json
import sys
import math

BASE_URL = "http://localhost:8080"

# Test counters
total_tests = 0
passed_tests = 0
failed_tests = 0

def print_test_header(test_name):
    """Print a formatted test header."""
    print("\n" + "=" * 70)
    print(f"TEST: {test_name}")
    print("=" * 70)

def assert_test(condition, test_description):
    """Assert a test condition and track results."""
    global total_tests, passed_tests, failed_tests
    total_tests += 1
    if condition:
        passed_tests += 1
        print(f"✅ PASS: {test_description}")
        return True
    else:
        failed_tests += 1
        print(f"❌ FAIL: {test_description}")
        return False

def test_health():
    """Test the health endpoint - must return {"status": "UP"}."""
    print_test_header("Health Check Endpoint")
    
    try:
        response = requests.get(f"{BASE_URL}/actuator/health")
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.json()}")
        
        # Verify status code is 200
        assert_test(response.status_code == 200, "Status code is 200")
        
        # Verify response contains "status": "UP"
        data = response.json()
        assert_test("status" in data, "Response contains 'status' field")
        assert_test(data.get("status") == "UP", "Status is 'UP'")
        
        return True
    except Exception as e:
        print(f"❌ Error: {e}")
        return False

def test_uid():
    """Test the UID endpoint - must return student ID as plain string."""
    print_test_header("Student UID Endpoint")
    
    try:
        response = requests.get(f"{BASE_URL}/api/v1/uid")
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text}")
        
        # Verify status code is 200
        assert_test(response.status_code == 200, "Status code is 200")
        
        # Verify response is a string starting with 's' (student ID format)
        uid = response.text.strip()
        assert_test(len(uid) > 0, "UID is not empty")
        assert_test(uid[0] == 's' or uid[0].isdigit(), "UID format is valid (starts with 's' or digit)")
        
        return True
    except Exception as e:
        print(f"❌ Error: {e}")
        return False

def test_distance_to():
    """Test the distanceTo endpoint - Euclidean distance calculation."""
    print_test_header("Distance Calculation Endpoint")
    
    try:
        # Test 1: Valid data - same longitude, different latitude
        print("\n📝 Test 1: Valid data - vertical distance")
        data = {
            "position1": {"lng": -3.192473, "lat": 55.946233},
            "position2": {"lng": -3.192473, "lat": 55.942617}
        }
        response = requests.post(f"{BASE_URL}/api/v1/distanceTo", json=data)
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text}")
        
        assert_test(response.status_code == 200, "Valid data returns 200")
        distance = float(response.text)
        expected_distance = abs(55.946233 - 55.942617)
        assert_test(abs(distance - expected_distance) < 0.0001, 
                   f"Distance is correct (expected ~{expected_distance:.6f}, got {distance:.6f})")
        
        # Test 2: Valid data - same latitude, different longitude
        print("\n📝 Test 2: Valid data - horizontal distance")
        data = {
            "position1": {"lng": -3.192473, "lat": 55.946233},
            "position2": {"lng": -3.184319, "lat": 55.946233}
        }
        response = requests.post(f"{BASE_URL}/api/v1/distanceTo", json=data)
        assert_test(response.status_code == 200, "Horizontal distance returns 200")
        distance = float(response.text)
        expected_distance = abs(-3.192473 - (-3.184319))
        assert_test(abs(distance - expected_distance) < 0.0001,
                   f"Horizontal distance is correct (expected ~{expected_distance:.6f}, got {distance:.6f})")
        
        # Test 3: Valid data - diagonal distance (Pythagorean)
        print("\n📝 Test 3: Valid data - diagonal distance")
        data = {
            "position1": {"lng": 0.0, "lat": 0.0},
            "position2": {"lng": 3.0, "lat": 4.0}
        }
        response = requests.post(f"{BASE_URL}/api/v1/distanceTo", json=data)
        assert_test(response.status_code == 200, "Diagonal distance returns 200")
        distance = float(response.text)
        expected_distance = 5.0  # 3-4-5 triangle
        assert_test(abs(distance - expected_distance) < 0.0001,
                   f"Diagonal distance is correct (expected 5.0, got {distance:.6f})")
        
        # Test 4: Same position (zero distance)
        print("\n📝 Test 4: Same position - zero distance")
        data = {
            "position1": {"lng": -3.192473, "lat": 55.946233},
            "position2": {"lng": -3.192473, "lat": 55.946233}
        }
        response = requests.post(f"{BASE_URL}/api/v1/distanceTo", json=data)
        assert_test(response.status_code == 200, "Same position returns 200")
        distance = float(response.text)
        assert_test(distance == 0.0, f"Same position gives zero distance (got {distance})")
        
        # Test 5: Invalid data - missing position1
        print("\n📝 Test 5: Invalid data - missing position1")
        invalid_data = {
            "position2": {"lng": -3.192473, "lat": 55.942617}
        }
        response = requests.post(f"{BASE_URL}/api/v1/distanceTo", json=invalid_data)
        assert_test(response.status_code == 400, "Missing position1 returns 400")
        
        # Test 6: Invalid data - missing position2
        print("\n📝 Test 6: Invalid data - missing position2")
        invalid_data = {
            "position1": {"lng": -3.192473, "lat": 55.946233}
        }
        response = requests.post(f"{BASE_URL}/api/v1/distanceTo", json=invalid_data)
        assert_test(response.status_code == 400, "Missing position2 returns 400")
        
        # Test 7: Invalid data - string instead of number
        print("\n📝 Test 7: Invalid data - string for lng")
        invalid_data = {
            "position1": {"lng": "invalid", "lat": 55.946233},
            "position2": {"lng": -3.192473, "lat": 55.942617}
        }
        response = requests.post(f"{BASE_URL}/api/v1/distanceTo", json=invalid_data)
        assert_test(response.status_code == 400, "Invalid string data returns 400")
        
        # Test 8: Invalid data - null values
        print("\n📝 Test 8: Invalid data - null values")
        invalid_data = {
            "position1": {"lng": None, "lat": 55.946233},
            "position2": {"lng": -3.192473, "lat": 55.942617}
        }
        response = requests.post(f"{BASE_URL}/api/v1/distanceTo", json=invalid_data)
        assert_test(response.status_code == 400, "Null values return 400")
        
        # Test 9: Invalid data - missing lng field
        print("\n📝 Test 9: Invalid data - missing lng field")
        invalid_data = {
            "position1": {"lat": 55.946233},
            "position2": {"lng": -3.192473, "lat": 55.942617}
        }
        response = requests.post(f"{BASE_URL}/api/v1/distanceTo", json=invalid_data)
        assert_test(response.status_code == 400, "Missing lng field returns 400")
        
        # Test 10: Extra fields should be ignored (per spec)
        print("\n📝 Test 10: Extra fields should be ignored")
        data = {
            "position1": {"lng": -3.192473, "lat": 55.946233, "extra": "ignored"},
            "position2": {"lng": -3.192473, "lat": 55.942617, "altitude": 100}
        }
        response = requests.post(f"{BASE_URL}/api/v1/distanceTo", json=data)
        assert_test(response.status_code == 200, "Extra fields are ignored (returns 200)")
        
        return True
    except Exception as e:
        print(f"❌ Error: {e}")
        return False

def test_is_close_to():
    """Test the isCloseTo endpoint - check if distance < 0.00015."""
    print_test_header("Is Close To Endpoint")
    
    try:
        # Test 1: Very close positions (should be true)
        print("\n📝 Test 1: Very close positions (< 0.00015)")
        data = {
            "position1": {"lng": -3.192473, "lat": 55.946233},
            "position2": {"lng": -3.192474, "lat": 55.946234}
        }
        response = requests.post(f"{BASE_URL}/api/v1/isCloseTo", json=data)
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text}")
        assert_test(response.status_code == 200, "Very close positions return 200")
        result = response.json()
        assert_test(result == True, "Very close positions return true")
        
        # Test 2: Same position (distance = 0, should be true)
        print("\n📝 Test 2: Same position (distance = 0)")
        data = {
            "position1": {"lng": -3.192473, "lat": 55.946233},
            "position2": {"lng": -3.192473, "lat": 55.946233}
        }
        response = requests.post(f"{BASE_URL}/api/v1/isCloseTo", json=data)
        assert_test(response.status_code == 200, "Same position returns 200")
        result = response.json()
        assert_test(result == True, "Same position returns true (distance = 0)")
        
        # Test 3: Exactly at threshold (0.00015 - should be false per spec)
        print("\n📝 Test 3: Exactly at threshold (0.00015)")
        data = {
            "position1": {"lng": 0.0, "lat": 0.0},
            "position2": {"lng": 0.00015, "lat": 0.0}
        }
        response = requests.post(f"{BASE_URL}/api/v1/isCloseTo", json=data)
        assert_test(response.status_code == 200, "Threshold distance returns 200")
        result = response.json()
        assert_test(result == False, "Distance = 0.00015 returns false (not <)")
        
        # Test 4: Just below threshold (should be true)
        print("\n📝 Test 4: Just below threshold (< 0.00015)")
        data = {
            "position1": {"lng": 0.0, "lat": 0.0},
            "position2": {"lng": 0.00014, "lat": 0.0}
        }
        response = requests.post(f"{BASE_URL}/api/v1/isCloseTo", json=data)
        assert_test(response.status_code == 200, "Below threshold returns 200")
        result = response.json()
        assert_test(result == True, "Distance < 0.00015 returns true")
        
        # Test 5: Far positions (should be false)
        print("\n📝 Test 5: Far positions (>> 0.00015)")
        data = {
            "position1": {"lng": -3.192473, "lat": 55.946233},
            "position2": {"lng": -3.192473, "lat": 55.942617}
        }
        response = requests.post(f"{BASE_URL}/api/v1/isCloseTo", json=data)
        assert_test(response.status_code == 200, "Far positions return 200")
        result = response.json()
        assert_test(result == False, "Far positions return false")
        
        # Test 6: Invalid data - missing fields
        print("\n📝 Test 6: Invalid data - missing position1")
        invalid_data = {
            "position2": {"lng": -3.192473, "lat": 55.942617}
        }
        response = requests.post(f"{BASE_URL}/api/v1/isCloseTo", json=invalid_data)
        assert_test(response.status_code == 400, "Missing position1 returns 400")
        
        # Test 7: Invalid data - invalid types
        print("\n📝 Test 7: Invalid data - string values")
        invalid_data = {
            "position1": {"lng": "not_a_number", "lat": 55.946233},
            "position2": {"lng": -3.192473, "lat": 55.942617}
        }
        response = requests.post(f"{BASE_URL}/api/v1/isCloseTo", json=invalid_data)
        assert_test(response.status_code == 400, "Invalid string values return 400")
        
        # Test 8: Diagonal close distance (Pythagorean)
        print("\n📝 Test 8: Diagonal close distance")
        data = {
            "position1": {"lng": 0.0, "lat": 0.0},
            "position2": {"lng": 0.0001, "lat": 0.0001}
        }
        response = requests.post(f"{BASE_URL}/api/v1/isCloseTo", json=data)
        assert_test(response.status_code == 200, "Diagonal close distance returns 200")
        result = response.json()
        # sqrt(0.0001^2 + 0.0001^2) = sqrt(0.00000002) ≈ 0.000141 < 0.00015
        assert_test(result == True, "Diagonal close distance returns true")
        
        return True
    except Exception as e:
        print(f"❌ Error: {e}")
        return False

def test_next_position():
    """Test the nextPosition endpoint - move 0.00015 in given direction."""
    print_test_header("Next Position Endpoint")
    
    MOVE_DISTANCE = 0.00015
    TOLERANCE = 0.00001  # For floating point comparison
    
    try:
        # Test 1: Move east (angle 0°)
        print("\n📝 Test 1: Move east (angle = 0°)")
        data = {
            "start": {"lng": -3.192473, "lat": 55.946233},
            "angle": 0.0
        }
        response = requests.post(f"{BASE_URL}/api/v1/nextPosition", json=data)
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text}")
        assert_test(response.status_code == 200, "Move east returns 200")
        result = response.json()
        expected_lng = -3.192473 + MOVE_DISTANCE
        assert_test(abs(result["lng"] - expected_lng) < TOLERANCE, f"Longitude increased by {MOVE_DISTANCE}")
        assert_test(abs(result["lat"] - 55.946233) < TOLERANCE, "Latitude unchanged")
        
        # Test 2: Move north (angle 90°)
        print("\n📝 Test 2: Move north (angle = 90°)")
        data = {
            "start": {"lng": -3.192473, "lat": 55.946233},
            "angle": 90.0
        }
        response = requests.post(f"{BASE_URL}/api/v1/nextPosition", json=data)
        assert_test(response.status_code == 200, "Move north returns 200")
        result = response.json()
        expected_lat = 55.946233 + MOVE_DISTANCE
        assert_test(abs(result["lng"] - (-3.192473)) < TOLERANCE, "Longitude unchanged")
        assert_test(abs(result["lat"] - expected_lat) < TOLERANCE, f"Latitude increased by {MOVE_DISTANCE}")
        
        # Test 3: Move west (angle 180°)
        print("\n📝 Test 3: Move west (angle = 180°)")
        data = {
            "start": {"lng": -3.192473, "lat": 55.946233},
            "angle": 180.0
        }
        response = requests.post(f"{BASE_URL}/api/v1/nextPosition", json=data)
        assert_test(response.status_code == 200, "Move west returns 200")
        result = response.json()
        expected_lng = -3.192473 - MOVE_DISTANCE
        assert_test(abs(result["lng"] - expected_lng) < TOLERANCE, f"Longitude decreased by {MOVE_DISTANCE}")
        assert_test(abs(result["lat"] - 55.946233) < TOLERANCE, "Latitude unchanged")
        
        # Test 4: Move south (angle 270°)
        print("\n📝 Test 4: Move south (angle = 270°)")
        data = {
            "start": {"lng": -3.192473, "lat": 55.946233},
            "angle": 270.0
        }
        response = requests.post(f"{BASE_URL}/api/v1/nextPosition", json=data)
        assert_test(response.status_code == 200, "Move south returns 200")
        result = response.json()
        expected_lat = 55.946233 - MOVE_DISTANCE
        assert_test(abs(result["lng"] - (-3.192473)) < TOLERANCE, "Longitude unchanged")
        assert_test(abs(result["lat"] - expected_lat) < TOLERANCE, f"Latitude decreased by {MOVE_DISTANCE}")
        
        # Test 5: Move north-east (angle 45°)
        print("\n📝 Test 5: Move north-east (angle = 45°)")
        data = {
            "start": {"lng": 0.0, "lat": 0.0},
            "angle": 45.0
        }
        response = requests.post(f"{BASE_URL}/api/v1/nextPosition", json=data)
        assert_test(response.status_code == 200, "Move north-east returns 200")
        result = response.json()
        # 45° means equal movement in both directions: MOVE_DISTANCE / sqrt(2) each
        import math
        expected_delta = MOVE_DISTANCE / math.sqrt(2)
        assert_test(abs(result["lng"] - expected_delta) < TOLERANCE, "Longitude increases correctly")
        assert_test(abs(result["lat"] - expected_delta) < TOLERANCE, "Latitude increases correctly")
        
        
        # Test 8: Invalid data - missing start position
        print("\n📝 Test 8: Invalid data - missing start")
        invalid_data = {
            "angle": 0.0
        }
        response = requests.post(f"{BASE_URL}/api/v1/nextPosition", json=invalid_data)
        assert_test(response.status_code == 400, "Missing start returns 400")
        
        # Test 9: Invalid data - missing angle
        print("\n📝 Test 9: Invalid data - missing angle")
        invalid_data = {
            "start": {"lng": -3.192473, "lat": 55.946233}
        }
        response = requests.post(f"{BASE_URL}/api/v1/nextPosition", json=invalid_data)
        assert_test(response.status_code == 400, "Missing angle returns 400")
        
        # Test 10: Invalid data - invalid types
        print("\n📝 Test 10: Invalid data - string angle")
        invalid_data = {
            "start": {"lng": -3.192473, "lat": 55.946233},
            "angle": "not_a_number"
        }
        response = requests.post(f"{BASE_URL}/api/v1/nextPosition", json=invalid_data)
        assert_test(response.status_code == 400, "Invalid angle type returns 400")
        
        # Test 11: Extra fields ignored (per spec)
        print("\n📝 Test 11: Extra fields ignored")
        data = {
            "start": {"lng": 0.0, "lat": 0.0},
            "angle": 0.0,
            "extraField": "should be ignored"
        }
        response = requests.post(f"{BASE_URL}/api/v1/nextPosition", json=data)
        assert_test(response.status_code == 200, "Extra fields ignored, returns 200")
        result = response.json()
        expected_lng = MOVE_DISTANCE
        assert_test(abs(result["lng"] - expected_lng) < TOLERANCE, "Calculation correct despite extra field")
        
        return True
    except Exception as e:
        print(f"❌ Error: {e}")
        return False

def test_is_in_region():
    """Test the isInRegion endpoint - point-in-polygon algorithm."""
    print_test_header("Is In Region Endpoint")
    
    try:
        # Test 1: Point clearly inside rectangle
        print("\n📝 Test 1: Point clearly inside rectangle")
        data = {
            "position": {"lng": -3.188, "lat": 55.944},
            "region": {
                "name": "central",
                "vertices": [
                    {"lng": -3.192473, "lat": 55.946233},
                    {"lng": -3.192473, "lat": 55.942617},
                    {"lng": -3.184319, "lat": 55.942617},
                    {"lng": -3.184319, "lat": 55.946233},
                    {"lng": -3.192473, "lat": 55.946233}
                ]
            }
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=data)
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text}")
        assert_test(response.status_code == 200, "Inside rectangle returns 200")
        result = response.json()
        assert_test(result == True, "Point inside returns true")
        
        # Test 2: Point clearly outside rectangle
        print("\n📝 Test 2: Point clearly outside rectangle")
        data = {
            "position": {"lng": -3.200, "lat": 55.950},
            "region": {
                "name": "central",
                "vertices": [
                    {"lng": -3.192473, "lat": 55.946233},
                    {"lng": -3.192473, "lat": 55.942617},
                    {"lng": -3.184319, "lat": 55.942617},
                    {"lng": -3.184319, "lat": 55.946233},
                    {"lng": -3.192473, "lat": 55.946233}
                ]
            }
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=data)
        assert_test(response.status_code == 200, "Outside rectangle returns 200")
        result = response.json()
        assert_test(result == False, "Point outside returns false")
        
        # Test 3: Point on vertex (boundary case)
        print("\n📝 Test 3: Point on vertex (boundary)")
        data = {
            "position": {"lng": -3.192473, "lat": 55.946233},
            "region": {
                "name": "central",
                "vertices": [
                    {"lng": -3.192473, "lat": 55.946233},
                    {"lng": -3.192473, "lat": 55.942617},
                    {"lng": -3.184319, "lat": 55.942617},
                    {"lng": -3.184319, "lat": 55.946233},
                    {"lng": -3.192473, "lat": 55.946233}
                ]
            }
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=data)
        assert_test(response.status_code == 200, "Point on vertex returns 200")
        # Note: Boundary behavior may vary by algorithm implementation
        
        # Test 4: Point on edge (boundary case)
        print("\n📝 Test 4: Point on edge (boundary)")
        data = {
            "position": {"lng": -3.188396, "lat": 55.942617},
            "region": {
                "name": "central",
                "vertices": [
                    {"lng": -3.192473, "lat": 55.946233},
                    {"lng": -3.192473, "lat": 55.942617},
                    {"lng": -3.184319, "lat": 55.942617},
                    {"lng": -3.184319, "lat": 55.946233},
                    {"lng": -3.192473, "lat": 55.946233}
                ]
            }
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=data)
        assert_test(response.status_code == 200, "Point on edge returns 200")
        
        # Test 5: Triangle region (inside)
        print("\n📝 Test 5: Triangle region - point inside")
        data = {
            "position": {"lng": 0.5, "lat": 0.3},
            "region": {
                "name": "Triangle",
                "vertices": [
                    {"lng": 0.0, "lat": 0.0},
                    {"lng": 1.0, "lat": 0.0},
                    {"lng": 0.5, "lat": 1.0},
                    {"lng": 0.0, "lat": 0.0}
                ]
            }
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=data)
        assert_test(response.status_code == 200, "Triangle inside returns 200")
        result = response.json()
        assert_test(result == True, "Point inside triangle returns true")
        
        # Test 6: Complex polygon (concave L-shape)
        print("\n📝 Test 6: Complex concave polygon")
        data = {
            "position": {"lng": 0.25, "lat": 0.25},
            "region": {
                "name": "L-Shape",
                "vertices": [
                    {"lng": 0.0, "lat": 0.0},
                    {"lng": 1.0, "lat": 0.0},
                    {"lng": 1.0, "lat": 0.5},
                    {"lng": 0.5, "lat": 0.5},
                    {"lng": 0.5, "lat": 1.0},
                    {"lng": 0.0, "lat": 1.0},
                    {"lng": 0.0, "lat": 0.0}
                ]
            }
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=data)
        assert_test(response.status_code == 200, "Complex polygon returns 200")
        result = response.json()
        assert_test(result == True, "Point inside L-shape returns true")
        
        # Test 7: Invalid - region not closed (first != last vertex)
        print("\n📝 Test 7: Invalid - unclosed region")
        data = {
            "position": {"lng": -3.188, "lat": 55.944},
            "region": {
                "name": "Unclosed",
                "vertices": [
                    {"lng": -3.192473, "lat": 55.946233},
                    {"lng": -3.192473, "lat": 55.942617},
                    {"lng": -3.184319, "lat": 55.942617}
                ]
            }
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=data)
        assert_test(response.status_code == 400, "Unclosed region returns 400")
        
        # Test 8: Invalid - too few vertices (< 4)
        print("\n📝 Test 8: Invalid - too few vertices")
        data = {
            "position": {"lng": 0.0, "lat": 0.0},
            "region": {
                "name": "Invalid",
                "vertices": [
                    {"lng": 0.0, "lat": 0.0},
                    {"lng": 1.0, "lat": 0.0},
                    {"lng": 0.0, "lat": 0.0}
                ]
            }
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=data)
        assert_test(response.status_code == 400, "Too few vertices returns 400")
        
        # Test 9: Invalid - missing position
        print("\n📝 Test 9: Invalid - missing position")
        invalid_data = {
            "region": {
                "name": "Test",
                "vertices": [
                    {"lng": 0.0, "lat": 0.0},
                    {"lng": 1.0, "lat": 0.0},
                    {"lng": 1.0, "lat": 1.0},
                    {"lng": 0.0, "lat": 0.0}
                ]
            }
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=invalid_data)
        assert_test(response.status_code == 400, "Missing position returns 400")
        
        # Test 10: Invalid - missing region
        print("\n📝 Test 10: Invalid - missing region")
        invalid_data = {
            "position": {"lng": 0.0, "lat": 0.0}
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=invalid_data)
        assert_test(response.status_code == 400, "Missing region returns 400")
        
        # Test 11: Invalid - null vertices
        print("\n📝 Test 11: Invalid - null vertices")
        invalid_data = {
            "position": {"lng": 0.0, "lat": 0.0},
            "region": {
                "name": "Test",
                "vertices": None
            }
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=invalid_data)
        assert_test(response.status_code == 400, "Null vertices returns 400")
        
        # Test 12: Extra fields ignored (per spec)
        print("\n📝 Test 12: Extra fields ignored")
        data = {
            "position": {"lng": -3.188, "lat": 55.944},
            "region": {
                "name": "central",
                "vertices": [
                    {"lng": -3.192473, "lat": 55.946233},
                    {"lng": -3.192473, "lat": 55.942617},
                    {"lng": -3.184319, "lat": 55.942617},
                    {"lng": -3.184319, "lat": 55.946233},
                    {"lng": -3.192473, "lat": 55.946233}
                ]
            },
            "extraField": "should be ignored"
        }
        response = requests.post(f"{BASE_URL}/api/v1/isInRegion", json=data)
        assert_test(response.status_code == 200, "Extra fields ignored, returns 200")
        result = response.json()
        assert_test(result == True, "Calculation correct despite extra field")
        
        return True
    except Exception as e:
        print(f"❌ Error: {e}")
        return False

def main():
    """Run all tests."""
    print("=" * 70)
    print("🚀 ILP CW1 Spring Boot REST API - Comprehensive Test Suite")
    print("=" * 70)
    print(f"📍 Testing against: {BASE_URL}")
    print(f"📅 Test run: {__import__('datetime').datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("⚠️  Make sure the server is running before running this script!")
    print("=" * 70)
    
    tests = [
        test_health,
        test_uid,
        test_distance_to,
        test_is_close_to,
        test_next_position,
        test_is_in_region
    ]
    
    results = []
    for test in tests:
        try:
            results.append(test())
        except Exception as e:
            print(f"\n❌ Test failed with exception: {e}")
            import traceback
            traceback.print_exc()
            results.append(False)
    
    # Print detailed summary
    print("\n" + "=" * 70)
    print("📊 DETAILED TEST SUMMARY")
    print("=" * 70)
    print(f"Total Endpoint Tests: {len(results)}")
    print(f"Total Individual Test Cases: {total_tests}")
    print(f"✅ Passed Test Cases: {passed_tests}")
    print(f"❌ Failed Test Cases: {failed_tests}")
    
    if total_tests > 0:
        success_rate = (passed_tests / total_tests) * 100
        print(f"📈 Success Rate: {success_rate:.1f}%")
    
    print("\n📋 Endpoint Test Results:")
    test_names = ["Health Check", "UID", "Distance To", "Is Close To", "Next Position", "Is In Region"]
    for i, (name, result) in enumerate(zip(test_names, results)):
        status = "✅ PASS" if result else "❌ FAIL"
        print(f"  {i+1}. {name:20s} {status}")
    
    print("=" * 70)
    
    if all(results) and failed_tests == 0:
        print("🎉 ALL TESTS PASSED! System working as expected per assignment spec.")
        print("=" * 70)
        sys.exit(0)
    else:
        print("⚠️  SOME TESTS FAILED! Please review the output above.")
        print("=" * 70)
        sys.exit(1)

if __name__ == "__main__":
    main()
