package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

//import com.android.server.lights.LightsManager;


public class CalibMsensor extends Activity {


    private static final String TAG = Utils.TAG+"CalibMsensor" ;

    private Sensor mMagSensor = null;
    private SensorManager mSensorManager = null;
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calibration_msensor);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mMagSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(listener,mMagSensor, SensorManager.SENSOR_DELAY_GAME);

    }
    private SensorEventListener listener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "onSensorChanged: "+event.toString());
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "onAccuracyChanged: "+sensor.getName()+"  "+ accuracy);
            if( sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD ){

                if( accuracy == 3 ){
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.calibration_success),Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    };


}
