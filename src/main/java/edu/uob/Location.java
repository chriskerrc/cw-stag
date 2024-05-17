package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

public class Location extends GameEntity {

    ArrayList<GameEntity> entityList = new ArrayList<>();

    public Location(String name, String description) {
        super(name, description);
    }

    //add paths to locations

    public void addEntity(GameEntity newGameEntity){
        entityList.add(newGameEntity);
    }


    public GameEntity getEntityFromName(String entityName){
        for(GameEntity gameEntity : entityList){
            if(Objects.equals(gameEntity.getName(), entityName)){
                return gameEntity;
            }
        }
        return null;
    }

    public ArrayList<GameEntity> getEntityList(){
        return entityList;
    }

    public boolean isNoFurniture(){
        for(GameEntity gameEntity : entityList){
            if(gameEntity instanceof Furniture){
                return false;
            }
        }
        return true;
    }

    public boolean isNoCharacter(){
        for(GameEntity gameEntity : entityList){
            if(gameEntity instanceof Character){
                return false;
            }
        }
        return true;
    }

    public boolean isNoArtefact(){
        for(GameEntity gameEntity : entityList){
            if(gameEntity instanceof Artefact){
                return false;
            }
        }
        return true;
    }

    //think about matching by case e.g. "key and "kEY"
    public GameEntity removeEntity(String entityName){
        for(GameEntity gameEntity : entityList){
            if(Objects.equals(gameEntity.getName(), entityName)){
                entityList.remove(gameEntity);
                return gameEntity;
            }
        }
        return null;
    }

    public boolean subjectIsInLocation(String subjectName){
        for (GameEntity gameEntity : entityList) {
            if (Objects.equals(gameEntity.getName(), subjectName)) {
                return true;
            }
        }
        return false;
    }



}
