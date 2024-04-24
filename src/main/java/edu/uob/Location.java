package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

public class Location extends GameEntity {

    ArrayList<Artefact> artefactList = new ArrayList<>();
    ArrayList<Furniture> furnitureList = new ArrayList<>();
    ArrayList<Character> characterList = new ArrayList<>();

    public Location(String name, String description) {
        super(name, description);
    }

    //add paths to locations

    public void addArtefact(Artefact newArtefact){
        artefactList.add(newArtefact);
    }

    public void addFurniture(Furniture newFurniture){
        furnitureList.add(newFurniture);
    }

    public void addCharacter(Character newCharacter){
        characterList.add(newCharacter);
    }

    //add methods to remove artefacts

    public Artefact getArtefactFromName(String artefactName){
        for(Artefact artefact : artefactList){
            if(Objects.equals(artefact.getName(), artefactName)){
                return artefact;
            }
        }
        return null;
    }

    public Furniture getFurnitureFromName(String furnitureName){
        for(Furniture furniture : furnitureList){
            if(Objects.equals(furniture.getName(), furnitureName)){
                return furniture;
            }
        }
        return null;
    }

    public Character getCharacterFromName(String characterName){
        for(Character character : characterList){
            if(Objects.equals(character.getName(), characterName)){
                return character;
            }
        }
        return null;
    }

}
