package com.example.miniprojet.review;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Review implements Serializable {
    private String id;
    private String restaurantId;
    private String userName;
    private float stars;
    private String content;
    private Date date;
    private List<String> imagesUrl = new ArrayList<>();

    public void addImage(String imageUrl) {
        imagesUrl.add(imageUrl);
    }

    public void removeImage(String imageUrl) {
        imagesUrl.remove(imageUrl);
    }

}
