package edu.uob;

public class Player {

    private Location currentLocation;

    private GameModel gameModel;

    private String name;


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


}
