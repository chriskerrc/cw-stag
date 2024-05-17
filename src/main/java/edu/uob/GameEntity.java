package edu.uob;

public abstract class GameEntity
{
    private final String entityName;
    private final String entityDescription;

    public GameEntity(String entityName, String entityDescription)
    {
        this.entityName = entityName;
        this.entityDescription = entityDescription;
    }

    public String getName() {
        return entityName;
    }

    public String getDescription() {
        return entityDescription;
    }
}
