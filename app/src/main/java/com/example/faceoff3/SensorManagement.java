package com.example.faceoff3;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class SensorManagement {

    private SensorManager sensorManager;
    private Sensor sensor;





    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    
}
