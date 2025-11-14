package uk.ac.ed.acp.cw2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw2.data.*;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

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
    public ArrayList<Integer> droneWithCooling(ArrayList<Drone> drones, boolean state)
    {
        ArrayList<Integer> dronesWithCooling = new ArrayList<>();
        for (Drone drone : drones) {
            if (drone.getCapability().getCooling() == state)
            {
                dronesWithCooling.add(drone.getId());
            }
        }
        return dronesWithCooling;
    }

    @Override
    public Drone droneDetails (ArrayList<Drone> drones, Integer droneId)
    {
        Drone droneWithId = null;
        for(Drone drone : drones)
        {
            if (drone.getId().equals(droneId))
            {
                droneWithId = drone;
                break;
            }
        }
        return droneWithId;
    }

    @Override
    public  ArrayList<Integer> query (ArrayList<Drone> drones, ArrayList<QueryRequest> queries)
    {
        String attribute;
        String value;
        String operator;

        ArrayList<Integer> droneIds = new ArrayList<>();

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
    public ArrayList<Integer> queryAvailableDrones (ArrayList<Drone> drones, ArrayList<ServicePointDrones> servicePointDrones, ArrayList<MedicineDispatchRequest> queries)
    {
        int id;
        LocalDate date;
        LocalTime time;
        ArrayList<Availability> availabilities;
        Capability capability;
        Requirements requirements;

        ArrayList<Integer> droneIds = new ArrayList<>();
        Map<Integer, ArrayList<Availability>> availabilityMap = utility.getAvailabilityMap(servicePointDrones);


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

                droneIsAvailable = utility.checkDroneIsAvailable(availabilities,date,time);
                droneMeetsRequirements = utility.checkDroneMeetsRequirements(capability, requirements);

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
        return droneIds;
    }

    @Override
    public ReturnedPath calcDeliveryPath(MedicineDispatchRequest queries)
    {
        return null;

    }
}
