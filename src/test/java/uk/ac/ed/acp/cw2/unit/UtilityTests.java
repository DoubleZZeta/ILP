package uk.ac.ed.acp.cw2.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;
import uk.ac.ed.acp.cw2.data.Position;
import uk.ac.ed.acp.cw2.data.PositionsRequest;
import uk.ac.ed.acp.cw2.data.Region;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.util.ArrayList;
import java.util.Objects;

public class UtilityTests
{
    Utility utility = new Utility();

    @Mock
    Position position1;

    @Mock
    Position position2;

    @Mock
    Position vertex;

    @Mock
    ArrayList<Position> vertices;

    @InjectMocks
    Region region;

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
        when(vertices.get(0)).thenReturn(new Position(0.0,0.0));
        when(vertices.get(1)).thenReturn(new Position(0.1,0.1));
        when(vertices.get(2)).thenReturn(new Position(0.2,0.2));
        when(vertices.get(3)).thenReturn(new Position(0.0,0.0));
        when(vertices.size()).thenReturn(4);

        ArrayList<PositionsRequest> result = utility.getRegionEdges(region);

        for (int i = 0; i<result.size(); i++ )
        {
            assert(Objects.equals(result.get(i).getPosition1(), vertices.get(i%vertices.size())));
            assert(Objects.equals(result.get(i).getPosition2(), vertices.get((i+1)%vertices.size())));
        }
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

        boolean result = utility.isVertexOnEdge(vertex,positionsRequest);

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

        boolean result = utility.isVertexOnEdge(vertex,positionsRequest);

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

        boolean result = utility.isVertexOnEdge(vertex,positionsRequest);

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

        boolean result = utility.isVertexOnEdge(vertex,positionsRequest);

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

        boolean result = utility.isVertexOnEdge(vertex,positionsRequest);

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

        boolean result = utility.isVertexOnEdge(vertex,positionsRequest);

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

        boolean result = utility.isVertexOnEdge(vertex,positionsRequest);

        assert(!result);
    }

}
