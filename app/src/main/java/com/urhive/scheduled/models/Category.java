package com.urhive.scheduled.models;

import com.orm.SugarRecord;

/**
 * Created by Chirag Bhatia on 05-06-2016.
 */
public class Category extends SugarRecord {
    String name;
    int color;
    int iconId;
    int position;
    int count;

    public Category() {

    }

    public Category(String name, int color, int iconId, int position, int count) {
        this.name = name;
        this.color = color;
        this.iconId = iconId;
        this.position = position;
        this.count = count;
    }

    // getter and setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    // to string

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", color=" + color +
                ", iconId=" + iconId +
                ", position=" + position +
                ", count=" + count +
                '}';
    }
}
