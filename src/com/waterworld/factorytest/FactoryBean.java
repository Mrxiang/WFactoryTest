package com.waterworld.factorytest;

import android.util.Log;

public class FactoryBean {
    private static final String TAG =Utils.TAG +"FactoryBean" ;
    private String name;

//    private String title;
    private int titleID;

    private String action;

    private int status = Utils.NONE;

    public FactoryBean(String name, int titleID, String action, int status) {
        this.name = name;
        this.titleID = titleID;
        this.action = action;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTitleID( ){
        return  titleID;
    }
    public void setTitleID(int titleID ){
        this.titleID = titleID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getStatus( ){
        return  status;
    }
    public void setStatus(int status ){
        Log.d(TAG, "setStatus: "+status);
        this.status = status;
    }

    public String toString( ){
        String s = "name :"+name+"-titleID:"+titleID+"-action:"+action+"-status:"+status+"\n";
        return s;
    }
}
