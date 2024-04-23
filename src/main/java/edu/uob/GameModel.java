package edu.uob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

public class GameModel {

    ArrayList<Location> locationsList = new ArrayList<>();

    public Graph parseEntities() throws FileNotFoundException, ParseException {
        Parser parser = new Parser();
        FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
        parser.parse(reader);
        return parser.getGraphs().get(0);
    }

    //currently, these locations are stored empty (no artefacts or furniture)
    public void storeLocations(Graph wholeDocument) throws FileNotFoundException, ParseException {
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

    public void storeArtefacts(Graph wholeDocument, Location location){
        //duplicated code
        ArrayList<Graph> graphSections = wholeDocument.getSubgraphs();
        //go to first location (update this so it goes to the given location instead)
        //get name of location
        ArrayList<Graph> graphLocations = graphSections.get(0).getSubgraphs();
        Graph firstLocation = graphLocations.get(0);
        ArrayList<Graph> subGraphs = firstLocation.getSubgraphs();
        ArrayList<Node> artefactNodes = new ArrayList<>();
        for(Graph graph : subGraphs){
            if(Objects.equals(graph.getId().getId(), "artefacts")){
                artefactNodes = graph.getNodes(true);
            }
        }
        String firstNodeName = artefactNodes.get(0).getId().getId();
        String secondNodeName = artefactNodes.get(1).getId().getId();
        String firstNodeDescription = artefactNodes.get(0).getAttribute("description");
        System.out.println("First artefact name: " + firstNodeName);
        System.out.println("First artefact description: " + firstNodeDescription);
        System.out.println("Second artefact name: " + secondNodeName);
        System.out.println("Artefact nodes: " + artefactNodes);

    }

    public void storeFurniture(Graph wholeDocument, Location location){

    }

    public void storeCharacters(Graph wholeDocument, Location location){

    }

    //temporary print method
    public void printLocations() {
        for(Location location: locationsList){
            System.out.println("Location name: " + location.getName());
            System.out.println("Location description: " + location.getDescription());
        }
    }





}
