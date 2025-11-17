package uk.ac.ed.acp.cw2.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import uk.ac.ed.acp.cw2.data.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Utility class contains helper functions.
 * Helper functions are being called by the Service Implementation class,
 * which makes the logic in the service implementation class easier.
 */
@Component
public class Utility
{
    private static final Double unitLength = 0.00015;
    private final ObjectMapper objectMapper;

    public Utility(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @Setter
    @Getter
    public static class Node implements Comparable<Node>
    {
        private Node parent;
        private Position position;
        private Double gScore;
        private Double fScore;
        private Boolean explored;

        public Node(Node parent, Position position, Double gScore, Double heuristic, boolean explored)
        {
            this.parent = parent;
            this.position = position;
            this.gScore = gScore;
            this.fScore = heuristic + this.gScore;
            this.explored = explored;
        }

        @Override
        public int compareTo(Node other)
        {
            return Double.compare(fScore, other.fScore);
        }

    }

    //Calculate the Euclidean distance between two given positions
    public Double calculateDistance(Position position1, Position position2)
    {
        Double lng1 = position1.getLng();
        Double lat1 = position1.getLat();
        Double lng2 = position2.getLng();
        Double lat2 = position2.getLat();

        double lngDiff = lng2 - lng1;
        double latDiff = lat2 - lat1;

        Double distance = Math.sqrt(Math.pow(lngDiff, 2) + Math.pow(latDiff, 2));

        //Rounding the result to five decimal places before returning
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.HALF_EVEN);

        return  Double.parseDouble(df.format(distance));
    }

    //Return list of edges based on the given vertices
    public ArrayList<PositionsRequest> getRegionEdges(Region region)
    {
        ArrayList<Position> vertices = region.getVertices();

        //Append the first vertex to the end to ensure the last edge closes the region
        vertices.add(vertices.getFirst());
        ArrayList<PositionsRequest> edges = new ArrayList<>();

        //Make positon pairs of the adjacent vertices
        for (int i = 0; i < vertices.size() - 1; i++)
        {
            Position start = vertices.get(i);
            Position end = vertices.get(i + 1);
            edges.add(new PositionsRequest(start, end));
        }

        return edges;
    }

    //Checking if the given position is on an edge of the region
    public boolean isPositionOnEdge(Position vertex, PositionsRequest edge)
    {
        Double x1 = edge.getPosition1().getLng();
        Double y1 = edge.getPosition1().getLat();
        Double x2 = edge.getPosition2().getLng();
        Double y2 = edge.getPosition2().getLat();
        Double xv = vertex.getLng();
        Double yv = vertex.getLat();

        //Gradient check
        if (Math.abs((y2-y1)*(xv-x1) - (yv-y1)*(x2-x1)) <= 1e-9)
        {
            //Boundary checks
            if (Math.min(y1, y2) <= yv && yv <= Math.max(y1, y2))
            {
                if (Math.min(x1, x2) <= xv && xv <= Math.max(x1, x2))
                {
                    return true;
                }
            }
        }
        return false;
    }

    //Check if the horizontal ray is intersecting with an edge of the region
    public boolean isEdgeIntersectWithRay(Position vertex, PositionsRequest edge)
    {
        Double x1 = edge.getPosition1().getLng();
        Double y1 = edge.getPosition1().getLat();
        Double x2 = edge.getPosition2().getLng();
        Double y2 = edge.getPosition2().getLat();
        Double xv = vertex.getLng();
        Double yv = vertex.getLat();

        //We don't take the horizontal line into consideration
        //Also, if the vertex is on the right of the right most point of the edge then there is no intersection
        if(Objects.equals(y1, y2) || Math.max(x1,x2) <= xv)
        {
            return false;
        }

        //Then consider when the ray is between two points of the edge
        if(Math.min(y1,y2) <= yv && yv <= Math.max(y1,y2))
        {
            //In the case where line is vertical, there must be an intersection
            if(Objects.equals(x1, x2))
            {
                return true;
            }
            //Otherwise we calculate the x coordinate of the intersection
            else
            {
                double m = (y2-y1)/(x2-x1);
                double xi = (yv - y1)/m + x1;
                return xv < xi;
            }
        }

        //If the ray is lower or higher than the edge, return false
        return false;
    }

    // Called by the main loop, try the get the attribute value based on the string
    public Object getDroneAttributeValue(Drone drone, String attribute)
    {
        // TODO evaluate the need of try
        try
        {
            Object attributeValue = null;
            // TODO is there a chance where capability is null no i guess
            Capability capability = drone.getCapability();

            Map<String, Object> droneAttributeDic = objectMapper.convertValue(drone, new TypeReference<>() {});
            Map<String, Object> capabilityAttributeDic = objectMapper.convertValue(capability, new TypeReference<>() {});

            if (droneAttributeDic.containsKey(attribute)) {
                attributeValue = droneAttributeDic.get(attribute);
            } else if (capabilityAttributeDic.containsKey(attribute)) {
                attributeValue = capabilityAttributeDic.get(attribute);
            }

            return attributeValue;
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        catch (Exception e)
        {
            System.err.println("Error matching condition: " + e.getMessage());
            return null;
        }
    }

    // Called by the main loop, accepting the attribute value and the passed value, check type and compare
    public boolean checkDroneMatchesQuery (Object droneAttributeValue, String value, String operator)
    {
        if (droneAttributeValue instanceof Number)
        {
            double valueDouble = Double.parseDouble(value);
            double droneAttributeValueDouble = ((Number)droneAttributeValue).doubleValue();
            return switch (operator)
            {
                case "=" -> droneAttributeValueDouble == valueDouble;
                case "!=" -> droneAttributeValueDouble != valueDouble;
                case ">=" -> droneAttributeValueDouble >= valueDouble;
                case "<=" -> droneAttributeValueDouble <= valueDouble;
                case ">" -> droneAttributeValueDouble > valueDouble;
                case "<" -> droneAttributeValueDouble < valueDouble;
                default -> false;
            };
        }
        else if (droneAttributeValue instanceof Boolean)
        {
            boolean valueBoolean = Boolean.parseBoolean(value);
            boolean droneAttributeValueBoolean = (boolean) droneAttributeValue;

            return switch (operator)
            {
                case "=" -> valueBoolean == droneAttributeValueBoolean;
                case "!=" -> valueBoolean != droneAttributeValueBoolean;
                default -> false;
            };
        }
        else if (droneAttributeValue instanceof String)
        {
            String droneAttributeValueString = (String) droneAttributeValue;

            return switch (operator)
            {
                case "=" -> Objects.equals(value, droneAttributeValueString);
                case "!=" -> !Objects.equals(value, droneAttributeValueString);
                default -> false;
            };

        }
        return true;
    }

    public Map<String, ArrayList<Availability>>  getAvailabilityMap(ArrayList<ServicePointDrones> droneServicePoints)
    {
        Map<String, ArrayList<Availability>> availabilityMap = new HashMap<>();
        for (ServicePointDrones droneServicePoint: droneServicePoints)
        {
            for (DroneAvailability droneAvailability: droneServicePoint.getDrones())
            {
                String id = droneAvailability.getId();
                availabilityMap.put(id, droneAvailability.getAvailability());
            }
        }

        return availabilityMap;
    }

    public boolean checkDroneMeetsRequirements(Capability capability, Requirements requirements, Position delivery)
    {
        boolean result = true;

        if(requirements.getCapacity() > capability.getCapacity())
        {
            result = false;
        }

        if(requirements.getCooling() != null && requirements.getCooling() && !capability.getCooling())
        {
            result = false;
        }

        if(requirements.getHeating() != null && requirements.getHeating() && !capability.getHeating())
        {
            result = false;
        }

        if(requirements.getMaxCost() != null)
        {

        }

        return result;
    }

    public boolean checkDroneIsAvailable(ArrayList<Availability> availabilities, LocalDate date, LocalTime time)
    {
        boolean result = false;
        boolean dayOfWeekMatches;
        boolean timeMatches;


        for (Availability availability: availabilities)
        {
            if (date != null)
            {
                dayOfWeekMatches = (date.getDayOfWeek() == availability.getDayOfWeek());
            }
            else
            {
                dayOfWeekMatches = true;
            }

            if (time != null)
            {
                timeMatches = (availability.getFrom().isBefore(time) && availability.getUntil().isAfter(time));
            }
            else
            {
                timeMatches = true;
            }

            if(dayOfWeekMatches && timeMatches)
            {
                result = true;
                break;
            }
        }
        return  result;
    }

    public Set<LocalDate> getAllDates(ArrayList<MedicineDispatchRequest> quires)
    {
        Set<LocalDate> dates = new HashSet<>();
        for (MedicineDispatchRequest query: quires)
        {
            dates.add(query.getDate());
        }
        return dates;
    }

    public ArrayList<MedicineDispatchRequest> getMedicineDispatchByDate(ArrayList<MedicineDispatchRequest> queries, LocalDate date)
    {
        ArrayList<MedicineDispatchRequest> medicineDispatchRequestsByDate = new ArrayList<>();
        for (MedicineDispatchRequest query: queries)
        {
            if(query.getDate().equals(date))
            {
                medicineDispatchRequestsByDate.add(query);
            }
        }
        return medicineDispatchRequestsByDate;
    }

    public Position getServicePointPositionByDroneIdAndTime(String droneId,ArrayList<ServicePointDrones> servicePointDrones, ArrayList<ServicePoint> servicePoints, LocalDate date, LocalTime time)
    {
        int servicePointId = -1;

        for (ServicePointDrones servicePointDrone: servicePointDrones)
        {
            servicePointId = servicePointDrone.getServicePointId();
            boolean found = false;
            for (DroneAvailability droneAvailability: servicePointDrone.getDrones())
            {
                String id = droneAvailability.getId();
                if (id.equals(droneId))
                {
                    found = true;
                    break;
                }
            }
            if (found)
            {
                break;
            }
        }

        for (ServicePoint servicePoint: servicePoints)
        {
            if  (servicePoint.getId().equals(servicePointId))
            {
                return servicePoint.getPosition();
            }
        }

        return null;
    }

    public ArrayList<Position> calculatePath(Node node, Position end)
    {
        ArrayList<Position> path = new ArrayList<>();
        path.add(end);
        while(node.parent != null)
        {
            path.add(node.position);
            node = node.parent;
        }
        Collections.reverse(path); // Reverses the path from [end...start] to [start...end]
        return path;
    }

    public ArrayList<Position> aStarSearch(Position start, Position end, ArrayList<RestrictedArea> restrictedAreas)
    {
        // Set up
        PriorityQueue<Node> minHeap = new PriorityQueue<>();
        minHeap.add(new Node( null, start,0.0,calculateDistance(start,end),false));


        //main loop
        while (!minHeap.isEmpty())
        {
            Node u = minHeap.poll();
            if (calculateDistance(u.getPosition(), end) < unitLength)
            {
                return calculatePath(u,end);
            }
            if (!u.getExplored())
            {
                u.setExplored(true);
                for (double angle = 0.0; angle < 360; angle += 22.5)
                {
                    Double nextLng = unitLength * Math.cos(angle) + u.getPosition().getLng();
                    Double nextLat = unitLength * Math.sin(angle) + u.getPosition().getLat();
                    Position nextPosition = new Position(nextLng,nextLat);
                    if (!nextLng.equals(u.getPosition().getLng()) && !nextLat.equals(u.getPosition().getLat()))
                    {
                        //TODO condition might be wrong
                        //TODO prevent going back and forth
                        Node v = new Node(u, u.getPosition(), u.getGScore() + unitLength, calculateDistance(nextPosition, end),false);
                        minHeap.add(v);
                    }
                }
            }


        }

        return new ArrayList<>();
    }




}
