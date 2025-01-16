package com.example.miniprojet.review;

import java.util.Date;
import java.util.List;

public class Review {

    private final float stars;
    private final String content;
    private final Date date;
    private final List<String> imagesUrl;

    public Review(float stars, String content, Date date, List<String> imagesUrl) {
        this.stars = stars;
        this.content = content;
        this.date = date;
        this.imagesUrl = imagesUrl;
    }

    public float getStars() {
        return stars;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public List<String> getImagesUrl() {
        return imagesUrl;
    }
}
