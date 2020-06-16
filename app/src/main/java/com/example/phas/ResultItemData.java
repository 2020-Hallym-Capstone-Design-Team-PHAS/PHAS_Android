package com.example.phas;

public class ResultItemData {
    private String content;
    private String date;

    public ResultItemData(String content, String date) {
        this.content = content;
        this.date = date;
    }

    public ResultItemData() {
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
