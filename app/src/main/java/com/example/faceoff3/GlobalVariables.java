package com.example.faceoff3;

import android.app.Application;

public class GlobalVariables extends Application
{
    /*Just trying to get global variables to work to share across activities.
    *
    * Still to add: Integer fTouches which pulls in from table user to allow calculations of how good/bad the user is doingl
    *
    * */
    private String currentActiveUser;


    public String getCurrentActiveUser()
    {
        return currentActiveUser;
    }

    public void setCurrentActiveUser(String currentActiveUser)
    {
        this.currentActiveUser = currentActiveUser;
    }
}
