package uk.ac.ed.acp.cw2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.acp.cw2.data.Drone;

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
    public ArrayList<Drone> getDrones()
    {
        String droneUrl = this.url + "/drones";
        Drone[] droneList = restTemplate.getForObject(droneUrl, Drone[].class);

        if (droneList == null)
        {
            return new ArrayList<>();
        }

        return new ArrayList<>(Arrays.asList(droneList));
    }
}
