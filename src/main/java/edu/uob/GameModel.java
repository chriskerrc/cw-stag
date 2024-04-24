package edu.uob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

public class GameModel {

    ArrayList<Location> locationsList = new ArrayList<>();
    HashMap<Location, Location> pathsMap = new HashMap<>();

    public Graph parseEntities() throws FileNotFoundException, ParseException {
        Parser parser = new Parser();
        FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
        parser.parse(reader);
        return parser.getGraphs().get(0);
    }

    public void storeLocations(Graph wholeDocument) {
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
            processLocationObjects(graphLocation, location);
            locationsList.add(location);
        }
        storePaths(graphSections);
    }


    //maybe simplify this method
    private void processLocationObjects(Graph graphLocation, Location location){
        ArrayList<Graph> subGraphs = graphLocation.getSubgraphs();
        ArrayList<Node> objectNodes;
        for(Graph graph : subGraphs){
            if(Objects.equals(graph.getId().getId(), "artefacts")){
                objectNodes = graph.getNodes(true);
                storeObjects(objectNodes, location, "artefacts");
            }
            if(Objects.equals(graph.getId().getId(), "furniture")){
                objectNodes = graph.getNodes(true);
                storeObjects(objectNodes, location, "furniture");
            }
            if(Objects.equals(graph.getId().getId(), "characters")){
                objectNodes = graph.getNodes(true);
                storeObjects(objectNodes, location, "characters");
            }
        }
    }

    //maybe simplify this method
    private void storeObjects(ArrayList<Node> objectNodes, Location location, String objectType){
        String objectName;
        String objectDescription;
        for (Node objectNode : objectNodes) {
            objectName = objectNode.getId().getId();
            objectDescription = objectNode.getAttribute("description");
            if(Objects.equals(objectType, "artefacts")) {
                Artefact artefact = new Artefact(objectName, objectDescription);
                location.addArtefact(artefact);
            }
            if(Objects.equals(objectType, "furniture")) {
                Furniture furniture = new Furniture(objectName, objectDescription);
                location.addFurniture(furniture);
            }
            if(Objects.equals(objectType, "characters")) {
                Character character = new Character(objectName, objectDescription);
                location.addCharacter(character);
            }
        }
    }

    //the HashMap<Location,Location> assumes each location will only have one destination
    //this is OK for basic entities but will need to be refactored for extended entities
    //for example, forest has both cabin and riverbank as destinations
    private void storePaths(ArrayList<Graph> graphSections){
        ArrayList<Edge> paths = graphSections.get(1).getEdges();
        for(Edge edge : paths){
            Node fromLocation = edge.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Location startLocation = getLocationFromName(fromName);
            Node toLocation = edge.getTarget().getNode();
            String toName = toLocation.getId().getId();
            Location endLocation = getLocationFromName(toName);
            pathsMap.put(startLocation,endLocation);
        }
    }

    public Location getLocationFromName(String locationName){
        for(Location location : locationsList){
            if(Objects.equals(location.getName(), locationName)){
                return location;
            }
        }
        return null;
    }

    public Location getDestinationsFromLocation(Location startLocation){
        return pathsMap.get(startLocation);
    }

}
