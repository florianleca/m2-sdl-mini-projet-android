package com.example.miniprojet.review;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Review {
    private String id;
    private String restaurantId;
    private String userName;
    private float stars;
    private String content;
    private Date date;
    private List<String> imagesUrl;

}
