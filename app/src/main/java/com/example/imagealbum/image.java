package com.example.imagealbum;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

public class image {
    private String uri;
    private long size;
    private String name;
    private String location;
    private String date;
    private String path;
    private int type;
    private int duration_mil;
    private boolean isSelected = false;

    public image(Uri uri, long size, String name, String location, String date, String path){
        this.uri = uri.toString();
        this.size = size;
        this.name = name;
        this.location = location;
        this.date = date;
        this.path = path;
    }

    public image(Uri uri, long size, String name, String location, String date, String path, int type){
        this.uri = uri.toString();
        this.size = size;
        this.name = name;
        this.location = location;
        this.date = date;
        this.path = path;
        this.type = type;
    }

    public image(Uri uri, long size, String name, String location, String date, String path, int type, int duration){
        this.uri = uri.toString();
        this.size = size;
        this.name = name;
        this.location = location;
        this.date = date;
        this.path = path;
        this.type = type;
        this.duration_mil = duration;

    }

    public image(){

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


    public String getPath(){return this.path;}

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

    public boolean isImage() {
        return this.type == Global.IMAGE_TYPE;
    }

    @SuppressLint("DefaultLocale")
    public String getDurationTimeString() {
        //Convert from duration in millisecond to string hh:mm:ss (src: stackoverflow)
        int millis = this.duration_mil;
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    @SuppressLint("DefaultLocale")
    public String millisecondToTimeString(int millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public String toJson(){
        String json = new Gson().toJson(this);
        return json;
    }

    public void setSelectedMode(boolean mode) {
        this.isSelected = mode;
    }

    public boolean isSelected() {
        return this.isSelected;
    }
}

