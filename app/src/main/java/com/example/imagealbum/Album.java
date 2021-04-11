package com.example.imagealbum;

public class Album {
    private String name;

    public Album(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public boolean checkDuplicate(Album album){
        if (album.getName().equals(this.name)){
            return true;
        }
        return false;
    }
}
