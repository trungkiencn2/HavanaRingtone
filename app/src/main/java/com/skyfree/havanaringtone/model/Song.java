package com.skyfree.havanaringtone.model;

/**
 * Created by KienBeu on 4/17/2018.
 */

public class Song {
    private String name;
    private int sound;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public Song(String name, int sound) {

        this.name = name;
        this.sound = sound;
    }

    public Song() {

    }
}
