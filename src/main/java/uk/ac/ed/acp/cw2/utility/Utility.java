package uk.ac.ed.acp.cw2.utility;

import org.springframework.stereotype.Component;
import uk.ac.ed.acp.cw2.data.Position;
import uk.ac.ed.acp.cw2.data.PositionsRequest;
import uk.ac.ed.acp.cw2.data.Region;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

@Component
public class Utility
{
    public boolean isVertexOnEdge(Position vertex, PositionsRequest edge)
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


    public Double calculateDistance(Position position1, Position position2)
    {
        Double lng1 = position1.getLng();
        Double lat1 = position1.getLat();
        Double lng2 = position2.getLng();
        Double lat2 = position2.getLat();

        double lngDiff = lng2 - lng1;
        double latDiff = lat2 - lat1;

        Double distance = Math.sqrt(Math.pow(lngDiff, 2) + Math.pow(latDiff, 2));
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.HALF_EVEN);

        return  Double.parseDouble(df.format(distance));
    }

    public ArrayList<PositionsRequest> getRegionEdges(Region region)
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
        return false;
    }
}
