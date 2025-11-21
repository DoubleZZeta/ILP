package uk.ac.ed.acp.cw2.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import uk.ac.ed.acp.cw2.data.Position;
import uk.ac.ed.acp.cw2.data.PositionRegionRequest;
import uk.ac.ed.acp.cw2.data.PositionsRequest;
import uk.ac.ed.acp.cw2.data.Region;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.util.ArrayList;
import java.util.Objects;
/**
 * Test class that conduct unit test for the Utility class.
 * Mocks some of the DTOs to ensure accurate results.
 */
public class UtilityTests
{
    Utility utility = new Utility(new ObjectMapper());

    @Mock
    Position position1;

    @Mock
    Position position2;

    @Mock
    Position vertex;

    PositionRegionRequest positionRegionRequest;

    //Can't do mock injection for two parameters with same type, use regular DTO instead
    PositionsRequest positionsRequest;

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.openMocks(this);
        positionsRequest = new PositionsRequest(position1, position2);

    }

    @Test
    public void calculateDistance_should_returnCorrectValueWhenRoundedUpToFiveDecimalPlaces_whenDifferentPositionsAreGiven()
    {
        when(position1.getLng()).thenReturn(0.1);
        when(position1.getLat()).thenReturn(-0.2);
        when(position2.getLng()).thenReturn(0.1);
        when(position2.getLat()).thenReturn(-0.3);

        Double result = utility.calculateDistance(position1, position2);

        assert(0.10000 == result);
    }

    @Test
    public void calculateDistance_should_returnZero_whenSamePositionsAreGiven()
    {
        when(position1.getLng()).thenReturn(0.1);
        when(position1.getLat()).thenReturn(0.2);
        when(position2.getLng()).thenReturn(0.1);
        when(position2.getLat()).thenReturn(0.2);

        Double result = utility.calculateDistance(position1, position2);

        assert(0.00000 == result);
    }

    @Test
    public void getRegionEdges_should_returnCorrectListOfEdges_whenValidVerticesAreGiven()
    {

        ArrayList<Position> vertices = new ArrayList<>();
        vertices.add(new Position(0.0, 0.0));
        vertices.add(new Position(0.1, 0.1));
        vertices.add(new Position(0.2, 0.2));
        vertices.add(new Position(0.0, 0.0));

        Region region = new Region("test-region", vertices);
        ArrayList<PositionsRequest> result = utility.getRegionEdges(region);

        assert(result.size() == 4);

        assert(vertices.get(0) == result.get(0).getPosition1());
        assert(vertices.get(1) == result.get(0).getPosition2());

        assert(vertices.get(1) == result.get(1).getPosition1());
        assert(vertices.get(2) == result.get(1).getPosition2());

        assert(vertices.get(2) == result.get(2).getPosition1());
        assert(vertices.get(3) == result.get(2).getPosition2());

        assert(vertices.get(3) == result.get(3).getPosition1());
        assert(vertices.get(0) == result.get(3).getPosition2());
    }
    @Test
    public void isEdgeIntersectWithRay_should_returnTrue_whenVertexIsOnTheLeftOfTheEdgeAndIntersectWithRay()
    {
        when(position1.getLng()).thenReturn(0.1);
        when(position1.getLat()).thenReturn(0.-1);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(result);
    }

    @Test
    public void isEdgeIntersectWithRay_should_returnFalse_whenTheVertexIsOnTheRightOfTheEdge()
    {
        when(position1.getLng()).thenReturn(0.-1);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.-2);
        when(position2.getLat()).thenReturn(0.1);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void isEdgeIntersectWithRay_should_returnFalse_whenTheVertexIsBelowTheWholeEdge()
    {
        when(position1.getLng()).thenReturn(0.1);
        when(position1.getLat()).thenReturn(0.1);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void isEdgeIntersectWithRay_should_returnFalse_whenTheVertexIsAboveTheWholeEdge()
    {
        when(position1.getLng()).thenReturn(0.1);
        when(position1.getLat()).thenReturn(0.-1);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.-2);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void isEdgeIntersectWithRay_should_returnFalse_whenWithTheVertexOverlapWithOneEndPointOfTheEdge()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(-0.2);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void isEdgeIntersectWithRay_should_returnTrue_whenTheVertexHasTheSameYCoordinateWithOneEndPointOfTheEdge()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(-0.2);
        when(vertex.getLng()).thenReturn(-0.1);
        when(vertex.getLat()).thenReturn(-0.2);

        boolean result = utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(result);
    }

    @Test
    public void isEdgeIntersectWithRay_should_returnFalse_whenTheVertexIsOnTheEdge()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.1);
        when(vertex.getLat()).thenReturn(0.1);

        boolean result = utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void isEdgeIntersectWithRay_should_returnFalse_whenWithTheVertexOverlapWithOneEndPointOfTheEdgeAndEdgeIsVertical()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.0);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.2);

        boolean result = utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void isEdgeIntersectWithRay_should_returnTrue_whenTheVertexHasTheSameYCoordinateWithOneEndPointOfTheEdgeAndEdgeIsVertical()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.0);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(-0.1);
        when(vertex.getLat()).thenReturn(0.2);

        boolean result = utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(result);
    }

    @Test
    public void isEdgeIntersectWithRay_should_returnFalse_whenTheVertexIsOnTheEdgeAndEdgeIsVertical()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.0);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.1);

        boolean result = utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(!result);
    }


    @Test
    public void isEdgeIntersectWithRay_should_returnFalse_whenTheEdgeIsHorizontal()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.1);
        when(position2.getLng()).thenReturn(0.1);
        when(position2.getLat()).thenReturn(0.1);
        when(vertex.getLng()).thenReturn(-0.1);
        when(vertex.getLat()).thenReturn(0.1);

        boolean result = utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void isVertexOnEdge_should_returnTrue_whenTheVertexIsOnEdge()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.1);
        when(vertex.getLat()).thenReturn(0.1);

        boolean result = utility.isPositionOnEdge(vertex,positionsRequest);

        assert(result);
    }

    @Test
    public void isVertexOnEdge_should_returnTrue_whenTheVertexIsOnEdgeAndEdgeIsVertical()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.0);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.1);

        boolean result = utility.isPositionOnEdge(vertex,positionsRequest);

        assert(result);
    }

    @Test
    public void isVertexOnEdge_should_returnTrue_whenTheVertexIsOnEdgeAndEdgeIsHorizontal()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.0);
        when(vertex.getLng()).thenReturn(0.1);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = utility.isPositionOnEdge(vertex,positionsRequest);

        assert(result);
    }

    @Test
    public void isVertexOnEdge_should_returnTrue_whenTheVertexIsAboveTheWholeEdge()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.1);
        when(vertex.getLat()).thenReturn(0.2);

        boolean result = utility.isPositionOnEdge(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void isVertexOnEdge_should_returnTrue_whenTheVertexIsBelowTheWholeEdge()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.1);
        when(vertex.getLat()).thenReturn(0.-2);

        boolean result = utility.isPositionOnEdge(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void isVertexOnEdge_should_returnTrue_whenTheVertexIsOnTheLeftOfTheWholeEdge()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(-0.1);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = utility.isPositionOnEdge(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void isVertexOnEdge_should_returnTrue_whenTheVertexIsOnTheRightOfTheWholeEdge()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.3);
        when(vertex.getLat()).thenReturn(0.3);

        boolean result = utility.isPositionOnEdge(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void isInRegion_should_returnFalse_when_theNumberOfIntersectionWithRayIsEvenAndThePositionIsNotOnRegion()
    {
        ArrayList<Position> vertices = new ArrayList<>();
        vertices.add(new Position(0.0, 0.0));
        vertices.add(new Position(0.1, 0.0));
        vertices.add(new Position(0.1, 0.1));
        vertices.add(new Position(0.0, 0.1));
        vertices.add(new Position(0.0, 0.0));
        Region region = new Region("",vertices);
        PositionRegionRequest positionRegionRequest = new PositionRegionRequest(new Position(-0.05,0.05),region);

        boolean result = utility.isInRegion(positionRegionRequest);

        assert(!result);
    }

    @Test
    public void isInRegion_should_returnTrue_when_theNumberOfIntersectionWithRayIsOddAndThePositionIsNotOnRegion()
    {
        ArrayList<Position> vertices = new ArrayList<>();
        vertices.add(new Position(0.0, 0.0));
        vertices.add(new Position(0.1, 0.0));
        vertices.add(new Position(0.1, 0.1));
        vertices.add(new Position(0.0, 0.1));
        vertices.add(new Position(0.0, 0.0));
        Region region = new Region("",vertices);
        PositionRegionRequest positionRegionRequest = new PositionRegionRequest(new Position(0.05,0.05),region);

        boolean result = utility.isInRegion(positionRegionRequest);

        assert(result);
    }

    @Test
    public void isInRegion_should_returnTrue_when_thePositionIsOnRegionAndNumberOfIntersectionIsEven()
    {
        ArrayList<Position> vertices = new ArrayList<>();
        vertices.add(new Position(0.0, 0.0));
        vertices.add(new Position(0.1, 0.0));
        vertices.add(new Position(0.1, 0.1));
        vertices.add(new Position(0.0, 0.1));
        vertices.add(new Position(0.0, 0.0));
        Region region = new Region("",vertices);
        PositionRegionRequest positionRegionRequest = new PositionRegionRequest(new Position(0.0,0.05),region);

        boolean result = utility.isInRegion(positionRegionRequest);

        assert(result);
    }

    @Test
    public void isInRegion_should_returnTrue_when_thePositionIsOnRegionAndNumberOfIntersectionIsOdd()
    {
        ArrayList<Position> vertices = new ArrayList<>();
        vertices.add(new Position(0.0, 0.0));
        vertices.add(new Position(0.1, 0.0));
        vertices.add(new Position(0.1, 0.1));
        vertices.add(new Position(0.0, 0.1));
        vertices.add(new Position(0.0, 0.0));
        Region region = new Region("",vertices);
        PositionRegionRequest positionRegionRequest = new PositionRegionRequest(new Position(0.1,0.05),region);

        boolean result = utility.isInRegion(positionRegionRequest);

        assert(result);
    }

    @Test
    public void isInRegion_should_returnTrue_when_thePositionIsOnOneVertexOfTheRegion()
    {
        ArrayList<Position> vertices = new ArrayList<>();
        vertices.add(new Position(0.0, 0.0));
        vertices.add(new Position(0.1, 0.0));
        vertices.add(new Position(0.1, 0.1));
        vertices.add(new Position(0.0, 0.1));
        vertices.add(new Position(0.0, 0.0));
        Region region = new Region("",vertices);
        PositionRegionRequest positionRegionRequest = new PositionRegionRequest(new Position(0.1,0.1),region);

        boolean result = utility.isInRegion(positionRegionRequest);

        assert(result);
    }
}
