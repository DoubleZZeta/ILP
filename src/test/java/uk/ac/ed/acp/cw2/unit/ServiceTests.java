package uk.ac.ed.acp.cw2.unit;

import org.hibernate.validator.constraints.ModCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ed.acp.cw2.data.*;
import uk.ac.ed.acp.cw2.service.RestService;
import uk.ac.ed.acp.cw2.service.RestServiceImplementation;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.util.ArrayList;
import java.util.Objects;

import static org.mockito.Mockito.when;

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

    @Mock
    PositionRegionRequest positionRegionRequest;

    @Mock
    ArrayList<PositionsRequest> edges;

    @Mock

    Double angle;

    //Cannot mock wrapper types, String.class or Class.class
    PositionAngleRequest positionAngleRequest;

    //Cannot do mock injection for two parameters with same type, use regular DTO instead
    PositionsRequest positionsRequest;

    RestService restService;

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.openMocks(this);

        positionsRequest = new PositionsRequest(position1, position2);
        positionAngleRequest = new PositionAngleRequest(start, angle);
        restService = new RestServiceImplementation(utility);
    }

    @Test
    public void distanceTo_should_returnFiveDecimalPlacesNumer_when_calculatedDistanceIsFiveDecimalPlaces()
    {
//        when(position1.getLng()).thenReturn(0.0);
//        when(position1.getLat()).thenReturn(0.0);
//        when(position2.getLng()).thenReturn(0.0);
//        when(position2.getLat()).thenReturn(0.12345);
        when(utility.calculateDistance(position1, position2)).thenReturn(0.12345);

        String result = restService.distanceTo(positionsRequest);

        //Five decimal places
        assert(result.length() == 7);
        assert(Objects.equals(result, "0.12345"));
    }

    @Test
    public void testDistanceToWithRoundingRequiredInResult()
    {
//        when(position1.getLng()).thenReturn(0.0);
//        when(position1.getLat()).thenReturn(0.0);
//        when(position2.getLng()).thenReturn(1.0);
//        when(position2.getLat()).thenReturn(1.0);
        when(utility.calculateDistance(position1, position2)).thenReturn(Math.sqrt(2));

        String result = restService.distanceTo(positionsRequest);

        //Five decimal places
        assert(result.length() == 7);
        assert(Objects.equals(result, "1.41421"));
    }

    @Test
    public void testDistanceToWithPaddingRequiredInResult()
    {
//        when(position1.getLng()).thenReturn(0.0);
//        when(position1.getLat()).thenReturn(0.0);
//        when(position2.getLng()).thenReturn(0.0);
//        when(position2.getLat()).thenReturn(1.0);
        when(utility.calculateDistance(position1, position2)).thenReturn(1.0);


        String result = restService.distanceTo(positionsRequest);

        //Five decimal places
        assert(result.length() == 7);
        assert(Objects.equals(result, "1.00000"));
    }

    @Test
    public void isClosedTo_should_returnFalse_when_distanceIsEqualToThreshold()
    {
//        when(position1.getLng()).thenReturn(0.0);
//        when(position1.getLat()).thenReturn(0.0);
//        when(position2.getLng()).thenReturn(0.0);
//        when(position2.getLat()).thenReturn(0.00015);
        when(utility.calculateDistance(position1, position2)).thenReturn(0.00015);

        String result = restService.isCloseTo(positionsRequest);

        assert(result.equals("false"));
    }

    @Test
    public void isClosedTo_should_returnTrue_when_distanceIsLessThanThreshold()
    {
//        when(position1.getLng()).thenReturn(0.0);
//        when(position1.getLat()).thenReturn(0.0);
//        when(position2.getLng()).thenReturn(0.0);
//        when(position2.getLat()).thenReturn(0.00014);
        when(utility.calculateDistance(position1, position2)).thenReturn(0.00014);

        String result = restService.isCloseTo(positionsRequest);

        assert(result.equals("true"));
    }

    @Test
    public void isClosedTo_should_returnFalse_when_distanceIsAboveThreshold()
    {
//        when(position1.getLng()).thenReturn(0.0);
//        when(position1.getLat()).thenReturn(0.0);
//        when(position2.getLng()).thenReturn(0.0);
//        when(position2.getLat()).thenReturn(0.00016);
        when(utility.calculateDistance(position1, position2)).thenReturn(0.00016);

        String result = restService.isCloseTo(positionsRequest);

        assert(result.equals("false"));
    }

    @Test
    public void isClosedTo_should_returnFalse_when_distanceIsEqualToThresholdAfterRounding()
    {
//        when(position1.getLng()).thenReturn(0.0);
//        when(position1.getLat()).thenReturn(0.0);
//        when(position2.getLng()).thenReturn(0.0);
//        when(position2.getLat()).thenReturn(0.000149999);
        when(utility.calculateDistance(position1, position2)).thenReturn(0.000149999);

        String result = restService.isCloseTo(positionsRequest);

        assert(result.equals("false"));
    }

    @Test
    public void nextPosition_should_returnFiveDecimalCoordinates_when_calculatedNextPositionCoordinatesAreFiveDecimalPlaces()
    {
        when(start.getLng()).thenReturn(0.0);
        when(start.getLat()).thenReturn(0.0);
        positionAngleRequest.setAngle(0.0);

        String result = restService.nextPosition(positionAngleRequest);

        assert(Objects.equals(result, "{ lng: 0.00015, lat: 0.00000 }"));
    }

    @Test
    public void nextPosition_should_returnFiveDecimalCoordinates_when_calculatedNextPositionCoordinatesAreLessThanFiveDecimalPlaces()
    {
        when(start.getLng()).thenReturn(0.00085);
        when(start.getLat()).thenReturn(0.0);
        positionAngleRequest.setAngle(0.0);

        String result = restService.nextPosition(positionAngleRequest);

        assert(Objects.equals(result, "{ lng: 0.00100, lat: 0.00000 }"));
    }

    @Test
    public void nextPosition_should_returnFiveDecimalCoordinates_when_calculatedNextPositionCoordinatesAreAboveFiveDecimalPlaces()
    {
        when(start.getLng()).thenReturn(0.0);
        when(start.getLat()).thenReturn(0.0);
        positionAngleRequest.setAngle(22.5);

        String result = restService.nextPosition(positionAngleRequest);

        assert(Objects.equals(result, "{ lng: 0.00014, lat: 0.00006 }"));
    }

    //Do I need to test for all angles ...?

    @Test
    public void isInRegion_should_returnFalse_when_thePositionIsOutsideARegion()
    {
        //Position(-0.05,0.05)
        //Region(Position(0,0),Position(0,0.1),Position(0.1,0.1),Position(0.1,0),Position(0,0))

        ArrayList<PositionsRequest> edges =  new ArrayList<>();
        edges.add(new PositionsRequest(new Position(0.0,0.0), new Position(0.0,0.1)));
        edges.add(new PositionsRequest(new Position(0.0,0.1), new Position(0.1,0.1)));
        edges.add(new PositionsRequest(new Position(0.1,0.1), new Position(0.1,0.0)));
        edges.add(new PositionsRequest(new Position(0.1,0.0), new Position(0.0,0.0)));

        when(utility.getRegionEdges(positionRegionRequest.getRegion())).thenReturn(edges);
        when(utility.isVertexOnEdge(positionRegionRequest.getPosition(),edges.get(0))).thenReturn(false);
        when(utility.isVertexOnEdge(positionRegionRequest.getPosition(),edges.get(1))).thenReturn(false);
        when(utility.isVertexOnEdge(positionRegionRequest.getPosition(),edges.get(2))).thenReturn(false);
        when(utility.isVertexOnEdge(positionRegionRequest.getPosition(),edges.get(3))).thenReturn(false);
        when(utility.isEdgeIntersectWithRay(positionRegionRequest.getPosition(),edges.get(0))).thenReturn(true);
        when(utility.isEdgeIntersectWithRay(positionRegionRequest.getPosition(),edges.get(1))).thenReturn(false);
        when(utility.isEdgeIntersectWithRay(positionRegionRequest.getPosition(),edges.get(2))).thenReturn(true);
        when(utility.isEdgeIntersectWithRay(positionRegionRequest.getPosition(),edges.get(3))).thenReturn(false);

        String result = restService.isInRegion(positionRegionRequest);

        assert(result.equals("false"));
    }

    @Test
    public void isInRegion_should_returnFalse_when_thePositionIsOutsideARegionAndRayOverlapWithOneEdge()
    {
        //Position(-0.1,0.1)
        //Region(Position(0,0),Position(0,0.1),Position(0.1,0.1),Position(0.1,0),Position(0,0))
        when(edges.get(0)).thenReturn(positionsRequest);
//        when(edges.get(1)).thenReturn(new PositionsRequest(new Position(0.0,0.1), new Position(0.1,0.1)));
//        when(edges.get(2)).thenReturn(new PositionsRequest(new Position(0.0,0.1), new Position(0.1,0.0)));
//        when(edges.get(3)).thenReturn(new PositionsRequest(new Position(0.1,0.0), new Position(0.0,0.0)));

        when(utility.getRegionEdges(positionRegionRequest.getRegion())).thenReturn(edges);
        when(utility.isVertexOnEdge(positionRegionRequest.getPosition(),edges.get(0))).thenReturn(false);
//        when(utility.isVertexOnEdge(positionRegionRequest.getPosition(),edges.get(1))).thenReturn(false);
//        when(utility.isVertexOnEdge(positionRegionRequest.getPosition(),edges.get(2))).thenReturn(false);
//        when(utility.isVertexOnEdge(positionRegionRequest.getPosition(),edges.get(3))).thenReturn(false);
        when(utility.isEdgeIntersectWithRay(positionRegionRequest.getPosition(),edges.get(0))).thenReturn(true);
//        when(utility.isEdgeIntersectWithRay(positionRegionRequest.getPosition(),edges.get(1))).thenReturn(false);
//        when(utility.isEdgeIntersectWithRay(positionRegionRequest.getPosition(),edges.get(2))).thenReturn(true);
//        when(utility.isEdgeIntersectWithRay(positionRegionRequest.getPosition(),edges.get(3))).thenReturn(false);

        String result = restService.isInRegion(positionRegionRequest);

        assert(result.equals("false"));
    }


}
