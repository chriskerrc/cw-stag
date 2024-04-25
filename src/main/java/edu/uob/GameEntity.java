package edu.uob;

import java.util.ArrayList;

public abstract class GameEntity
{
    private String name;
    private String description;


    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
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
