package com.waterworld.factorytest.activitys;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;


//import com.android.server.lights.LightsManager;


public class GySensorTest extends FactoryActivity implements View.OnClickListener, SensorEventListener{


    private static final String TAG = Utils.TAG+"GySensorTest" ;
    private TextView GySensor_X;
    private TextView GySensor_Y;
    private TextView GySensor_Z;
    private Sensor  mGySensor;
    private SensorManager mSensorManager;
    private int    test_result;
    private Button success;
    private Button failed;
    private ImageView gy_image;

    private boolean  up_pass;
    private boolean  down_pass;
    private boolean  left_pass;
    private boolean  right_pass;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gy_sensor);

        success = findViewById(R.id.success);
        success.setOnClickListener(this);

        failed = findViewById(R.id.failed);
        failed.setOnClickListener(this);

        GySensor_X = (TextView) findViewById(R.id.GySensor_X);
        GySensor_Y = (TextView) findViewById(R.id.GySensor_Y);
        GySensor_Z = (TextView) findViewById(R.id.GySensor_Z);

        gy_image = (ImageView) findViewById( R.id.gy_image );
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (mGySensor != null) {
            mSensorManager.registerListener((SensorEventListener) this, mGySensor, SensorManager.SENSOR_DELAY_NORMAL);
            GySensor_X.setText("GySensor X values is   0");
            GySensor_Y.setText("GySensor Y values is   0");
            GySensor_Z.setText("GySensor Z values is   0");
        }

    }




    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        //by yds start
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            GySensor_X.setText("GySensor X values is   " + event.values[0] + "");
            GySensor_Y.setText("GySensor Y values is   " + event.values[1] + "");
            GySensor_Z.setText("GySensor Z values is   " + event.values[2] + "");

            if( event.values[0] > 2.0f ){
                gy_image.setImageResource(R.drawable.gsensor_up);
                up_pass = true;
            }else if( event.values[0] < -2.0f ){
                gy_image.setImageResource(R.drawable.gsensor_down);
                down_pass = true;

            }
            if( event.values[1] > 2.0f ){
                gy_image.setImageResource(R.drawable.gsensor_left);
                left_pass = true;
            }else if( event.values[1] < -2.0f ){
                gy_image.setImageResource(R.drawable.gsensor_right);
                right_pass = true;

            }
            if( event.values[2] > 2.0f ){
                gy_image.setImageResource(R.drawable.gsensor_right);
                right_pass = true;
            }else if( event.values[2] < -2.0f ){
                gy_image.setImageResource(R.drawable.gsensor_left);
                left_pass = true;

            }

            if( up_pass  && down_pass && left_pass && right_pass ){
                success.setClickable( true );
                setResultBeforeFinish( Utils.SUCCESS );
                finish();
            }
        }
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }



    private void restoreCurrentState() {

    }


    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch ( v.getId() ){
            case R.id.success:
                test_result = 1;
                break;
            case R.id.failed:
                test_result = -1;
                break;
            default:
                break;

        }

        setResultBeforeFinish( test_result );
        finish();
    }
    public void finish() {
        super.finish();
        mSensorManager.unregisterListener((SensorEventListener) this);

    }

    @Override
    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "gySensor");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);

    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
//		super.onBackPressed();


    }


}
