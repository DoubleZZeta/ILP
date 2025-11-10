package uk.ac.ed.acp.cw2.service;

import uk.ac.ed.acp.cw2.data.Drone;
import uk.ac.ed.acp.cw2.data.DronesServicePoint;

import java.util.ArrayList;

public interface DataFetchService
{
    ArrayList<Drone> getDrones();
    ArrayList<DronesServicePoint> getDronesServicePoints();
}
