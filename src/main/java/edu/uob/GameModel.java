package edu.uob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

public class GameModel {

    ArrayList<Location> locationsList = new ArrayList<>();

    //currently, these locations are stored empty (no artefacts or furniture)
    public void StoreLocations() throws FileNotFoundException, ParseException {
        Parser parser = new Parser();
        FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> graphSections = wholeDocument.getSubgraphs();
        ArrayList<Graph> graphLocations = graphSections.get(0).getSubgraphs();
        Graph graphLocation;
        Node locationDetails;
        String locationName;
        String locationDescription;
        for (Graph graph : graphLocations) {
            graphLocation = graph;
            locationDetails = graphLocation.getNodes(false).get(0);
            locationName = locationDetails.getId().getId();
            locationDescription = locationDetails.getAttribute("description");
            Location location = new Location(locationName, locationDescription);
            locationsList.add(location);
        }
    }

    //temporary print method
    public void PrintLocations() {
        for(Location location: locationsList){
            System.out.println("Location name: " + location.getName());
            System.out.println("Location description: " + location.getDescription());
        }
    }





}
