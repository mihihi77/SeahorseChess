package model;

public abstract class GameEntity {
    private int id;

    protected GameEntity() {
        this.id = 0;
    }

    protected GameEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract String getEntityType();
}
