package com.urhive.scheduled.models;

import com.orm.SugarRecord;

/**
 * Created by Chirag Bhatia on 04-06-2016.
 */
public class Icon extends SugarRecord {
    String name;
    int useFrequency;

    public Icon() {

    }

    public Icon(String name, int useFrequency) {
        this.name = name;
        this.useFrequency = useFrequency;
    }

    // getter and setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUseFrequency() {
        return useFrequency;
    }

    public void setUseFrequency(int useFrequency) {
        this.useFrequency = useFrequency;
    }

    // tostring

    @Override
    public String toString() {
        return "Icon{" +
                "name='" + name + '\'' +
                ", useFrequency=" + useFrequency +
                '}';
    }
}
