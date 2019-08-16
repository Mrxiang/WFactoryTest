package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.Button;

import com.waterworld.factorytest.FactoryActivity;

import com.waterworld.factorytest.Utils;

public class FpTestResult extends FactoryActivity  //implements View.OnClickListener
{
	private static final String TAG =Utils.TAG +"FpTestResult";
	public static int FpTestStatus = 0;
	//protected static boolean mCreateFirstTime;

	private Button mSuccessButton;
	private Button mFailButton;

	public static final int FACTORYTEST_RESULT_CODE_SUCCESS = 114;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (SystemProperties.getBoolean("ro.hx_using_fp_blestech", false)) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName cn = new ComponentName("com.blestech.fingerprinttest",
					"com.blestech.fingerprinttest.DisPrintActivity");
			intent.setComponent(cn);
			this.startActivityForResult(intent, 100);
		} else if (SystemProperties.getBoolean("ro.hx_microarray", false)) { //ro.hx_microarray
			Intent intent = new Intent();
			intent.setClassName("ma.android.com.mafactory", "ma.android.com.mafactory.MainActivity");
			startActivityForResult(intent, 10011);//the second para should not smaller then 0.

		} else if (SystemProperties.getBoolean("ro.hx_using_fingerprint_silead", false)) {
			Intent intent = new Intent("www.sileadinc.com.fingertest");
			//Intent intent = new Intent();
			//intent.setClassName("ma.fprint", "ma.fprint.CaptureActivity");
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("launchedBy", "factorytest");
			startActivityForResult(intent, 11110);//the second para should not smaller then 0.

		}
		// add by cdfinger
		else if (SystemProperties.getBoolean("ro.hx_fps998", false)) {
			Intent intent = new Intent();
			intent.setClassName("com.cdfinger.factory", "com.cdfinger.factory.MainActivity");
			//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// getApplicationContext().startActivity(intent);
			startActivityForResult(intent, 10011);//the second para should not smaller then 0.

		}
		// add by cdfinger
		else if (SystemProperties.getBoolean("ro.hx_cdfinger", false)) {
			Intent intent = new Intent();
			intent.setClassName("com.cdfinger.factorytest", "com.cdfinger.factorytest.FactoryTestActivity");
			//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// getApplicationContext().startActivity(intent);
			startActivityForResult(intent, 10011);//the second para should not smaller then 0.

		}
		// add by cdfinger
		else if (SystemProperties.getBoolean("ro.hx_using_fp_sunwave_fps", false)) {
			Intent intent = new Intent();
			intent.setClassName("com.swfp.factory", "com.swfp.activity.DetectActivity");
			//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// getApplicationContext().startActivity(intent);
			startActivityForResult(intent, 10111);//the second para should not smaller then 0.

		}
		else if (SystemProperties.getBoolean("ro.hx_using_fp_cs3716_tee", false)||SystemProperties.getBoolean("hw.hx_using_fp_cs3716_tee", false)) {
			Intent intent = new Intent();
			//intent.setClassName("com.chipsailing.fingerdebug","com.chipsailing.fingerdebug.TeeTestActivity");
			intent.setClassName("com.cs.cittest", "com.cs.cittest.MainActivity");
			startActivityForResult(intent, 1);
		}
		else if (SystemProperties.getBoolean("ro.hx_using_fp_cs3716", false)||SystemProperties.getBoolean("hw.hx_using_fp_cs3716", false)) {
			Intent intent = new Intent();
			intent.setClassName("chipsailing.fingerprint","chipsailing.fingerprint.MainActivity");
			startActivityForResult(intent, 1);//the second para should not smaller then 0.

		}
		else if (SystemProperties.getBoolean("ro.hx_icn7152l", false)||SystemProperties.getBoolean("hw.hx_icn7152l", false)) { //xjl 20170919
			Log.i("aaaaa", "FpTestResult chip is hx_icn7152l");
			Intent  testIntent = new Intent();
			testIntent.putExtra("config_autoexit", true);
			testIntent.putExtra("config_autotest", true);
			testIntent.putExtra("config_autoexit_delay_time", 500);
			testIntent.putExtra("config_supportTouchTest", true);
			testIntent.putExtra("config_showcapturedImg", true);
			testIntent.putExtra("config_savecapturedImg", false);
			//testIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			testIntent.setClassName("com.fpsensor.fpSensorExtensionSvc2", "com.fpsensor.sensortesttool.sensorTestActivity");
			startActivityForResult(testIntent,1);
		}
		else
		{
//			Intent intent = new Intent("com.ykq.intent.action.FINGER_TEST");
			Intent intent = new Intent("com.waterworld.intent.action.FINGER_TEST");

			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//intent.putExtra("launchedBy", "factorytest");
			startActivityForResult(intent, 0);

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent IntentData) {
		Log.i("FactoryTest", "onActivityResult resultCode = "+resultCode);
		if (SystemProperties.getBoolean("ro.hx_microarray", false)) { //ro.hx_microarray
			Log.i("xjl", "xjl resultCode = "+resultCode+", requestCode = "+requestCode);
			if( requestCode == 10011)
			{
				if( resultCode != 0)
				{
					Log.i("aaaaa", "fail");
					FpTestStatus = -1;
				}
				else if(resultCode == 0)
				{
					Log.i("aaaaa", "success");
					FpTestStatus = 1;
				}
			}
		} else if(SystemProperties.getBoolean("ro.hx_using_fp_blestech", false)) {
			if( requestCode == 100)
			{
				if( resultCode == 0)
				{
					Log.i("aaaaa", "fail");
					FpTestStatus = -1;
				}
				else if(resultCode == 1)
				{
					Log.i("aaaaa", "success");
					FpTestStatus = 1;
				}
			}
		} else if (SystemProperties.getBoolean("ro.hx_using_fingerprint_silead", false)) {
			Log.i("wlf", "wlf resultCode = "+resultCode+", requestCode = "+requestCode);
			if( requestCode == 11110)
			{
				if( resultCode == 0)
				{
					Log.i("aaaaa", "fail");
					FpTestStatus = -1;
				}
				else if(resultCode == 1)
				{
					Log.i("aaaaa", "success");
					FpTestStatus = 1;
				}
			}
		}
		//add by cdfinger
		else if (SystemProperties.getBoolean("ro.hx_fps998", false)) {
			Log.i("wlf", "wlf resultCode = "+resultCode+", requestCode = "+requestCode);
			if( requestCode == 10011)
			{
				if( resultCode != 0)
				{
					Log.i("aaaaa", "fail");
					FpTestStatus = -1;
				}
				else if(resultCode == 0)
				{
					Log.i("aaaaa", "success");
					FpTestStatus = 1;
				}
			}
		}
		//add by cdfinger
		else if (SystemProperties.getBoolean("ro.hx_using_fp_sunwave_fps", false)) {
			Log.i("wlf", "wlf resultCode = "+resultCode+", requestCode = "+requestCode);
			if( requestCode == 10111)
			{
				if( resultCode != 0)
				{
					Log.i("aaaaa", "fail");
					FpTestStatus = -1;
				}
				else if(resultCode == 0)
				{
					Log.i("aaaaa", "success");
					FpTestStatus = 1;
				}
			}
		}
		else if (SystemProperties.getBoolean("ro.hx_using_fp_cs3716_tee", false)||SystemProperties.getBoolean("hw.hx_using_fp_cs3716_tee", false)) {
			Log.i("wlf", "wlf resultCode = "+resultCode+", requestCode = "+requestCode);
			if((IntentData!=null)&&IntentData.hasExtra("test_result"))
			{
				int result=IntentData.getIntExtra("test_result",0);
				Log.i("aaaaa", "result="+result);
				if( result != 1)
				{
					Log.i("aaaaa", "fail");
					FpTestStatus = -1;
				}
				else if(result == 1)
				{
					Log.i("aaaaa", "success");
					FpTestStatus = 1;
				}
			}
			else
			{
				Log.i("aaaaa", "fail B");
				FpTestStatus = -1;
			}

		}
		else if (SystemProperties.getBoolean("ro.hx_using_fp_cs3716", false)||SystemProperties.getBoolean("hw.hx_using_fp_cs3716", false)) {
			Log.i("wlf", "wlf resultCode = "+resultCode+", requestCode = "+requestCode);
			if((IntentData!=null)&&IntentData.hasExtra("test_result"))
			{
				int result=IntentData.getIntExtra("test_result",0);
				Log.i("aaaaa", "result="+result);
				if( result != 1)
				{
					Log.i("aaaaa", "fail");
					FpTestStatus = -1;
				}
				else if(result == 1)
				{
					Log.i("aaaaa", "success");
					FpTestStatus = 1;
				}
			}
			else
			{
				Log.i("aaaaa", "fail B");
				FpTestStatus = -1;
			}

		}
		else if (SystemProperties.getBoolean("ro.hx_icn7152l", false)||SystemProperties.getBoolean("hw.hx_icn7152l", false)) { //xjl 20170919
			Log.i("xjl", "xjl resultCode = "+resultCode+", requestCode = "+requestCode);
			if( requestCode == 1)
			{
				if( resultCode != -1)
				{
					Log.i("aaaaa", "fail");
					FpTestStatus = -1;
				}
				else if(resultCode == -1)
				{
					Log.i("aaaaa", "success");
					FpTestStatus = 1;
				}
			}
		}
		else
		{
			if (resultCode == 5) {
				FpTestStatus = 1;
			} else {
				FpTestStatus = -1;
			}

		}
		setResultBeforeFinish( FpTestStatus );
		finish();
	}


	@Override
	public void setResultBeforeFinish(int status) {
		Log.d(TAG, "setResultBeforeFinish: " + status);
		Intent intent = new Intent();
		intent.putExtra(Utils.NAME, "finger");
		intent.putExtra(Utils.TEST_RESULT, status);
		setResult(status, intent);

	}
}
