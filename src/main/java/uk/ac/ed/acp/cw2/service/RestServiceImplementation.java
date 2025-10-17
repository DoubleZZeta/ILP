package uk.ac.ed.acp.cw2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.data.*;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.util.ArrayList;

@Service
public class RestServiceImplementation implements RestService
{
    private final Utility utility;
    private static final Double unitLength = 0.00015;

    @Autowired
    public RestServiceImplementation(final Utility utility) {
        this.utility = utility;
    }

    @Override
    public Double distanceTo(PositionsRequest Request)
    {
        Position position1 = Request.getPosition1();
        Position position2 = Request.getPosition2();

        return utility.calculateDistance(position1,position2);
    }

    @Override
    public boolean isCloseTo(PositionsRequest Request)
    {
        Position position1 = Request.getPosition1();
        Position position2 = Request.getPosition2();

        Double distance = utility.calculateDistance(position1,position2);

        return distance.compareTo(unitLength) < 0;
    }

    @Override
    public String nextPosition(PositionAngleRequest Request) {
        Position start = Request.getStart();
        Double angle = Request.getAngle();

        angle = Math.toRadians(angle);
        Double lng = unitLength * Math.cos(angle) + start.getLng();
        Double lat = unitLength * Math.sin(angle) + start.getLat();

        return String.format("{ \"lng\": %.5f, \"lat\": %.5f }", lng, lat);
    }

    @Override
    public boolean isInRegion(PositionRegionRequest Request)
    {
        Position position = Request.getPosition();
        Region region = Request.getRegion();

        int count = 0;
        ArrayList<PositionsRequest> edges = utility.getRegionEdges(region);

        for(PositionsRequest edge : edges)
        {
            if (utility.isVertexOnEdge(position,edge))
            {
                return true;
            }
            else if(utility.isEdgeIntersectWithRay(position, edge))
            {
                count++;
            }
        }

        return count % 2 != 0;
    }

}
