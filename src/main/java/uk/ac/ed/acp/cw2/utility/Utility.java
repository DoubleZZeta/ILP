package uk.ac.ed.acp.cw2.utility;

import org.springframework.stereotype.Component;
import uk.ac.ed.acp.cw2.data.Position;
import uk.ac.ed.acp.cw2.data.PositionsRequest;
import uk.ac.ed.acp.cw2.data.Region;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Utility class contains helper functions.
 * Helper functions are being called by the Service Implementation class,
 * which makes the logic in the service implementation class easier.
 */
@Component
public class Utility
{
    //Calculate the Euclidean distance between two given positions
    public Double calculateDistance(Position position1, Position position2)
    {
        Double lng1 = position1.getLng();
        Double lat1 = position1.getLat();
        Double lng2 = position2.getLng();
        Double lat2 = position2.getLat();

        double lngDiff = lng2 - lng1;
        double latDiff = lat2 - lat1;

        Double distance = Math.sqrt(Math.pow(lngDiff, 2) + Math.pow(latDiff, 2));

        //Rounding the result to five decimal places before returning
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.HALF_EVEN);

        return  Double.parseDouble(df.format(distance));
    }

    //Return list of edges based on the given vertices
    public ArrayList<PositionsRequest> getRegionEdges(Region region)
    {
        ArrayList<Position> vertices = region.getVertices();

        //Append the first vertex to the end to ensure the last edge closes the region
        vertices.add(vertices.getFirst());
        ArrayList<PositionsRequest> edges = new ArrayList<>();

        //Make positon pairs of the adjacent vertices
        for (int i = 0; i < vertices.size() - 1; i++)
        {
            Position start = vertices.get(i);
            Position end = vertices.get(i + 1);
            edges.add(new PositionsRequest(start, end));
        }

        return edges;
    }

    //Checking if the given position is on an edge of the region
    public boolean isPositionOnEdge(Position vertex, PositionsRequest edge)
    {
        Double x1 = edge.getPosition1().getLng();
        Double y1 = edge.getPosition1().getLat();
        Double x2 = edge.getPosition2().getLng();
        Double y2 = edge.getPosition2().getLat();
        Double xv = vertex.getLng();
        Double yv = vertex.getLat();

        //Gradient check
        if (Math.abs((y2-y1)*(xv-x1) - (yv-y1)*(x2-x1)) <= 1e-9)
        {
            //Boundary checks
            if (Math.min(y1, y2) <= yv && yv <= Math.max(y1, y2))
            {
                if (Math.min(x1, x2) <= xv && xv <= Math.max(x1, x2))
                {
                    return true;
                }
            }
        }
        return false;
    }

    //Check if the horizontal ray is intersecting with an edge of the region
    public boolean isEdgeIntersectWithRay(Position vertex, PositionsRequest edge)
    {
        Double x1 = edge.getPosition1().getLng();
        Double y1 = edge.getPosition1().getLat();
        Double x2 = edge.getPosition2().getLng();
        Double y2 = edge.getPosition2().getLat();
        Double xv = vertex.getLng();
        Double yv = vertex.getLat();

        //We don't take the horizontal line into consideration
        //Also, if the vertex is on the right of the right most point of the edge then there is no intersection
        if(Objects.equals(y1, y2) || Math.max(x1,x2) <= xv)
        {
            return false;
        }

        //Then consider when the ray is between two points of the edge
        if(Math.min(y1,y2) <= yv && yv <= Math.max(y1,y2))
        {
            //In the case where line is vertical, there must be an intersection
            if(Objects.equals(x1, x2))
            {
                return true;
             }
            //Otherwise we calculate the x coordinate of the intersection
            else
            {
                double m = (y2-y1)/(x2-x1);
                double xi = (yv - y1)/m + x1;
                return xv < xi;
            }
        }

        //If the ray is lower or higher than the edge, return false
        return false;
    }
}
