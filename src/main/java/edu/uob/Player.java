package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

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

    public Artefact dropArtefactFromInventory(String artefactName){
        for(Artefact artefact : inventoryList){
            if(Objects.equals(artefact.getName(), artefactName)){
                inventoryList.remove(artefact);
                return artefact;
            }
        }
        return null;
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


}
