package com.example.imagealbum;

import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

public class image {
    private String uri;
    private long size;
    private String name;
    private String location;
    private String date;
    private String path;
    private boolean isSelected = false;

    public image(Uri uri, long size, String name, String location, String date, String path){
        this.uri = uri.toString();
        this.size = size;
        this.name = name;
        this.location = location;
        this.date = date;
        this.path = path;
    }
    public image(Uri uri){
        this.uri = uri.toString();
    }

    public Uri getImage_URI() {
        return Uri.parse(uri);
    }

    public String getImage_name(){return name;}

    public long getImage_size() {
        return size;
    }

    public String getLocation(){return this.location;}

    public String getDate(){return this.date;}

    public boolean getSelected(){return this.isSelected;}

    public void setImage_uri(Uri URI) {
        this.uri = URI.toString();
    }

    public void setImage_size(int size){
        this.size = size;
    }

    public void setImage_name(String name){
        this.name = name;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setSelected(boolean selected){this.isSelected = selected;}

    public String toJson(){
        String json = new Gson().toJson(this);
        return json;
    }
}

