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

//A class to store game state
public class GameModel {

    private final ArrayList<Location> locationsList = new ArrayList<>();
    private final HashMap<String, HashSet<Location>> pathsMap = new HashMap<>();
    private final ArrayList<Player> playerList = new ArrayList<>();
    private final HashMap<String,HashSet<GameAction>> actionsList = new HashMap<String, HashSet<GameAction>>();
    private File entitiesFile;
    private File actionsFile;
    private Location startLocation;

    public void loadEntitiesFile(File inputEntitiesFile){
        entitiesFile = inputEntitiesFile;
    }
    public void loadActionsFile(File inputActionsFile) { actionsFile = inputActionsFile; }

    public Graph parseEntities() throws FileNotFoundException, ParseException {
        Parser entityParser = new Parser();
        FileReader fileReader = new FileReader(entitiesFile);
        entityParser.parse(fileReader);
        return entityParser.getGraphs().get(0);
    }

    public Document parseActions() throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return docBuild.parse(actionsFile);
    }

    //HANDLE ENTITIES
    public void storeLocations(Graph wholeDocument) {
        ArrayList<Graph> graphSections = wholeDocument.getSubgraphs();
        ArrayList<Graph> graphLocations = graphSections.get(0).getSubgraphs();
        Node locationDetails;
        String locationName;
        String locationDescription;
        for (Graph graphLocation : graphLocations) {
            locationDetails = graphLocation.getNodes(false).get(0);
            locationName = locationDetails.getId().getId();
            locationDescription = locationDetails.getAttribute("description");
            Location gameLocation = new Location(locationName, locationDescription);
            //add artefacts, characters, furniture
            processLocationObjects(graphLocation, gameLocation);
            locationsList.add(gameLocation);
        }
        //start location is always the first in the list i.e. entities file
        startLocation = locationsList.get(0);
        storePaths(graphSections);
    }

    private void processLocationObjects(Graph graphLocation, Location gameLocation){
        ArrayList<Graph> subGraphs = graphLocation.getSubgraphs();
        ArrayList<Node> objectNodes;
        for(Graph subGraph : subGraphs){
            if(Objects.equals(subGraph.getId().getId(), "artefacts")){
                objectNodes = subGraph.getNodes(true);
                storeObjects(objectNodes, gameLocation, "artefacts");
            }
            if(Objects.equals(subGraph.getId().getId(), "furniture")){
                objectNodes = subGraph.getNodes(true);
                storeObjects(objectNodes, gameLocation, "furniture");
            }
            if(Objects.equals(subGraph.getId().getId(), "characters")){
                objectNodes = subGraph.getNodes(true);
                storeObjects(objectNodes, gameLocation, "characters");
            }
        }
    }

    private void storeObjects(ArrayList<Node> nodesList, Location location, String objType){
        String objectName;
        String objectDescription;
        for (Node objectNode : nodesList) {
            objectName = objectNode.getId().getId();
            objectDescription = objectNode.getAttribute("description");
            if(Objects.equals(objType, "artefacts")) {
                Artefact gameArtefact = new Artefact(objectName, objectDescription);
                location.addEntity(gameArtefact);
            }
            if(Objects.equals(objType, "furniture")) {
                Furniture gameFurniture = new Furniture(objectName, objectDescription);
                location.addEntity(gameFurniture);
            }
            if(Objects.equals(objType, "characters")) {
                Character gameCharacter = new Character(objectName, objectDescription);
                location.addEntity(gameCharacter);
            }
        }
    }

    private void storePaths(ArrayList<Graph> graphSections){
        //Paths are always at the end of the entities file
        ArrayList<Edge> locationPaths = graphSections.get(1).getEdges();
        for(Edge locationPath : locationPaths){
            Node fromLocation = locationPath.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Location startLocation = getLocationFromName(fromName);
            Node toLocation = locationPath.getTarget().getNode();
            String toName = toLocation.getId().getId();
            Location endLocation = getLocationFromName(toName);
            HashSet<Location> locationHashSet;
            //if it's a location we've seen before
            if(pathsMap.containsKey(fromName)){
                locationHashSet = pathsMap.get(fromName);
            }
            //if it's a new location
            else{
                locationHashSet = new HashSet<>();
            }
            locationHashSet.add(endLocation);
            pathsMap.put(startLocation.getName(),locationHashSet);
        }
    }

    public Location getLocationFromName(String locationName){
        for(Location gameLocation : locationsList){
            if(Objects.equals(gameLocation.getName(), locationName)){
                return gameLocation;
            }
        }
        return null;
    }

    public void createPlayerFromName (String playerName){
        Player newPlayer = new Player(this, playerName);
        playerList.add(newPlayer);
    }

    public Player getPlayerFromName(String playerName){
        for(Player registeredPlayer : playerList){
            if(Objects.equals(registeredPlayer.getName(), playerName)){
                return registeredPlayer;
            }
        }
        return null;
    }

    public ArrayList<Player> getPlayersInGame(){
        return playerList;
    }

    public void updatePlayerLocation(String playerName, Location newLocation){
        for(Player registeredPlayer : playerList){
            if(Objects.equals(registeredPlayer.getName(), playerName)){
                registeredPlayer.setCurrentLocation(newLocation);
            }
        }
    }

    public Location getStartLocation(){
        return startLocation;
    }

    public HashSet<Location> getDestinations(String startLocationName){
        return pathsMap.get(startLocationName);
    }

    //HANDLE ACTIONS

    public void storeActions(Document document){
        Element documentRoot = document.getDocumentElement();
        NodeList gameActions = documentRoot.getChildNodes();
        for(int i = 0; i < gameActions.getLength(); i++) {
            //only the odd elements are actually actions
            if(i % 2 != 0) {
                Element gameAction = (Element) gameActions.item(i);
                storeEachAction(gameAction);
            }
        }
    }

    private void storeEachAction(Element actionElement) {
        GameAction gameAction = new GameAction();
        storeEntities(actionElement, gameAction, "subjects");
        storeEntities(actionElement, gameAction, "produced");
        storeEntities(actionElement, gameAction, "consumed");
        storeActionNarration(actionElement, gameAction);
        storeActionTriggers(actionElement, gameAction);
    }

    private void storeEntities(Element actionElement, GameAction gameAction, String tagName){
        Element actionEntities = (Element)actionElement.getElementsByTagName(tagName).item(0);
        NodeList entityNodeList = actionEntities.getElementsByTagName("entity");
        if(entityNodeList.getLength() > 0){
            for(int i = 0; i < entityNodeList.getLength(); i++){
                String entityName = entityNodeList.item(i).getTextContent();
                addActionEntities(tagName, entityName, gameAction);
            }
        }
    }

    private void addActionEntities(String tagName, String entityName, GameAction gameAction){
        //tagName corresponds to entity file
        switch (tagName) {
            case "subjects" -> {
                Subject actionSubject = new Subject(entityName);
                gameAction.addSubjectEntity(actionSubject);
            }
            case "produced" -> {
                Product actionProduct = new Product(entityName);
                gameAction.addProductEntity(actionProduct);
            }
            case "consumed" -> {
                Consumable actionConsumable = new Consumable(entityName);
                gameAction.addConsumableEntity(actionConsumable);
            }
        }
    }

    private void storeActionNarration(Element actionElement, GameAction gameAction){
        Element actionNarration = (Element)actionElement.getElementsByTagName("narration").item(0);
        gameAction.setNarration(actionNarration.getTextContent());
    }

    private void storeActionTriggers(Element actionElement, GameAction gameAction){
        Element actionTriggers = (Element)actionElement.getElementsByTagName("triggers").item(0);
        NodeList triggersNodeList = actionTriggers.getElementsByTagName("keyphrase");
        for(int i = 0; i < triggersNodeList.getLength(); i++){
            String triggerPhrase = triggersNodeList.item(i).getTextContent();
            addActionToList(triggerPhrase, gameAction);
        }
    }

    private void addActionToList(String keyPhrase, GameAction gameAction){
        HashSet<GameAction> gameActionsHashSet;
        if(actionsList.containsKey(keyPhrase)){
            gameActionsHashSet = actionsList.get(keyPhrase);
        }
        else{
            gameActionsHashSet = new HashSet<>();
        }
        gameActionsHashSet.add(gameAction);
        actionsList.put(keyPhrase, gameActionsHashSet);
    }

    public HashSet<GameAction> getGameActionHashSet (String keyPhrase){
        return actionsList.get(keyPhrase);
    }

    public void addEntityToLocation (String locationName, GameEntity consumedEntity){
        for(Location gameLocation : locationsList){
            if(Objects.equals(gameLocation.getName(), locationName)){
                gameLocation.addEntity(consumedEntity);
            }
        }
    }

    public GameEntity getEntityFromItsCurrentLocation (String entityName) {
        for (Location gameLocation : locationsList) {
            GameEntity movedEntity = gameLocation.getEntityFromName(entityName);
            if(movedEntity != null){
                gameLocation.removeEntity(entityName);
                return movedEntity;
            }
        }
        return null;
    }

    public void updateLocationPath(String fromName, String toName, boolean isNewPath){
        Location fromLocation = getLocationFromName(fromName);
        Location toLocation = getLocationFromName(toName);
        if(fromLocation == null || toLocation == null){
            return;
        }
        HashSet<Location> destinationsSet = pathsMap.get(fromLocation.getName());
        if(destinationsSet == null){
            return;
        }
        //new path to distinct location
        if(isNewPath){
            destinationsSet.add(toLocation);
        }
        else{
            destinationsSet.remove(toLocation);
        }
    }
}
