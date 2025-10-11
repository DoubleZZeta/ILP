package uk.ac.ed.acp.cw2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.acp.cw2.data.PositionAngleRequest;
import uk.ac.ed.acp.cw2.data.PositionRegionRequest;
import uk.ac.ed.acp.cw2.data.PositionsRequest;
import uk.ac.ed.acp.cw2.service.RestService;

import java.net.URL;
import java.time.Instant;

/**
 * Controller class that handles various HTTP endpoints for the application.
 * Provides functionality for serving the index page, retrieving a static UUID,
 * and managing key-value pairs through POST requests.
 */
@RestController()
@RequestMapping("/api/v1")
public class ServiceController
{
    @Autowired
    RestService restService;

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

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
    public String distanceTo(@Valid @RequestBody PositionsRequest request)
    {
        return  restService.distanceTo(request);
    }

    @PostMapping("/isCloseTo")
    public String isCloseTo(@Valid @RequestBody PositionsRequest request)
    {
        return  restService.isCloseTo(request);
    }

    @PostMapping("/nextPosition")
    public String nextPosition(@Valid @RequestBody PositionAngleRequest request)
    {
        return  restService.nextPosition(request);
    }

    @PostMapping("/isInRegion")
    public String isInRegion(@Valid @RequestBody PositionRegionRequest request)
    {
        return  restService.isInRegion(request);
    }
}
