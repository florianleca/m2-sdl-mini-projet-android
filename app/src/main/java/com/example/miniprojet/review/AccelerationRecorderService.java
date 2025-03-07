package com.example.miniprojet.review;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;
import android.widget.Toast;

import lombok.Getter;

@Getter
public class AccelerationRecorderService implements SensorEventListener {

    private final Context context;
    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private boolean isRecordingAcceleration;
    private float totalAcceleration;

    public AccelerationRecorderService(Context context, SensorManager sensorManager) {
        this.context = context;
        this.sensorManager = sensorManager;
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.isRecordingAcceleration = false;
        this.totalAcceleration = 0f;
    }

    public void startRecordingAcceleration() {
        isRecordingAcceleration = true;
        totalAcceleration = 0f;
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public Bitmap stopRecordingAccelerationAndApplyBlur(Bitmap bitmap) {
        isRecordingAcceleration = false;
        sensorManager.unregisterListener(this);

        float blurRadius = Math.min(25, Math.max(1, totalAcceleration / 50));
        return applyBlur(bitmap, blurRadius);
    }

    public Bitmap applyBlur(Bitmap bitmap, float blurRadius) {
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap);

        if (blurRadius < 2) {
            Toast.makeText(context, "Effet trop faible - aucun flou n'a été appliqué.", Toast.LENGTH_SHORT).show();
            return bitmap;
        }
        blurRadius = Math.min(25, blurRadius * 4);

        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createFromBitmap(rs, outputBitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, input.getElement());
        blur.setInput(input);
        blur.setRadius(blurRadius);
        blur.forEach(output);
        output.copyTo(outputBitmap);
        rs.destroy();

        double blurPercentage = Math.floor((blurRadius - 4) * 400) / 100;
        Toast.makeText(context, "Effet de flou appliqué avec une force de " + blurPercentage + "% !", Toast.LENGTH_SHORT).show();
        return outputBitmap;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isRecordingAcceleration && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);
            totalAcceleration += Math.abs(acceleration - SensorManager.GRAVITY_EARTH);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used but had to override it to implement SensorEventListener
    }

    public void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

}
