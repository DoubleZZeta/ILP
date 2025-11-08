package uk.ac.ed.acp.cw2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.acp.cw2.data.*;
import uk.ac.ed.acp.cw2.service.DataFetchService;
import uk.ac.ed.acp.cw2.service.RestService;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;

/**
 * Controller class that handles various HTTP endpoints for the application.
 * Provides functionality for serving the index page, retrieving a static UUID,
 * and managing key-value pairs through POST requests.
 */
@RestController()
@RequestMapping("/api/v1")
public class ServiceController
{
    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);
    private final RestService restService;
    private final DataFetchService dataFetchService;

    //Inject the service
    @Autowired
    public ServiceController(final RestService restService, final DataFetchService dataFetchService)
    {
        this.restService = restService;
        this.dataFetchService = dataFetchService;
    }

    @Value("${ilp.service.url}")
    public URL serviceUrl;

    @GetMapping("/")
    public String index()
    {
        return "<html><body>" +
                "<h1>Welcome from ILP</h1>" +
                "<h4>ILP-REST-Service-URL:</h4> <a href=\"" + serviceUrl + "\" target=\"_blank\"> " + serviceUrl+ " </a>" +
                "</body></html>";
    }

    @GetMapping("/uid")
    public String uid()
    {
        return "s2487866";
    }

    @PostMapping("/distanceTo")
    public Double distanceTo(@Valid @RequestBody PositionsRequest request)
    {
        return  restService.distanceTo(request);
    }

    @PostMapping("/isCloseTo")
    public boolean isCloseTo(@Valid @RequestBody PositionsRequest request)
    {
        return  restService.isCloseTo(request);
    }

    @PostMapping("/nextPosition")
    public String nextPosition(@Valid @RequestBody PositionAngleRequest request)
    {
        return  restService.nextPosition(request);
    }

    @PostMapping("/isInRegion")
    public boolean isInRegion(@Valid @RequestBody PositionRegionRequest request)
    {
        return  restService.isInRegion(request);
    }

    @GetMapping("/dronesWithCooling/{state}")
    public ArrayList<Integer> dronesWithCooling(@PathVariable("state") boolean state)
    {
        return restService.droneWithCooling(dataFetchService.getDrones(), state);
    }

    @GetMapping("/droneDetails/{id}")
    public ResponseEntity<Drone> droneDetails(@PathVariable("id") Integer id)
    {
        Drone drone = restService.droneDetails(dataFetchService.getDrones(),id);

        if (drone == null)
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(drone);
    }



}
