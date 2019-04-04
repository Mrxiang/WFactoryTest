package com.waterworld.factorytest.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.FactoryDatas;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

public class LedTest extends FactoryActivity implements View.OnClickListener{


    private Button success;
    private Button failed;
    private int    LED_test_result;
    private static final String TAG = Utils.TAG+"CouplingTest" ;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.led_test);




        success = findViewById(R.id.led_success);
        success.setOnClickListener(this);

        failed = findViewById(R.id.led_failed);
        failed.setOnClickListener(this);
    }

    public void finish() {
        super.finish();
//        setResultBeforeFinish( Coupling_test_result );
    }

    @Override
    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "led");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch ( v.getId() ){
            case R.id.led_success:
                LED_test_result = 1;
                break;
            case R.id.led_failed:
                LED_test_result = -1;
                break;
            default:
                break;

        }

        setResultBeforeFinish( LED_test_result );
        finish();
    }
    public void onBackPressed() {
        // TODO Auto-generated method stub
//		super.onBackPressed();
        setResultBeforeFinish( LED_test_result );
        finish();


    }


}
