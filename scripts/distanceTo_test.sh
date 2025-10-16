curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
        "position1":{
          "lng": 1,
          "lat": 1
        },
        "position2":{
          "lng": ,
          "lat": 2
        }
      }' \
  http://localhost:8080/api/v1/distanceTo