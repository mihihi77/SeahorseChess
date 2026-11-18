package model;

public class Player {
    String username;
    String name;
    String color;
    int finishingRank;


    //public String getEntityType(){
    //}

    public String getName(String name){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getUsername(String username){
        return username;
    }

    public String setUsername(String username){
        this.username=username;
        return username;
    }

}
