package uk.ac.ed.acp.cw2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.data.*;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

//Service interface implementation
/**
 * Service Implementation class that implements the service interface,
 * providing actual functionality.
 */
@Service
public class RestServiceImplementation implements RestService
{

    private final Utility utility;
    private static final Double unitLength = 0.00015;

    //Dependency inject the utility clas
    @Autowired
    public RestServiceImplementation(final Utility utility)
    {
        this.utility = utility;
    }

    @Override
    public Double distanceTo(PositionsRequest Request)
    {
        Position position1 = Request.getPosition1();
        Position position2 = Request.getPosition2();

        return utility.calculateDistance(position1,position2);
    }

    @Override
    public boolean isCloseTo(PositionsRequest Request)
    {
        Position position1 = Request.getPosition1();
        Position position2 = Request.getPosition2();

        Double distance = utility.calculateDistance(position1,position2);

        //If the distance is less than 0.00015, return true
        return distance.compareTo(unitLength) < 0;
    }

    @Override
    public String nextPosition(PositionAngleRequest Request) {
        Position start = Request.getStart();
        Double angle = Request.getAngle();

        //Calculate the new position using trigonometric functions
        angle = Math.toRadians(angle);
        Double lng = unitLength * Math.cos(angle) + start.getLng();
        Double lat = unitLength * Math.sin(angle) + start.getLat();

        //Make sure that the return lng lat pair is in five decimal places
        return String.format("{ \"lng\": %.5f, \"lat\": %.5f }", lng, lat);
    }

    @Override
    public boolean isInRegion(PositionRegionRequest Request)
    {
        // Moved the logic to utility for cw2
        return utility.isInRegion(Request);
    }

    @Override
    public ArrayList<String> droneWithCooling(ArrayList<Drone> drones, boolean state)
    {
        ArrayList<String> dronesWithCooling = new ArrayList<>();
        for (Drone drone : drones) {
            if (drone.getCapability().getCooling() == state)
            {
                dronesWithCooling.add(drone.getId());
            }
        }
        return dronesWithCooling;
    }

    @Override
    public Drone droneDetails (ArrayList<Drone> drones, String droneId)
    {
        Drone droneWithId = null;
        for(Drone drone : drones)
        {
            if (Objects.equals(drone.getId(), droneId))
            {
                droneWithId = drone;
                break;
            }
        }
        return droneWithId;
    }

    @Override
    public  ArrayList<String> query (ArrayList<Drone> drones, ArrayList<QueryRequest> queries)
    {
        String attribute;
        String value;
        String operator;

        ArrayList<String> droneIds = new ArrayList<>();

        for (Drone drone : drones)
        {
            boolean matchesAll = true;
            for (QueryRequest queryRequest : queries)
            {
                attribute = queryRequest.getAttribute();
                value = queryRequest.getValue();
                operator = queryRequest.getOperator();

                Object droneAttributeValue = utility.getDroneAttributeValue(drone, attribute);
                if(!utility.checkDroneMatchesQuery(droneAttributeValue, value ,operator))
                {
                    matchesAll = false;
                    break;
                }
            }

            if (matchesAll)
            {
                 droneIds.add(drone.getId());
            }
        }

        return droneIds;
    }

    @Override
    public ArrayList<String> queryAvailableDrones (ArrayList<Drone> drones, ArrayList<ServicePointDrones> servicePointDrones, ArrayList<ServicePoint> servicePoints, ArrayList<MedicineDispatchRequest> queries)
    {
        String id;
        LocalDate date;
        LocalTime time;
        Position droneBase;

        ArrayList<String> droneIds = new ArrayList<>();
        Map<String, ArrayList<Availability>> availabilityMap = utility.getAvailabilityMap(servicePointDrones);


        Set<LocalDate> dates = utility.getAllDates(queries);
        if (dates.size() <= 1)
        {
            for  (Drone drone : drones)
            {
                id = drone.getId();

                boolean matchesAll = true;
                boolean droneIsAvailable;
                boolean droneMeetsRequirements;

                for (MedicineDispatchRequest query : queries)
                {
                    date = query.getDate();
                    time = query.getTime();
                    droneBase = utility.getServicePointPosition(id, servicePointDrones,servicePoints, date, time);

                    System.out.println("droneId before checking:" +  drone.getId());
                    droneIsAvailable = utility.checkDroneIsAvailable(drone,query, availabilityMap);
                    droneMeetsRequirements = utility.checkDroneMeetsRequirements(drone, query, droneBase);

                    if (!(droneIsAvailable && droneMeetsRequirements))
                    {
                        matchesAll = false;
                        break;
                    }
                }

                if (matchesAll)
                {
                    droneIds.add(drone.getId());
                }

            }
        }


        return droneIds;
    }

    @Override
    public ReturnedPath calcDeliveryPath(ArrayList<MedicineDispatchRequest> queries, ArrayList<ServicePoint> servicePoints, ArrayList<RestrictedArea> restrictedAreas, ArrayList<Drone> drones, ArrayList<ServicePointDrones> servicePointsDrones)
    {
        //TODO optimise the function for getting drone bases
        Set<LocalDate> dates = utility.getAllDates(queries);
        List<LocalDate> sortedDates = dates.stream().sorted().toList();

        int totalMoves = 0;
        double totalCost = 0;
        ReturnedPath returnedPath = new ReturnedPath(0.0,0, new ArrayList<>());

        // Do this date by date, since no cross date delivery allowed
        for (LocalDate date: sortedDates)
        {
            // Sort the deliveries based on the required arrival time of the orders
            ArrayList<MedicineDispatchRequest> queryByDate = utility.getMedicineDispatchByDate(queries, date);
            queryByDate.sort(Comparator.comparing(MedicineDispatchRequest::getTime));
            for (Drone drone : drones)
            {
                boolean progress;
                do
                {
                    progress = false;
                    Capability droneCapability = drone.getCapability();
                    double currentDroneCapacity = droneCapability.getCapacity();
                    int currentDroneMoves = 0;
                    int currentNumberOfDeliveries = 0;
                    double currentCostPerDelivery;
                    double currentLandingAndTakeOffCost = droneCapability.getCostInitial() + droneCapability.getCostFinal();
                    boolean currentDroneCooling = droneCapability.getCooling();
                    boolean currentDroneHeating = droneCapability.getHeating();
                    Position currentDroneBase = null;
                    ArrayList<Position> currentDronePath = new ArrayList<>();
                    ArrayList<MedicineDispatchRequest> delivered = new ArrayList<>();
                    Position start;
                    Position end;

                    for(MedicineDispatchRequest query : queryByDate)
                    {
                        Requirements queryRequirements = query.getRequirements();
                        LocalTime time = query.getTime();
                        Position droneBase = utility.getServicePointPosition(drone.getId(), servicePointsDrones, servicePoints, date, time);

                        // Current drone is not available
                        if (droneBase == null)
                        {
                            // Move to next drone
                            continue;
                        }

                        // If we haven't fixed a base for this flight yet, lock it in
                        if (currentDroneBase == null)
                        {
                            currentDroneBase = droneBase;
                        }
                        else if (!currentDroneBase.equals(droneBase))
                        {
                            // This query would require starting from a different base,
                            // so it can't belong to this particular string
                            continue;
                        }


                        boolean canDeliver = true;
                        if(currentDroneCapacity < queryRequirements.getCapacity())
                        {
                            canDeliver = false;
                        }

                        Boolean reqCooling = queryRequirements.getCooling();
                        if (reqCooling != null && reqCooling && !currentDroneCooling)
                        {
                            canDeliver = false;
                        }

                        Boolean reqHeating = queryRequirements.getHeating();
                        if (reqHeating != null && reqHeating && !currentDroneHeating)
                        {
                            canDeliver = false;
                        }

                        if (canDeliver)
                        {
                            if(currentDronePath.isEmpty())
                            {
                                start = droneBase;
                            }
                            else
                            {
                                start = currentDronePath.getLast();
                            }
                            end = query.getDelivery();

                            ArrayList<Position> toDeliver = utility.aStarSearch(start,end,restrictedAreas);
                            int movesTo = toDeliver.size();
                            ArrayList<Position> toBase = utility.aStarSearch(end,droneBase,restrictedAreas);
                            int movesBack = toBase.size();

                            if (toDeliver.isEmpty() || toBase.isEmpty())
                            {
                                // no valid path, treat as cannot deliver this query
                                continue;
                            }

                            int estimatedCurrentDroneMoves = currentDroneMoves + (movesTo + movesBack);
                            int estimatedCurrentNumberOfDeliveries = currentNumberOfDeliveries + 1;
                            double estimatedCurrentFlightCost = currentLandingAndTakeOffCost + estimatedCurrentDroneMoves * droneCapability.getCostPerMove();
                            currentCostPerDelivery = estimatedCurrentFlightCost / estimatedCurrentNumberOfDeliveries;

                            boolean lowerThanMaxMove = (estimatedCurrentDroneMoves <= droneCapability.getMaxMoves());
                            boolean lowerThanMaxCost = ((queryRequirements.getMaxCost() == null) || (currentCostPerDelivery <= queryRequirements.getMaxCost()));

                            if( lowerThanMaxMove && lowerThanMaxCost )
                            {

                                currentDroneMoves += movesTo;
                                currentNumberOfDeliveries += 1;
                                currentDroneCapacity -=  queryRequirements.getCapacity();
                                currentDronePath.addAll(toDeliver);
                                delivered.add(query);

                                totalMoves += movesTo;

                                // For hover
                                toDeliver.add(toDeliver.getLast());
                                utility.addDeliveriesToRetunedPath(drone.getId(),query.getId(),toDeliver,returnedPath);
                            }
                        }

                    }
                    // close flight if  did anything
                    if (!currentDronePath.isEmpty())
                    {
                        progress = true;
                        Position last = currentDronePath.getLast();
                        ArrayList<Position> back = utility.aStarSearch(last, currentDroneBase, restrictedAreas);
                        utility.addDeliveriesToRetunedPath(drone.getId(),null,back,returnedPath);
                        currentDroneMoves += back.size();
                        totalMoves += back.size();
                        totalCost += currentLandingAndTakeOffCost + currentDroneMoves * droneCapability.getCostPerMove();
                        queryByDate.removeAll(delivered);
                    }

                }
                while(progress && !queryByDate.isEmpty());

            }

        }
        returnedPath.setTotalCost(totalCost);
        returnedPath.setTotalMoves(totalMoves);
        return returnedPath;
    }

    @Override
    public GeoJson calcDeliveryPathAsGeoJson(ArrayList<MedicineDispatchRequest> queries, ArrayList<ServicePoint> servicePoints, ArrayList<RestrictedArea> restrictedAreas, ArrayList<Drone> drones, ArrayList<ServicePointDrones> servicePointDrones)
    {
        ReturnedPath returnedPath = calcDeliveryPath(queries, servicePoints, restrictedAreas, drones, servicePointDrones);
        GeoJson geoJson = new GeoJson("FeatureCollection",new ArrayList<>());

        for(DronePath dronePath: returnedPath.getDronePaths())
        {
            for(Deliveries deliveries: dronePath.getDeliveries())
            {
                Geometry geometry = new Geometry("LineString",new ArrayList<>());
                for (Position position: deliveries.getFlightPath())
                {
                    ArrayList<Double> lngLat = new ArrayList<>();
                    lngLat.add(position.getLng());
                    lngLat.add(position.getLat());
                    geometry.getCoordinates().add(lngLat);
                }
                Feature feature = new Feature("Feature",null,geometry);
                geoJson.getFeatures().add(feature);

            }

        }
        return geoJson;
    }

}
