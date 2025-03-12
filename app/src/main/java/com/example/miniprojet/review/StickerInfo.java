package com.example.miniprojet.review;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public  class StickerInfo {
    private final Bitmap bitmap;
    private final Matrix matrix;
    private  int width;
    private  int height;
    private float scaleFactor;

    public StickerInfo(Bitmap bitmap, Matrix matrix) {
        this(bitmap, matrix, 150, 150,1.0f);
    }

    public StickerInfo(Bitmap bitmap, Matrix matrix, int width, int height, float scaleFactor) {
        this.bitmap = bitmap;
        this.matrix = matrix;
        this.width = width;
        this.height = height;
        this.scaleFactor = scaleFactor;
    }
}
