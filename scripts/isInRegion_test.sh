curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
        "position": {
          "lng": 1.234,
          "lat": 1.222
        },
        "region": {
          "name": "central",
          "vertices": [
            {
              "lng": -3.192473,
              "lat": 55.946233
            },
            {
              "lng": -3.192473,
              "lat": 55.942617
            },
            {
              "lng": -3.184319,
              "lat": 55.942617
            },
            {
              "lng": -3.184319,
              "lat": 55.946233
            },
            {
              "lng": -3.192473,
              "lat": 55.946233
            }
          ]
        }
      }' \
  http://localhost:8080/api/v1/isInRegion