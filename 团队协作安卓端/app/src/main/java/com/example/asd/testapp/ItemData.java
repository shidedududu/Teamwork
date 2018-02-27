package com.example.asd.testapp;

import android.graphics.Color;

import java.util.Date;

/**
 * Created by Jing on 15/5/27.
 */
public class ItemData {
    String id;
    int color;
    public int icon;
    public String title;
    public String time;

    public ItemData(String id,int color, int icon, String title, String time) {
        this.id=id;
        this.color = color;
        this.icon = icon;
        this.title = title;
        this.time = time;
    }

    /*public ItemData(int icon, String title) {
        this(Color.DKGRAY, icon, title, new Date());
    }*/
}
