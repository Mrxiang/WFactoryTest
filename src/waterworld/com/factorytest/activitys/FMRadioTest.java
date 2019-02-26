package waterworld.com.factorytest.activitys;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import waterworld.com.factorytest.FactoryActivity;
import waterworld.com.factorytest.FactoryDatas;
import waterworld.com.factorytest.Utils;
import waterworld.com.factorytest.R;

public class FMRadioTest extends FactoryActivity implements View.OnClickListener{

	private static final String TAG = Utils.TAG+"FMRadioTest";
	static int CameraTestStatus = 0;
	Timer mTimer;
	boolean flags = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test_result);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Intent PhoneTestIntent =  new Intent();
		Handler handler = new Handler();
		try {
			PhoneTestIntent.setClassName("com.android.fmradio","com.android.fmradio.FmMainActivity");
			PhoneTestIntent.putExtra("default_station", 987);//fangfengfan add for FactoryTest default station
			PhoneTestIntent.putExtra("lyaotao", 1);
			startActivity(PhoneTestIntent);
		} catch (Exception e2) {
			try {
				PhoneTestIntent.setClassName("com.mediatek.fmradio","com.mediatek.fmradio.FmRadioActivity");
				PhoneTestIntent.putExtra("lyaotao", 1);
				startActivity(PhoneTestIntent);
			} catch (Exception e) {
				try {
					PhoneTestIntent.setClassName("com.yunos.FMRadio","com.yunos.FMRadio.FMRadioActivity");
					PhoneTestIntent.putExtra("lyaotao", 1);
					PhoneTestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PhoneTestIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(PhoneTestIntent);
				} catch (Exception e1) {
					CameraTestStatus = -1;
					handler.post(new Runnable() {
						public void run () {
							gotoNextItemIfTextAll();
							Toast.makeText(FMRadioTest.this, getString(R.string.cant_find_fm_app), Toast.LENGTH_SHORT).show();
						}
					});
				
					finish();
				}
			}
		}
	       Button success_Button =(Button) findViewById(R.id.Button_Success);
	       Button fail_Button = (Button) findViewById(R.id.Button_Fail);
	       success_Button.setOnClickListener(this);
	       fail_Button.setOnClickListener(this);
		
}
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}
	
	public void gotoNextItemIfTextAll() {
	
		Intent mIntent = getIntent();
		boolean fl = mIntent.getBooleanExtra("textall", false);
		if(fl == true){
			Intent miIntent = new Intent();
			miIntent.setAction("com.ykq.intent.action.MIC_TEST");
			miIntent.putExtra("textall", true);
			startActivityForResult(miIntent, RESULT_OK);
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
	}
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		CameraTestStatus = 0;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intentB = new Intent("com.ykq.intent.action.FACTORY_TEST");		
		sendBroadcast(intentB);//turn off fm radio
		

//        setResult(RESULT_OK,intent);
		switch (v.getId()) {
		case R.id.Button_Success:
			CameraTestStatus  = 1;
			Intent mIntent = getIntent();
			boolean fl = mIntent.getBooleanExtra("textall", false);
			if(fl == true){
				Intent miIntent = new Intent();
				miIntent.setAction("com.ykq.intent.action.MIC_TEST");
				miIntent.putExtra("textall", true);
				startActivityForResult(miIntent, RESULT_OK);
			}
			if(mTimer != null){
			mTimer.cancel();
			}
			break;
		case R.id.Button_Fail:
			CameraTestStatus = -1;
			Intent mIntent1 = getIntent();
			boolean fl1 = mIntent1.getBooleanExtra("textall", false);
			if(fl1 == true){
				Intent miIntent = new Intent();
				miIntent.setAction("com.ykq.intent.action.MIC_TEST");
				miIntent.putExtra("textall", true);
				startActivityForResult(miIntent, RESULT_OK);
			}
			if(mTimer != null){
				mTimer.cancel();
				}
			break;
		default:
			break;
		}


		setResultBeforeFinish(RESULT_OK);
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		mTimer.cancel();
		Intent service = new Intent().setClassName("com.mediatek.FMRadio","com.mediatek.FMRadio.FMRadioService");
		stopService(service);
		Intent service1 = new Intent("com.yunos.FMRadio.FMRadioService.stopAction");
		sendBroadcast(service1);
		//Intent service1 = new Intent().setClassName("com.yunos.FMRadio","com.yunos.FMRadio.FMRadioService");
		//stopService(service1);
	}

	public void setResultBeforeFinish(int status) {
		Log.d(TAG, "setResultBeforeFinish: "+status );
		Intent intent = new Intent();
		intent.putExtra(Utils.NAME, "fm");
		intent.putExtra(Utils.TEST_RESULT, CameraTestStatus);
		setResult(status, intent);
	}

}
