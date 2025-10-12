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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class UtilityTests
{
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

    PositionsRequest positionsRequest;

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.openMocks(this);

        positionsRequest = new PositionsRequest(position1, position2);
    }

    @Test
    public void testGetRightPositionWithDifferentPositions()
    {
        when(position1.getLat()).thenReturn(-1.0);
        when(position1.getLng()).thenReturn(-1.0);
        when(position2.getLat()).thenReturn(2.0);
        when(position2.getLng()).thenReturn(2.0);

        Position result = Utility.getRightPosition(positionsRequest);

        assert(Objects.equals(position2.getLat(), result.getLat()));
        assert(Objects.equals(position2.getLng(), result.getLng()));
    }

    @Test
    public void testGetRightPositionWithSamePositions()
    {
        when(position1.getLat()).thenReturn(0.0);
        when(position1.getLng()).thenReturn(0.0);
        when(position2.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.0);

        Position result = Utility.getRightPosition(positionsRequest);

        assert(Objects.equals(position2.getLat(), result.getLat()));
        assert(Objects.equals(position2.getLng(), result.getLng()));
    }

    @Test
    public void testGetUpperPositionWithDifferentPositions()
    {
        when(position1.getLat()).thenReturn(-1.0);
        when(position1.getLng()).thenReturn(-1.0);
        when(position2.getLat()).thenReturn(2.0);
        when(position2.getLng()).thenReturn(2.0);

        Position result = Utility.getUpperPosition(positionsRequest);

        assert(Objects.equals(position2.getLat(), result.getLat()));
        assert(Objects.equals(position2.getLng(), result.getLng()));
    }

    @Test
    public void testGetUpperPositionWithSamePositions()
    {
        when(position1.getLat()).thenReturn(0.0);
        when(position1.getLng()).thenReturn(0.0);
        when(position2.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.0);

        Position result = Utility.getUpperPosition(positionsRequest);

        assert(Objects.equals(position2.getLat(), result.getLat()));
        assert(Objects.equals(position2.getLng(), result.getLng()));
    }

    @Test
    public void testGetLowerPositionWithDifferentPositions()
    {
        when(position1.getLat()).thenReturn(-1.0);
        when(position1.getLng()).thenReturn(-1.0);
        when(position2.getLat()).thenReturn(2.0);
        when(position2.getLng()).thenReturn(2.0);

        Position result = Utility.getLowerPosition(positionsRequest);

        assert(Objects.equals(position1.getLat(), result.getLat()));
        assert(Objects.equals(position1.getLng(), result.getLng()));
    }

    @Test
    public void testGetLowerPositionWithSamePositions()
    {
        when(position1.getLat()).thenReturn(0.0);
        when(position1.getLng()).thenReturn(0.0);
        when(position2.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.0);

        Position result = Utility.getLowerPosition(positionsRequest);

        assert(Objects.equals(position1.getLat(), result.getLat()));
        assert(Objects.equals(position1.getLng(), result.getLng()));
    }

    @Test
    public void testPositionToJSONStringWith5DecimalPlacesNumbers()
    {
        when(position1.getLng()).thenReturn(0.00001);
        when(position1.getLat()).thenReturn(0.00002);

        String result = Utility.positionToJSONString(position1);
        String expected = "{ lng: 0.00001, lat: 0.00002 }";

        assert(Objects.equals(result, expected));
    }

    @Test
    public void testPositionToJSONStringWithPaddingAndRoundingRequired()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.000149);

        String result = Utility.positionToJSONString(position1);
        String expected = "{ lng: 0.00000, lat: 0.00015 }";

        assert(Objects.equals(result, expected));
    }

    @Test
    public void testCalculateDistanceWithDifferentPositions()
    {
        when(position1.getLng()).thenReturn(0.1);
        when(position1.getLat()).thenReturn(-0.2);
        when(position2.getLng()).thenReturn(0.1);
        when(position2.getLat()).thenReturn(-0.3);

        Double result = Utility.calculateDistance(position1, position2);

        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        result = Double.parseDouble(df.format(result));

        assert(0.10000 == result);
    }

    @Test
    public void testCalculateDistanceWithSamePositions()
    {
        when(position1.getLng()).thenReturn(0.1);
        when(position1.getLat()).thenReturn(0.2);
        when(position2.getLng()).thenReturn(0.1);
        when(position2.getLat()).thenReturn(0.2);

        Double result = Utility.calculateDistance(position1, position2);

        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        result = Double.parseDouble(df.format(result));

        assert(0.00000 == result);
    }

    @Test
    public void testGetRegionEdges()
    {
        when(vertices.get(0)).thenReturn(new Position(0.0,0.0));
        when(vertices.get(1)).thenReturn(new Position(0.1,0.1));
        when(vertices.get(2)).thenReturn(new Position(0.2,0.2));
        when(vertices.get(3)).thenReturn(new Position(0.0,0.0));
        when(vertices.size()).thenReturn(4);

        ArrayList<PositionsRequest> result = Utility.getRegionEdges(region);

        for (int i = 0; i<result.size(); i++ )
        {
            assert(Objects.equals(result.get(i).getPosition1(), vertices.get(i%vertices.size())));
            assert(Objects.equals(result.get(i).getPosition2(), vertices.get((i+1)%vertices.size())));
        }
    }
    @Test
    public void testIsEdgeIntersectWithRayWithEdgeOnRightAndIntersects()
    {
        when(position1.getLng()).thenReturn(0.1);
        when(position1.getLat()).thenReturn(0.-1);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = Utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(result);
    }

    @Test
    public void testIsEdgeIntersectWithRayWithEdgeOnTheLeftOfVertex()
    {
        when(position1.getLng()).thenReturn(0.-1);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.-2);
        when(position2.getLat()).thenReturn(0.0);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = Utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void testIsEdgeIntersectWithRayWithEdgeHigherThanVertex()
    {
        when(position1.getLng()).thenReturn(0.1);
        when(position1.getLat()).thenReturn(0.1);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.2);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = Utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void testIsEdgeIntersectWithRayWithEdgeLowerThanVertex()
    {
        when(position1.getLng()).thenReturn(0.1);
        when(position1.getLat()).thenReturn(0.-1);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.-2);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = Utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(!result);
    }

    @Test
    public void testIsEdgeIntersectWithRayWithOnePositionOverlapWithVertex()
    {
        when(position1.getLng()).thenReturn(0.0);
        when(position1.getLat()).thenReturn(0.0);
        when(position2.getLng()).thenReturn(0.2);
        when(position2.getLat()).thenReturn(0.-2);
        when(vertex.getLng()).thenReturn(0.0);
        when(vertex.getLat()).thenReturn(0.0);

        boolean result = Utility.isEdgeIntersectWithRay(vertex,positionsRequest);

        assert(result);
    }



}
