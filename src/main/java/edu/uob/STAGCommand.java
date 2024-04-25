package edu.uob;

import java.util.ArrayList;

public class STAGCommand {

    ArrayList<String> commandTokens;
    GameModel gameModel;

    public STAGCommand(ArrayList<String> commandTokens, GameModel gameModel) {
        this.commandTokens = commandTokens;
        this.gameModel = gameModel;
    }

    public String interpretSTAGCommand(){
        String response = "";
        if(commandTokens.contains("look")){ //currently only matches exact case of "look"
            response = interpretLookCommand();
        }
        else{
            response = "command not recognised";
        }
        return response;
    }

    //try to implement subclasses that extend this class e.g. Look

    //this method is too long
    private String interpretLookCommand(){
        Location currentLocation = gameModel.getCurrentLocation();
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
        Location destinationLocation = gameModel.getDestinationsFromLocation(currentLocation);
        String destinationString = destinationLocation.getName();
        String pathsResponse = "You can access from here: \n" + destinationString + "\n";
        return locationResponse + artefactResponse + furnitureResponse + characterResponse + pathsResponse;
    }

    //method: check if tokens list contains "look"




}
