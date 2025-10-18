package uk.ac.ed.acp.cw2.unit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.ed.acp.cw2.data.*;

import java.util.ArrayList;
import java.util.Set;

/**
 * Test class that conduct unit test for the DTO.
 * Checks if the DTO can identify the invalid data.
 */

public class DTOTests
{
    private static Validator validator;

    @BeforeAll
    static void setup()
    {
        // Build the default validator factory
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void position_should_returnNoFaults_whenValidLngLatAreGiven()
    {
        Position position = new Position(0.0,0.0);

        Set<ConstraintViolation<Position>> violations = validator.validate(position);

        assert(violations.isEmpty());
    }

    @Test
    public void position_should_returnFaults_whenLngIsNull()
    {
        Position position = new Position(null,0.0);

        Set<ConstraintViolation<Position>> violations = validator.validate(position);

        assert(violations.size() == 1);
    }

    @Test
    public void position_should_returnFaults_whenLatIsNull()
    {
        Position position = new Position(0.0,null);

        Set<ConstraintViolation<Position>> violations = validator.validate(position);

        assert(violations.size() == 1);
    }

    @Test
    public void position_should_returnFaults_whenLngIsOutOfRange()
    {
        Position position = new Position(181.0,0.0);

        Set<ConstraintViolation<Position>> violations = validator.validate(position);

        assert(violations.size() == 1);
    }

    @Test
    public void position_should_returnFaults_whenLatIsOutOfRange()
    {
        Position position = new Position(180.0,-91.0);

        Set<ConstraintViolation<Position>> violations = validator.validate(position);

        assert(violations.size() == 1);
    }

    @Test
    public void positionsRequest_should_returnNoFaults_whenValidPositionAreGiven()
    {
        Position position1 = new Position(0.0,0.0);
        Position position2 = new Position(0.0,0.0);
        PositionsRequest positionsRequest = new PositionsRequest(position1,position2);

        Set<ConstraintViolation<PositionsRequest>> violations = validator.validate(positionsRequest);

        assert(violations.isEmpty());
    }

    @Test
    public void positionsRequest_should_returnFaults_whenPositionOneIsNull()
    {
        Position position2 = new Position(0.0,0.0);
        PositionsRequest positionsRequest = new PositionsRequest(null,position2);

        Set<ConstraintViolation<PositionsRequest>> violations = validator.validate(positionsRequest);

        assert(violations.size() == 1);
    }

    @Test
    public void positionsRequest_should_returnFaults_whenPositionTwoIsNull()
    {
        Position position1 = new Position(0.0,0.0);
        PositionsRequest positionsRequest = new PositionsRequest(position1,null);

        Set<ConstraintViolation<PositionsRequest>> violations = validator.validate(positionsRequest);

        assert(violations.size() == 1);
    }

    @Test
    public void positionsRequest_should_returnFaults_whenPositionOneIsInvalid()
    {
        Position position1 = new Position(0.0,91.0);
        Position position2 = new Position(-180.0,0.0);
        PositionsRequest positionsRequest = new PositionsRequest(position1,position2);

        Set<ConstraintViolation<PositionsRequest>> violations = validator.validate(positionsRequest);
        System.out.println(violations);

        assert(violations.size() == 1);
    }

    @Test
    public void positionsRequest_should_returnFaults_whenPositionTwoIsInvalid()
    {
        Position position1 = new Position(0.0,0.0);
        Position position2 = new Position(-180.1,0.0);
        PositionsRequest positionsRequest = new PositionsRequest(position1,position2);

        Set<ConstraintViolation<PositionsRequest>> violations = validator.validate(positionsRequest);

        assert(violations.size() == 1);
    }

    @Test
    public void positionAngleRequest_should_returnNoFaults_whenValidPositionAndAngleAreGiven()
    {
        Position position = new Position(0.0,0.0);
        Double angle = 0.0;
        PositionAngleRequest positionAngleRequest = new PositionAngleRequest(position,angle);

        Set<ConstraintViolation<PositionAngleRequest>> violations = validator.validate(positionAngleRequest);

        assert(violations.isEmpty());
    }

    @Test
    public void positionAngleRequest_should_returnFaults_whenPositionIsNull()
    {
        Double angle = 0.0;
        PositionAngleRequest positionAngleRequest = new PositionAngleRequest(null,angle);

        Set<ConstraintViolation<PositionAngleRequest>> violations = validator.validate(positionAngleRequest);

        assert(violations.size() == 1);
    }

    @Test
    public void positionAngleRequest_should_returnFaults_whenAngleIsNull()
    {
        Position position = new Position(0.0,0.0);
        PositionAngleRequest positionAngleRequest = new PositionAngleRequest(position,null);

        Set<ConstraintViolation<PositionAngleRequest>> violations = validator.validate(positionAngleRequest);

        assert(violations.size() == 1);
    }

    @Test
    public void positionAngleRequest_should_returnFaults_whenPositionIsInvalid()
    {
        Position position = new Position(181.0,91.0);
        Double angle = 0.0;
        PositionAngleRequest positionAngleRequest = new PositionAngleRequest(position,angle);

        Set<ConstraintViolation<PositionAngleRequest>> violations = validator.validate(positionAngleRequest);

        assert(violations.size() == 2);
    }

    @Test
    public void positionAngleRequest_should_returnFaults_whenAngleIsOutOfRange()
    {
        Position position = new Position(180.0,90.0);
        Double angle = 360.0;
        PositionAngleRequest positionAngleRequest = new PositionAngleRequest(position,angle);

        Set<ConstraintViolation<PositionAngleRequest>> violations = validator.validate(positionAngleRequest);

        assert(violations.size() == 1);
    }

    @Test
    public void positionAngleRequest_should_returnFaults_whenAngleIsNotInTheSixteenDirections()
    {
        Position position = new Position(180.0,90.0);
        Double angle = 91.0;
        PositionAngleRequest positionAngleRequest = new PositionAngleRequest(position,angle);

        Set<ConstraintViolation<PositionAngleRequest>> violations = validator.validate(positionAngleRequest);

        assert(violations.size() == 1);
    }

    @Test
    public void region_should_returnNoFaults_whenValidNameAndVerticesAreGiven()
    {
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.1,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);

        Set<ConstraintViolation<Region>> violations = validator.validate(region);

        assert(violations.isEmpty());
    }

    @Test
    public void region_should_returnFaults_whenNameIsNull()
    {
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.1,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region(null,vertices);

        Set<ConstraintViolation<Region>> violations = validator.validate(region);

        assert(violations.size() == 1);
    }

    @Test
    public void region_should_returnFaults_whenVerticesIsNull()
    {

        Region region = new Region("Name",null);
        Set<ConstraintViolation<Region>> violations = validator.validate(region);

        assert(violations.size() == 1);
    }

    @Test
    public void region_should_returnFaults_whenVerticesDoesNotFormAPolygon()
    {
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);

        Set<ConstraintViolation<Region>> violations = validator.validate(region);

        assert(violations.size() == 1);
    }

    @Test
    public void region_should_returnFaults_whenVerticesDoesNotFormAClosedPolygon()
    {
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.1,0.0));
        Region region = new Region("Name",vertices);

        Set<ConstraintViolation<Region>> violations = validator.validate(region);

        assert(violations.size() == 1);
    }

    @Test
    public void positionRegionRequest_should_returnNoFaults_whenValidPositionAndRegionAreGiven()
    {
        Position position = new Position(180.0,90.0);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.1,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest positionRegionRequest = new PositionRegionRequest(position,region);

        Set<ConstraintViolation<PositionRegionRequest>> violations = validator.validate(positionRegionRequest);

        assert(violations.isEmpty());
    }

    @Test
    public void positionRegionRequest_should_returnFaults_whenPositionIsNull()
    {
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.1,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest positionRegionRequest = new PositionRegionRequest(null,region);

        Set<ConstraintViolation<PositionRegionRequest>> violations = validator.validate(positionRegionRequest);

        assert(violations.size() == 1);
    }

    @Test
    public void positionRegionRequest_should_returnFaults_whenRegionIsNull()
    {
        Position position = new Position(180.0,90.0);
        PositionRegionRequest positionRegionRequest = new PositionRegionRequest(position,null);

        Set<ConstraintViolation<PositionRegionRequest>> violations = validator.validate(positionRegionRequest);

        assert(violations.size() == 1);
    }

    @Test
    public void positionRegionRequest_should_returnFaults_whenPositionIsInvalid()
    {
        Position position = new Position(181.0,91.0);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.1,0.1));
        vertices.add(new Position(0.1,0.0));
        vertices.add(new Position(0.0,0.0));
        Region region = new Region("Name",vertices);
        PositionRegionRequest positionRegionRequest = new PositionRegionRequest(position,region);

        Set<ConstraintViolation<PositionRegionRequest>> violations = validator.validate(positionRegionRequest);

        assert(violations.size() == 2);
    }
    @Test
    public void positionRegionRequest_should_returnFaults_whenRegionIsInvalid()
    {
        Position position = new Position(180.0,90.0);
        ArrayList<Position> vertices =  new ArrayList<>();
        vertices.add(new Position(0.0,0.0));
        vertices.add(new Position(0.0,0.1));
        vertices.add(new Position(0.1,0.1));

        Region region = new Region("Name",vertices);
        PositionRegionRequest positionRegionRequest = new PositionRegionRequest(position,region);

        Set<ConstraintViolation<PositionRegionRequest>> violations = validator.validate(positionRegionRequest);

        assert(violations.size() == 2);
    }
}
