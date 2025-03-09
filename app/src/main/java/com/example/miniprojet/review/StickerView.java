package com.example.miniprojet.review;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StickerView extends AppCompatImageView implements View.OnTouchListener {

    private final List<StickerInfo> stickerInfos = new ArrayList<>();
    private final PointF initialTouchPoint = new PointF();
    private int touchedStickerIndex = -1;

    public StickerView(Context context) {
        super(context);
        init();
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (StickerInfo info : stickerInfos) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(info.getBitmap(), info.getWidth(), info.getHeight(), true);
            canvas.drawBitmap(scaledBitmap, info.getMatrix(), null);
        }
    }

    public void addSticker(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        stickerInfos.add(new StickerInfo(bitmap, matrix));
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialTouchPoint.set(event.getX(), event.getY());
                touchedStickerIndex = findTouchedSticker(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchedStickerIndex != -1) {
                    float dx = event.getX() - initialTouchPoint.x;
                    float dy = event.getY() - initialTouchPoint.y;
                    stickerInfos.get(touchedStickerIndex).getMatrix().postTranslate(dx, dy);
                    initialTouchPoint.set(event.getX(), event.getY());
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                touchedStickerIndex = -1;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + event.getAction());
        }
        return true;
    }

    private int findTouchedSticker(float x, float y) {
        for (int i = stickerInfos.size() - 1; i >= 0; i--) {
            StickerInfo info = stickerInfos.get(i);
            if (isPointInsideSticker(x, y, info)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isPointInsideSticker(float x, float y, StickerInfo info) {
        float[] pts = {x, y};
        Matrix inverse = new Matrix();
        info.getMatrix().invert(inverse);
        inverse.mapPoints(pts);
        return pts[0] >= 0 && pts[0] < info.getBitmap().getWidth() && pts[1] >= 0 && pts[1] < info.getBitmap().getHeight();
    }
}