package uk.ac.ed.acp.cw2.utility;

import uk.ac.ed.acp.cw2.data.Position;
import uk.ac.ed.acp.cw2.data.PositionsRequest;
import uk.ac.ed.acp.cw2.data.Region;

import java.util.ArrayList;

public class Utility
{
    public static Position getRightPosition(PositionsRequest edge)
    {
        Position position1 = edge.getPosition1();
        Position position2 = edge.getPosition2();

        if (position1.getLng() >= position2.getLng())
        {
            return position1;
        }
        else
        {
            return position2;
        }
    }

    public static Position getUpperPosition(PositionsRequest edge)
    {
        Position position1 = edge.getPosition1();
        Position position2 = edge.getPosition2();

        if (position1.getLat() >= position2.getLat())
        {
            return position1;
        }
        else
        {
            return position2;
        }
    }

    public static Position getLowerPosition(PositionsRequest edge)
    {
        Position position1 = edge.getPosition1();
        Position position2 = edge.getPosition2();

        if (position1.getLat() <= position2.getLat())
        {
            return position1;
        }
        else
        {
            return position2;
        }
    }

    public static String positionToJSONString(Position position)
    {
        return String.format("{ lng: %.5f, lat: %.5f }", position.getLng(), position.getLat());
    }

    public static Double calculateDistance(Position position1, Position position2)
    {
        Double lng1 = position1.getLng();
        Double lat1 = position1.getLat();
        Double lng2 = position2.getLng();
        Double lat2 = position2.getLat();

        Double lngDiff = lng2 - lng1;
        Double latDiff = lat2 - lat1;

        return Math.sqrt(Math.pow(lngDiff, 2) + Math.pow(latDiff, 2));
    }

    public static ArrayList<PositionsRequest> getRegionEdges(Region region)
    {
        ArrayList<Position> vertices = region.getVertices();

        vertices.add(vertices.getFirst());
        ArrayList<PositionsRequest> edges = new ArrayList<>();

        for (int i = 0; i < vertices.size() - 1; i++)
        {
            Position start = vertices.get(i);
            Position end = vertices.get(i + 1);
            edges.add(new PositionsRequest(start, end));
        }

        return edges;
    }

    public static boolean isEdgeIntersectWithRay(Position vertex, PositionsRequest edge)
    {
        Position rightPosition = getRightPosition(edge);
        Position lowerPosition = getLowerPosition(edge);
        Position upperPosition = getUpperPosition(edge);

        boolean EdgeOnRightOfVertex = true;
        boolean EdgeIntersectWithRay = true;

        if(rightPosition.getLng() < vertex.getLng())
        {
            EdgeOnRightOfVertex = false;
        }

        if(upperPosition.getLat() < vertex.getLat() || lowerPosition.getLat() > vertex.getLat())
        {
            EdgeIntersectWithRay = false;
        }

        return EdgeOnRightOfVertex && EdgeIntersectWithRay;
    }
}
