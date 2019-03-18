package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

public class GpsTestResult extends FactoryActivity  implements View.OnClickListener{
	private static final String TAG = Utils.TAG +"GpsTestResult";
	public static int LocationTestStatus = 0;
	protected static boolean mCreateFirstTime = true;
	
	private Button mSuccessButton;
	private Button mFailButton;
	private int mLocationMode = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			mLocationMode = Secure.getInt(getContentResolver(), Secure.LOCATION_MODE);
//			Log.i("FactoryTest", "Gps mode = "+mLocationMode);
			if(mLocationMode != Secure.LOCATION_MODE_SENSORS_ONLY) {
				Secure.putInt(getContentResolver(), Secure.LOCATION_MODE, Secure.LOCATION_MODE_SENSORS_ONLY);
//				int mode = Secure.getInt(getContentResolver(), Secure.LOCATION_MODE);
//				Log.i("FactoryTest", "Gps mode = "+mode);
			}
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}
		if (mCreateFirstTime) {
			mCreateFirstTime = false;
//			Intent intent = new Intent("com.ykq.intent.action.ygps");
			Intent intent = new Intent();
			intent.setComponent(new ComponentName("com.mediatek.ygps", "com.mediatek.ygps.YgpsActivity"));
			startActivityForResult(intent, 0);//the second para should not smaller then 0.
		}
		
		setContentView(R.layout.test_result);
		
		mSuccessButton = (Button) findViewById(R.id.Button_Success);
		mFailButton = (Button) findViewById(R.id.Button_Fail);
		
		mSuccessButton.setOnClickListener(this);
		mFailButton.setOnClickListener(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent IntentData) {
//		Log.i("FactoryTest", "onActivityResult");
		if (requestCode == 0) {
			Secure.putInt(getContentResolver(), Secure.LOCATION_MODE, mLocationMode);

//			Log.i("FactoryTest", "onActivityResult mLocationMode");
		}
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(); 
        intent.setAction("com.ykq.intent.action.FACTORY_TEST_MAIN");
        setResult(RESULT_OK,intent);
		switch (v.getId()) {
		case R.id.Button_Success:
			LocationTestStatus  = 1;
			break;
		case R.id.Button_Fail:
			LocationTestStatus = -1;
			break;
		default:
			break;
		}
		setResultBeforeFinish( LocationTestStatus );
		finish();
	}

	@Override
	public void setResultBeforeFinish(int status) {
		Log.d(TAG, "setResultBeforeFinish: " + status);
		Intent intent = new Intent();
		intent.putExtra(Utils.NAME, "GPS");
		intent.putExtra(Utils.TEST_RESULT, status);
		setResult(status, intent);
	}
}
