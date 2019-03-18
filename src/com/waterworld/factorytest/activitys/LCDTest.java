package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher.ViewFactory;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.FactoryTestFeatureoption.FeatureOption;

import java.util.Timer;
import java.util.TimerTask;

import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

public class LCDTest extends FactoryActivity implements View.OnClickListener, ViewFactory {

    private static final String TAG = Utils.TAG+"LCDTest";
    static int LCDTestStatus = 0;
    int position = 0;
    Button success_Button;
    Timer mTimer;
    Timer mTimer1;
    int flags = 0;
    private ImageView mSwitcher;
    RelativeLayout RV_button;
    RelativeLayout RL_image;
    private Integer[] mImageIds = {
            R.drawable.lcd_test_00,
            R.drawable.lcd_test_01,

            R.drawable.lcd_test_03,
            R.drawable.lcd_test_04,
            R.drawable.lcd_test_05,
            R.drawable.lcd_test_02};

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        LCDTestStatus = 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.lcd_test);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        RL_image = (RelativeLayout) findViewById(R.id.RL_image);
        RV_button = (RelativeLayout) findViewById(R.id.RV_button);
        mSwitcher = (ImageView) findViewById(R.id.imageSwitcher1);
        if (android.os.SystemProperties.getBoolean("ro.factorytest_LcdTestClick", false)) {
            //mSwitcher.setFactory((ViewFactory) this);
            mSwitcher.setImageResource(mImageIds[position]);
            mSwitcher.setOnClickListener(this);
        } else {
            RL_image.removeView(mSwitcher);
        }
        success_Button = (Button) findViewById(R.id.LCD_BUTTON_SUCCESS);
        Button fail_Button = (Button) findViewById(R.id.LCD_BUTTON_FAIL);
//	       Button next_Button = (Button) findViewById(R.id.LCD_BUTTON_NEXT);
        success_Button.setOnClickListener(this);
        fail_Button.setOnClickListener(this);
//	       next_Button.setOnClickListener(this);


        final Handler mHandler1 = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                if (flags == 2) {
                    flags = 3;
                    setBrightness1(LCDTest.this, 255);
                } else if (flags == 3) {
                    flags = 4;
                    setBrightness1(LCDTest.this, 220);
                } else if (flags == 4) {
                    flags = 5;
                    setBrightness1(LCDTest.this, 150);
                } else if (flags == 5) {
                    flags = 6;
                    setBrightness1(LCDTest.this, 100);
                } else if (flags == 6) {
                    if (mTimer1 != null) {
                        mTimer1.cancel();
                    }
                    flags = 7;
                    setBrightness1(LCDTest.this, 50);
                    RV_button.setVisibility(View.VISIBLE);

                }

                super.handleMessage(msg);
            }

        };


        final Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub

                if (flags == 0) {
                    flags = 1;
                    RL_image.setBackgroundResource(R.drawable.lcd_test_01);
                } else if (flags == 1) {
                    flags = 2;

                    if (FeatureOption.HX_LCD_TEST_CONTAINS_WHITE_BLACK) {
                        flags = 300;
                    }
                    RL_image.setBackgroundResource(R.drawable.lcd_test_02);

                } else if (flags == 300) {
                    flags = 301;
                    RL_image.setBackgroundResource(R.drawable.lcd_test_04);
                } else if (flags == 301) {
                    flags = 2;
                    RL_image.setBackgroundResource(R.drawable.lcd_test_05);
                    RV_button.setBackgroundResource(R.drawable.lcd_test_05);

                } else if (flags == 2) {
                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                    if (FeatureOption.HX_FACTORYTEST_BACKLIGHT)
                        RV_button.setVisibility(View.VISIBLE);

                    else {
                        mTimer1 = new Timer();
                        TimerTask mTask = new TimerTask() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Message message = new Message();
                                mHandler1.sendMessage(message);
                                Log.e("lyaotao", "mTimer1 is running");
                            }
                        };
                        mTimer1.schedule(mTask, 0, 500);
                    }
                }


                super.handleMessage(msg);
            }

        };

        mTimer = new Timer();
        TimerTask mTask = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                mHandler.sendMessage(message);
                Log.e("lyaotao", "mTimer is running");
            }
        };
        if (android.os.SystemProperties.getBoolean("ro.factorytest_LcdTestClick", false)) {
        } else {
            mTimer.schedule(mTask, 1500, 1000);
        }

    }


    public static void setBrightness1(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);

        activity.getWindow().setAttributes(lp);

    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.imageSwitcher1:
                if (position < 5) {
                    position++;
                    mSwitcher.setImageResource(mImageIds[position]);
                }
                if (position == 5) {
                    v.setClickable(false);
                    RV_button.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.LCD_BUTTON_SUCCESS:
                LCDTestStatus = 1;
                if (mTimer != null) {
                    mTimer.cancel();
                }
                if (!FeatureOption.HX_FACTORYTEST_BACKLIGHT) {
                    if (mTimer1 != null) {
                        mTimer1.cancel();
                    }
                }
                setResultBeforeFinish( LCDTestStatus );
                finish();

                break;
            case R.id.LCD_BUTTON_FAIL:
                LCDTestStatus = -1;
                if (mTimer != null) {
                    mTimer.cancel();
                }
                if (!FeatureOption.HX_FACTORYTEST_BACKLIGHT) {
                    if (mTimer1 != null) {
                        mTimer1.cancel();
                    }
                }
                setResultBeforeFinish( LCDTestStatus );
                finish();
                break;

            default:
                break;
        }

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


    @Override
    public View makeView() {
        ImageView i = new ImageView(this);
        i.setBackgroundColor(0xFF000000);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        return i;
    }

    public void setResultBeforeFinish(int status ){
        Log.d(TAG, "setResultBeforeFinish: "+status );
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "lcd");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }
}
