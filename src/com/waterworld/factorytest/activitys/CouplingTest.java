package com.waterworld.factorytest.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.internal.util.HexDump;
import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.FactoryBean;
import com.waterworld.factorytest.FactoryDatas;
import com.waterworld.factorytest.Utils;
import com.waterworld.factorytest.R;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import vendor.mediatek.hardware.nvram.V1_0.INvram;

public class CouplingTest extends FactoryActivity implements View.OnClickListener{


    private Button success;
    private Button failed;
    private int    Coupling_test_result;
    private static final String TAG = Utils.TAG+"CouplingTest" ;

    private TextView g_coupling_result;
    private TextView wbg_coupling_result;
    private static final String CUSTOM_ADDRESS_FILENAME = "/vendor/nvdata/APCFG/APRDEB/PRODUCT_INFO";

    private static int   COUPLING_VALUE = 1024;
    private static int   SPECIAL_VALUE = 700;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coupling_test);

        // 读天线耦合标志位
        g_coupling_result = findViewById( R.id.g_coupling_result );
        try {
            String barCodeValue = SystemProperties.get("vendor.gsm.serial");
            Log.d(TAG, "vendor.gsm.serial: "+barCodeValue);
            if( barCodeValue != null && barCodeValue.length() != 0 && barCodeValue.length() >= 61
                    && barCodeValue.substring(59, 60).equals("P")) {

                g_coupling_result.setText( R.string.pass);
                g_coupling_result.setTextColor( Color.GREEN );

            }else if(barCodeValue != null && barCodeValue.length() != 0 && barCodeValue.length() >= 61
                    && barCodeValue.substring(59, 60).equals("F")){
                g_coupling_result.setText( R.string.failed);
                g_coupling_result.setTextColor( Color.RED );
            }else{
                g_coupling_result.setText( R.string.no_test);
                g_coupling_result.setTextColor( Color.RED );

            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            g_coupling_result.setText( R.string.failed );
            g_coupling_result.setTextColor( Color.RED );

        }
        // 读wbg耦合标志位
        wbg_coupling_result = findViewById( R.id.wbg_coupling_result );
        try {
            String  wbg_result = readDatasStatusFromNvram();
            if( wbg_result.equals("80")){
                wbg_coupling_result.setText( R.string.pass );
                wbg_coupling_result.setTextColor( Color.GREEN );
            }else if( wbg_result.equals("70")){
                wbg_coupling_result.setText( R.string.failed );
                wbg_coupling_result.setTextColor( Color.RED );
            }else{
                wbg_coupling_result.setText( R.string.no_test );
                wbg_coupling_result.setTextColor( Color.RED );
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            wbg_coupling_result.setText( R.string.failed );
            wbg_coupling_result.setTextColor( Color.RED );

        }

        success = findViewById(R.id.coupling_success);
        success.setOnClickListener(this);

        failed = findViewById(R.id.coupling_failed);
        failed.setOnClickListener(this);
    }

    public String  readDatasStatusFromNvram( ){
        Log.d(TAG, "readDatasStatusFromNvram: Begin");
        String value=null;
        try {
            INvram agent = INvram.getService();
            if (agent == null) {
                Log.d(TAG,"readFileByNamevec write agent == null");
                return value;
            }
            String buff = agent.readFileByName(CUSTOM_ADDRESS_FILENAME, COUPLING_VALUE );
            Log.d(TAG, "readDatasStatusFromNvram: "+buff.length()+" -> "+buff.toString());
            byte[] buffArr = HexDump.hexStringToByteArray(buff.substring(0, buff.length() - 1));
            Log.d(TAG,"readFileByNamevec read buffArr == "+buffArr.length+" -> "+ Arrays.toString(buffArr));
            value = Byte.toString(buffArr[SPECIAL_VALUE]);
            Log.d(TAG,"readFileByNamevec read value == "+ value);

        } catch (Exception e) {
            Log.d(TAG,"readFileByNamevec Exception == "+e);
            e.printStackTrace();
        }
        Log.d(TAG, "readDatasStatusFromNvram: End");
        return value;
    }

    public void finish() {
        super.finish();
//        setResultBeforeFinish( Coupling_test_result );
    }

    @Override
    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "coupling");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch ( v.getId() ){
            case R.id.coupling_success:
                Coupling_test_result = 1;
                break;
            case R.id.coupling_failed:
                Coupling_test_result = -1;
                break;
            default:
                break;

        }

        setResultBeforeFinish( Coupling_test_result );
        finish();
    }
    public void onBackPressed() {
        // TODO Auto-generated method stub
//		super.onBackPressed();
        setResultBeforeFinish( Coupling_test_result );
        finish();


    }


}
