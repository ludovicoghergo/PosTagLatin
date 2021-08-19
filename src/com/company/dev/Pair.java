package com.company.dev;

public class Pair {
    private String tag;
    private double cout;

    public Pair(String tag, double cout) {
        this.tag = tag;
        this.cout = cout;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double getCout() {
        return cout;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }
}
