package com.msah.insight.models;

import android.graphics.Color;

import java.io.Serializable;


public class AtItem implements Serializable {
    public int mIconId;
    public String mName;
    public String mKey;
    public int mColor;

    public AtItem(String key, String name) {
        this(key, name, Color.BLUE);
    }

    public AtItem(String key, String name, int color) {
        this.mKey = key;
        this.mName = name;
        this.mIconId = Integer.parseInt(key);
        this.mColor = color;
    }
}

