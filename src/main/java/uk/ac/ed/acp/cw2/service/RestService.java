package uk.ac.ed.acp.cw2.service;

import uk.ac.ed.acp.cw2.data.PositionAngleRequest;
import uk.ac.ed.acp.cw2.data.PositionRegionRequest;
import uk.ac.ed.acp.cw2.data.PositionsRequest;

public interface RestService
{
    String distanceTo(PositionsRequest Request); //TODO public abstract???
    String isCloseTo(PositionsRequest Request);
    String nextPosition(PositionAngleRequest Request);
    String isInRegion(PositionRegionRequest Request);
}
