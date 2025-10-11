package uk.ac.ed.acp.cw2.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ed.acp.cw2.data.Position;
import uk.ac.ed.acp.cw2.data.PositionsRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DistanceToTests
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDistanceToStatus200() throws Exception
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
    void testDistanceToMoreGivenDataStatus200() throws Exception
    {
        String requestBody = "{" +
                                "\"position1\": {" +
                                    "\"lng\": -3.192473," +
                                    "\"lat\": 55.946233" +
                                "}," +
                                "\"position3\": {" +
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
                .andExpect(status().isOk());
    }

    @Test
    void testDistanceToOnePositionMissingStatus400() throws Exception
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


    //TODO may also want to test the case where lng/lat missing
    //TODO consider having that in the unit tests???
    @Test
    void testDistanceToOnePositionIsNullStatus400() throws Exception
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
    void testDistanceToInvalidFieldNameStatus400() throws Exception
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
}
