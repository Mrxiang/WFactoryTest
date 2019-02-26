package waterworld.com.factorytest.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IPowerManager;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import waterworld.com.factorytest.FactoryActivity;
import waterworld.com.factorytest.FactoryTestFeatureoption.FeatureOption;
import waterworld.com.factorytest.R;
import waterworld.com.factorytest.Utils;

//import com.mediatek.common.featureoption.FeatureOption;

public class SensorTest extends FactoryActivity implements OnClickListener, SensorEventListener {

    private static final String TAG = Utils.TAG + "SensorTest";
    TextView Gsensor_X;
    TextView Gsensor_Y;
    TextView Gsensor_Z;

    //by yds start
    TextView GySensor_X;
    TextView GySensor_Y;
    TextView GySensor_Z;
    //by yds end
    TextView tv_G_XR;
    TextView tv_G_XR1;
    TextView tv_G_YR;
    TextView tv_G_YR1;
    TextView tv_G_ZR;
    TextView tv_G_ZR1;
    TextView tv_AL;

//	TextView textView3;

    TextView AlsPs_Light;
    TextView AlsPs_Proxi;
    TextView AlsPs_pro;
    TextView AlsPs_pro_result;
    TextView tv_G_result;
    TextView tv_AL_result;
    TextView tv_AP_result;

    TextView mCompassTitle;
    TextView mCompassValue;
    ImageView mLeftIcon;
    ImageView mRightIcon;
    ImageView mUpIcon;
    ImageView mDownIcon;
    private boolean mZOrientationOk = false;
    double als_value1 = 0.0;
    double als_value2 = 0.0;
    double ps_value1 = 2.0;
    double ps_value2 = 2.0;
    boolean isSendIntent = false;

    IPowerManager server;
    private SensorManager mSensorManager;
    private Sensor mGSensor;
    private Sensor mALSSensor;
    private Sensor mPSSensor;
    private Sensor mMagnetic; //wlf
    private Sensor mGySensor;//by yds
    Button button_success;
    static int SensorTestStatus = 0;
    Thread mThread;

    private float[] accelerometerValues = new float[3];//wlf
    private float[] magneticFieldValues = new float[3];//wlf

    //fangfengfan add start
    private static final boolean HX_HAVE_MAGNETIC_FIELD = SystemProperties.getBoolean("ro.hx_have_magnetic_field", false);
    //fangfengfan add end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//		
//		if(!FeatureOption.FACTORYMODE_SENSOR_NOPS)
//		{
//		  setContentView(R.layout.sensor_test);
//		}else{
        setContentView(R.layout.sensor_test_nops);
//		}


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mALSSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mPSSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mGySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);//add by yds


        server = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));


//		System.out.println(mPSSensor.getName());

        button_success = (Button) findViewById(R.id.Button_Success);
        Button button_fail = (Button) findViewById(R.id.Button_Fail);
        button_success.setOnClickListener(this);
        button_fail.setOnClickListener(this);
        button_success.setEnabled(false);
        Gsensor_X = (TextView) findViewById(R.id.Gsensor_X);
        Gsensor_Y = (TextView) findViewById(R.id.Gsensor_Y);
        Gsensor_Z = (TextView) findViewById(R.id.Gsensor_Z);

        //by yds start
        GySensor_X = (TextView) findViewById(R.id.GySensor_X);
        GySensor_Y = (TextView) findViewById(R.id.GySensor_Y);
        GySensor_Z = (TextView) findViewById(R.id.GySensor_Z);
        //by yds end

        tv_G_XR = (TextView) findViewById(R.id.tv_G_XR);
        tv_G_XR1 = (TextView) findViewById(R.id.tv_G_XR1);
        tv_G_YR = (TextView) findViewById(R.id.tv_G_YR);
        tv_G_YR1 = (TextView) findViewById(R.id.tv_G_YR1);
        tv_G_ZR = (TextView) findViewById(R.id.tv_G_ZR);
        tv_G_ZR1 = (TextView) findViewById(R.id.tv_G_ZR1);
        tv_AL = (TextView) findViewById(R.id.tv_AL);
        AlsPs_Light = (TextView) findViewById(R.id.AlsPs_Light);
//		if(!FeatureOption.FACTORYMODE_SENSOR_NOPS)
//		{
//		AlsPs_Proxi = (TextView) findViewById(R.id.AlsPs_Proxi);
//		}else{
        //		textView3 = (TextView) findViewById(R.id.textView3);
//		}
        AlsPs_Proxi = (TextView) findViewById(R.id.AlsPs_Proxi);
        AlsPs_pro = (TextView) findViewById(R.id.AlsPs_pro);
        AlsPs_pro_result = (TextView) findViewById(R.id.AlsPs_pro_result);

        tv_G_result = (TextView) findViewById(R.id.tv_G_result);
        tv_AL_result = (TextView) findViewById(R.id.tv_AL_result);

        mCompassTitle = (TextView) findViewById(R.id.compass_title);
        mCompassValue = (TextView) findViewById(R.id.compass_value);
//		if(!FeatureOption.FACTORYMODE_SENSOR_NOPS)
//		{
//		tv_AP_result = (TextView) findViewById(R.id.tv_AP_result);
//		}

//		if(FeatureOption.FACTORYMODE_ALPS_NORYES){

////		if(!FeatureOption.FACTORYMODE_SENSOR_NOPS)
////		{
////		tv_AP_result.setText(R.string.Button_Status_Fail);
////		tv_AP_result.setTextColor(Color.RED);
////		}
//		}else{

        //	textView3.setVisibility(View.GONE);
//		}

        if (null != mALSSensor && !FeatureOption.TOUCHPANEL_TOUCHFELL_IN_PHONE_CALL) {//(FeatureOption.HX_FACTORYTEST_ALS) {
            tv_AL_result.setText(getString(R.string.Button_Status_Fail));
            tv_AL_result.setTextColor(Color.RED);
            tv_AL.setVisibility(View.VISIBLE);
            AlsPs_Light.setVisibility(View.VISIBLE);
        } else {
            tv_AL.setVisibility(View.GONE);
            tv_AL_result.setVisibility(View.GONE);
            AlsPs_Light.setVisibility(View.GONE);
        }
        if (null != mPSSensor && !FeatureOption.TOUCHPANEL_TOUCHFELL_IN_PHONE_CALL) {//(FeatureOption.HX_FACTORYTEST_PS) {
            AlsPs_Proxi.setVisibility(View.VISIBLE);
            AlsPs_pro.setVisibility(View.VISIBLE);
            AlsPs_pro_result.setVisibility(View.VISIBLE);
        } else {
            AlsPs_Proxi.setVisibility(View.GONE);
            AlsPs_pro.setVisibility(View.GONE);
            AlsPs_pro_result.setVisibility(View.GONE);
        }

        if (mMagnetic != null && HX_HAVE_MAGNETIC_FIELD) {
            mCompassTitle.setVisibility(View.VISIBLE);
            mCompassValue.setVisibility(View.VISIBLE);
        } else {
            mCompassTitle.setVisibility(View.GONE);
            mCompassValue.setVisibility(View.GONE);
        }

        if (mGSensor != null) {
            mSensorManager.registerListener((SensorEventListener) this, mGSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
        //add by yds start
        if (mGySensor != null) {
            mSensorManager.registerListener((SensorEventListener) this, mGySensor, SensorManager.SENSOR_DELAY_NORMAL);
            GySensor_X.setText("GySensor X values is   0");
            GySensor_Y.setText("GySensor Y values is   0");
            GySensor_Z.setText("GySensor Z values is   0");
        } else {
            //GySensor_Y.setText("Gysensor not support");
            //GySensor_Y.setTextColor(Color.RED);
        }
        //add by yds end
        if (mALSSensor != null) {
            mSensorManager.registerListener((SensorEventListener) this, mALSSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }

        if (mPSSensor != null) {
            //if(!FeatureOption.NO_ALSPS_SENSOR){
            mSensorManager.registerListener((SensorEventListener) this, mPSSensor, SensorManager.SENSOR_DELAY_NORMAL);
            //}
        }

        if (mMagnetic != null) {
            mSensorManager.registerListener((SensorEventListener) this, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        }

        RelativeLayout xyzResult = (RelativeLayout) findViewById(R.id.RL_xyz);
        mUpIcon = (ImageView) findViewById(R.id.gsensor_orientation_up);
        mLeftIcon = (ImageView) findViewById(R.id.gsensor_orientation_left);
        mRightIcon = (ImageView) findViewById(R.id.gsensor_orientation_right);
        mDownIcon = (ImageView) findViewById(R.id.gsensor_orientation_down);
        if (android.os.SystemProperties.getBoolean("ro.hx_gsensor_graphic", false)) {
            xyzResult.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        mSensorManager.unregisterListener((SensorEventListener) this);
        super.onDestroy();
    }

    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        //by yds start
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            GySensor_X.setText("GySensor X values is   " + event.values[0] + "");
            GySensor_Y.setText("GySensor Y values is   " + event.values[1] + "");
            GySensor_Z.setText("GySensor Z values is   " + event.values[2] + "");
        }
        //by yds end

        //Gsensor
        if (event.sensor.getType() == 1) {
            Gsensor_X.setText("X values is   " + event.values[0] + "");
            Gsensor_Y.setText("Y values is   " + event.values[1] + "");
            Gsensor_Z.setText("Z values is   " + event.values[2] + "");

            //add by leiyaotao for XYZ change
            //X
            if (android.os.SystemProperties.getBoolean("ro.hx_gsensor_graphic", false)) {
                if ((int) event.values[2] >= 9) {
                    mZOrientationOk = true;
                }

                if (mZOrientationOk && (int) event.values[0] >= 5) {
                    mLeftIcon.setVisibility(View.VISIBLE);
                }
                if (mZOrientationOk && (int) event.values[0] <= -5) {
                    mRightIcon.setVisibility(View.VISIBLE);
                }
                if (mZOrientationOk && (int) event.values[1] >= 5) {
                    mDownIcon.setVisibility(View.VISIBLE);
                }
                if (mZOrientationOk && (int) event.values[1] <= -5) {
                    mUpIcon.setVisibility(View.VISIBLE);
                }
                if (mRightIcon.isShown() && mLeftIcon.isShown() && mUpIcon.isShown() && mDownIcon.isShown()) {
                    tv_G_result.setText(R.string.Button_Status_Success);
                    tv_G_result.setTextColor(Color.GREEN);
                    button_success.setEnabled(true);

                    if (als_value2 < 300.0 && als_value1 - als_value2 > 700) {
                        if (ps_value1 == 0.0 && ps_value2 == 1.0) {
                            if (isSendIntent)
                                return;
                            isSendIntent = true;

                            Intent mIntent = getIntent();
                            boolean fl = mIntent.getBooleanExtra("textall", false);
                            if (fl == true) {
                                Intent miIntent = new Intent();
                                if (FeatureOption.MTK_GPS_SUPPORT) { //FeatureOption.MTK_GPS_SUPPORT
                                    miIntent.setAction("com.ykq.intent.action.GPS_TEST");
                                    GpsTestResult.mCreateFirstTime = true;
                                } else {
                                    miIntent.setAction("com.ykq.intent.action.WIFI_TEST");
                                }
                                miIntent.putExtra("textall", true);
                                startActivityForResult(miIntent, RESULT_OK);
                                SensorTestStatus = 1;

                                finish();
                            }
                        }
                    }
                } else {
                    tv_G_result.setText(R.string.Button_Status_Fail);
                    tv_G_result.setTextColor(Color.RED);
                }

            } else {
                if ((int) event.values[0] == 9 && (int) event.values[1] == 0 && (int) event.values[2] == 0) {
                    tv_G_XR.setText("OK");
                    tv_G_XR.setTextColor(Color.GREEN);
                }
                if ((int) event.values[0] == -9 && (int) event.values[1] == 0 && (int) event.values[2] == 0) {
                    tv_G_XR1.setText("OK");
                    tv_G_XR1.setTextColor(Color.GREEN);
                }
                //Y
                if ((int) event.values[0] == 0 && (int) event.values[1] == 9 && (int) event.values[2] == 0) {
                    tv_G_YR.setText("OK");
                    tv_G_YR.setTextColor(Color.GREEN);
                }
                if ((int) event.values[0] == 0 && (int) event.values[1] == -9 && (int) event.values[2] == 0) {
                    tv_G_YR1.setText("OK");
                    tv_G_YR1.setTextColor(Color.GREEN);
                }
                //Z
                if ((int) event.values[0] == 0 && (int) event.values[1] == 0 && (int) event.values[2] == 9) {
                    tv_G_ZR.setText("OK");
                    tv_G_ZR.setTextColor(Color.GREEN);
                }
                if ((int) event.values[0] == 0 && (int) event.values[1] == 0 && (int) event.values[2] == -9) {
                    tv_G_ZR1.setText("OK");
                    tv_G_ZR1.setTextColor(Color.GREEN);
                }

                if ((("OK".equals(tv_G_XR.getText())) || ("OK".equals(tv_G_XR1.getText()))) &&
                        (("OK".equals(tv_G_YR.getText())) || ("OK".equals(tv_G_YR1.getText()))) &&
                        (("OK".equals(tv_G_ZR.getText())) || ("OK".equals(tv_G_ZR1.getText())))) {
                    tv_G_result.setText(R.string.Button_Status_Success);
                    tv_G_result.setTextColor(Color.GREEN);
                    button_success.setEnabled(true);
                } else {
                    tv_G_result.setText(R.string.Button_Status_Fail);
                    tv_G_result.setTextColor(Color.RED);
                }
            }


            //end modify
		/*
		if((!"X values is".equals(Gsensor_X.getText())) && (!"X values is".equals(Gsensor_Y.getText())) 
				&& (!"Z values is".equals(Gsensor_Z.getText()))){
			tv_G_result.setText(R.string.Button_Status_Success);
			tv_G_result.setTextColor(Color.GREEN);
		}else{
			tv_G_result.setText(R.string.Button_Status_Fail);
			tv_G_result.setTextColor(Color.RED);
		}
		*/
        }
        //LightSensor

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            AlsPs_Light.setText("als values is " + event.values[0] + "");
//			if(FeatureOption.FACTORYMODE_ALPS_NORYES){
            if (event.values[0] > 1000) {
                als_value1 = event.values[0];
            }
            als_value2 = event.values[0];
            if (!"als values is".equals(AlsPs_Light.getText()) && !"als values is 0.0".equals(AlsPs_Light.getText())) {
                tv_AL_result.setText(R.string.Button_Status_Success);
                tv_AL_result.setTextColor(Color.GREEN);
            } else {
                tv_AL_result.setText(R.string.Button_Status_Fail);
                tv_AL_result.setTextColor(Color.RED);
            }
//			}
        }
        //ProximitySensor
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
//			if(!FeatureOption.NO_ALSPS_SENSOR){
//				Log.e(TAG, "2222");
////				if(!FeatureOption.FACTORYMODE_SENSOR_NOPS)
////				{
            AlsPs_Proxi.setText("ps values is " + event.values[0] + "");

            if (event.values[0] == 0.0)
                ps_value1 = event.values[0];
            if (event.values[0] == 1.0)
                ps_value2 = event.values[0];
////				}
//			}else{
            Log.e(TAG, "event.values[0] = " + event.values[0]);

            try {
                //	if("0.0".equals(""+event.values[0])){
//					server.enableProximityLockLocked();
                //	AlsPs_pro.setText(getResources().getString(R.string.Text_ALSPS_VALUE_CLOSE)+"");
//					if(FeatureOption.FACTORYMODE_ALPS_NORYES){
                if (!"ps values is".equals(AlsPs_Proxi.getText()) && !"ps values is 0.0".equals(AlsPs_Proxi.getText())) {
                    AlsPs_pro_result.setText(R.string.Text_ALSPS_VALUE_AWAY);
                    AlsPs_pro_result.setTextColor(Color.RED);
                } else {
                    AlsPs_pro_result.setText(R.string.Text_ALSPS_VALUE_CLOSE);
                    AlsPs_pro_result.setTextColor(Color.GREEN);
                }
//					}

                //		}else{
                //AlsPs_pro.setText(getResources().getString(R.string.Text_ALSPS_VALUE_AWAY)+"");
//					server.disableProximityLockLocked();
                //	}
            } catch (Exception e) {
                // TODO: handle exception
                Log.e(TAG, "333");
            }

//			}

        }
        //wlf start
        if (mMagnetic == null) return;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;
        }
        calculateOrientation();
        //wlf add end
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
//		if(FeatureOption.NO_ALSPS_SENSOR){
        if (event.getY() > 0 && event.getY() < 200) {
            Log.e(TAG, "event.getY = " + event.getY());
            mSensorManager.registerListener((SensorEventListener) this, mPSSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
//		}

        return super.onTouchEvent(event);
    }

    //	@Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.Button_Success:
                SensorTestStatus = 1;
                break;
            case R.id.Button_Fail:
                SensorTestStatus = -1;
                break;
            default:
                break;
        }
        setResultBeforeFinish( SensorTestStatus );
        finish();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    //wlf add for compass 20150520
    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R1 = new float[9];
        SensorManager.getRotationMatrix(R1, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R1, values);
        values[0] = (float) Math.toDegrees(values[0]);

        //Log.i("wlf", values[0] + "");
        String orientation = "unknown";
        if (values[0] >= -15 && values[0] < 15) {
            orientation = getString(R.string.compass_value_n);
        } else if (values[0] >= 15 && values[0] < 75) {
            orientation = getString(R.string.compass_value_ne);
        } else if (values[0] >= 75 && values[0] <= 105) {
            orientation = getString(R.string.compass_value_e);
        } else if (values[0] >= 105 && values[0] < 165) {
            orientation = getString(R.string.compass_value_se);
        } else if ((values[0] >= 165 && values[0] <= 180)
                || (values[0]) >= -180 && values[0] < -165) {
            orientation = getString(R.string.compass_value_s);
        } else if (values[0] >= -165 && values[0] < -105) {
            orientation = getString(R.string.compass_value_sw);
        } else if (values[0] >= -105 && values[0] < -75) {
            orientation = getString(R.string.compass_value_w);
        } else if (values[0] >= -75 && values[0] < -15) {
            orientation = getString(R.string.compass_value_nw);
        }
        mCompassValue.setText(orientation /*+ "\n" + (int)values[0]*/);
        mCompassValue.setTextColor(Color.GREEN);
    }
    //wlf add end.

    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "sensor");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }
}
