package edu.uob;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class STAGCommand {

    private final ArrayList<String> commandTokens;
    private final GameModel gameModel;
    private String currentPlayerName;

    private Player currentPlayerObject;

    private String matchingDestinationName;

    private String matchingArtefactName;

    private Location currentLocation;

    private GameAction currentGameAction;

    public STAGCommand(ArrayList<String> commandTokens, GameModel gameModel) {
        this.commandTokens = commandTokens;
        this.gameModel = gameModel;
    }

    public String interpretSTAGCommand(){
        String commandResponse = "Command not recognised";
        if(!isPlayerNameValid()){
            return "Enter a valid player name";
        }
        setUpPlayer();
        currentLocation = currentPlayerObject.getCurrentLocation();
        boolean actionExecuted = false;
        for(String commandToken : commandTokens){
            if(commandToken.toLowerCase().contains("look")){
                commandResponse = interpretLookCommand();
            }
            if(commandToken.toLowerCase().contains("goto")){
                commandResponse = interpretGotoCommand();
            }
            if(commandToken.toLowerCase().contains("get")){
                commandResponse = interpretGetCommand();
            }
            if(commandToken.toLowerCase().contains("inv") || commandToken.toLowerCase().contains("inventory")){
                commandResponse = interpretInvCommand();
            }
            if(commandToken.toLowerCase().contains("drop")){
                commandResponse = interpretDropCommand();
            }
            if(commandToken.toLowerCase().contains("health")){
                commandResponse = interpretHealthCommand();
            }
            if(actionCommandIsValid() && !actionExecuted){
                commandResponse = interpretActionCommand();
                actionExecuted = true;
            }
        }
        if(currentPlayerObject.getPlayerHealth() == 0){
            commandResponse = getPlayerDeathResponse();
        }
        return commandResponse;
    }

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

    private boolean isPlayerNameValid(){
        String potentialPlayerName = commandTokens.get(0);
        if(isPlayerNameValid(potentialPlayerName)) {
            currentPlayerName = potentialPlayerName;
            return true;
        }
        return false;
    }

    private boolean isPlayerNameValid(String playerName){
        return playerName.matches("^[a-zA-Z '-]+$");
    }

    private String getPlayerDeathResponse(){
        currentPlayerObject.killPlayer();
        return "You died and lost all of your items, you must return to the start of the game";
    }

    private String interpretLookCommand(){
        String currentLocationDescription = currentLocation.getDescription();
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("You are in ").append(currentLocationDescription).append(". You can see:\n"); //does newline char work on all OSes?
        for (GameEntity gameEntity : currentLocation.getEntityList()) {
            responseBuilder.append(gameEntity.getDescription()).append("\n");
        }
        for (Player playerInGame : gameModel.getPlayersInGame()){
            if(playerInGame != currentPlayerObject){
                responseBuilder.append("A player called ").append(playerInGame.getName()).append("\n");;
            }
        }
        responseBuilder.append("You can access from here: \n");
        HashSet<Location> destinations = gameModel.getDestinationsFromLocation(currentLocation.getName());
        for(Location location : destinations){
            responseBuilder.append(location.getName()).append("\n");
        }
        return responseBuilder.toString();
    }

    private String interpretGotoCommand(){
        if(!commandIncludesDestinationThatExists()){
            return "Did you provide a location to go to?";
        }
        HashSet<Location> destinations = gameModel.getDestinationsFromLocation(currentLocation.getName());
        for(Location potentialDestination : destinations){
            if(Objects.equals(potentialDestination.getName(), matchingDestinationName)){
                gameModel.updatePlayerLocation(currentPlayerName, potentialDestination);
                return "You went to the " + potentialDestination.getName();
            }
        }
            return "You can't get there from here";
    }

    //assumes only one artefact in command for now
    private String interpretGetCommand(){
        if(!commandIncludesArtefactInRoom(currentLocation)){
            return "That artefact isn't in this location";
        }
        Artefact pickedUpArtefact = (Artefact) currentLocation.removeEntity(matchingArtefactName); //is it dangerous to cast like this?
        currentPlayerObject.addArtefactToInventory(pickedUpArtefact);
        return "You picked up " + matchingArtefactName;
    }

    private String interpretDropCommand(){
        ArrayList<Artefact> inventoryList = currentPlayerObject.getInventoryList();
        Artefact possibleArtefact = getArtefactFromInventory(inventoryList);
        if(possibleArtefact == null){
            return "You don't have that artefact or it doesn't exist";
        }
        currentPlayerObject.dropArtefactFromInventory(possibleArtefact.getName());
        currentLocation.addEntity(possibleArtefact);
        return "You dropped " + matchingArtefactName;
    }

    private String interpretInvCommand(){
        String inventoryResponse = "You are carrying: \n";
        StringBuilder inventoryResponseBuilder = new StringBuilder();
        ArrayList<Artefact> inventoryList = currentPlayerObject.getInventoryList();
        for (Artefact artefact : inventoryList) {
            inventoryResponseBuilder.append(artefact.getDescription()).append("\n");
        }
        String artefactsInInventory = inventoryResponseBuilder.toString();
        return inventoryResponse + artefactsInInventory;
    }

    private String interpretHealthCommand() {
        int currentHealth = currentPlayerObject.getPlayerHealth();
        String healthNumber = String.valueOf(currentHealth);
        return "Your current health level is " + healthNumber;
    }

    private boolean commandIncludesDestinationThatExists(){
        for(String token: commandTokens){
            Location location = gameModel.getLocationFromName(token);
            if(location != null && Objects.equals(gameModel.getLocationFromName(token).getName(), token)){
                matchingDestinationName = gameModel.getLocationFromName(token).getName();
                return true;
            }
        }
        return false;
    }

    private boolean commandIncludesArtefactInRoom(Location currentLocation){
        if(currentLocation.isNoArtefact()){
            return false;
        }
        for(String token: commandTokens){
            Artefact artefact = (Artefact) currentLocation.getEntityFromName(token);
            if(artefact != null && Objects.equals(artefact.getName(), token)){
                matchingArtefactName = artefact.getName();
                return true;
            }
        }
        return false;
    }

    //refactor this to use stream
    private Artefact getArtefactFromInventory(ArrayList<Artefact> inventoryList){
        if(inventoryList.isEmpty()){
            return null;
        }
        //too deeply nested
        for(String token: commandTokens){
            for(Artefact artefact : inventoryList){
                if(Objects.equals(artefact.getName(), token)){
                    matchingArtefactName = artefact.getName();
                    return artefact;
                }
            }

        }
        return null;
    }

    private boolean actionCommandIsValid(){
        HashSet<GameAction> gameActionHashSet = commandContainsKeyPhrase();
        if(gameActionHashSet == null){
            return false;
        }
        GameAction gameAction = findGameActionMatchingSubject(gameActionHashSet);
        if(gameAction == null){
            return false;
        }
        currentGameAction = gameAction;
        return allSubjectsAreAvailable(gameAction);
    }

    private HashSet<GameAction> commandContainsKeyPhrase(){
        for(String token: commandTokens){
            HashSet<GameAction> gameActionHashSet = gameModel.getGameActionHashSet(token);
            if(gameActionHashSet != null){
                return gameActionHashSet;
            }
        }
        return null;
    }

    //check if hashset contains subject
    private GameAction findGameActionMatchingSubject(HashSet<GameAction> gameActionHashSet){
        for(GameAction gameAction: gameActionHashSet){
            return isSubjectInCommand(gameAction);
        }
        return null;
    }

    private GameAction isSubjectInCommand(GameAction gameAction){
        for(String commandToken: commandTokens){
            Subject commandSubject = (Subject) gameAction.getSubjectEntityFromName(commandToken);
            if(commandSubject != null){
                return gameAction;
            }
        }
        return null;
    }

    private Boolean allSubjectsAreAvailable(GameAction gameAction){
        ArrayList<Subject> subjectList = gameAction.getSubjectList();
        int matchingSubjects = 0;
        Location currentLocation = currentPlayerObject.getCurrentLocation();
        for(Subject subject: subjectList){
            if(currentPlayerObject.subjectIsInInventory(subject.getName())){
                matchingSubjects++;
            }
            if(currentLocation.subjectIsInLocation(subject.getName())){
                matchingSubjects++;
            }
        }
        return matchingSubjects == subjectList.size();
    }

    private String interpretActionCommand(){
        consumeEntities();
        produceEntities();
        return currentGameAction.getNarration();
    }

    private void consumeEntities() {
        ArrayList<Consumable> consumableEntities = currentGameAction.getConsumableEntities();
        Artefact droppedArtefact;
        GameEntity consumedGameEntity;
        for (Consumable consumable : consumableEntities) {
            String consumableName = consumable.getName();
            Location consumedLocation = gameModel.getLocationFromName(consumableName);
            if (currentPlayerObject.subjectIsInInventory(consumableName)) {
                droppedArtefact = currentPlayerObject.dropArtefactFromInventory(consumableName);
                gameModel.addEntityToLocation("storeroom", droppedArtefact);
            }
            if(currentLocation.subjectIsInLocation(consumableName)){
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

    private void produceEntities(){
        ArrayList<Product> producedEntities = currentGameAction.getProducedEntities();
        for (Product product : producedEntities) {
            String productName = product.getName();
            GameEntity gameEntity = gameModel.getEntityFromItsCurrentLocation(productName);
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