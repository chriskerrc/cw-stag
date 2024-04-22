package edu.uob;

import java.util.ArrayList;

public abstract class GameEntity
{
    private String name;
    private String description;

    private ArrayList<String> entityList;

    public GameEntity(String name, String description, ArrayList<String> entityList)
    {
        this.name = name;
        this.description = description;
        this.entityList = entityList;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
}
