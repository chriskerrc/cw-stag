package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

public class GameAction
{
    ArrayList<Subject> subjectEntities = new ArrayList<>();
    ArrayList<Consumable> consumedEntities = new ArrayList<>();
    ArrayList<Product> producedEntities = new ArrayList<>();
    String actionNarration;

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
        actionNarration = inputNarration;
    }

    public GameEntity getSubjectEntity(String entityName){
        for(GameEntity gameEntity : subjectEntities){
            if(Objects.equals(gameEntity.getName(), entityName)){
                return gameEntity;
            }
        }
        return null;
    }

    public GameEntity getConsumedEntity(String entityName){
        for(GameEntity gameEntity : consumedEntities){
            if(Objects.equals(gameEntity.getName(), entityName)){
                return gameEntity;
            }
        }
        return null;
    }

    public GameEntity getProducedEntity(String entityName){
        for(GameEntity gameEntity : producedEntities){
            if(Objects.equals(gameEntity.getName(), entityName)){
                return gameEntity;
            }
        }
        return null;
    }

    public String getNarration(){
        return actionNarration;
    }

    public ArrayList<Subject> getSubjects(){
        return subjectEntities;
    }

    public ArrayList<Product> getProducts() {
        return producedEntities;
    }

    public ArrayList<Consumable> getConsumables() {
        return consumedEntities;
    }
}
