package com.waterworld.factorytest.activitys;

//import com.android.internal.telephony.ITelephony;
//import android.telephony.TelephonyManager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import java.util.List;
import com.waterworld.factorytest.R;

public class FingerTest extends Activity implements View.OnClickListener{

	private static final String TAG = "FingerTest";

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//PhoneTestStatus = 0;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);


		setContentView(R.layout.test_result);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Button success_Button =(Button) findViewById(R.id.Button_Success);
		Button fail_Button = (Button) findViewById(R.id.Button_Fail);
		success_Button.setOnClickListener(this);
		fail_Button.setOnClickListener(this);

		FingerprintManager fpm = (FingerprintManager) getSystemService(
				Context.FINGERPRINT_SERVICE);

		if (fpm != null && !fpm.isHardwareDetected()) {
			Log.v(TAG, "No fingerprint hardware detected!!");
			return;
		}
		if( fpm == null ){
			return;
		}

		final List<Fingerprint> items = fpm.getEnrolledFingerprints();
		final int fingerprintCount = items != null ? items.size() : 0;
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		if (fingerprintCount > 0)
		{
			ComponentName cn = new ComponentName("com.android.settings",
					"com.android.settings.fingerprint.FingerprintSettings");
			intent.setComponent(cn);
			startActivity(intent);
		}
		else
		{

			ComponentName cn = new ComponentName("com.android.settings",
					"com.android.settings.fingerprint.FingerprintEnrollIntroduction");
			intent.setComponent(cn);
			startActivity(intent);
		}

	}

	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//	super.onBackPressed();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//Intent intent = new Intent();
		//intent.setAction("com.ykq.intent.action.HX_FINGERPRINT_TEST");
		/*setResult(RESULT_OK,intent);*/
		switch (v.getId()) {
			case R.id.Button_Success:
				setResult(5);
				break;
			case R.id.Button_Fail:
				setResult(10);
				break;
			default:
				break;
		}

		finish();
		//startActivity(intent);
	}

}
