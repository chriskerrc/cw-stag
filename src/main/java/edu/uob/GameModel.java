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
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return documentBuilder.parse(actionsFile);
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
        startLocation = locationsList.get(0);
        storePaths(graphSections);
    }

    //maybe simplify this method
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

    //maybe simplify this method
    private void storeObjects(ArrayList<Node> objectNodes, Location gameLocation, String objectType){
        String objectName;
        String objectDescription;
        for (Node objectNode : objectNodes) {
            objectName = objectNode.getId().getId();
            objectDescription = objectNode.getAttribute("description");
            if(Objects.equals(objectType, "artefacts")) {
                Artefact gameArtefact = new Artefact(objectName, objectDescription);
                gameLocation.addEntity(gameArtefact);
            }
            if(Objects.equals(objectType, "furniture")) {
                Furniture gameFurniture = new Furniture(objectName, objectDescription);
                gameLocation.addEntity(gameFurniture);
            }
            if(Objects.equals(objectType, "characters")) {
                Character gameCharacter = new Character(objectName, objectDescription);
                gameLocation.addEntity(gameCharacter);
            }
        }
    }

    private void storePaths(ArrayList<Graph> graphSections){
        ArrayList<Edge> locationPaths = graphSections.get(1).getEdges();
        for(Edge locationPath : locationPaths){
            Node fromLocation = locationPath.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Location startLocation = getLocationFromName(fromName);
            Node toLocation = locationPath.getTarget().getNode();
            String toName = toLocation.getId().getId();
            Location endLocation = getLocationFromName(toName);
            HashSet<Location> locationHashSet;
            if(pathsMap.containsKey(fromName)){
                locationHashSet = pathsMap.get(fromName);
            }
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

    public HashSet<Location> getDestinationsFromLocation(String startLocationName){
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

    //break up this giant method
    private void storeEachAction(Element actionElement) {
        GameAction gameAction = new GameAction();
        Element actionSubjects = (Element)actionElement.getElementsByTagName("subjects").item(0);
        NodeList subjectsNodeList = actionSubjects.getElementsByTagName("entity");
        for(int i = 0; i < subjectsNodeList.getLength(); i++){
            String subjectName = subjectsNodeList.item(i).getTextContent();
            Subject actionSubject = new Subject(subjectName, null);
            gameAction.addSubjectEntity(actionSubject);
        }
        Element actionProducts = (Element)actionElement.getElementsByTagName("produced").item(0);
        NodeList productsNodeList = actionProducts.getElementsByTagName("entity");
        if(productsNodeList.getLength() > 0){
            for(int i = 0; i < productsNodeList.getLength(); i++){
                String productName = productsNodeList.item(i).getTextContent();
                Product actionProduct = new Product(productName, null);
                gameAction.addProductEntity(actionProduct);
            }
        }
        Element actionConsumables = (Element)actionElement.getElementsByTagName("consumed").item(0);
        NodeList consumablesNodeList = actionConsumables.getElementsByTagName("entity");
        if(consumablesNodeList.getLength() > 0){
            for(int i = 0; i < consumablesNodeList.getLength(); i++){
                String consumableName = consumablesNodeList.item(i).getTextContent();
                Consumable actionConsumable = new Consumable(consumableName);
                gameAction.addConsumableEntity(actionConsumable);
            }
        }
        Element actionNarration = (Element)actionElement.getElementsByTagName("narration").item(0);
        gameAction.setNarration(actionNarration.getTextContent());
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

    public void updateLocationPath(String originName, String destinationName, boolean pathIsCreated){
        Location fromLocation = getLocationFromName(originName);
        Location toLocation = getLocationFromName(destinationName);
        if(fromLocation == null || toLocation == null){
            return;
        }
        HashSet<Location> destinations = pathsMap.get(fromLocation.getName());
        if(destinations == null){
            return;
        }
        if(pathIsCreated){
            destinations.add(toLocation);
        }
        else{
            destinations.remove(toLocation);
        }
    }
}
