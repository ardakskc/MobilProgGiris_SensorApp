package com.example.sensorapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

public class AirplaneModeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(isAirplaneModeOn(context.getApplicationContext())){
            Toast.makeText(context, "Airplane mode on!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Airplane mode off!", Toast.LENGTH_SHORT).show();
        }
    }
    private static boolean isAirplaneModeOn(Context context){
        return Settings.System.getInt(context.getContentResolver(),Settings.Global.AIRPLANE_MODE_ON,0)!=0;
    }
}
