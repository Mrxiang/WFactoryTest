package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.internal.telephony.ITelephony;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.FactoryTestFeatureoption.FeatureOption;

import java.util.Timer;

import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;


public class PhoneTest extends FactoryActivity implements View.OnClickListener {

    public static final String TAG = Utils.TAG + "PhoneTest";

    boolean isEmergencyNumber911 = "1".equals(SystemProperties.get("ro.hx_emergency_number_911", "0"));

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
        if (mThread != null) {
            mThread.stop();
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        PhoneTestStatus = 0;
    }

    static int PhoneTestStatus = 0;
    private ITelephony iTelephony;
    private TelephonyManager telephonyMgr;
    Thread mThread;
    Timer mTimer;
    boolean flags = true;

    public static Intent generateDialIntent(boolean paramBoolean, int paramInt, String paramString) {
        Intent localIntent = new Intent();
        localIntent.setAction("out_going_call_to_phone_app");
        if (paramBoolean) {
            localIntent.putExtra("number", paramString);
            localIntent.putExtra("launch_from_dialer", true);
            localIntent.putExtra("is_sip_call", true);
        }
        while (true) {
            localIntent.putExtra("number", paramString);
            localIntent.putExtra("simId", paramInt);
            localIntent.putExtra("launch_from_dialer", true);
            localIntent.putExtra("is_sip_call", false);
            return localIntent;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


        setContentView(R.layout.test_result);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Button success_Button = (Button) findViewById(R.id.Button_Success);
        Button fail_Button = (Button) findViewById(R.id.Button_Fail);
        success_Button.setOnClickListener(this);
        fail_Button.setOnClickListener(this);

//	       sendBroadcast(generateDialIntent(false, 0, "112"));
//			  telephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//			  try {
//			    	 Method getITelephonyMethod = TelephonyManager.class.getDeclaredMethod("getITelephony", (Class[]) null);
//						getITelephonyMethod.setAccessible(true);
//						iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyMgr, (Object[]) null);
//
//
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
        Intent mIntent = new Intent(Intent.ACTION_CALL_EMERGENCY, Uri.parse("tel:" + 112));
        if (isEmergencyNumber911) {
            mIntent = new Intent(Intent.ACTION_CALL_EMERGENCY, Uri.parse("tel:" + 911));
        }
        startActivity(mIntent);

//			Intent intent = new Intent(Intent.ACTION_CALL_EMERGENCY);
        //           intent.setData(Uri.fromParts("tel", "112", null));
        //           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //           startActivity(intent//);

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
        switch (v.getId()) {
            case R.id.Button_Success:
                PhoneTestStatus = 1;
//			mTimer.cancel();
                break;
            case R.id.Button_Fail:
                PhoneTestStatus = -1;
//			mTimer.cancel();
                break;
            default:
                break;
        }
        //intent.getExtras()
//		try {
//			iTelephony.endCall();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        setResultBeforeFinish( PhoneTestStatus );
        finish();
    }

    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "phone");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }
}
