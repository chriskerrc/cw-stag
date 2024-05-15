package edu.uob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
//To handle entities
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
//To handle actions
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GameModel {

    private ArrayList<Location> locationsList = new ArrayList<>();
    private HashMap<Location, Location> pathsMap = new HashMap<>();

    private ArrayList<Player> playerList = new ArrayList<>();

    private HashMap<String,HashSet<GameAction>> actionsList = new HashMap<String, HashSet<GameAction>>();

    private File entitiesFile;
    private File actionsFile;

    private Location startLocation;

    public void loadEntitiesFile(File inputEntitiesFile){
        entitiesFile = inputEntitiesFile;
    }
    public void loadActionsFile(File inputActionsFile) { actionsFile = inputActionsFile; }

    public Graph parseEntities() throws FileNotFoundException, ParseException {
        Parser parser = new Parser();
        FileReader reader = new FileReader(entitiesFile);
        parser.parse(reader);
        return parser.getGraphs().get(0);
    }

    public Document parseActions() throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(actionsFile);
    }

    //HANDLE ENTITIES
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
        startLocation = locationsList.get(0);
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
                location.addEntity(artefact);
            }
            if(Objects.equals(objectType, "furniture")) {
                Furniture furniture = new Furniture(objectName, objectDescription);
                location.addEntity(furniture);
            }
            if(Objects.equals(objectType, "characters")) {
                Character character = new Character(objectName, objectDescription);
                location.addEntity(character);
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

    public void createPlayerFromName (String playerName){
        Player newPlayer = new Player(this, playerName);
        playerList.add(newPlayer);
    }

    public Player getPlayerFromName(String playerName){
        for(Player player : playerList){
            if(Objects.equals(player.getName(), playerName)){
                return player;
            }
        }
        return null;
    }

    public void updatePlayerLocation(String playerName, Location newLocation){
        for(Player player : playerList){
            if(Objects.equals(player.getName(), playerName)){
                player.setCurrentLocation(newLocation);
            }
        }
    }

    public Location getStartLocation(){
        return startLocation;
    }

    public Location getDestinationFromLocation(Location startLocation){
        return pathsMap.get(startLocation);
    }

    //HANDLE ACTIONS

    public void storeActions(Document document){
        Element root = document.getDocumentElement();
        NodeList actions = root.getChildNodes();
        for(int i = 0; i < actions.getLength(); i++) {
            //only the odd elements are actually actions
            if(i % 2 != 0) {
                Element action = (Element) actions.item(i);
                processAction(action);
            }
        }
    }

    //break up this giant method
    private void processAction(Element actionElement) {
        GameAction gameAction = new GameAction();
        Element subjects = (Element)actionElement.getElementsByTagName("subjects").item(0);
        NodeList subjectsNodeList = subjects.getElementsByTagName("entity");
        for(int i = 0; i < subjectsNodeList.getLength(); i++){
            String subjectName = subjectsNodeList.item(i).getTextContent();
            Subject subject = new Subject(subjectName, null);
            gameAction.addSubjectEntity(subject);
        }
        Element products = (Element)actionElement.getElementsByTagName("produced").item(0);
        NodeList productsNodeList = products.getElementsByTagName("entity");
        if(productsNodeList.getLength() > 0){
            for(int i = 0; i < productsNodeList.getLength(); i++){
                String productName = productsNodeList.item(i).getTextContent();
                Product product = new Product(productName, null);
                gameAction.addProductEntity(product);
            }
        }
        Element consumables = (Element)actionElement.getElementsByTagName("consumed").item(0);
        NodeList consumablesNodeList = consumables.getElementsByTagName("entity");
        if(consumablesNodeList.getLength() > 0){
            for(int i = 0; i < consumablesNodeList.getLength(); i++){
                String consumableName = consumablesNodeList.item(i).getTextContent();
                Consumable consumable = new Consumable(consumableName, null);
                gameAction.addConsumableEntity(consumable);
            }
        }
        Element narration = (Element)actionElement.getElementsByTagName("narration").item(0);
        gameAction.setNarration(narration.getTextContent());
        //for each keyphrase, add hashset of gameActions
        Element triggers = (Element)actionElement.getElementsByTagName("triggers").item(0);
        NodeList triggersNodeList = triggers.getElementsByTagName("keyphrase");
        for(int i = 0; i < triggersNodeList.getLength(); i++){
            String triggerPhrase = triggersNodeList.item(i).getTextContent();
            addActionToList(triggerPhrase, gameAction);
        }
    }

    private void addActionToList(String keyphrase, GameAction gameAction){
        HashSet<GameAction> gameActionsHashSet;
        if(actionsList.containsKey(keyphrase)){
            gameActionsHashSet = actionsList.get(keyphrase);
        }
        else{
            gameActionsHashSet = new HashSet<>();
        }
        gameActionsHashSet.add(gameAction);
        actionsList.put(keyphrase, gameActionsHashSet);
    }

    public HashSet<GameAction> getGameActionHashSet (String keyphrase){
        return actionsList.get(keyphrase);
    }

    public void addEntityToStoreroom (GameEntity consumedEntity){
        for(Location location : locationsList){
            if(Objects.equals(location.getName(), "storeroom")){
                location.addEntity(consumedEntity);
            }
        }
    }

}
