package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

public class Player {

    private Location currentLocation;

    private GameModel gameModel;

    private String name;

    private int playerHealth;

    private ArrayList<Artefact> inventoryList = new ArrayList<>();


    public Player(GameModel gameModel, String name){
        this.gameModel = gameModel;
        this.currentLocation = gameModel.getStartLocation();
        this.name = name;
        playerHealth = 3;
    }

    public String getName()
    {
        return name;
    }

    public Location getCurrentLocation(){
        return currentLocation;
    }

    public void setCurrentLocation(Location newLocation){
        this.currentLocation = newLocation;
    }

    public void addArtefactToInventory(Artefact pickedUpArtefact){
        inventoryList.add(pickedUpArtefact);
    }

    public Artefact dropArtefactFromInventory(String artefactName){
        for(Artefact artefact : inventoryList){
            if(Objects.equals(artefact.getName(), artefactName)){
                inventoryList.remove(artefact);
                return artefact;
            }
        }
        return null;
    }

    public void killPlayer(){
        emptyInventory();
        movePlayerToStartLocation();
        resetPlayerHealth();
    }

    private void emptyInventory(){
        ArrayList<Artefact> artefactsToDrop = new ArrayList<>(inventoryList);
        for(Artefact artefact : artefactsToDrop){
            inventoryList.remove(artefact);
            gameModel.addEntityToLocation(currentLocation.getName(), artefact);
        }
    }

    private void movePlayerToStartLocation() {
        currentLocation = gameModel.getStartLocation();
    }

    private void resetPlayerHealth(){
        playerHealth = 3;
    }



    public ArrayList<Artefact> getInventoryList(){
        return inventoryList;
    }

    public boolean subjectIsInInventory(String subjectName){
        for (Artefact artefact : inventoryList){
            if(Objects.equals(artefact.getName(), subjectName)){
                return true;
            }
        }
        return false;
    }

    public void increasePlayerHealth(){
        if(playerHealth < 3){
            playerHealth++;
        }
    }

    public void decreasePlayerHealth(){
        if(playerHealth > 0){
            playerHealth--;
        }
    }

    public int getPlayerHealth(){
        return playerHealth;
    }



}
