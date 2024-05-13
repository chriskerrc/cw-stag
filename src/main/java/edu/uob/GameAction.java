package edu.uob;

import java.util.ArrayList;

public class GameAction
{
    ArrayList<GameEntity> subjectEntities = new ArrayList<>();
    ArrayList<GameEntity> consumedEntities = new ArrayList<>();

    ArrayList<GameEntity> producedEntities = new ArrayList<>();

    String narration;

    public void addSubjectEntity(GameEntity subjectEntity){
        subjectEntities.add(subjectEntity);
    }

    public void addConsumedEntity(GameEntity consumedEntity){
        consumedEntities.add(consumedEntity);
    }

    public void addProducedEntity(GameEntity producedEntity){
        producedEntities.add(producedEntity);
    }


}
