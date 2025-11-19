package com.example.mylists;

public class DeleteByTextRequest {
    private String listType;
    private String text;

    public String getListType() { return listType; }
    public void setListType(String listType) { this.listType = listType; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
