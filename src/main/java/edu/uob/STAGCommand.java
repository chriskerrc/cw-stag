package edu.uob;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class STAGCommand {

    private ArrayList<String> commandTokens;
    private GameModel gameModel;
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
        getPlayerNameFromCommand();
        Player potentialPlayer = gameModel.getPlayerFromName(currentPlayerName);
        if(potentialPlayer != null){
            currentPlayerObject = potentialPlayer;
        }
        else{
            gameModel.createPlayerFromName(currentPlayerName);
            currentPlayerObject = gameModel.getPlayerFromName(currentPlayerName);
        }
        currentLocation = currentPlayerObject.getCurrentLocation();
        boolean actionExecuted = false;
        for(String token : commandTokens){
            if(token.toLowerCase().contains("look")){
                commandResponse = interpretLookCommand();
            }
            if(token.toLowerCase().contains("goto")){
                commandResponse = interpretGotoCommand();
            }
            if(token.toLowerCase().contains("get")){
                commandResponse = interpretGetCommand();
            }
            if(token.toLowerCase().contains("inv") || token.toLowerCase().contains("inventory")){
                commandResponse = interpretInvCommand();
            }
            if(token.toLowerCase().contains("drop")){
                commandResponse = interpretDropCommand();
            }
            if(token.toLowerCase().contains("health")){
                commandResponse = interpretHealthCommand();
            }
            if(actionCommandIsValid() && !actionExecuted){
                commandResponse = interpretActionCommand();
                actionExecuted = true;
            }
        }
        if(currentPlayerObject.getPlayerHealth() == 0){
            currentPlayerObject.killPlayer();
            commandResponse = "You died and lost all of your items, you must return to the start of the game";
        }
        return commandResponse;
    }


    private void getPlayerNameFromCommand(){
        String playerName = commandTokens.get(0);
        if(playerName.contains(":")) {
            playerName = playerName.replaceAll(":$", ""); //remove colon
            playerName = playerName.toLowerCase(); // "Simon" is the same as "simon" - this is wrong
        }
        else{
            playerName = null;
        }
        currentPlayerName = playerName;
    }

    //try to implement subclasses that extend this class e.g. Look

    //this method is too long
    private String interpretLookCommand(){
        String currentLocationDescription = currentLocation.getDescription();
        String locationResponse = "You are in " + currentLocationDescription + ". You can see:\n"; //does newline char work on all OSes?
        //Important: some of this code won't be specific to look command - generalise it out
        ArrayList<GameEntity> entityList = currentLocation.getEntityList();
        StringBuilder artefactResponseBuilder = new StringBuilder();
        StringBuilder furnitureResponseBuilder = new StringBuilder();
        StringBuilder characterResponseBuilder = new StringBuilder();
        for (GameEntity gameEntity : entityList) {
            if(gameEntity instanceof Artefact){
                artefactResponseBuilder.append(gameEntity.getDescription()).append("\n");
            }
            if(gameEntity instanceof Furniture){
                furnitureResponseBuilder.append(gameEntity.getDescription()).append("\n");
            }
            if(gameEntity instanceof Character){
                characterResponseBuilder.append(gameEntity.getDescription()).append("\n");
            }

        }
        String artefactResponse = artefactResponseBuilder.toString();
        String furnitureResponse = furnitureResponseBuilder.toString();
        String characterResponse = characterResponseBuilder.toString();
        //Paths
        StringBuilder pathResponseBuilder = new StringBuilder();
        HashSet<Location> destinations = gameModel.getDestinationsFromLocation(currentLocation.getName());
        for(Location location : destinations){
            pathResponseBuilder.append(location.getName()).append("\n");
        }
        String pathsResponse = "You can access from here: \n" + pathResponseBuilder + "\n";
        return locationResponse + artefactResponse + furnitureResponse + characterResponse + pathsResponse;
    }

    private String interpretGotoCommand(){
        if(!commandIncludesDestinationThatExists()){
            return "Did you provide a location to goto?";
        }
        HashSet<Location> destinations = gameModel.getDestinationsFromLocation(currentLocation.getName());
        for(Location potentialDestination : destinations){
            if(Objects.equals(potentialDestination.getName(), matchingDestinationName)){
                gameModel.updatePlayerLocation(currentPlayerName, potentialDestination);
                //not sure if i need to automatically do look command: example video seems to suggest so, but ExampleSTAGTests do look command after goto
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

    //this method assumes that there is only one destination in command -  to do: guard against there being two
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
        HashSet<GameAction> gameActionHashSet = commandContainsKeyphrase();
        if(gameActionHashSet == null){
            return false;
        }
        GameAction gameAction = subjectInCommandIsInGameAction(gameActionHashSet);
        if(gameAction == null){
            return false;
        }
        currentGameAction = gameAction;
        return allSubjectsAreAvailable(gameAction);
    }

    private HashSet<GameAction> commandContainsKeyphrase(){
        for(String token: commandTokens){
            HashSet<GameAction> gameActionHashSet = gameModel.getGameActionHashSet(token);
            if(gameActionHashSet != null){
                return gameActionHashSet;
            }
        }
        return null;
    }

    //check if hashset contains subject
    private GameAction subjectInCommandIsInGameAction(HashSet<GameAction> gameActionHashSet){
        for(GameAction gameAction: gameActionHashSet){
            for(String token: commandTokens){
                Subject subject = (Subject) gameAction.getSubjectEntityFromName(token);
                if(subject != null){
                    return gameAction;
                }
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
                gameModel.updatePath(currentLocation.getName(), consumableName, false);
            }
            if(Objects.equals(consumableName, "health")){
                currentPlayerObject.decreasePlayerHealth();
            }
            //update the above, so that entities in different locations can be consumed (not just those in current location)
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
                gameModel.updatePath(currentLocation.getName(), productName, true);
            }
            if(Objects.equals(productName, "health")){
                currentPlayerObject.increasePlayerHealth();
            }
        }
    }
}
//there could be an action that has no consumption or production - still output narration if triggers and subjects match