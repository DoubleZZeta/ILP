package uk.ac.ed.acp.cw2.service;

import uk.ac.ed.acp.cw2.data.*;

import java.util.ArrayList;

public interface DataFetchService
{
    ArrayList<ServicePoint> getServicePoints();
    ArrayList<RestrictedArea> getRestrictedAreas();
    ArrayList<Drone> getDrones();
    ArrayList<ServicePointDrones> getServicePointsDrones();


}
