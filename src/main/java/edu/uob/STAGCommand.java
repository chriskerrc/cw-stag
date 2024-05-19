package edu.uob;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class STAGCommand {

    private final ArrayList<String> commandTokens;
    private final GameModel gameModel;
    private String currentPlayerName;
    private Player currentPlayerObject;
    private String destinationNameMatch;
    private String matchingArtefactName;
    private Location currentLocation;
    private GameAction currentGameAction;

    public STAGCommand(ArrayList<String> commandTokens, GameModel gameModel) {
        this.commandTokens = commandTokens;
        this.gameModel = gameModel;
    }

    public String interpretSTAGCommand(){
        String commandResponse = "Command not recognised";
        if(!isPlayerNameSet()){
            return "Enter a valid player name";
        }
        setUpPlayer();
        currentLocation = currentPlayerObject.getCurrentLocation();
        boolean actionExecuted = false;
        for(String commandToken : commandTokens){
            if(commandToken.contains("look")){
                commandResponse = interpretLookCommand();
            }
            if(commandToken.contains("goto")){
                commandResponse = interpretGotoCommand();
            }
            if(commandToken.contains("get")){
                commandResponse = interpretGetCommand();
            }
            if(commandToken.contains("inv") || commandToken.contains("inventory")){
                commandResponse = interpretInvCommand();
            }
            if(commandToken.contains("drop")){
                commandResponse = interpretDropCommand();
            }
            if(commandToken.contains("health")){
                commandResponse = interpretHealth();
            }
            if(isActionCommandValid() && !actionExecuted){
                commandResponse = interpretAction();
                actionExecuted = true;
            }
        }
        if(currentPlayerObject.getPlayerHealth() == 0){
            commandResponse = getPlayerDieResponse();
        }
        return commandResponse;
    }

    //check if player exists and create new player if needed
    private void setUpPlayer(){
        Player potentialPlayer = gameModel.getPlayerFromName(currentPlayerName);
        if(potentialPlayer != null){
            currentPlayerObject = potentialPlayer;
        }
        else{
            gameModel.createPlayerFromName(currentPlayerName);
            currentPlayerObject = gameModel.getPlayerFromName(currentPlayerName);
        }
    }

    //if player name is valid, it will be stored
    private boolean isPlayerNameSet(){
        String potentialPlayerName = commandTokens.get(0);
        if(isPlayerNameValid(potentialPlayerName)) {
            currentPlayerName = potentialPlayerName;
            return true;
        }
        return false;
    }

    private boolean isPlayerNameValid(String playerName){
        //player name can only contain alpha, space, apostrophe or hyphen characters
        return playerName.matches("^[a-zA-Z '-]+$");
    }

    private String getPlayerDieResponse(){
        currentPlayerObject.killPlayer();
        return "You died and lost all of your items. You return to the start";
    }

    //look includes location, gameEntities, other player(s) and paths
    private String interpretLookCommand(){
        String locationDescription = currentLocation.getDescription();
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("You are in ");
        responseBuilder.append(locationDescription);
        responseBuilder.append(". You can see:\n");
        for (GameEntity gameEntity : currentLocation.getEntityList()) {
            responseBuilder.append(gameEntity.getDescription()).append("\n");
        }
        for (Player playerInGame : gameModel.getPlayersInGame()){
            if(playerInGame != currentPlayerObject){
                responseBuilder.append("A player called ");
                responseBuilder.append(playerInGame.getName()).append("\n");;
            }
        }
        responseBuilder.append("You can access from here: \n");
        HashSet<Location> destinationsSet = gameModel.getDestinations(currentLocation.getName());
        for(Location locationDestination : destinationsSet){
            responseBuilder.append(locationDestination.getName()).append("\n");
        }
        return responseBuilder.toString();
    }

    //matchingDestinationName is destination in command
    private String interpretGotoCommand(){
        if(!isDestinationValid()){
            return "Did you provide a location to go to?";
        }
        HashSet<Location> destinationsSet = gameModel.getDestinations(currentLocation.getName());
        for(Location potentialDestination : destinationsSet){
            if(Objects.equals(potentialDestination.getName(), destinationNameMatch)){
                gameModel.updatePlayerLocation(currentPlayerName, potentialDestination);
                return "You went to the " + potentialDestination.getName();
            }
        }
            return "You can't get there from here";
    }

    //matchingArtefactName is artefact in command
    private String interpretGetCommand(){
        if(!isArtefactInRoom(currentLocation)){
            return "That artefact isn't in this location";
        }
        Artefact gotArtefact = (Artefact) currentLocation.removeEntity(matchingArtefactName);
        currentPlayerObject.addArtefactInventory(gotArtefact);
        return "You picked up " + matchingArtefactName;
    }

    private String interpretDropCommand(){
        ArrayList<Artefact> inventoryList = currentPlayerObject.getInventoryList();
        Artefact possibleArtefact = getArtefactInventory(inventoryList);
        if(possibleArtefact == null){
            return "You don't have that artefact or it doesn't exist";
        }
        currentPlayerObject.dropArtefact(possibleArtefact.getName());
        currentLocation.addEntity(possibleArtefact);
        return "You dropped " + matchingArtefactName;
    }

    private String interpretInvCommand(){
        String inventoryResponse = "You are carrying: \n";
        StringBuilder invResponseBuilder = new StringBuilder();
        ArrayList<Artefact> inventoryList = currentPlayerObject.getInventoryList();
        for (Artefact artefact : inventoryList) {
            invResponseBuilder.append(artefact.getDescription()).append("\n");
        }
        String inventoryArtefacts = invResponseBuilder.toString();
        return inventoryResponse + inventoryArtefacts;
    }

    private String interpretHealth() {
        int currentHealth = currentPlayerObject.getPlayerHealth();
        String healthNumber = String.valueOf(currentHealth);
        return "Your current health level is " + healthNumber;
    }

    private boolean isDestinationValid(){
        for(String commandToken: commandTokens){
            Location potentialLocation = gameModel.getLocationFromName(commandToken);
            String gameLocationName = null;
            if(potentialLocation != null){
                gameLocationName = potentialLocation.getName();
            }
            if(Objects.equals(gameLocationName, commandToken)){
                destinationNameMatch = gameModel.getLocationFromName(commandToken).getName();
                return true;
            }
        }
        return false;
    }

    private boolean isArtefactInRoom(Location currentLocation){
        if(currentLocation.isNoArtefact()){
            return false;
        }
        for(String token: commandTokens){
            Artefact artefactInRoom = (Artefact) currentLocation.getEntityFromName(token);
            if(artefactInRoom != null && Objects.equals(artefactInRoom.getName(), token)){
                matchingArtefactName = artefactInRoom.getName();
                return true;
            }
        }
        return false;
    }

    //Get artefact from inventory
    private Artefact getArtefactInventory(ArrayList<Artefact> inventoryList){
        if(inventoryList.isEmpty()){
            return null;
        }
        for(String commandToken: commandTokens){
            Artefact inventoryArtefact = findArtefact(inventoryList, commandToken);
            if(inventoryArtefact != null){
                return inventoryArtefact;
            }
        }
        return null;
    }

    //Does artefact in inventory match artefact in command?
    private Artefact findArtefact(ArrayList<Artefact> invList, String commandToken){
        for(Artefact inventoryArtefact : invList){
            if(Objects.equals(inventoryArtefact.getName(), commandToken)){
                matchingArtefactName = inventoryArtefact.getName();
                return inventoryArtefact;
            }
        }
        return null;
    }

    private boolean isActionCommandValid(){
        HashSet<GameAction> gameActionSet = commandHasKeyPhrase();
        if(gameActionSet == null){
            return false;
        }
        GameAction gameAction = getActionForSubject(gameActionSet);
        if(gameAction == null){
            return false;
        }
        currentGameAction = gameAction;
        return areAllSubjectsHere(gameAction);
    }

    private HashSet<GameAction> commandHasKeyPhrase(){
        for(String commandToken: commandTokens){
            HashSet<GameAction> gameActionHashSet = gameModel.getGameActionHashSet(commandToken);
            if(gameActionHashSet != null){
                return gameActionHashSet;
            }
        }
        return null;
    }

    //check if hashset of gameActions contains subject
    private GameAction getActionForSubject(HashSet<GameAction> gameActionHashSet){
        for(GameAction gameAction: gameActionHashSet){
            return isSubjectInCommand(gameAction);
        }
        return null;
    }

    private GameAction isSubjectInCommand(GameAction gameAction){
        for(String commandToken: commandTokens){
            Subject commandSubject = (Subject) gameAction.getSubjectEntity(commandToken);
            if(commandSubject != null){
                return gameAction;
            }
        }
        return null;
    }

    //check subjects are present
    private Boolean areAllSubjectsHere(GameAction gameAction){
        ArrayList<Subject> subjectList = gameAction.getSubjects();
        int matchingSubjects = 0;
        Location currentLocation = currentPlayerObject.getCurrentLocation();
        for(Subject subjectEntity: subjectList){
            if(currentPlayerObject.isSubjectInInventory(subjectEntity.getName())){
                matchingSubjects++;
            }
            if(currentLocation.isSubjectInLocation(subjectEntity.getName())){
                matchingSubjects++;
            }
        }
        return matchingSubjects == subjectList.size();
    }

    private String interpretAction(){
        consumeEntities();
        produceEntities();
        return currentGameAction.getNarration();
    }

    //Consume entities from inventory, location, consume location, consume health
    private void consumeEntities() {
        ArrayList<Consumable> consumableEntities = currentGameAction.getConsumables();
        Artefact droppedArtefact;
        GameEntity consumedGameEntity;
        for (Consumable consumableEntity : consumableEntities) {
            String consumableName = consumableEntity.getName();
            Location consumedLocation = gameModel.getLocationFromName(consumableName);
            if (currentPlayerObject.isSubjectInInventory(consumableName)) {
                droppedArtefact = currentPlayerObject.dropArtefact(consumableName);
                gameModel.addEntityToLocation("storeroom", droppedArtefact);
            }
            if(currentLocation.isSubjectInLocation(consumableName)){
                consumedGameEntity = currentLocation.getEntityFromName(consumableName);
                currentLocation.removeEntity(consumableName);
                gameModel.addEntityToLocation("storeroom", consumedGameEntity);
            }
            if(consumedLocation != null){
                gameModel.updateLocationPath(currentLocation.getName(), consumableName, false);
            }
            if(Objects.equals(consumableName, "health")){
                currentPlayerObject.decreasePlayerHealth();
            }
        }
    }

    //Produce entities from anywhere in game and add them to current location
    private void produceEntities(){
        ArrayList<Product> producedEntities = currentGameAction.getProducts();
        for (Product productEntity : producedEntities) {
            String productName = productEntity.getName();
            GameEntity gameEntity = gameModel.getEntityWhereItIs(productName);
            Location producedLocation = gameModel.getLocationFromName(productName);
            if(gameEntity != null){
                currentLocation.addEntity(gameEntity);
            }
            if(producedLocation != null){
                gameModel.updateLocationPath(currentLocation.getName(), productName, true);
            }
            if(Objects.equals(productName, "health")){
                currentPlayerObject.increasePlayerHealth();
            }
        }
    }
}