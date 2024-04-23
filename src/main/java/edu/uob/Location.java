package edu.uob;

import java.util.ArrayList;

public class Location extends GameEntity {

    ArrayList<Artefact> artefactList = new ArrayList<>();
    ArrayList<Furniture> furnitureList = new ArrayList<>();

    public Location(String name, String description) {
        super(name, description);
    }

}
