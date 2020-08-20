package com.dane.jpopword.model;

import com.dane.jpopword.Song;

import java.util.ArrayList;

public class newCategory {
    private int id;
    private String name;
    private String introCopy;
    private String songs;

    public newCategory(int id, String name, String introCopy, String songs) {
        this.id = id;
        this.name = name;
        this.introCopy = introCopy;
        this.songs = songs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroCopy() {
        return introCopy;
    }

    public void setIntroCopy(String introCopy) {
        this.introCopy = introCopy;
    }

    public ArrayList<Integer> getSongs() {
        ArrayList<Integer> temp = null;
        //String raw = "1,3,5,7,9";
        String raw = songs;
        String[] splitedRaw = raw.split(",");
        for (int i=0;i<splitedRaw.length;i++){
            temp.add(Integer.valueOf(splitedRaw[i]));
        }
        //"1,3,5,7,9" => 1, 3, 5, 7, 9,
        return temp;
    }

    public void setSongs(String songsAll) {
        this.songs = songsAll;
    }
}
