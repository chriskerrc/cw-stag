package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

public class GameAction
{
    ArrayList<Subject> subjectEntities = new ArrayList<>();
    ArrayList<Consumable> consumedEntities = new ArrayList<>();

    ArrayList<Product> producedEntities = new ArrayList<>();

    String narration;

    public void addSubjectEntity(Subject subjectEntity){
        subjectEntities.add(subjectEntity);
    }

    public void addConsumableEntity(Consumable consumedEntity){
        consumedEntities.add(consumedEntity);
    }

    public void addProductEntity(Product producedEntity){
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

    public String getNarration(){
        return narration;
    }

    public ArrayList<Subject> getSubjectList(){
        return subjectEntities;
    }


    public ArrayList<Product> getProducedEntities() {
        return producedEntities;
    }

    public ArrayList<Consumable> getConsumableEntities() {
        return consumedEntities;
    }
}
