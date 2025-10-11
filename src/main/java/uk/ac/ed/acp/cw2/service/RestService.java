package uk.ac.ed.acp.cw2.service;

import uk.ac.ed.acp.cw2.data.PositionAngleRequest;
import uk.ac.ed.acp.cw2.data.PositionRegionRequest;
import uk.ac.ed.acp.cw2.data.PositionsRequest;

public interface RestService
{
    public abstract String distanceTo(PositionsRequest Request);
    public abstract String isCloseTo(PositionsRequest Request);
    public abstract String nextPosition(PositionAngleRequest Request);
    public abstract String isInRegion(PositionRegionRequest Request);
}
