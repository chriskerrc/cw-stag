package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

public class GameAction
{
    ArrayList<GameEntity> subjectEntities = new ArrayList<>();
    ArrayList<GameEntity> consumedEntities = new ArrayList<>();

    ArrayList<GameEntity> producedEntities = new ArrayList<>();

    String narration;

    public void addSubjectEntity(GameEntity subjectEntity){
        subjectEntities.add(subjectEntity);
    }

    public void addConsumableEntity(GameEntity consumedEntity){
        consumedEntities.add(consumedEntity);
    }

    public void addProductEntity(GameEntity producedEntity){
        producedEntities.add(producedEntity);
    }

    public void setNarration(String inputNarration){
        narration = inputNarration;
    }

    public GameEntity getSubjectEntityFromName(String entityName){
        for(GameEntity gameEntity : subjectEntities){
            if(Objects.equals(gameEntity.getName(), entityName)){
                return gameEntity;
            }
        }
        return null;
    }

    public GameEntity getConsumedEntityFromName(String entityName){
        for(GameEntity gameEntity : consumedEntities){
            if(Objects.equals(gameEntity.getName(), entityName)){
                return gameEntity;
            }
        }
        return null;
    }

    public GameEntity getProducedEntityFromName(String entityName){
        for(GameEntity gameEntity : producedEntities){
            if(Objects.equals(gameEntity.getName(), entityName)){
                return gameEntity;
            }
        }
        return null;
    }

    public String getProducedNarration(){
        return narration;
    }


}
