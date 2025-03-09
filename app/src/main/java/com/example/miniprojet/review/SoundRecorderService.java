package com.example.miniprojet.review;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import lombok.Getter;

@Getter
public class SoundRecorderService {

    private static final String TAG = SoundRecorderService.class.getSimpleName();

    private MediaRecorder mediaRecorder;
    private boolean isRecordingSound;
    private final Context context;

    public SoundRecorderService(Context context) {
        this.isRecordingSound = false;
        this.context = context;
    }

    public void startRecordingSound(File cacheDir) {
        isRecordingSound = true;
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(cacheDir.getAbsolutePath() + "/temp.3gp");
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            // Must be called for the first time here to record the max amplitude
            mediaRecorder.getMaxAmplitude();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred while starting sound recording", e);
        }
    }

    public Bitmap stopRecordingSoundAndApplyObscurity(Bitmap bitmap) {
        isRecordingSound = false;
        int amplitudeMax;
        if (mediaRecorder != null) {
            amplitudeMax = mediaRecorder.getMaxAmplitude();
            try {
                mediaRecorder.stop();
            } catch (RuntimeException e) {
                Log.e(TAG, "Error occurred while stopping sound recording", e);
            }
            mediaRecorder.release();
            mediaRecorder = null;
        } else {
            amplitudeMax = 0;
        }
        return applyObscurity(bitmap, amplitudeMax);
    }

    public Bitmap applyObscurity(Bitmap bitmap, int amplitudeMax) {
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();

        float obscurityFactor = Math.min(1f, amplitudeMax / 40000f);
        float brightness = 1 - obscurityFactor;
        float[] colorMatrix = {
                brightness, 0, 0, 0, 0,
                0, brightness, 0, 0, 0,
                0, 0, brightness, 0, 0,
                0, 0, 0, 1, 0
        };
        paint.setColorFilter(new android.graphics.ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        double obscurityPercentage = Math.floor(obscurityFactor * 10000) / 100;
        Toast.makeText(context, "Effet d'obscurité appliqué avec une force de " + obscurityPercentage + "% !", Toast.LENGTH_SHORT).show();
        return outputBitmap;
    }

}
