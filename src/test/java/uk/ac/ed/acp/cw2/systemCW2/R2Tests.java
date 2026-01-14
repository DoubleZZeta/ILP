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

import java.io.File;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class R2Tests
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

    private boolean isPointInRestrictedArea(Position point, RestrictedArea restrictedArea)
    {
        double x = point.getLng();
        double y = point.getLat();

        double rightMost = restrictedArea.getVertices().getFirst().getLng();
        double leftMost = restrictedArea.getVertices().getFirst().getLng();
        double upMost  = restrictedArea.getVertices().getFirst().getLat();
        double bottomMost =  restrictedArea.getVertices().getFirst().getLat();

        for (Position vertex: restrictedArea.getVertices())
        {
            if (vertex.getLng() > rightMost)
            {
                rightMost = vertex.getLng();
            }

            if(vertex.getLng() < leftMost)
            {
                leftMost = vertex.getLng();
            }

            if(vertex.getLat() > upMost)
            {
                upMost = vertex.getLat();
            }

            if(vertex.getLat() < bottomMost)
            {
                bottomMost = vertex.getLat();
            }
        }

        return (leftMost <= x && x <= rightMost) && (bottomMost <= y && y <= upMost);
    }

    private boolean isAnyPointInRestrictedArea(ReturnedPath returnedPath, ArrayList<RestrictedArea> restrictedAreas)
    {
        for (DronePath path: returnedPath.getDronePaths())
        {
            for (Deliveries deliveries: path.getDeliveries())
            {
                for (Position position: deliveries.getFlightPath())
                {
                    for (RestrictedArea restrictedArea: restrictedAreas)
                    {
                        if (isPointInRestrictedArea(position,restrictedArea))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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
        // Description: No no-fly zones

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
        assert(returnedPath.getTotalMoves() != 0);
        assert(!isAnyPointInRestrictedArea(returnedPath,dataFetchService.getRestrictedAreas()));
    }

    @Test
    public void P2Test() throws Exception
    {
        // Description: One no-fly zone but placed far away
        String restrictedAreaJson =
                //language=JSON
                """
                 [
                   {
                     "name": "Restricted Area One",
                     "id": 1,
                     "limits":
                     {
                       "lower": 0,
                       "upper": -1
                     },
                     "vertices":
                     [
                      {
                        "lng": -3.0,
                        "lat": 55.0,
                        "alt": null
                      },
                      {
                        "lng": -3.01,
                        "lat": 55.0,
                        "alt": null
                      },
                      {
                        "lng": -3.01,
                        "lat": 55.01,
                        "alt": null
                      },
                      {
                        "lng": -3.0,
                        "lat": 55.01,
                        "alt": null
                      },
                      {
                        "lng": -3.0,
                        "lat": 55.0,
                        "alt": null
                      }
                     ]
                   }
                 ]
                """;

        setupMockRestTemplate
        (
            getDefaultServicePointsJson(),
            restrictedAreaJson,
            getDefaultDronesJson(),
            getDefaultServicePointDronesJson()
        );


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
        assert(returnedPath.getTotalMoves() != 0);
        assert(!isAnyPointInRestrictedArea(returnedPath,dataFetchService.getRestrictedAreas()));
    }

    @Test
    public void P3Test() throws Exception
    {
        // Description: One no-fly zone placed on the shortest path
        String restrictedAreaJson =
                //language=JSON
                """
                 [
                   {
                     "name": "Restricted Area Two",
                     "id": 2,
                     "limits":
                     {
                       "lower": 0,
                       "upper": -1
                     },
                     "vertices":
                     [
                      {
                        "lng": -3.16,
                        "lat": 55.94,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.94,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.95,
                        "alt": null
                      },
                      {
                        "lng": -3.16,
                        "lat": 55.95,
                        "alt": null
                      },
                      {
                        "lng": -3.16,
                        "lat": 55.94,
                        "alt": null
                      }
                     ]
                   }
                 ]
                """;

        setupMockRestTemplate
        (
            getDefaultServicePointsJson(),
            restrictedAreaJson,
            getDefaultDronesJson(),
            getDefaultServicePointDronesJson()
        );

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
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("scripts/returned_path.json"), returnedPath);
        assert(returnedPath.getTotalMoves() != 0);
        assert(!isAnyPointInRestrictedArea(returnedPath,dataFetchService.getRestrictedAreas()));
    }

    @Test
    public void P4Test() throws Exception
    {
        // Description: Three no-fly zones which forces the dron to go up, right then down
        String restrictedAreaJson =
                //language=JSON
                """
                 [
                   {
                     "name": "Restricted Area Three",
                     "id": 3,
                     "limits":
                     {
                       "lower": 0,
                       "upper": -1
                     },
                     "vertices":
                     [
                      {
                        "lng": -3.16,
                        "lat": 55.94,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.94,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.95,
                        "alt": null
                      },
                      {
                        "lng": -3.16,
                        "lat": 55.95,
                        "alt": null
                      },
                      {
                        "lng": -3.16,
                        "lat": 55.94,
                        "alt": null
                      }
                     ]
                   },
                   {
                     "name": "Restricted Area Four",
                     "id": 4,
                     "limits":
                     {
                       "lower": 0,
                       "upper": -1
                     },
                     "vertices":
                     [
                      {
                        "lng": -3.17,
                        "lat": 55.94,
                        "alt": null
                      },
                      {
                        "lng": -3.2,
                        "lat": 55.94,
                        "alt": null
                      },
                      {
                        "lng": -3.2,
                        "lat": 55.944,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.944,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.94,
                        "alt": null
                      }
                     ]
                   },
                   {
                     "name": "Restricted Area Five",
                     "id": 5,
                     "limits":
                     {
                       "lower": 0,
                       "upper": -1
                     },
                     "vertices":
                     [
                      {
                        "lng": -3.175,
                        "lat": 55.95,
                        "alt": null
                      },
                      {
                        "lng": -3.2,
                        "lat": 55.95,
                        "alt": null
                      },
                      {
                        "lng": -3.2,
                        "lat": 55.948,
                        "alt": null
                      },
                      {
                        "lng": -3.175,
                        "lat": 55.948,
                        "alt": null
                      },
                      {
                        "lng": -3.175,
                        "lat": 55.95,
                        "alt": null
                      }
                     ]
                   }
                 ]
                """;

        setupMockRestTemplate
        (
            getDefaultServicePointsJson(),
            restrictedAreaJson,
            getDefaultDronesJson(),
            getDefaultServicePointDronesJson()
        );

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
        assert(returnedPath.getTotalMoves() != 0);
        assert(!isAnyPointInRestrictedArea(returnedPath,dataFetchService.getRestrictedAreas()));
    }

    @Test
    public void P5Test() throws Exception
    {
        // Description: Three no-fly zones which blocks all paths
        String restrictedAreaJson =
                //language=JSON
                """
                 [
                   {
                     "name": "Restricted Area Six",
                     "id": 6,
                     "limits":
                     {
                       "lower": 0,
                       "upper": -1
                     },
                     "vertices":
                     [
                      {
                        "lng": -3.16,
                        "lat": 55.94,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.94,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.95,
                        "alt": null
                      },
                      {
                        "lng": -3.16,
                        "lat": 55.95,
                        "alt": null
                      },
                      {
                        "lng": -3.16,
                        "lat": 55.94,
                        "alt": null
                      }
                     ]
                   },
                   {
                     "name": "Restricted Area Seven",
                     "id": 7,
                     "limits":
                     {
                       "lower": 0,
                       "upper": -1
                     },
                     "vertices":
                     [
                      {
                        "lng": -3.17,
                        "lat": 55.94,
                        "alt": null
                      },
                      {
                        "lng": -3.2,
                        "lat": 55.94,
                        "alt": null
                      },
                      {
                        "lng": -3.2,
                        "lat": 55.944,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.944,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.94,
                        "alt": null
                      }
                     ]
                   },
                   {
                     "name": "Restricted Area Eight",
                     "id": 8,
                     "limits":
                     {
                       "lower": 0,
                       "upper": -1
                     },
                     "vertices":
                     [
                      {
                        "lng": -3.17,
                        "lat": 55.95,
                        "alt": null
                      },
                      {
                        "lng": -3.2,
                        "lat": 55.95,
                        "alt": null
                      },
                      {
                        "lng": -3.2,
                        "lat": 55.948,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.948,
                        "alt": null
                      },
                      {
                        "lng": -3.17,
                        "lat": 55.95,
                        "alt": null
                      }
                     ]
                   },
                  {
                     "name": "Restricted Area Nine",
                     "id": 9,
                     "limits":
                     {
                       "lower": 0,
                       "upper": -1
                     },
                     "vertices":
                     [
                      {
                        "lng": -3.18,
                        "lat": 55.948,
                        "alt": null
                      },
                      {
                        "lng": -3.2,
                        "lat": 55.948,
                        "alt": null
                      },
                      {
                        "lng": -3.2,
                        "lat": 55.944,
                        "alt": null
                      },
                      {
                        "lng": -3.18,
                        "lat": 55.944,
                        "alt": null
                      },
                      {
                        "lng": -3.18,
                        "lat": 55.948,
                        "alt": null
                      }
                     ]
                   }
                 ]
                """;

        setupMockRestTemplate
        (
            getDefaultServicePointsJson(),
            restrictedAreaJson,
            getDefaultDronesJson(),
            getDefaultServicePointDronesJson()
        );

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
        assert(returnedPath.getTotalMoves() == 0);
        assert(!isAnyPointInRestrictedArea(returnedPath,dataFetchService.getRestrictedAreas()));
    }
}
