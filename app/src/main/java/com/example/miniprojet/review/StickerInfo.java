package com.example.miniprojet.review;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import lombok.Getter;

@Getter
public  class StickerInfo {
    private final Bitmap bitmap;
    private final Matrix matrix;
    private final int width;
    private final int height;

    public StickerInfo(Bitmap bitmap, Matrix matrix) {
        this(bitmap, matrix, 150, 150);
    }

    public StickerInfo(Bitmap bitmap, Matrix matrix, int width, int height) {
        this.bitmap = bitmap;
        this.matrix = matrix;
        this.width = width;
        this.height = height;
    }
}
