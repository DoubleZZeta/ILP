package uk.ac.ed.acp.cw2.service;

import uk.ac.ed.acp.cw2.data.*;

import java.util.ArrayList;

/**
 * Service interface that is to be called by the controller.
 */
public interface RestService
{
    Double distanceTo(PositionsRequest Request);
    boolean isCloseTo(PositionsRequest Request);
    String nextPosition(PositionAngleRequest Request);
    boolean isInRegion(PositionRegionRequest Request);
    ArrayList<Integer> droneWithCooling(ArrayList<Drone> drones, boolean state);
    Drone droneDetails (ArrayList<Drone> drones, Integer droneId);
    ArrayList<Integer> query (ArrayList<Drone> drones, ArrayList<QueryRequest> queries);
    ArrayList<Integer> queryAvailableDrones (ArrayList<Drone> drones, ArrayList<ServicePointDrones> servicePointDrones, ArrayList<MedicineDispatchRequest> queries);
    ReturnedPath calcDeliveryPath(ArrayList<MedicineDispatchRequest> queries, ArrayList<ServicePoint> servicePoints, ArrayList<RestrictedArea> restrictedAreas, ArrayList<Drone> drones, ArrayList<ServicePointDrones> servicePointDrones);

}
