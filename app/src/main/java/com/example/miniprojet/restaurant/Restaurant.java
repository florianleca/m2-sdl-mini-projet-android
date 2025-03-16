package com.example.miniprojet.restaurant;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Restaurant implements Serializable {
    private String id;
    private String name;
    private String description;
    private String imageUrl;

    public float getRating() {
        return (float) (2.5 + Math.random() * (5 - 2.5));
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
