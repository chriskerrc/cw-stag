package edu.uob;

import java.util.ArrayList;

public class Player {

    private Location currentLocation;

    private GameModel gameModel;

    private String name;

    private ArrayList<Artefact> inventoryList = new ArrayList<>();


    public Player(GameModel gameModel, String name){
        this.gameModel = gameModel;
        this.currentLocation = gameModel.getStartLocation();
        this.name = name;
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

    public ArrayList<Artefact> getInventoryList(){
        return inventoryList;
    }


}
