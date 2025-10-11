package uk.ac.ed.acp.cw2.service;

import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.data.*;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.util.ArrayList;

@Service
public class RestServiceImplementation implements RestService
{
    private static final Double unitLength = 0.00015;

    @Override
    public String distanceTo(PositionsRequest Request)
    {
        Position position1 = Request.getPosition1();
        Position position2 = Request.getPosition2();

        Double distance = Utility.calculateDistance(position1,position2);

        return String.format("%.5f", distance);
    }

    @Override
    public String isCloseTo(PositionsRequest Request)
    {
        Position position1 = Request.getPosition1();
        Position position2 = Request.getPosition2();

        Double distance = Utility.calculateDistance(position1,position2);
        boolean result = distance.compareTo(unitLength) <= 0;

        return result ? "true" : "false";
    }

    @Override
    public String nextPosition(PositionAngleRequest Request)
    {
        Position start = Request.getStart();
        Double angle = Request.getAngle();

        angle = Math.toRadians(angle);
        Double lng = unitLength * Math.cos(angle) + start.getLng();
        Double lat = unitLength * Math.sin(angle) + start.getLat();

        return Utility.PostionToJSONString(new Position(lng, lat));
    }

    @Override
    public String isInRegion(PositionRegionRequest Request)
    {
        Position position = Request.getPosition();
        Region region = Request.getRegion();

        int count = 0;
        ArrayList<PositionsRequest> edges = Utility.GetRegionEdges(region);

        for(PositionsRequest edge : edges)
        {
            if(Utility.isEdgeIntersectWithRay(position, edge))
            {
                count++;
            }
        }

        return count % 2 != 0 ? "true" : "false";
    }

}
