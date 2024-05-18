package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

public class Player {

    private Location currentLocation;
    private final GameModel gameModel;
    private final String playerName;
    private int playerHealth;
    private final ArrayList<Artefact> inventoryList = new ArrayList<>();
    private final int maximumHealth = 3;

    public Player(GameModel gameModel, String playerName){
        this.gameModel = gameModel;
        this.currentLocation = gameModel.getStartLocation();
        this.playerName = playerName;
        playerHealth = maximumHealth;
    }

    public String getName()
    {
        return playerName;
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

    public Artefact dropArtefact(String artefactName){
        for(Artefact inventoryArtefact : inventoryList){
            if(Objects.equals(inventoryArtefact.getName(), artefactName)){
                inventoryList.remove(inventoryArtefact);
                return inventoryArtefact;
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
        for(Artefact inventoryArtefact : artefactsToDrop){
            inventoryList.remove(inventoryArtefact);
            gameModel.addEntityToLocation(currentLocation.getName(), inventoryArtefact);
        }
    }

    private void movePlayerToStartLocation() {
        currentLocation = gameModel.getStartLocation();
    }

    private void resetPlayerHealth(){
        playerHealth = maximumHealth;
    }

    public ArrayList<Artefact> getInventoryList(){
        return inventoryList;
    }

    public boolean subjectIsInInventory(String subjectName){
        for (Artefact inventoryArtefact : inventoryList){
            if(Objects.equals(inventoryArtefact.getName(), subjectName)){
                return true;
            }
        }
        return false;
    }

    public void increasePlayerHealth(){
        if(playerHealth < maximumHealth){
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
