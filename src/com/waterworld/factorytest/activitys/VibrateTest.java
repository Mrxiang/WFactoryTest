package com.waterworld.factorytest.activitys;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

//pingyh add
//pingyh add end

public class VibrateTest extends FactoryActivity  implements View.OnClickListener{

	private static final String TAG = Utils.TAG +"VibrateTest";

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mVibrator.vibrate(mVibratePattern, -1);
		mVibrator.cancel();
	}
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		VibrateTestStatus = 0;
	}
	static int VibrateTestStatus = 0;
    private Vibrator mVibrator;
    protected long[] mVibratePattern = new long[] {0, 200};// {20, 200};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	       setContentView(R.layout.test_result_vibrate);
	       
	       Button success_Button =(Button) findViewById(R.id.Button_Success);
	       Button fail_Button = (Button) findViewById(R.id.Button_Fail);
	       success_Button.setOnClickListener(this);
	       fail_Button.setOnClickListener(this);
	       
	       mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	        mVibrator.vibrate(mVibratePattern, 0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Button_Success:
			VibrateTestStatus = 1;
			break;
		case R.id.Button_Fail:
			VibrateTestStatus = -1;
			break;
		default:
			break;
		}
		setResultBeforeFinish( VibrateTestStatus );
		finish();
	}


	@Override
	public void setResultBeforeFinish(int status) {
		Log.d(TAG, "setResultBeforeFinish: " + status);
		Intent intent = new Intent();
		intent.putExtra(Utils.NAME, "vibrator");
		intent.putExtra(Utils.TEST_RESULT, status);
		setResult(status, intent);
	}
}
