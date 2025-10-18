package uk.ac.ed.acp.cw2.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ed.acp.cw2.data.*;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class that conduct integration test for each end point.
 * The mockMVC is used to create a mock running environment.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties =
                {
                        "server.port=8080"
                }
)
@AutoConfigureMockMvc
public class ControllerTests
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void healthCheck_should_returnUPAndOk() throws Exception
    {
        this.mockMvc.perform(get("/actuator/health")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("\"status\":\"UP\"")));
    }

    @Test
    public void uid_should_returnCorrectUidAnd200() throws Exception
    {
        this.mockMvc.perform(get("/api/v1/uid")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("s2487866"));
    }

    @Test
    public void distanceTo_should_returnCorrectValueAnd200_whenValidDataIsGiven() throws Exception
    {
        Position position1 = new Position(-3.192473, 55.946233);
        Position position2 = new Position(-3.192473, 55.942617);
        PositionsRequest requestBody = new PositionsRequest (position1, position2);

        this.mockMvc.perform(post("/api/v1/distanceTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0.00362"));
    }

    @Test
    public void distanceTo_should_return200_whenMoreDataIsGiven() throws Exception
    {
        String requestBody = "{" +
                                "\"position1\": {" +
                                    "\"lng\": -3.192473," +
                                    "\"lat\": 55.946233" +
                                "}," +
                                "\"position2\": {" +
                                    "\"lng\": -3.192473," +
                                    "\"lat\": 55.940000" +
                                "}," +
                                "\"position2\": {" +
                                    "\"lng\": -3.192473," +
                                    "\"lat\": 55.942617" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/distanceTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(content().string("0.00362"))
                    .andExpect(status().is(200));
    }

    @Test
    public void distanceTo_should_return400_whenLngOfOnePositionIsNull() throws Exception
    {
        String requestBody = "{" +
                                "\"position1\": {" +
                                    "\"lng\": -3.192473," +
                                    "\"lat\": 55.942617" +
                                "}," +
                                "\"position2\": {" +
                                    "\"lng\": ," +
                                    "\"lat\": 55.942617" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/distanceTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void distanceTo_should_return400_whenLatOfOnePositionIsNull() throws Exception
    {
        String requestBody = "{" +
                                "\"position1\": {" +
                                    "\"lng\": -3.192473," +
                                    "\"lat\": 55.942617" +
                                "}," +
                                "\"position2\": {" +
                                    "\"lng\": -3.192473," +
                                    "\"lat\": " +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/distanceTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void distanceTo_should_return400_whenLngAndLatOfOnePositionAreNull() throws Exception
    {
        String requestBody = "{" +
                                "\"position1\": {" +
                                    "\"lng\": -3.192473," +
                                    "\"lat\": 55.942617" +
                                "}," +
                                "\"position2\": {" +
                                    "\"lng\": ," +
                                    "\"lat\": " +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/distanceTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void distanceTo_should_return400_whenOnePositionIsMissingStatus400() throws Exception
    {
        String requestBody = "{" +
                                "\"position1\": {" +
                                    "\"lng\": -3.192473," +
                                    "\"lat\": 55.942617" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/distanceTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void distanceTo_should_return400_whenNameOfTheFieldsAreInvalid() throws Exception
    {
        String requestBody = "{" +
                                "\"OptimusPrime\": {" +
                                    "\"lng\": -3.192473," +
                                    "\"lat\": 55.942617" +
                                "}," +
                                "\"Megatron\": {" +
                                    "\"lng\": -3.192473" +
                                    "\"lat\": 55.942617" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/distanceTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void distanceTo_should_return400_whenLatOutOfRange() throws Exception
    {
        String requestBody = "{" +
                                "\"Position1\": {" +
                                    "\"lng\": -91" +
                                    "\"lat\": 55.942617" +
                                "}," +
                                "\"Position2\": {" +
                                    "\"lng\": -3.192473" +
                                    "\"lat\": 55.942617" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/distanceTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void distanceTo_should_return400_whenLngOutOfRange() throws Exception
    {
        String requestBody = "{" +
                                "\"Position1\": {" +
                                    "\"lng\": -90" +
                                    "\"lat\": 181" +
                                "}," +
                                "\"Position2\": {" +
                                    "\"lng\": -3.192473" +
                                    "\"lat\": 55.942617" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/distanceTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void isCloseTo_should_returnFalseAnd200_whenTheDistanceBetweenPositionsIsGreaterThanTheThreshold() throws Exception
    {
        Position position1 = new Position(-3.192473, 55.946233);
        Position position2 = new Position(-3.192473, 55.942617);
        PositionsRequest requestBody = new PositionsRequest(position1, position2);

        this.mockMvc.perform(post("/api/v1/isCloseTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
    }

    @Test
    public void isCloseTo_should_returnFalseAnd200_whenTheDistanceBetweenPositionsIsEqualToTheThreshold() throws Exception
    {
        Position position1 = new Position(0.0, 0.0);
        Position position2 = new Position(0.0, 0.00015);
        PositionsRequest requestBody = new PositionsRequest(position1, position2);

        this.mockMvc.perform(post("/api/v1/isCloseTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
    }

    @Test
    public void isCloseTo_should_returnFalseAnd200_whenTheDistanceBetweenPositionsIsEqualToTheThresholdAfterRounding() throws Exception
    {
        Position position1 = new Position(0.0, 0.0);
        Position position2 = new Position(0.0, 0.00014999999);
        PositionsRequest requestBody = new PositionsRequest(position1, position2);

        this.mockMvc.perform(post("/api/v1/isCloseTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
    }

    @Test
    public void isCloseTo_should_returnTrueAnd200_whenTheDistanceBetweenPositionsIsLessThanThreshold() throws Exception
    {
        Position position1 = new Position(0.0, 0.0);
        Position position2 = new Position(0.0, 0.00014);
        PositionsRequest requestBody = new PositionsRequest(position1, position2);

        this.mockMvc.perform(post("/api/v1/isCloseTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
    }

    @Test
    public void isCloseTo_should_returnTrueAnd200_whenMoreDataIsGiven() throws Exception
    {
        String requestBody = "{" +
                                "\"position1\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.0" +
                                "}," +
                                "\"position2\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.00015" +
                                "}," +
                                "\"position2\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.00014" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/isCloseTo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(content().string("true"))
                    .andExpect(status().is(200));
    }

    @Test
    public void nextPosition_should_returnCorrectLngLatPairAnd200_whenAngleIsZero() throws Exception
    {
        Position position = new Position(0.0, 0.0);
        double angle = 0.0;
        PositionAngleRequest requestBody = new PositionAngleRequest(position, angle);
        this.mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("{ \"lng\": 0.00015, \"lat\": 0.00000 }"));
    }

    @Test
    public void nextPosition_should_returnCorrectLngLatPairAnd200_whenAngleIsTwentyTwoPointFive() throws Exception
    {
        Position position = new Position(0.0, 0.0);
        double angle = 22.5;
        PositionAngleRequest requestBody = new PositionAngleRequest(position, angle);
        this.mockMvc.perform(post("/api/v1/nextPosition")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("{ \"lng\": 0.00014, \"lat\": 0.00006 }"));
    }

    @Test
    public void nextPosition_should_returnCorrectLngLatPairAnd200_whenAngleIsFortyFive() throws Exception
    {
        Position position = new Position(0.0, 0.0);
        double angle = 45.0;
        PositionAngleRequest requestBody = new PositionAngleRequest(position, angle);
        this.mockMvc.perform(post("/api/v1/nextPosition")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("{ \"lng\": 0.00011, \"lat\": 0.00011 }"));
    }

    @Test
    public void nextPosition_should_returnCorrectLngLatPairAnd200_whenAngleIsNinety() throws Exception
    {
        Position position = new Position(0.0, 0.0);
        double angle = 90.0;
        PositionAngleRequest requestBody = new PositionAngleRequest(position, angle);
        this.mockMvc.perform(post("/api/v1/nextPosition")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("{ \"lng\": 0.00000, \"lat\": 0.00015 }"));
    }

    @Test
    public void nextPosition_should_returnTrueAnd200_whenMoreDataIsGiven() throws Exception
    {
        String requestBody = "{" +
                                "\"start\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.0" +
                                "}," +
                                "\"angle\": 135,"+
                                "\"start\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.00014" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(content().string("{ \"lng\": -0.00011, \"lat\": 0.00025 }"))
                .andExpect(status().is(200));
    }

    @Test
    public void nextPosition_should_return400_whenAngleIsNull() throws Exception
    {
        String requestBody = "{" +
                                "\"start\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.0" +
                                "}," +
                                "\"angle\": "+
                            "}";

        this.mockMvc.perform(post("/api/v1/nextPosition")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void nextPosition_should_return400_whenStartIsMissing() throws Exception
    {
        String requestBody = "{" +
                                "\"angle\": 0.0"+
                            "}";

        this.mockMvc.perform(post("/api/v1/nextPosition")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void nextPosition_should_return400_whenAngleIsMissing() throws Exception
    {
        String requestBody = "{" +
                                "\"start\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.0" +
                            "}" ;
        this.mockMvc.perform(post("/api/v1/nextPosition")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void nextPosition_should_return400_whenAngleIsOutOfRange() throws Exception
    {
        String requestBody = "{" +
                                "\"start\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.0" +
                                "}," +
                                "\"angle\": 360"+
                            "}";

        this.mockMvc.perform(post("/api/v1/nextPosition")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void nextPosition_should_return400_whenAngleIsNotAMultipleOfTwentyTwoPointFive() throws Exception
    {
        String requestBody = "{" +
                                "\"start\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.0" +
                                "}," +
                                "\"angle\": 31"+
                            "}";

        this.mockMvc.perform(post("/api/v1/nextPosition")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void isInRegion_should_returnTrueAnd200_whenThePositionIsInsideASquareRegion() throws Exception
    {
        Position position = new Position(0.05,0.05);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.1,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest requestBody = new PositionRegionRequest(position,region);

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
    }

    @Test
    public void isInRegion_should_returnFalseAnd200_whenThePositionIsOutsideASquareRegion() throws Exception
    {
        Position position = new Position(-0.01,0.01);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.1,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest requestBody = new PositionRegionRequest(position,region);

        this.mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                        .andExpect(status().isOk())
                        .andExpect(content().string("false"));
    }

    @Test
    public void isInRegion_should_returnTrueAnd200_whenThePositionIsOnAnEdgeOfTheSquareRegion() throws Exception
    {
        Position position = new Position(0.0,0.05);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.1,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest requestBody = new PositionRegionRequest(position,region);

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
    }

    @Test
    public void isInRegion_should_returnTrueAnd200_whenThePositionIsOnAVertexOfTheSquareRegion() throws Exception
    {
        Position position = new Position(0.0,0.1);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.1,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest requestBody = new PositionRegionRequest(position,region);

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
    }

    @Test
    public void isInRegion_should_returnTrueAnd200_whenThePositionIsInsideATriangleRegion() throws Exception
    {
        Position position = new Position(0.05,0.05);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.2,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest requestBody = new PositionRegionRequest(position,region);

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
    }

    @Test
    public void isInRegion_should_returnFalseAnd200_whenThePositionIsOutsideATriangleRegion() throws Exception
    {
        Position position = new Position(-0.05,0.05);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.2,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest requestBody = new PositionRegionRequest(position,region);

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
    }

    @Test
    public void isInRegion_should_returnFalseAnd200_whenThePositionIsOnTheRightOfATriangleRegion() throws Exception
    {
        Position position = new Position(0.2,0.05);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.2,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest requestBody = new PositionRegionRequest(position,region);

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
    }

    @Test
    public void isInRegion_should_returnTrueAnd200_whenThePositionIsOnAnEdgeOfTheTriangleRegion() throws Exception
    {
        Position position = new Position(0.2,0.2/3);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.3,0.1));
        vertices.add(new Position(0.3,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest requestBody = new PositionRegionRequest(position,region);

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
    }


    @Test
    public void isInRegion_should_returnTrueAnd200_whenThePositionIsOnAVertexOfTheTriangleRegion() throws Exception
    {
        Position position = new Position(0.1,0.1);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.2,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest requestBody = new PositionRegionRequest(position,region);

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
    }

    @Test
    public void isInRegion_should_returnTrueAnd200_whenMoreDataIsGiven() throws Exception
    {
        String requestBody = "{" +
                                "\"position\": {" +
                                    "\"lng\": 1.0," +
                                    "\"lat\": 1.0" +
                                "}," +
                                "\"position\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.1" +
                                "}," +
                                "\"region\": {" +
                                    "\"name\": \"Name\"," +
                                    "\"vertices\": [" +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.0" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.1" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.1," +
                                            "\"lat\": 0.0" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.0" +
                                    "}" +
                                "]" +
                            "}" +
                        "}";



        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

    }

    @Test
    public void isInRegion_should_return400_whenThePositionIsMissing() throws Exception
    {
        String requestBody = "{" +
                                "\"region\": {" +
                                    "\"name\": \"Name\"," +
                                    "\"vertices\": [" +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.0" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.1" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.1," +
                                            "\"lat\": 0.0" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.0" +
                                        "}" +
                                    "]" +
                                "}" +
                            "}";



        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void isInRegion_should_return400_whenRegionIsMissing() throws Exception
    {
        String requestBody = "{" +
                                "\"position\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.0" +
                                "}" +
                            "}";



        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void isInRegion_should_return400_whenTheNameIsMissing() throws Exception
    {
        String requestBody = "{" +
                                "\"position\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.0" +
                                "}," +
                                    "\"region\": {" +
                                        "\"vertices\": [" +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.0" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.1" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.1," +
                                            "\"lat\": 0.0" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.0" +
                                        "}" +
                                    "]" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void isInRegion_should_return400_whenVerticesIsMissing() throws Exception
    {
        String requestBody = "{" +
                "\"position\": {" +
                "\"lng\": 0.0," +
                "\"lat\": 0.0" +
                "}," +
                "\"region\": {" +
                "\"name\": \"Name\"" +
                "}" +
                "}";

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void isInRegion_should_return400_whenNameIsNull() throws Exception
    {
        String requestBody = "{" +
                            "\"position\": {" +
                                "\"lng\": 0.0," +
                                "\"lat\": 0.0" +
                            "}," +
                            "\"region\": {" +
                                    "\"name\": ," +
                                    "\"vertices\": [" +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.0" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.1" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.1," +
                                            "\"lat\": 0.0" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.0" +
                                        "}" +
                                    "]" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void isInRegion_should_return400_whenVerticesIsNull() throws Exception
    {
        String requestBody = "{" +
                                "\"position\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.0" +
                                "}," +
                                    "\"region\": {" +
                                        "\"name\": \"Name\"," +
                                        "\"vertices\": [" +

                                    "]" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }
    @Test
    public void isInRegion_should_return400_whenRegionIsNotClosed() throws Exception
    {
        String requestBody = "{" +
                                "\"position\": {" +
                                    "\"lng\": 0.0," +
                                    "\"lat\": 0.0" +
                                "}," +
                                "\"region\": {" +
                                    "\"name\": ," +
                                    "\"vertices\": [" +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.0" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.1" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.1," +
                                            "\"lat\": 0.0" +
                                        "}," +
                                        "{" +
                                            "\"lng\": 0.0," +
                                            "\"lat\": 0.2" +
                                        "}" +
                                    "]" +
                                "}" +
                            "}";

        this.mockMvc.perform(post("/api/v1/isInRegion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isBadRequest());
    }
}
