package com.example.android.testrun;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Codetribe on 2016/12/09.
 */

public class Songs implements Serializable {

    private String email;
    private String name;
    private String downloaduri;

    public Songs() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDownloaduri(String downloaduri) {
        this.downloaduri = downloaduri;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getDownloaduri() {
        return downloaduri;
    }
}
