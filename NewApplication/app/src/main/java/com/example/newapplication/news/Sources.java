package com.example.newapplication.news;

public class Sources {
    private String id;
    private String name;
    private String category;
    private String Color;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public Sources(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }
}
