package uk.ac.ed.acp.cw2.service;

import uk.ac.ed.acp.cw2.data.PositionAngleRequest;
import uk.ac.ed.acp.cw2.data.PositionRegionRequest;
import uk.ac.ed.acp.cw2.data.PositionsRequest;

/**
 * Service interface that is to be called by the controller.
 */
public interface RestService
{
    Double distanceTo(PositionsRequest Request);
    boolean isCloseTo(PositionsRequest Request);
    String nextPosition(PositionAngleRequest Request);
    boolean isInRegion(PositionRegionRequest Request);
}
