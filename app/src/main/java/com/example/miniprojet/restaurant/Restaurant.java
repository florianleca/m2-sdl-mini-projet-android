package com.example.miniprojet.restaurant;

public class Restaurant {

    private final String name;
    private final String description;
    private final String imageUrl;

    public Restaurant(String name, String description, String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
