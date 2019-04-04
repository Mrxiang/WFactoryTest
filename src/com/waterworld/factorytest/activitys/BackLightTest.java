package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

public class BackLightTest extends FactoryActivity implements OnClickListener{

	private static final String TAG = Utils.TAG +"BackLightTest";
	static int CameraTestStatus = 0;
	MediaPlayer mediaPlayer;
	AudioManager audioManager;
	static int mbrightness;
	static boolean plus ;
	private Timer mtimer;
	private TimerTask mTask;
	boolean flags = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);


		     setContentView(R.layout.test_result_backlight);

//		     WindowManager.LayoutParams params = getWindow().getAttributes();
//             params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//             params.screenBrightness = 200;
//             getWindow().setAttributes(params);

//         final Button Button_backup = (Button)findViewById(R.id.Button_backup);
//         Button Button_backdown = (Button)findViewById(R.id.Button_backdown);
         if(isAutoBrightness(getContentResolver())){
        	 plus = true;
         }else{
        	 Log.e("lyaotao", "1111");
         mbrightness = getScreenBrightness(BackLightTest.this);
         }

         mbrightness = getScreenBrightness(BackLightTest.this);

	       Button success_Button =(Button) findViewById(R.id.Button_Success);
	       Button fail_Button = (Button) findViewById(R.id.Button_Fail);
	       success_Button.setOnClickListener(this);
	       fail_Button.setOnClickListener(this);


	       final Handler mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				if(flags){
					flags = false;
					mbrightness = 50;
				}else{
					flags = true;
					mbrightness = 150;
				}
				setBrightness(BackLightTest.this, mbrightness);
				Log.e("lyaotao","333333");

				super.handleMessage(msg);
			}

	       };
	       mtimer = new Timer();
	       mTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				Message message = new Message();
				mHandler.sendMessage(message);
			}
		};
	       mtimer.schedule(mTask, 1000, 1000);



	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		if(mtimer!=null){
			mtimer.cancel();
		}
		if(mTask!=null){
			mTask.cancel();
		}

		super.onDestroy();
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		CameraTestStatus = 0;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("com.ykq.intent.action.FACTORY_TEST_MAIN");
        setResult(RESULT_OK,intent);
		switch (v.getId()) {
		case R.id.Button_Success:
			CameraTestStatus  = 1;
			Intent mIntent = getIntent();
			boolean fl = mIntent.getBooleanExtra("textall", false);
			if(fl == true){
				Intent miIntent = new Intent();
				miIntent.setAction("com.ykq.intent.action.CAMERA_TEST");
				miIntent.putExtra("textall", true);
				startActivityForResult(miIntent, RESULT_OK);
			}
			break;
		case R.id.Button_Fail:
			CameraTestStatus = -1;
			Intent mIntent1 = getIntent();
			boolean fl1 = mIntent1.getBooleanExtra("textall", false);
			if(fl1 == true){
				Intent miIntent = new Intent();
				miIntent.setAction("com.ykq.intent.action.CAMERA_TEST");
				miIntent.putExtra("textall", true);
				startActivityForResult(miIntent, RESULT_OK);
			}
			break;
		default:
			break;
		}
		//intent.getExtras()
//		startActivity(intent);
		finish();
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
    public static void setBrightness(Activity activity, int brightness) {
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


	@Override
	public void setResultBeforeFinish(int status) {
		Log.d(TAG, "setResultBeforeFinish: "+status );
		Intent intent = new Intent();
		intent.putExtra(Utils.NAME, "backlight");
		intent.putExtra(Utils.TEST_RESULT, status);
		setResult(status, intent);


	}
}
