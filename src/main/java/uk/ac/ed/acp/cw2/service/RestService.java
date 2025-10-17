package uk.ac.ed.acp.cw2.service;

import uk.ac.ed.acp.cw2.data.PositionAngleRequest;
import uk.ac.ed.acp.cw2.data.PositionRegionRequest;
import uk.ac.ed.acp.cw2.data.PositionsRequest;

public interface RestService
{
    Double distanceTo(PositionsRequest Request); //TODO public abstract???
    boolean isCloseTo(PositionsRequest Request);
    String nextPosition(PositionAngleRequest Request);
    boolean isInRegion(PositionRegionRequest Request);
}
