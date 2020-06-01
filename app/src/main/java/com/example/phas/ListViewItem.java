package com.example.phas;

import android.view.View;

public class ListViewItem {

    private String fileName ;
    public View.OnClickListener onClickListener;

    public ListViewItem(String fileName){
        this.fileName = fileName;
    }

    public void setFileName(String text) {
        fileName = text ;
    }

    public String getFileName() {
        return this.fileName ;
    }
}
