curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "start":{
      "lng": 0,
      "lat": 0
    },
    "angle": 45
  }' \
  http://localhost:8080/api/v1/nextPosition