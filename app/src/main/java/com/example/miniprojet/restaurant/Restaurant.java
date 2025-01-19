package com.example.miniprojet.restaurant;

import org.jetbrains.annotations.NotNull;

public class Restaurant {

    private String name;
    private String description;
    private String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRating() {
        return (float) (1 + Math.random() * (5 - 1));
    }

    @Override
    @NotNull
    public String toString() {
        return "Restaurant {" +
                "\n\tname='" + name + '\'' +
                ",\n\tdescription='" + description + '\'' +
                ",\n\timageUrl='" + imageUrl + '\'' +
                "\n} ";
    }

}
