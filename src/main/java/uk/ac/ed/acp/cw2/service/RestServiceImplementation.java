package uk.ac.ed.acp.cw2.service;

import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.data.*;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.lang.reflect.Array;
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
        //Ray casting algorithm
        Position position = Request.getPosition();
        Region region = Request.getRegion();

        //Number of intersection between the ray and the region edges
        int count = 0;
        ArrayList<PositionsRequest> edges = utility.getRegionEdges(region);

        //Iterate through all edges
        for(PositionsRequest edge : edges)
        {
            //If the position is on the edge of the region, return true directly
            if (utility.isPositionOnEdge(position,edge))
            {
                return true;
            }
            //Else check if the ray intersect with the edge
            else if(utility.isEdgeIntersectWithRay(position, edge))
            {
                count++;
            }
        }

        //If the number of intersection is odd, then the position is inside the region
        return count % 2 != 0;
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
    public ArrayList<String> queryAvailableDrones (ArrayList<Drone> drones, ArrayList<ServicePointDrones> servicePointDrones, ArrayList<MedicineDispatchRequest> queries)
    {
        String id;
        LocalDate date;
        LocalTime time;
        ArrayList<Availability> availabilities;
        Capability capability;
        Requirements requirements;
        Position delivery;

        ArrayList<String> droneIds = new ArrayList<>();
        Map<String, ArrayList<Availability>> availabilityMap = utility.getAvailabilityMap(servicePointDrones);


        Set<LocalDate> dates = utility.getAllDates(queries);
        // if multiple dates return empty list
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
                    availabilities = availabilityMap.get(id);
                    capability = drone.getCapability();
                    requirements = query.getRequirements();
                    delivery = query.getDelivery();

                    droneIsAvailable = utility.checkDroneIsAvailable(availabilities,date,time);
                    droneMeetsRequirements = utility.checkDroneMeetsRequirements(capability, requirements, delivery);

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
    public ReturnedPath calcDeliveryPath(ArrayList<MedicineDispatchRequest> queries, ArrayList<ServicePoint> servicePoints, ArrayList<RestrictedArea> restrictedAreas, ArrayList<Drone> drones, ArrayList<ServicePointDrones> servicePointDrones)
    {

        int totalMoves = 0;
        double totalCost = 0;
        ArrayList<DronePath> dronePaths = new ArrayList<>();
        Set<LocalDate> dates = utility.getAllDates(queries);
        //TODO sort dates by order

        // Do this date by date, since no cross date delivery allowed
        for (LocalDate date: dates)
        {
            ArrayList<MedicineDispatchRequest> queryByDate = utility.getMedicineDispatchByDate(queries, date);
            // Sort the deliveries based on the required arrival time of the orders
            queryByDate.sort(Comparator.comparing(MedicineDispatchRequest::getTime));

            // Loop continues if not all deliveries in a date has been made
            // Each loop indicates a new fly
            int currentDroneIndex = 0;
            while (!queryByDate.isEmpty())
            {
                // Needed local variables
                double flightMaxCost = queryByDate.getFirst().getRequirements().getMaxCost();
                Integer droneMaxMove;
                Drone currentDrone;
                Position start;
                Position end;
                ArrayList<Position> path = new ArrayList<>();

                // Get list of available drones based on the first query
                ArrayList<String> availableDronesIds = queryAvailableDrones(drones, servicePointDrones, new ArrayList<>(Collections.singletonList(queryByDate.getFirst())));
                currentDrone = droneDetails(drones,availableDronesIds.get(currentDroneIndex));
                droneMaxMove = currentDrone.getCapability().getMaxMoves();

                // Choose a drone and then get its service point location (potential optimisation is choosing a drone with base closet to the destination)
                Position droneBase = utility.getServicePointPositionByDroneIdAndTime(currentDrone.getId(), servicePointDrones, servicePoints);

                // Before take-off, reduce the flightMaxCost by the cost to take-off
                flightMaxCost -= currentDrone.getCapability().getCostInitial();

                // Apply A* from the start to the first point, then try to integrate other deliveries
                ArrayList<Position> intermediatePath = new ArrayList<>();
                ArrayList<Position> returnPath = new ArrayList<>();
                ArrayList<MedicineDispatchRequest> delivered = new ArrayList<>();
                for (MedicineDispatchRequest query : queryByDate)
                {
                    // Get start and end first
                    end = query.getDelivery();
                    if (intermediatePath.isEmpty())
                    {
                        start = droneBase;
                    }
                    else
                    {
                        start = intermediatePath.getLast();
                    }

                    // Try to add one more delivery point
                    intermediatePath = utility.aStarSearch(start,end,restrictedAreas);
                    int moves = intermediatePath.size();
                    double cost = moves * currentDrone.getCapability().getCostPerMove();

                    // Checking if the costs/moves is enough to return to base
                    returnPath = utility.aStarSearch(end,droneBase,restrictedAreas);
                    int movesReturn = returnPath.size();
                    double costReturn = movesReturn * currentDrone.getCapability().getCostPerMove();

                    // Update move and cost
                    droneMaxMove -= moves;
                    flightMaxCost -= cost;

                    // Cannot add delivery due to out of move/cost. But should I consider no path found ? yes
                    if(!((flightMaxCost - costReturn) <= 0 || (droneMaxMove - movesReturn) <= 0 || intermediatePath.isEmpty() || returnPath.isEmpty()))
                    {
                        // Merge path, and remove the added query
                        totalCost += cost;
                        totalMoves += moves;
                        path.addAll(intermediatePath);
                        delivered.add(query);
                    }
                    // If any of the above condition not satisfied,  don't do anything
                }
                //remove delivered
                queryByDate.removeAll(delivered);

                // Return to base
                if (!delivered.isEmpty()) // Check if we actually delivered anything
                {
                    Position lastDeliveryPoint = delivered.getLast().getDelivery();
                    returnPath = utility.aStarSearch(lastDeliveryPoint, droneBase, restrictedAreas);
                    path.addAll(returnPath);
                    totalMoves += returnPath.size();
                    totalCost += returnPath.size() * currentDrone.getCapability().getCostPerMove();
                }


                // After the path is found, construct the response and put it aside
                    // toDelivery function and more there
                    // it's just datatype conversion, do it later
                // Pick another drone (iterate)
                currentDroneIndex = (currentDroneIndex+1) % drones.size(); // wrap around
                System.out.println(path);
            }
        }

        return null;
    }
}
