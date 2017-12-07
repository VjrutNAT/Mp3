package com.demo.vjrutnat.mp3.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by iceman on 10/11/2016.
 */

public class Album implements Serializable {

    private int id;
    private String title;
    private String artist;
    ArrayList<Song> lstSong;
    Bitmap albumArt;
    String albumArtPath;

    public Album(int id, String title, String artist, String albumArtPath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumArtPath = albumArtPath;
    }

    public String getAlbumArtPath() {
        return albumArtPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

}
