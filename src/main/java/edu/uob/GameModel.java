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
            //add artefacts, characters, furniture
            findArtefacts(graphLocation, location);
            locationsList.add(location);
        }
    }

    //improve names of these methods
    public void findArtefacts(Graph graphLocation, Location location){
        ArrayList<Graph> subGraphs = graphLocation.getSubgraphs();
        ArrayList<Node> artefactNodes;
        for(Graph graph : subGraphs){
            if(Objects.equals(graph.getId().getId(), "artefacts")){
                artefactNodes = graph.getNodes(true);
                storeArtefacts(artefactNodes, location);
            }
        }
    }

    public void storeArtefacts(ArrayList<Node> artefactNodes, Location location){
        String artefactName;
        String artefactDescription;
        for (Node artefactNode : artefactNodes) {
            artefactName = artefactNode.getId().getId();
            artefactDescription = artefactNode.getAttribute("description");
            Artefact artefact = new Artefact(artefactName, artefactDescription);
            location.addArtefact(artefact);
        }
    }

    public void storeFurniture(Graph wholeDocument, Location location){

    }

    public void storeCharacters(Graph wholeDocument, Location location){

    }

    public Location getLocationFromName(String locationName){
        for(Location location : locationsList){
            if(Objects.equals(location.getName(), locationName)){
                return location;
            }
        }
        return null;
    }


    //temporary print method
    public void printLocations() {
        for(Location location: locationsList){
            System.out.println("Location name: " + location.getName());
            System.out.println("Location description: " + location.getDescription());
        }
    }





}
