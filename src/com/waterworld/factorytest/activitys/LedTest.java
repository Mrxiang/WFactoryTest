package com.waterworld.factorytest.activitys;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.android.server.lights.LightsManager;
import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.FactoryDatas;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;



public class LedTest extends FactoryActivity{


    private int    LED_test_result;
    private static final String TAG = Utils.TAG+"LedTest" ;

    private  boolean lightMode= false;
    private  Thread  thread;
    private  NotificationManager mNM;
    private PowerManager mPowerManager;
    private final static String NOTIFY_TAG = "foo";
    private static final String SIM_SELECT_NOTIFICATION_CHANNEL =
            "sim_select_notification_channel";
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.led_test);
        storeCurrenState( );
//        entryLightMode( );
        entryPowerMode( );
    }

    private void entryPowerMode() {

        lightMode = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (lightMode) {
                        Log.d(TAG, "run: " + lightMode);
                        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        mPowerManager.turnOffLedLight();
                        Thread.sleep(1000);

                        mPowerManager.setLedLight(Color.RED);
                        Thread.sleep(1000);

                        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        mPowerManager.setLedLight(Color.GREEN);
                        Thread.sleep(1000);

                        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        mPowerManager.setLedLight(Color.BLUE);
                        Thread.sleep(1000);

                    }
//xshx 20190510					
		    if( !lightMode ){
			mPowerManager.setLedLight(Color.BLACK);
		    }
                } catch (Exception e) {

                }
            }
        });
        thread.start();


        new AlertDialog.Builder(this)
                .setCancelable( false)
                .setMessage(getString(R.string.light_mode))
                .setNegativeButton(R.string.msg_fou,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LED_test_result = Utils.FAILED;
                                lightMode = false;
                                setResultBeforeFinish( LED_test_result );
                                restoreCurrentState();
                                finish();
                            }
                        })
                .setPositiveButton(R.string.msg_shi,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                LED_test_result = Utils.SUCCESS;
                                lightMode= false;
                                setResultBeforeFinish( LED_test_result );
                                restoreCurrentState();
                                finish();
                            }
                        })
                .create()
                .show();
    }




    private void storeCurrenState() {

    }


    private void entryLightMode() {

        turnOffAll();
        lightMode = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while( lightMode ) {
                        Log.d(TAG, "run: "+lightMode);
                        Utils.runRootCommand(Utils.LED_RED_ON);
                        Thread.sleep(1000);
                        Utils.runRootCommand(Utils.LED_RED_OFF);
                        Thread.sleep(1000);
                        Utils.runRootCommand(Utils.LED_GREEN_ON);
                        Thread.sleep(1000);
                        Utils.runRootCommand(Utils.LED_GREEN_OFF);
                        Thread.sleep(1000);
                        Utils.runRootCommand(Utils.LED_YELLOW_ON);
                        Thread.sleep(1000);
                        Utils.runRootCommand(Utils.LED_YELLOW_OFF);
                        Thread.sleep(1000);

                    }

                }catch (Exception e){

                }
            }
        });
        thread.start();


        new AlertDialog.Builder(this)
                .setCancelable( false)
                .setMessage(getString(R.string.light_mode))
                .setNegativeButton(R.string.msg_fou,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LED_test_result = Utils.FAILED;
                                lightMode = false;
                                setResultBeforeFinish( LED_test_result );
                                restoreCurrentState();
                                finish();
                            }
                        })
                .setPositiveButton(R.string.msg_shi,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                LED_test_result = Utils.SUCCESS;
                                lightMode= false;
                                setResultBeforeFinish( LED_test_result );
                                restoreCurrentState();
                                finish();
                            }
                        })
                .create()
                .show();

    }


    private void restoreCurrentState() {

    }

    private void turnOffAll( ){
        Utils.runRootCommand( Utils.LED_LIGHT_MODE );
        Utils.runRootCommand( Utils.LED_RED_OFF );
        Utils.runRootCommand( Utils.LED_GREEN_OFF );
        Utils.runRootCommand( Utils.LED_YELLOW_OFF);
    }

    public void finish() {
        super.finish();
    }

    @Override
    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "led");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);

    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
//		super.onBackPressed();
        setResultBeforeFinish( LED_test_result );
        finish();


    }

    public void light_red(View view){
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mPowerManager.setLedLight( Color.RED);
    }

    public void light_blue(View view){
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mPowerManager.setLedLight( Color.BLUE);
    }
    public void light_green(View view){
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mPowerManager.setLedLight( Color.GREEN);
    }

}
