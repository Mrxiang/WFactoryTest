package com.waterworld.factorytest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class HardwareVersion extends Activity {


    public TextView mHWVarint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hardware_info_layout);
        init();

    }

    public void init(){

        mHWVarint = findViewById( R.id.hw_variant_info );
        mHWVarint.setText( getHWVariant() );
    }


    public String getHWVariant( ){

        return "";
    }
}
