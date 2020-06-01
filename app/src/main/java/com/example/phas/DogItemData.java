package com.example.phas;

public class DogItemData {
    private String sName;
    private int count;

    public DogItemData(int count, String sName) {
        this.count = count;
        this.sName = sName;
    }

    public String getsName() { return sName; }
    public void setsName(String sName) { this.sName = sName; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
