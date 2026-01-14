package uk.ac.ed.acp.cw2.systemCW2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.acp.cw2.data.*;
import uk.ac.ed.acp.cw2.service.DataFetchServiceImplementation;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class R1Tests
{
    @TestConfiguration
    public static class MockRestTemplateConfiguration
    {
        @Bean
        @Primary
        public RestTemplate mockRestTemplate()
        {
            return mock(RestTemplate.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DataFetchServiceImplementation dataFetchService;

    private String getDefaultServicePointsJson()
    {
        return
        //language=JSON
        """
            [
              {
                "name": "Appleton Tower",
                "id": 1,
                "location":
                {
                  "lng": -3.18635808,
                  "lat": 55.94468067
                }
              }
            ]
        """;
    }

    private String getDefaultRestrictedAreasJson()
    {
        return
        //language=JSON
        """
            [
            ]
        """;
    }

    private String getDefaultDronesJson()
    {
        return
        //language=JSON
        """
            [
              {
                "name": "Drone 1",
                "id": "1",
                "capability": {
                  "cooling": true,
                  "heating": true,
                  "capacity": 10,
                  "maxMoves": 1000,
                  "costPerMove": 0.01,
                  "costInitial": 1,
                  "costFinal": 1
                }
              }
            ]
        """;
    }

    private String getDefaultServicePointDronesJson()
    {
        return
        //language=JSON
        """
            [
              {
                "servicePointId": 1,
                "drones": [
                  {
                    "id": "1",
                    "availability": [
                      {
                        "dayOfWeek": "MONDAY",
                        "from": "00:00:00",
                        "until": "23:59:59"
                      },
                      {
                        "dayOfWeek": "TUESDAY",
                        "from": "00:00:00",
                        "until": "23:59:59"
                      },
                      {
                        "dayOfWeek": "WEDNESDAY",
                        "from": "08:00:00",
                        "until": "18:00:00"
                      },
                      {
                        "dayOfWeek": "THURSDAY",
                        "from": "00:00:00",
                        "until": "23:59:59"
                      },
                      {
                        "dayOfWeek": "FRIDAY",
                        "from": "00:00:00",
                        "until": "23:59:59"
                      },
                      {
                        "dayOfWeek": "SATURDAY",
                        "from": "00:00:00",
                        "until": "23:59:59"
                      },
                      {
                        "dayOfWeek": "SUNDAY",
                        "from": "00:00:00",
                        "until": "23:59:59"
                      }
                    ]
                  }
                ]
              }
            ]
        """;
    }

    private void setupMockRestTemplate(String servicePointsJson, String restrictedAreasJson, String dronesJson, String servicePointDronesJson) throws Exception
    {
        //Deserialize JSON strings into objects using ObjectMapper
        ServicePoint[] servicePoints = objectMapper.readValue(servicePointsJson, ServicePoint[].class);
        RestrictedArea[] restrictedAreas = objectMapper.readValue(restrictedAreasJson, RestrictedArea[].class);
        Drone[] drones = objectMapper.readValue(dronesJson, Drone[].class);
        ServicePointDrones[] servicePointDrones = objectMapper.readValue(servicePointDronesJson, ServicePointDrones[].class);

        //Configure mock RestTemplate to return deserialized objects
        when(restTemplate.getForObject(anyString(), eq(ServicePoint[].class))).thenReturn(servicePoints);
        when(restTemplate.getForObject(anyString(), eq(RestrictedArea[].class))).thenReturn(restrictedAreas);
        when(restTemplate.getForObject(anyString(), eq(Drone[].class))).thenReturn(drones);
        when(restTemplate.getForObject(anyString(), eq(ServicePointDrones[].class))).thenReturn(servicePointDrones);
    }

    private int getMaxMoves(String droneId)
    {
        ArrayList<Drone> drones = dataFetchService.getDrones();
        for (Drone drone : drones)
        {
            if (drone.getId().equals(droneId))
            {
                return drone.getCapability().getMaxMoves();
            }
        }
        throw new IllegalArgumentException("Drone with ID " + droneId + " not found");
    }

    @BeforeEach
    public void setup() throws Exception
    {
        setupMockRestTemplate
        (
            getDefaultServicePointsJson(),
            getDefaultRestrictedAreasJson(),
            getDefaultDronesJson(),
            getDefaultServicePointDronesJson()
        );
    }

    @Test
    public void P1Test() throws Exception
    {
        // Get maxMoves from the mocked drone
        int droneMaxMoves = getMaxMoves("1");

        String requestBody =
        //language=JSON
        """
            [
                {
                    "id": 1,
                    "date": "2026-01-01",
                    "time": "12:00",
                    "requirements":
                    {
                        "capacity": 1.0
                    },
                    "delivery":
                    {
                      "lng": -3.15635808,
                      "lat": 55.94468067
                    }
                }
            ]
        """;

        String response = mockMvc.perform(post("/api/v1/calcDeliveryPath")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ReturnedPath returnedPath = objectMapper.readValue(response, ReturnedPath.class);

        assert(returnedPath.getTotalMoves() <= droneMaxMoves);
    }

    @Test
    public void P2Test() throws Exception
    {
        // Get maxMoves from the mocked drone
        int droneMaxMoves = getMaxMoves("1");

        String requestBody =
                //language=JSON
                """
                    [
                        {
                            "id": 2,
                            "date": "2026-01-01",
                            "time": "12:00",
                            "requirements":
                            {
                                "capacity": 1.0
                            },
                            "delivery":
                            {
                              "lng": -3.11150808,
                              "lat": 55.94468067
                            }
                        }
                    ]
                """;

        String response = mockMvc.perform(post("/api/v1/calcDeliveryPath")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ReturnedPath returnedPath = objectMapper.readValue(response, ReturnedPath.class);

        assert(returnedPath.getTotalMoves() == droneMaxMoves-1);
    }

    @Test
    public void P3Test() throws Exception
    {
        // Get maxMoves from the mocked drone
        int droneMaxMoves = getMaxMoves("1");

        String requestBody =
                //language=JSON
                """
                    [
                        {
                            "id": 3,
                            "date": "2026-01-01",
                            "time": "12:00",
                            "requirements":
                            {
                                "capacity": 1.0
                            },
                            "delivery":
                            {
                              "lng": -3.12635808,
                              "lat": 55.94468067
                            }
                        },
                        {
                            "id": 4,
                            "date": "2026-01-01",
                            "time": "12:00",
                            "requirements":
                            {
                                "capacity": 1.0
                            },
                            "delivery":
                            {
                              "lng": -3.18635808,
                              "lat": 56.00468067
                            }
                        }
                    ]
                """;

        String response = mockMvc.perform(post("/api/v1/calcDeliveryPath")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        int moves = 0;
        ReturnedPath returnedPath = objectMapper.readValue(response, ReturnedPath.class);
        for (DronePath dronePath: returnedPath.getDronePaths())
        {
            for(Deliveries delivery: dronePath.getDeliveries())
            {
                if(delivery.getDeliveryId() != null)
                {
                    moves += delivery.getFlightPath().size()-1;
                }
                else
                {
                    assert(moves <= droneMaxMoves);
                    moves = 0;
                }
            }
        }
    }

    @Test
    public void P4Test() throws Exception
    {
        int droneMaxMoves = getMaxMoves("1");
        String requestBody =
                //language=JSON
                """
                    [
                        {
                            "id": 5,
                            "date": "2026-01-01",
                            "time": "12:00",
                            "requirements":
                            {
                                "capacity": 1.0
                            },
                            "delivery":
                            {
                              "lng": -3.11135808,
                              "lat": 55.94468067
                            }
                        }
                    ]
                """;

        String response = mockMvc.perform(post("/api/v1/calcDeliveryPath")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ReturnedPath returnedPath = objectMapper.readValue(response, ReturnedPath.class);

        assert(returnedPath.getTotalMoves() == 0);
    }

    @Test
    public void P5Test() throws Exception
    {
        int droneMaxMoves = getMaxMoves("1");
        String requestBody =
                //language=JSON
                """
                    [
                        {
                            "id": 5,
                            "date": "2026-01-01",
                            "time": "12:00",
                            "requirements":
                            {
                                "capacity": 1.0
                            },
                            "delivery":
                            {
                              "lng": -3.03635808,
                              "lat": 55.94468067
                            }
                        }
                    ]
                """;

        String response = mockMvc.perform(post("/api/v1/calcDeliveryPath")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ReturnedPath returnedPath = objectMapper.readValue(response, ReturnedPath.class);

        assert(returnedPath.getTotalMoves() == 0);
    }
}
