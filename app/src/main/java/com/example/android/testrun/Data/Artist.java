package com.example.android.testrun.Data;

import java.io.Serializable;

/**
 * Created by Manto on 16-Jan-17.
 */

public class Artist implements Serializable {

    public String downloadu;
    public String email;
    public String songName;
    public String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDownloadu() {
        return downloadu;
    }

    public void setDownloadu(String downloadu) {
        this.downloadu = downloadu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Artist() {
        super();
    }


    @Override
    public String toString() {
        return "Artist{" +
                "downloadu='" + downloadu + '\'' +
                ", email='" + email + '\'' +
                ", songname='" + songName + '\'' +
                '}';
    }
}

