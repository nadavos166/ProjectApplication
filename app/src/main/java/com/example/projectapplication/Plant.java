package com.example.projectapplication;

public class Plant
{
    String key;
    String name;
    String place;
    String time;
    int wateramount;
    public Plant(){

    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWateramount(int wateramount) {
        this.wateramount = wateramount;
    }



    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getTime() {
        return time;
    }

    public int getWateramount() {
        return wateramount;
    }



}
