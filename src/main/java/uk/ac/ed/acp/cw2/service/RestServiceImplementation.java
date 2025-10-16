package uk.ac.ed.acp.cw2.service;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.data.*;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
    public String distanceTo(PositionsRequest Request)
    {
        Position position1 = Request.getPosition1();
        Position position2 = Request.getPosition2();

        Double distance = utility.calculateDistance(position1,position2);

        return String.format("%.5f", distance);
    }

    @Override
    public String isCloseTo(PositionsRequest Request)
    {
        Position position1 = Request.getPosition1();
        Position position2 = Request.getPosition2();

        Double distance = utility.calculateDistance(position1,position2);
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        distance = Double.parseDouble(df.format(distance));

        boolean result = distance.compareTo(unitLength) < 0;

        return result ? "true" : "false";
    }

    @Override
    public String nextPosition(PositionAngleRequest Request) {
        Position start = Request.getStart();
        Double angle = Request.getAngle();

        angle = Math.toRadians(angle);
        Double lng = unitLength * Math.cos(angle) + start.getLng();
        Double lat = unitLength * Math.sin(angle) + start.getLat();

        return String.format("{ lng: %.5f, lat: %.5f }", lng, lat);
    }

    @Override
    public String isInRegion(PositionRegionRequest Request)
    {
        Position position = Request.getPosition();
        Region region = Request.getRegion();

        int count = 0;
        ArrayList<PositionsRequest> edges = utility.getRegionEdges(region);

        for(PositionsRequest edge : edges)
        {
            if (utility.isVertexOnEdge(position,edge))
            {
                return "true";
            }
            else if(utility.isEdgeIntersectWithRay(position, edge))
            {
                count++;
            }
        }

        return count % 2 != 0 ? "true" : "false";
    }

}
