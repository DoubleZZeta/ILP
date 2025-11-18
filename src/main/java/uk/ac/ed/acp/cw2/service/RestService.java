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
    ArrayList<String> droneWithCooling(ArrayList<Drone> drones, boolean state);
    Drone droneDetails (ArrayList<Drone> drones, String droneId);
    ArrayList<String> query (ArrayList<Drone> drones, ArrayList<QueryRequest> queries);
    ArrayList<String> queryAvailableDrones (ArrayList<Drone> drones, ArrayList<ServicePointDrones> servicePointDrones, ArrayList<ServicePoint> servicePoints,ArrayList<MedicineDispatchRequest> queries);
    ReturnedPath calcDeliveryPath(ArrayList<MedicineDispatchRequest> queries, ArrayList<ServicePoint> servicePoints, ArrayList<RestrictedArea> restrictedAreas, ArrayList<Drone> drones, ArrayList<ServicePointDrones> servicePointDrones);
    GeoJson calcDeliveryPathAsGeoJson(ArrayList<MedicineDispatchRequest> queries, ArrayList<ServicePoint> servicePoints, ArrayList<RestrictedArea> restrictedAreas, ArrayList<Drone> drones, ArrayList<ServicePointDrones> servicePointDrones);

}
