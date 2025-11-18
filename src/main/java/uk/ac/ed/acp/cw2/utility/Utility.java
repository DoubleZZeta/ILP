package uk.ac.ed.acp.cw2.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final double unitLength = 0.00015;
    private static final double error = 1e-9;
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

        public Node(Node parent, Position position, Double gScore, Double heuristic)
        {
            this.parent = parent;
            this.position = position;
            this.gScore = gScore;
            this.fScore = heuristic + this.gScore;
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
        // don't mutate the original list by copying
        ArrayList<Position> vertices = new ArrayList<>(region.getVertices());

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

    public boolean isInRegion(PositionRegionRequest Request)
    {
        //Ray casting algorithm
        Position position = Request.getPosition();
        Region region = Request.getRegion();

        //Number of intersection between the ray and the region edges
        int count = 0;
        ArrayList<PositionsRequest> edges = getRegionEdges(region);

        //Iterate through all edges
        for(PositionsRequest edge : edges)
        {
            //If the position is on the edge of the region, return true directly
            if (isPositionOnEdge(position,edge))
            {
                return true;
            }
            //Else check if the ray intersect with the edge
            else if(isEdgeIntersectWithRay(position, edge))
            {
                count++;
            }
        }

        //If the number of intersection is odd, then the position is inside the region
        return count % 2 != 0;
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


    // For a given date time and a given drone, return position of the service point if the drone is available there
    public Position getServicePointPosition(String droneId, ArrayList<ServicePointDrones> servicePointsDrones, ArrayList<ServicePoint> servicePoints, LocalDate date, LocalTime time)
    {
        int servicePointId = -1;
        for (ServicePointDrones servicePointDrone: servicePointsDrones)
        {
            for (DroneAvailability droneAvailability : servicePointDrone.getDrones())
            {
                if (droneId.equals(droneAvailability.getId()))
                {
                    for (Availability availability: droneAvailability.getAvailability())
                    {
                        if(availability.getDayOfWeek().equals(date.getDayOfWeek()))
                        {
                            if(time.isAfter(availability.getFrom()) && time.isBefore(availability.getUntil()))
                            {
                                servicePointId = servicePointDrone.getServicePointId();
                                break;
                            }
                        }
                    }
                }
                if (servicePointId != -1)
                {
                    break;
                }
            }
            if (servicePointId != -1)
            {
                break;
            }
        }

        Position servicePointPosition = null;
        if  (servicePointId != -1)
        {
            for (ServicePoint servicePoint: servicePoints)
            {
                if (servicePoint.getId().equals(servicePointId))
                {
                    servicePointPosition = servicePoint.getLocation();
                    break;
                }
            }
        }
        return servicePointPosition;
    }


    public void addDeliveriesToRetunedPath(String droneId, Integer deliveryId, ArrayList<Position> delivery, ReturnedPath returnedPath)
    {
        boolean droneIdStored = false;
        for (DronePath dronePath: returnedPath.getDronePaths())
        {
            if (dronePath.getDroneId().equals(droneId))
            {
                droneIdStored = true;
                dronePath.getDeliveries().add(new Deliveries(deliveryId, delivery));
                break;
            }
        }

        if(!droneIdStored)
        {
            ArrayList<Deliveries> deliveries = new ArrayList<>();
            deliveries.add(new Deliveries(deliveryId, delivery));
            DronePath newDronePath = new DronePath(droneId,deliveries);
            returnedPath.getDronePaths().add(newDronePath);
        }

    }

    private double orient(Position a, Position b, Position c) {
        double x1 = b.getLng() - a.getLng();
        double y1 = b.getLat() - a.getLat();
        double x2 = c.getLng() - a.getLng();
        double y2 = c.getLat() - a.getLat();
        return x1 * y2 - y1 * x2;
    }

    private boolean onSegment(Position a, Position b, Position c) {
        // assume a,b,c are collinear; check if c is between a and b (inclusive)
        double minLng = Math.min(a.getLng(), b.getLng()) - error;
        double maxLng = Math.max(a.getLng(), b.getLng()) + error;
        double minLat = Math.min(a.getLat(), b.getLat()) - error;
        double maxLat = Math.max(a.getLat(), b.getLat()) + error;
        return c.getLng() >= minLng && c.getLng() <= maxLng && c.getLat() >= minLat && c.getLat() <= maxLat;
    }

    public boolean segmentsIntersect(Position p1, Position p2, Position q1, Position q2)
    {
        double o1 = orient(p1, p2, q1);
        double o2 = orient(p1, p2, q2);
        double o3 = orient(q1, q2, p1);
        double o4 = orient(q1, q2, p2);

        // Proper intersection
        if (o1 * o2 < 0 && o3 * o4 < 0) {
            return true;
        }

        // Collinear / Touching cases
        if (Math.abs(o1) < error && onSegment(p1, p2, q1)) return true;
        if (Math.abs(o2) < error && onSegment(p1, p2, q2)) return true;
        if (Math.abs(o3) < error && onSegment(q1, q2, p1)) return true;
        if (Math.abs(o4) < error && onSegment(q1, q2, p2)) return true;

        return false;
    }

    public boolean isPathIntersectingAreaEdges(PositionsRequest path, RestrictedArea restrictedArea)
    {
        boolean intersects = false;
        ArrayList<PositionsRequest> edges = getRegionEdges(new Region(restrictedArea.getName(), restrictedArea.getVertices()));

        Position p1 = path.getPosition1();
        Position p2 = path.getPosition2();

        for (PositionsRequest edge : edges)
        {
            Position q1 = edge.getPosition1();
            Position q2 = edge.getPosition2();

            if (segmentsIntersect(p1, p2, q1, q2))
            {
                intersects = true;
                break;
            }
        }
        return intersects;
    }



    public boolean isPathCrossingRestrictionArea (PositionsRequest path, ArrayList<RestrictedArea> restrictedAreas)
    {
        boolean crossed = false;

        for (RestrictedArea restrictedArea: restrictedAreas)
        {
            Region region = new Region(restrictedArea.getName(), restrictedArea.getVertices());
            PositionRegionRequest positionRegionRequest = new PositionRegionRequest(path.getPosition2(),region);
            if(isInRegion(positionRegionRequest) || isPathIntersectingAreaEdges(path,restrictedArea))
            {
                crossed = true;
                break;
            }
        }

        return crossed;
    }

    public String getKey(Position position)
    {
        long lngRounded = Math.round(position.getLng() / unitLength);
        long latRounded = Math.round(position.getLat() / unitLength);

        return lngRounded + "," + latRounded;
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
        path.add(node.position);
        Collections.reverse(path); // Reverses the path from [end...start] to [start...end]
        return path;
    }

    public ArrayList<Position> aStarSearch(Position start, Position end, ArrayList<RestrictedArea> restrictedAreas)
    {
        // Set up
        int expansions = 0;
        int maxExpansions = 1000000;
        PriorityQueue<Node> minHeap = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();
        minHeap.add(new Node( null, start,0.0,calculateDistance(start,end)));

        //main loop
        while (!minHeap.isEmpty())
        {
            Node u = minHeap.poll();

            if (expansions++ > maxExpansions)
            {
                return new ArrayList<>(); // treat as "no path", too many expansions
            }

            if (calculateDistance(u.getPosition(), end) < unitLength)
            {
                return calculatePath(u,end);
            }
            if (!visited.contains(getKey(u.getPosition())))
            {
                visited.add(getKey(u.getPosition()));
                for (double angle = 0.0; angle < 360; angle += 22.5)
                {
                    double angleRad = Math.toRadians(angle);
                    Double nextLng = unitLength * Math.cos(angleRad) + u.getPosition().getLng();
                    Double nextLat = unitLength * Math.sin(angleRad) + u.getPosition().getLat();
                    Position nextPosition = new Position(nextLng,nextLat);
                    PositionsRequest path = new  PositionsRequest(u.getPosition(),nextPosition);
                    if (!nextLng.equals(u.getPosition().getLng()) && !nextLat.equals(u.getPosition().getLat()) && !isPathCrossingRestrictionArea(path,restrictedAreas))
                    {
                        Node v = new Node(u, nextPosition, u.getGScore() + unitLength, calculateDistance(nextPosition, end));
                        minHeap.add(v);
                    }
                }
            }


        }

        return new ArrayList<>();
    }

    public boolean isDroneDeliveredAll(ReturnedPath path, ArrayList<MedicineDispatchRequest> queries)
    {
        // No valid routes
        if (path == null || path.getDronePaths() == null || path.getDronePaths().isEmpty())
        {
            return false;
        }

        boolean deliveredAll = true;
        Set<Integer> deliveredQueryIds = new HashSet<>();
        for (DronePath dronePath: path.getDronePaths())
        {
            for (Deliveries deliveries: dronePath.getDeliveries())
            {
                if(deliveries.getDeliveryId() != null && !deliveries.getFlightPath().isEmpty())
                {
                    deliveredQueryIds.add(deliveries.getDeliveryId());
                }
            }
        }

        for(MedicineDispatchRequest query: queries)
        {
            if (!deliveredQueryIds.contains(query.getId()))
            {
                deliveredAll = false;
                break;
            }
        }

        return  deliveredAll;
    }

    public GeoJson toGeoJson(ReturnedPath path)
    {
        GeoJson geoJson = new GeoJson("LineString",new ArrayList<>());
        for (DronePath dronePath: path.getDronePaths())
        {
            for (Deliveries deliveries: dronePath.getDeliveries())
            {
                for (Position position: deliveries.getFlightPath())
                {
                    ArrayList<Double> lngLat = new ArrayList<>();
                    lngLat.add(position.getLng());
                    lngLat.add(position.getLat());
                    geoJson.getCoordinates().add(lngLat);
                }
            }
        }

        return  geoJson;
    }




}
