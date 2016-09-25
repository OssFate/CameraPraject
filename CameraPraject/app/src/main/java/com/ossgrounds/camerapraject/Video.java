package com.ossgrounds.camerapraject;

/**
 * Created by USER on 20/09/2016.
 */
public class Video {
    private long id;
    private String title;

    public Video(long songID, String songTitle) {
        id=songID;
        title=songTitle;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
}
