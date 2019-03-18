package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;

import com.waterworld.factorytest.FactoryTestFeatureoption.FeatureOption;
import com.waterworld.factorytest.Utils;

import java.util.Timer;
//import com.mediatek.common.featureoption.FeatureOption;

public class KeyTest extends FactoryActivity implements View.OnClickListener {

    public static final String TAG = Utils.TAG + "KeyTest";
    static int CameraTestStatus = 0;
    MediaPlayer mediaPlayer;
    Button menu;
    Button main;
    Button vu;
    Button retu;
    Button vd;
    Button power;
    Button camera;
    TextView mada;
    Button success_Button;
    long[] mVibratePattern;
    //	Vibrator mVibrator;
    Timer mTimer;
    boolean flags = true;
    Timer mTimer2;
    boolean flags2 = true;
    static int mbrightness;
    int keyflag = 0;

    LinearLayout mNotificationLight;
    Button light_R, light_G, light_B;
    private boolean mLightROn, mLightGOn, mLightBOn;

    private int SWIPE_UP_TO_SWITCH_APPS_ENABLED = 0;
    private int defValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

//	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.test_result_key);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        menu = (Button) findViewById(R.id.Button_menu);
        if (!FeatureOption.HX_SHOW_KEYTEST_SHOW_MENU) {
            //menu.setVisibility(View.GONE);
        }
        main = (Button) findViewById(R.id.Button_main);
        retu = (Button) findViewById(R.id.Button_return);
//		power = (Button) findViewById(R.id.Button_power);
        vu = (Button) findViewById(R.id.Button_volumeup);
        vd = (Button) findViewById(R.id.Button_volumedown);
//		 mada = (TextView) findViewById(R.id.Button_mada);
        camera = (Button) findViewById(R.id.Button_camera);
        if (FeatureOption.HX_FACTORYTEST_CAMERAKEY) {
            camera.setVisibility(View.VISIBLE);
        }
        success_Button = (Button) findViewById(R.id.Button_Success);
        Button fail_Button = (Button) findViewById(R.id.Button_Fail);
        success_Button.setOnClickListener(this);
        fail_Button.setOnClickListener(this);
        success_Button.setEnabled(false);  // changed by wanghe 2013-08-10
//		  mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mVibratePattern = new long[]{10, 20};
        mbrightness = getScreenBrightness(KeyTest.this);

        mNotificationLight = (LinearLayout) findViewById(R.id.LL_notification_light);
        light_R = (Button) findViewById(R.id.Button_R);
        light_G = (Button) findViewById(R.id.Button_G);
        light_B = (Button) findViewById(R.id.Button_B);
        if (android.os.SystemProperties.getBoolean("ro.hx_factorytest_noti_light", false)) {
            mNotificationLight.setVisibility(View.VISIBLE);
            light_R.setOnClickListener(this);
            light_G.setOnClickListener(this);
            light_B.setOnClickListener(this);
        }

//		  Button Button_button_light = (Button) findViewById(R.id.Button_button_light);


        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                if (keyflag == 0) {
                    keyflag = 1;
                    setBrightness(KeyTest.this, 50);
                } else if (keyflag == 1) {
                    keyflag = 2;
                    setBrightness(KeyTest.this, 0);
                } else if (keyflag == 2) {
                    keyflag = 3;
                    setBrightness(KeyTest.this, 50);
                } else if (keyflag == 3) {
                    keyflag = 4;
                    setBrightness(KeyTest.this, 0);
                } else if (keyflag == 4) {
                    keyflag = 5;
                    setBrightness(KeyTest.this, 50);
                } else if (keyflag == 5) {
                    keyflag = 6;
                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                    setBrightness(KeyTest.this, 0);
						/*
						if((main.getVisibility() == View.GONE) &&
								(retu.getVisibility() == View.GONE) &&
								(vd.getVisibility() == View.GONE) &&
								(vu.getVisibility() == View.GONE) &&
								(menu.getVisibility() == View.GONE)){
							 success_Button.setEnabled(true);//useless,if removed ,also can work well
						}*/


                }

                super.handleMessage(msg);
            }

        };

        SWIPE_UP_TO_SWITCH_APPS_ENABLED = Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED, defValue);
        Log.d(TAG, "onCreate: " + SWIPE_UP_TO_SWITCH_APPS_ENABLED);
        //by yds start
        if (android.os.SystemProperties.getBoolean("ro.hx_factorytest_hide_menu", false)
                || (SWIPE_UP_TO_SWITCH_APPS_ENABLED == 1)) {
            menu.setVisibility(View.GONE);
        }
        if (android.os.SystemProperties.getBoolean("ro.hx_factorytest_hide_back", false)) {
            retu.setVisibility(View.GONE);
        }
        if (android.os.SystemProperties.getBoolean("ro.hx_factorytest_hide_home", false)) {
            main.setVisibility(View.GONE);
        }
        //by yds end
    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
//		super.onBackPressed();
    }

    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }

    public void QuickTest() {
        if (!android.os.SystemProperties.getBoolean("ro.hx_factorytest_noti_light", false)) {
            if (mTimer != null) {
                mTimer.cancel();
            }

            Intent mIntent1 = getIntent();
            boolean fl1 = mIntent1.getBooleanExtra("textall", false);
            if (fl1 == true) {
                if (FeatureOption.HX_FACTORYTEST_OTG) {
                    Intent miIntent = new Intent();
                    miIntent.setAction("com.ykq.intent.action.OTG_TEST");
                    miIntent.putExtra("textall", true);
                    startActivityForResult(miIntent, RESULT_OK);
                } else {
                    Intent miIntent = new Intent();
                    miIntent.setAction("com.ykq.intent.action.FMRADIO_TEST");
                    miIntent.putExtra("textall", true);
                    startActivityForResult(miIntent, RESULT_OK);
                }
                CameraTestStatus = 1;
                finish();
            }


        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onKeyDown occured:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Log.d(TAG, "onKeyDown KEYCODE_HOME occured");
            main.setVisibility(View.GONE);
//			setBrightness(KeyTest.this, 50);
//			setBrightness1(KeyTest.this, 220);
            if ((main.getVisibility() == View.GONE) &&
                    (retu.getVisibility() == View.GONE) &&
                    (vd.getVisibility() == View.GONE) &&
                    (vu.getVisibility() == View.GONE) &&
                    (menu.getVisibility() == View.GONE)) {
//				mada.setVisibility(View.VISIBLE);
//				mVibrator.vibrate(mVibratePattern, 0);
                if (FeatureOption.HX_FACTORYTEST_CAMERAKEY) {
                    if (camera.getVisibility() == View.GONE) {
                        success_Button.setEnabled(true);
                    }
                } else {
                    success_Button.setEnabled(true);
                    QuickTest();
                }
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//			setBrightness(KeyTest.this, 0);
//			setBrightness1(KeyTest.this, 150);
            retu.setVisibility(View.GONE);
            if ((main.getVisibility() == View.GONE) &&
                    (retu.getVisibility() == View.GONE) &&
                    (vd.getVisibility() == View.GONE) &&
                    (vu.getVisibility() == View.GONE) &&
                    (menu.getVisibility() == View.GONE)) {
//				mada.setVisibility(View.VISIBLE);
//				mVibrator.vibrate(mVibratePattern, 0);
                if (FeatureOption.HX_FACTORYTEST_CAMERAKEY) {
                    if (camera.getVisibility() == View.GONE) {
                        success_Button.setEnabled(true);
                    }
                } else {
                    success_Button.setEnabled(true);
                    QuickTest();
                }
            }
            return true;
        }
//		if(keyCode == KeyEvent.KEYCODE_POWER){
//			power.setVisibility(View.GONE);
//		}


        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            vu.setVisibility(View.GONE);
//			setBrightness(KeyTest.this, 50);
//			setBrightness1(KeyTest.this, 220);
            if ((main.getVisibility() == View.GONE) &&
                    (retu.getVisibility() == View.GONE) &&
                    (vd.getVisibility() == View.GONE) &&
                    (vu.getVisibility() == View.GONE) &&
                    (menu.getVisibility() == View.GONE)) {
//				mada.setVisibility(View.VISIBLE);
//				mVibrator.vibrate(mVibratePattern, 0);
                if (FeatureOption.HX_FACTORYTEST_CAMERAKEY) {
                    if (camera.getVisibility() == View.GONE) {
                        success_Button.setEnabled(true);
                    }
                } else {
                    success_Button.setEnabled(true);
                    QuickTest();
                }
            }

            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            vd.setVisibility(View.GONE);
//			setBrightness(KeyTest.this, 0);
//			setBrightness1(KeyTest.this, 150);
            if ((main.getVisibility() == View.GONE) &&
                    (retu.getVisibility() == View.GONE) &&
                    (vd.getVisibility() == View.GONE) &&
                    (vu.getVisibility() == View.GONE) &&
                    (menu.getVisibility() == View.GONE)) {
//				mada.setVisibility(View.VISIBLE);
//				mVibrator.vibrate(mVibratePattern, 0);

                if (FeatureOption.HX_FACTORYTEST_CAMERAKEY) {
                    if (camera.getVisibility() == View.GONE) {
                        success_Button.setEnabled(true);
                    }
                } else {
                    success_Button.setEnabled(true);
                    QuickTest();
                }
            }


            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
//			setBrightness(KeyTest.this, 0);
//			setBrightness1(KeyTest.this, 150);
            menu.setVisibility(View.GONE);
            if ((main.getVisibility() == View.GONE) &&
                    (retu.getVisibility() == View.GONE) &&
                    (vd.getVisibility() == View.GONE) &&
                    (vu.getVisibility() == View.GONE) &&
                    (menu.getVisibility() == View.GONE)) {
//				mada.setVisibility(View.VISIBLE);
//				mVibrator.vibrate(mVibratePattern, 0);
                if (FeatureOption.HX_FACTORYTEST_CAMERAKEY) {
                    if (camera.getVisibility() == View.GONE) {
                        success_Button.setEnabled(true);
                    }
                } else {
                    success_Button.setEnabled(true);
                    QuickTest();
                }
            }
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            camera.setVisibility(View.GONE);
//			setBrightness(KeyTest.this, 0);
//			setBrightness1(KeyTest.this, 150);
            if ((main.getVisibility() == View.GONE) &&
                    (retu.getVisibility() == View.GONE) &&
                    (vd.getVisibility() == View.GONE) &&
                    (vu.getVisibility() == View.GONE) &&
                    (menu.getVisibility() == View.GONE) &&
                    (vd.getVisibility() == View.GONE)) {
//				mada.setVisibility(View.VISIBLE);
//				mVibrator.vibrate(mVibratePattern, 0);

                success_Button.setEnabled(true);

                QuickTest();
            }

            return true;
        }

        //it seems redundant
        if ((main.getVisibility() == View.GONE) &&
                (retu.getVisibility() == View.GONE) &&
                (vd.getVisibility() == View.GONE) &&
                (vu.getVisibility() == View.GONE) &&
                (menu.getVisibility() == View.GONE)) {
//			mada.setVisibility(View.VISIBLE);
//			mVibrator.vibrate(mVibratePattern, 0);
            if (FeatureOption.HX_FACTORYTEST_CAMERAKEY) {
                if (camera.getVisibility() == View.GONE) {
                    success_Button.setEnabled(true);
                }
            } else {
                success_Button.setEnabled(true);
                QuickTest();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//		if(mVibrator != null){
//		mVibrator.vibrate(mVibratePattern, -1);
//		}
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mTimer2 != null) {
            mTimer2.cancel();
        }
        if (android.os.SystemProperties.getBoolean("ro.hx_factorytest_noti_light", false)) {
            Intent it = new Intent("com.mediatek.factorytest.keyTestQuit");
            sendBroadcast(it);
        }
    }

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        CameraTestStatus = 0;
        if (android.os.SystemProperties.getBoolean("ro.hx_factorytest_noti_light", false)) {
            Intent it = new Intent("com.mediatek.factorytest.batteryLightOff");
            sendBroadcast(it);
        }
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.Button_Success:
                CameraTestStatus = 1;
//			mVibrator.vibrate(mVibratePattern, -1);
                if (mTimer != null) {
                    mTimer.cancel();
                }
                break;
            case R.id.Button_Fail:
                CameraTestStatus = -1;
//			mVibrator.vibrate(mVibratePattern, -1);
                if (mTimer != null) {
                    mTimer.cancel();
                }
                break;
            case R.id.Button_R: {
                mLightROn = !mLightROn;
                Intent it = new Intent("com.mediatek.factorytest.batteryLightSwitch");
                it.putExtra("color", Color.RED);
                it.putExtra("switch_on", mLightROn);
                sendBroadcast(it);
                if (mLightROn) {
                    light_R.setBackgroundResource(R.drawable.bk_true);
                } else {
                    light_R.setBackgroundResource(R.drawable.bk_custom);
                }
                break;
            }
            case R.id.Button_G: {
                mLightGOn = !mLightGOn;
                Intent it = new Intent("com.mediatek.factorytest.batteryLightSwitch");
                it.putExtra("color", Color.GREEN);
                it.putExtra("switch_on", mLightGOn);
                sendBroadcast(it);
                if (mLightGOn) {
                    light_G.setBackgroundResource(R.drawable.bk_true);
                } else {
                    light_G.setBackgroundResource(R.drawable.bk_custom);
                }
                break;
            }
            case R.id.Button_B: {
                mLightBOn = !mLightBOn;
                Intent it = new Intent("com.mediatek.factorytest.batteryLightSwitch");
                it.putExtra("color", Color.BLUE);
                it.putExtra("switch_on", mLightBOn);
                sendBroadcast(it);
                if (mLightBOn) {
                    light_B.setBackgroundResource(R.drawable.bk_true);
                } else {
                    light_B.setBackgroundResource(R.drawable.bk_custom);
                }
                break;
            }
            default:
                break;
        }
        if (v.getId() == R.id.Button_Success || v.getId() == R.id.Button_Fail) {
            setResultBeforeFinish(CameraTestStatus);
            finish();
        }
    }

    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.buttonBrightness = Float.valueOf(brightness) * (1f / 255f);

        activity.getWindow().setAttributes(lp);
    }

    /**
     * 判断是否开启了自动亮度调节
     *
     * @param aContext
     * @return
     */
    public static boolean isAutoBrightness(ContentResolver aContentResolver) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 获取屏幕的亮度
     *
     * @param activity
     * @return
     */
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(
                    resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 设置亮度
     *
     * @param activity
     * @param brightness
     */
    public static void setBrightness1(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);

        activity.getWindow().setAttributes(lp);

    }

    /**
     * 停止自动亮度调节
     *
     * @param activity
     */
    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }


    /**
     * 保存亮度设置状态
     *
     * @param resolver
     * @param brightness
     */
    public static void saveBrightness(ContentResolver resolver, int brightness) {
        Uri uri = Settings.System
                .getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS,
                brightness);
        resolver.notifyChange(uri, null);
    }

    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "key");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }

}
