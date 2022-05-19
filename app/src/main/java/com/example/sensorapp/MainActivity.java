package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{


    private final int SAMPLE_SIZE = 25; // ornekleme sayisi
    private final double THRESHOLD = 0.2; // ornekleme threshold

    protected Button btn;
    protected TextView light_inf,acc_inf,durum;
    protected ImageView img;
    protected AirplaneModeChangeReceiver airplaneModeChangeReceiver;
    protected SensorManager mySensorManager;
    protected Sensor myLightSensor, myAccSensor;
    private boolean isik,hareket;
    private double acc_curr,acc_prev,acc;
    private int walkCount = 0;
    private double walkSum = 0;
    private double walkResult = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isik=false;hareket=false;
        light_inf = findViewById(R.id.textView);
        acc_inf = findViewById(R.id.textView3);
        durum = findViewById(R.id.textView4);
        img  = findViewById(R.id.imageView);
        btn = findViewById(R.id.button);

        airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();
        mySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        myLightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        myAccSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        acc_curr = SensorManager.GRAVITY_EARTH;
        acc_prev = SensorManager.GRAVITY_EARTH;
        acc=0.00f;


        mySensorManager.registerListener(this,myLightSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this,myAccSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT){
            if(sensorEvent.values[0]<=0.2){
                light_inf.setText("ISIK KAPALI");
                isik=false;
            }else{
                light_inf.setText("ISIK ACIK");
                isik=true;
            }
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            acc_inf.setText("X: "+sensorEvent.values[0]+" Y: "+sensorEvent.values[1]+" Z: "+sensorEvent.values[2]);
            acc_prev=acc_curr;
            acc_curr = Math.sqrt(x*x+y*y+z*z);
            double change = acc_curr-acc_prev;
            acc = acc * 0.9f + change;

            if (walkCount <= SAMPLE_SIZE) {
                walkCount++;
                walkSum += Math.abs(acc);
            } else {
                walkResult = walkSum / SAMPLE_SIZE;
                if (walkResult > THRESHOLD) {
                    hareket=true;
                } else {
                    hareket=false;
                }
                walkCount = 0;
                walkSum = 0;
                walkResult = 0;
            }
        }
        Intent intent = new Intent("com.example.sensor");
        if(hareket){
            if (!isik){
                durum.setText("HAREKETLİ CEPTE");
                img.setImageResource(R.drawable.man);
                intent.putExtra("player", "on");
                sendBroadcast(intent);
            }
        }else{
            if (isik){
                durum.setText("MASADA VE AÇIK");
                img.setImageResource(R.drawable.desk1);
                intent.putExtra("player", "on");
                sendBroadcast(intent);
            }else{
                durum.setText("MASADA VE UYKUDA");
                img.setImageResource(R.drawable.uyku);
                intent.putExtra("player", "off");
                sendBroadcast(intent);
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    @Override
    protected void onPause() {
        super.onPause();
        mySensorManager.unregisterListener(this,myAccSensor);
        mySensorManager.unregisterListener(this,myLightSensor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mySensorManager.registerListener(this,myLightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this,myAccSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneModeChangeReceiver,filter);
    }
    //masaüstü durumu, cepte ve oturuyor ,cepte ve yürüyor....ivme ölçer ve yakınlık sensörü-lightsensor kullandık.
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airplaneModeChangeReceiver);
        mySensorManager.unregisterListener(this,myAccSensor);
        mySensorManager.unregisterListener(this,myLightSensor);
    }

    public void reset(View W){
        acc_inf.setText("X: 0 Y: 0 Z: 0");
        light_inf.setText("ISIK SENSORU");
        durum.setText("DURUM");
    }
}