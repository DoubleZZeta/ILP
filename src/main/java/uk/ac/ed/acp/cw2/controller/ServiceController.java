package uk.ac.ed.acp.cw2.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.acp.cw2.data.*;
import uk.ac.ed.acp.cw2.service.DataFetchService;
import uk.ac.ed.acp.cw2.service.RestService;

import java.net.URL;
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

    @GetMapping("/queryAsPath/{attribute-name}/{attribute-value}")
    public ArrayList<Integer> queryAsPath(@PathVariable("attribute-name") String attributeName, @PathVariable("attribute-value")  String attributeValue)
    {
        ArrayList<QueryRequest> queries = new ArrayList<>();
        QueryRequest queryRequest = new QueryRequest(attributeName,"=", attributeValue);
        queries.add(queryRequest);

        return restService.query(dataFetchService.getDrones(), queries);
    }

    //TODO all drone ids need to be string
    @PostMapping("/query")
    public ArrayList<Integer> query(@RequestBody ArrayList<QueryRequest> queries)
    {
        return restService.query(dataFetchService.getDrones(), queries);
    }

    //TODO all drone ids need to be string
    @PostMapping("/queryAvailableDrones")
    public ArrayList<Integer> queryAvailableDrones(@RequestBody ArrayList<MedicineDispatchRequest> queries)
    {
        return restService.queryAvailableDrones(dataFetchService.getDrones(), dataFetchService.getServicePointsDrones(), queries);
    }

    @PostMapping("/calcDeliveryPath")
    public ReturnedPath calcDeliveryPath(@RequestBody ArrayList<MedicineDispatchRequest> queries)
    {
        return null;
    }




}
