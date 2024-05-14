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

    public STAGCommand(ArrayList<String> commandTokens, GameModel gameModel) {
        this.commandTokens = commandTokens;
        this.gameModel = gameModel;
    }

    public String interpretSTAGCommand(){
        String response = "Command not recognised";
        getPlayerNameFromCommand();
        Player potentialPlayer = gameModel.getPlayerFromName(currentPlayerName);
        if(potentialPlayer != null){
            currentPlayerObject = potentialPlayer;
        }
        else{
            gameModel.createPlayerFromName(currentPlayerName);
            currentPlayerObject = gameModel.getPlayerFromName(currentPlayerName);
        }
        for(String token : commandTokens){
            if(token.toLowerCase().contains("look")){
                response = interpretLookCommand();
            }
            if(token.toLowerCase().contains("goto")){
                response = interpretGotoCommand();
            }
            if(token.toLowerCase().contains("get")){
                response = interpretGetCommand();
            }
            if(token.toLowerCase().contains("inv")){
                response = interpretInvCommand();
            }
            if(token.toLowerCase().contains("inventory")){ //alternative to "inv"
                response = interpretInvCommand();
            }
            if(token.toLowerCase().contains("drop")){
                response = interpretDropCommand();
            }
            //else check if command contains keyphrase and subject
        }
        return response;
    }

    private void getPlayerNameFromCommand(){
        String playerName = commandTokens.get(0);
        if(playerName.contains(":")) {
            playerName = playerName.replaceAll(":$", ""); //remove colon
            playerName = playerName.toLowerCase(); // "Simon" is the same as "simon"
        }
        else{
            playerName = null;
        }
        currentPlayerName = playerName;
    }

    //try to implement subclasses that extend this class e.g. Look

    //this method is too long
    private String interpretLookCommand(){
        Location currentLocation = currentPlayerObject.getCurrentLocation();
        String currentLocationDescription = currentLocation.getDescription();
        String locationResponse = "You are in " + currentLocationDescription + ". You can see:\n"; //does newline char work on all OSes?
        //Important: some of this code won't be specific to look command - generalise it out
        //a lot of this code is similar: can it be simplified?
        //Artefacts
        ArrayList<Artefact> artefactList = currentLocation.getArtefactList();
        StringBuilder artefactResponseBuilder = new StringBuilder();
        for (Artefact artefact : artefactList) {
            artefactResponseBuilder.append(artefact.getDescription()).append("\n");
        }
        String artefactResponse = artefactResponseBuilder.toString();
        //Furniture
        ArrayList<Furniture> furnitureList = currentLocation.getFurnitureList();
        StringBuilder furnitureResponseBuilder = new StringBuilder();
        for (Furniture furniture : furnitureList) {
            furnitureResponseBuilder.append(furniture.getDescription()).append("\n");
        }
        String furnitureResponse = furnitureResponseBuilder.toString();
        //Character
        ArrayList<Character> characterList = currentLocation.getCharacterList();
        StringBuilder characterResponseBuilder = new StringBuilder();
        for (Character character : characterList) {
            characterResponseBuilder.append(character.getDescription()).append("\n");
        }
        String characterResponse = characterResponseBuilder.toString();
        //Paths
        Location destinationLocation = gameModel.getDestinationFromLocation(currentLocation);
        String destinationString = destinationLocation.getName();
        String pathsResponse = "You can access from here: \n" + destinationString + "\n";
        return locationResponse + artefactResponse + furnitureResponse + characterResponse + pathsResponse;
    }

    private String interpretGotoCommand(){
        Location currentLocation = currentPlayerObject.getCurrentLocation(); //generalise this rather than copy pasting across methods
        if(!commandIncludesDestinationThatExists()){
            return "Did you provide a location to goto?";
        }
        Location potentialDestination = gameModel.getDestinationFromLocation(currentLocation);
        if(Objects.equals(potentialDestination.getName(), matchingDestinationName)){
            gameModel.updatePlayerLocation(currentPlayerName, potentialDestination);
            //not sure if i need to automatically do look command: example video seems to suggest so, but ExampleSTAGTests do look command after goto
            //return interpretLookCommand();
            return "You went to the " + potentialDestination.getName();
        }
        else{
            return "You can't get there from here";
        }
    }

    //assumes only one artefact in command for now
    private String interpretGetCommand(){
        Location currentLocation = currentPlayerObject.getCurrentLocation();
        if(!commandIncludesArtefactInRoom(currentLocation)){
            return "That artefact isn't in this location";
        }
        Artefact pickedUpArtefact = currentLocation.removeArtefact(matchingArtefactName);
        currentPlayerObject.addArtefactToInventory(pickedUpArtefact);
        return "You picked up " + matchingArtefactName;
    }

    private String interpretDropCommand(){
        ArrayList<Artefact> inventoryList = currentPlayerObject.getInventoryList();
        Artefact possibleArtefact = getArtefactFromInventory(inventoryList);
        if(possibleArtefact == null){
            return "You don't have that artefact or it doesn't exist";
        }
        currentPlayerObject.dropArtefactFromInventory(possibleArtefact);
        Location currentLocation = currentPlayerObject.getCurrentLocation();
        currentLocation.addArtefact(possibleArtefact);
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
        if(currentLocation.isArtefactListEmpty()){
            return false;
        }
        for(String token: commandTokens){
            Artefact artefact = currentLocation.getArtefactFromName(token);
            if(artefact != null && Objects.equals(currentLocation.getArtefactFromName(token).getName(), token)){
                matchingArtefactName = currentLocation.getArtefactFromName(token).getName();
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
}
//there could be an action that has no consumption or production - still output narration if triggers and subjects match