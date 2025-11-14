package uk.ac.ed.acp.cw2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.acp.cw2.data.*;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class DataFetchServiceImplementation implements DataFetchService
{
    private final RestTemplate restTemplate;
    private final String url;

    @Autowired
    public DataFetchServiceImplementation(RestTemplate restTemplate, String url)
    {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    @Override
    public ArrayList<ServicePoint> getServicePoints()
    {
        String servicePointUrl = this.url + "/service-points";
        ServicePoint[] servicePoints = restTemplate.getForObject(servicePointUrl, ServicePoint[].class);

        if (servicePoints == null)
        {
            return new ArrayList<>();
        }

        return new ArrayList<>(Arrays.asList(servicePoints));
    }

    @Override
    public  ArrayList<RestrictedArea> getRestrictedAreas()
    {
        String restrictedAreaUrl = this.url + "/restricted-areas";
        RestrictedArea[] restrictedAreas = restTemplate.getForObject(restrictedAreaUrl, RestrictedArea[].class);

        if (restrictedAreas == null)
        {
            return new ArrayList<>();
        }

        return new ArrayList<>(Arrays.asList(restrictedAreas));
    }

    @Override
    public ArrayList<Drone> getDrones()
    {
        String droneUrl = this.url + "/drones";
        Drone[] drones = restTemplate.getForObject(droneUrl, Drone[].class);

        if (drones == null)
        {
            return new ArrayList<>();
        }

        return new ArrayList<>(Arrays.asList(drones));
    }

    @Override
    public ArrayList<ServicePointDrones> getServicePointsDrones()
    {
        String servicePointUrlDrones = this.url + "/drones-for-service-points";
        ServicePointDrones[] servicePointsDrones = restTemplate.getForObject(servicePointUrlDrones, ServicePointDrones[].class);

        if (servicePointsDrones == null)
        {
            return new ArrayList<>();
        }

        return new ArrayList<>(Arrays.asList(servicePointsDrones));
    }

}
