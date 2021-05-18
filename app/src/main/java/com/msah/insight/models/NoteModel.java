package com.msah.insight.models;


public class NoteModel {

    private String name;
    private String description;
    private String photo_thumbnail;

    public NoteModel() {

    }

    public NoteModel(String name, String description, String photo_thumbnail) {
        this.name = name;
        this.description = description;
        this.photo_thumbnail = photo_thumbnail;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String name) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }


    public String getPhoto_thumbnail() {
        return photo_thumbnail;
    }

    public void setPhoto_thumbnail(String photo_profile) {
        this.photo_thumbnail = photo_thumbnail;
    }

}

