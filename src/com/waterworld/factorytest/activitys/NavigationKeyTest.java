package com.waterworld.factorytest.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;
//import com.mediatek.common.featureoption.FeatureOption;

public class NavigationKeyTest extends FactoryActivity implements View.OnClickListener {

    public static final String TAG = Utils.TAG + "KeyTest";
    public static int KeyTestStatus = 0;
    public Button vu;
    public Button vd;
    public Button keySmart;
    public Button keyBack;
    public Button keyHome;
//    public Button keyMenu;
    public Button failedBtn;
    public boolean flagUp=false;
    public boolean flagDown=false;
    public boolean flagSmart=false;
    public boolean flagBack=false;
    public boolean flagHome=false;
//    public boolean flagMenu=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_navigation_key);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        vu = findViewById( R.id.volume_up);
        vd = findViewById( R.id.volume_down );

        keySmart = findViewById( R.id.key_sos );
        keyBack = findViewById( R.id.key_back );
        keyHome = findViewById( R.id.key_home );
//        keyMenu = findViewById( R.id.key_menu );

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
        }else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            flagDown = true;
            vd.setBackgroundColor(getColor(R.color.limegreen));

        }

        if (keyCode == KeyEvent.KEYCODE_SOS) {
            flagSmart = true;
            keySmart.setBackgroundColor(getColor(R.color.limegreen));

        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            flagBack = true;
            keyBack.setBackgroundColor(getColor(R.color.limegreen));

        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            flagHome = true;
            keyHome.setBackgroundColor(getColor(R.color.limegreen));

        }
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            flagMenu = true;
//            keyMenu.setBackgroundColor(getColor(R.color.limegreen));
//
//        }


        if( flagUp && flagDown  && flagSmart && flagBack && flagHome ){
            KeyTestStatus = Utils.SUCCESS;
            setResultBeforeFinish( KeyTestStatus );
            finish();
        }
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
        intent.putExtra(Utils.NAME, "NavigationKey");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }

}
