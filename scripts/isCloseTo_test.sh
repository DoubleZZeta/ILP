curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
        "position1": {
          "lng": -3.192473,
          "lat": 55.00000
        },
        "position2": {
          "lng": -3.192473,
          "lat": 55.00014999
        }
    }' \
  http://localhost:8080/api/v1/isCloseTo