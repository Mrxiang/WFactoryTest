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
import android.os.SystemProperties;
//import com.mediatek.common.featureoption.FeatureOption;

public class KeyTest extends FactoryActivity implements View.OnClickListener {

    public static final String TAG = Utils.TAG + "KeyTest";
    public static int KeyTestStatus = 0;
    public Button vu;
    public Button vd;
    public Button failedBtn;
    public boolean flagUp=false;
    public boolean flagDown=false;
    //hzr add start for Key test in FactoryTest
    public Button mMenu,mHome,mBack;
    public boolean flagMenu=false;
    public boolean flagHome=false;
    public boolean flagBack=false;
    //hzr add end
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_result_key);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        vu = findViewById( R.id.volume_up);
        vd = findViewById( R.id.volume_down );
        //hzr add start for Key test in FactoryTest
        mMenu = findViewById( R.id.key_menu);
        mHome = findViewById( R.id.key_home );
        mBack = findViewById( R.id.key_back);
        if(SystemProperties.getBoolean("ro.hx_ouqi_s35_press_key_test", false)){
        	mMenu.setVisibility(View.VISIBLE);
        	mHome.setVisibility(View.VISIBLE);
        	mBack.setVisibility(View.VISIBLE);
        }
        //hzr add end
        failedBtn = findViewById( R.id.Button_Fail);
        failedBtn.setOnClickListener( this );
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


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onKeyDown occured:" + keyCode);

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            flagUp = true;
            vu.setBackgroundColor(getColor(R.color.limegreen));
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            flagDown = true;
            vd.setBackgroundColor(getColor(R.color.limegreen));

        }
        //hzr add start for Key test in FactoryTest
        if (keyCode == KeyEvent.KEYCODE_APP_SWITCH) {
            flagMenu = true;
            mMenu.setBackgroundColor(getColor(R.color.limegreen));

        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            flagHome = true;
            mHome.setBackgroundColor(getColor(R.color.limegreen));

        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            flagBack = true;
            mBack.setBackgroundColor(getColor(R.color.limegreen));

        }
        if(SystemProperties.getBoolean("ro.hx_ouqi_s35_press_key_test", false)){
        	if( flagUp && flagDown && flagMenu && flagHome && flagBack){
                KeyTestStatus = Utils.SUCCESS;
                setResultBeforeFinish( KeyTestStatus );
                finish();
            }
        }else{
        	if( flagUp && flagDown){
                KeyTestStatus = Utils.SUCCESS;
                setResultBeforeFinish( KeyTestStatus );
                finish();
            }
        }
        //hzr add end
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.Button_Fail) {
            KeyTestStatus = Utils.FAILED;
            setResultBeforeFinish(KeyTestStatus);
            finish();
        }
    }

    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "key");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }

}
