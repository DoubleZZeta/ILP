package uk.ac.ed.acp.cw2.unitCW1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.ac.ed.acp.cw2.data.*;
import uk.ac.ed.acp.cw2.service.RestService;
import uk.ac.ed.acp.cw2.service.RestServiceImplementation;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.util.Objects;

import static org.mockito.Mockito.when;

/**
 * Test class that conduct unit test for the service interface and implementation class
 * Mocks the Utility class and part of the DTOs to ensure accurate results.
 */
public class ServiceTests
{

    @Mock
    Utility utility;

    @Mock
    Position position1;

    @Mock
    Position position2;

    @Mock
    Position start;

    Double angle;

    //Cannot mock primitive types, wrapper types, String.class or Class.class
    PositionAngleRequest positionAngleRequest;

    //Cannot do mock injection for two parameters with same type, use regular DTO instead
    PositionsRequest positionsRequest;

    RestService restService;

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.openMocks(this);

        positionsRequest = new PositionsRequest(position1, position2);
        positionAngleRequest = new PositionAngleRequest(start,angle);
        restService = new RestServiceImplementation(utility);
    }

    @Test
    public void distanceTo_should_returnTheResultOfCalculateDistance()
    {
        when(utility.calculateDistance(position1, position2)).thenReturn(0.12345);

        Double result = restService.distanceTo(positionsRequest);

        //Five decimal places
        assert(result.toString().length() == 7);
        assert(result == 0.12345);
    }

    @Test
    public void isClosedTo_should_returnFalse_when_distanceIsEqualToThreshold()
    {
        when(utility.calculateDistance(position1, position2)).thenReturn(0.00015);

        boolean result = restService.isCloseTo(positionsRequest);

        System.out.println();

        assert(!result);
    }

    @Test
    public void isClosedTo_should_returnTrue_when_distanceIsLessThanThreshold()
    {

        when(utility.calculateDistance(position1, position2)).thenReturn(0.00014);

        boolean result = restService.isCloseTo(positionsRequest);

        assert(result);
    }

    @Test
    public void isClosedTo_should_returnFalse_when_distanceIsAboveThreshold()
    {
        when(utility.calculateDistance(position1, position2)).thenReturn(0.00016);

        boolean result = restService.isCloseTo(positionsRequest);

        assert(!result);
    }

    @Test
    public void nextPosition_should_returnFiveDecimalCoordinates_when_calculatedNextPositionCoordinatesAreFiveDecimalPlaces()
    {
        when(start.getLng()).thenReturn(0.0);
        when(start.getLat()).thenReturn(0.0);
        positionAngleRequest.setAngle(0.0);

        String result = restService.nextPosition(positionAngleRequest);

        assert(Objects.equals(result, "{ \"lng\": 0.00015, \"lat\": 0.00000 }"));
    }

    @Test
    public void nextPosition_should_returnFiveDecimalCoordinates_when_calculatedNextPositionCoordinatesAreLessThanFiveDecimalPlaces()
    {
        when(start.getLng()).thenReturn(0.00085);
        when(start.getLat()).thenReturn(0.0);
        positionAngleRequest.setAngle(0.0);

        String result = restService.nextPosition(positionAngleRequest);

        assert(Objects.equals(result, "{ \"lng\": 0.00100, \"lat\": 0.00000 }"));
    }

    @Test
    public void nextPosition_should_returnFiveDecimalCoordinates_when_calculatedNextPositionCoordinatesAreAboveFiveDecimalPlaces()
    {
        when(start.getLng()).thenReturn(0.0);
        when(start.getLat()).thenReturn(0.0);
        positionAngleRequest.setAngle(22.5);

        String result = restService.nextPosition(positionAngleRequest);

        assert(Objects.equals(result, "{ \"lng\": 0.00014, \"lat\": 0.00006 }"));
    }


}
