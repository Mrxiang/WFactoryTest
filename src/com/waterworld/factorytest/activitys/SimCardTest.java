package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.FactoryTestFeatureoption.FeatureOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

//import com.mediatek.common.featureoption.FeatureOption;
//import com.mediatek.common.telephony.ITelephonyEx;
//import android.telephony.gsm.SmsManager;
//import android.telephony.SmsManager;

public class SimCardTest extends FactoryActivity implements OnClickListener {

    private static final String TAG = Utils.TAG + "SimCardTest";
    static int CameraTestStatus = 0;
    Timer mTimer;
    boolean flags = true;

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onDestroy();
    }

    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
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
        switch (v.getId()) {
            case R.id.Button_Success:
                CameraTestStatus = 1;
                break;
            case R.id.Button_Fail:
                CameraTestStatus = -1;
                break;
            default:
                break;
        }
        setResultBeforeFinish(CameraTestStatus);
        finish();
    }

    //private TelephonyManagerEx telMgr;
    private List item = new ArrayList();
    private List value = new ArrayList();
    boolean flags1 = false;
    boolean flags2 = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_result_simcard);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //	    setContentView(R.layout.test_result);
        ListView list = (ListView) findViewById(R.id.listv_sim);
        Button success_Button = (Button) findViewById(R.id.Button_Success);
        Button fail_Button = (Button) findViewById(R.id.Button_Fail);
        success_Button.setOnClickListener(this);
        fail_Button.setOnClickListener(this);
        success_Button.setEnabled(false);
//	        Intent intent = new Intent();
//           intent.setClassName("com.android.settings", "com.android.settings.gemini.SimManagement");
//           startActivity(intent);
        //telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //telMgr = (TelephonyManagerEx) getSystemService(TELEPHONY_SERVICEEX);
        //   telMgr = (TelephonyManagerEx)ITelephonyEx.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICEEX));
        //telMgr = Class.forName("TelephonyManagerEx").getConstructor().newInstance();
        //Log.w("FactoryTest", "simcardTest:execute getSimState telMgr = "+getSystemService(TELEPHONY_SERVICEEX));

        Object result_0 = null;
        Object result_1 = null;
        int simState0 = 0;
        int simState1 = 0;
        try {
            //Method method = TelephonyManager.class.getMethod("getSimStateGemini",new Class[] { int.class });
			   /*Method method = MtkTelephonyManagerEx.class.getMethod("getSimCardState",new Class[] { int.class });
		       result_0 = method.invoke(MtkTelephonyManagerEx.getDefault(), new Object[] { new Integer(0) });
		       result_1 = method.invoke(MtkTelephonyManagerEx.getDefault(), new Object[] { new Integer(1) }); */

            simState0 = SubscriptionManager.getSimStateForSlotIndex(0);
            simState1 = SubscriptionManager.getSimStateForSlotIndex(1);
            Log.d("FactoryTest", "simcardTest:simState0 == " + simState0 + ", simState1 == " + simState1);
        } catch (Exception e) {
            // TODO: handle exception
            Log.w("FactoryTest", "simcardTest:execute getSimState failed");
            e.printStackTrace();
        }
        if (simState0 == 10 || simState0 == TelephonyManager.SIM_STATE_READY )  {

//	    	   if(FeatureOption.MTK_GEMINI_ENHANCEMENT){
            item.add(getString(R.string.msg_sim1));
//	    	   }else{
//	    	   item.add(getString(R.string.msg_sim));
//	    	   }
            value.add("");
            item.add("");
            value.add(getString(R.string.msg_sim_state));
            flags1 = true;

        } else {

//	    	   if(FeatureOption.MTK_GEMINI_ENHANCEMENT){
            item.add(getString(R.string.msg_sim1));
//	    	   }else{
//	    	   item.add(getString(R.string.msg_sim));
//	    	   }

            value.add("");
            item.add("");
            value.add(getString(R.string.msg_sim_stateno));
        }

//	      if(FeatureOption.MTK_GEMINI_ENHANCEMENT){
        if (FeatureOption.MTK_GEMINI_SUPPORT) {
            if (simState1 == 10 || simState0 == TelephonyManager.SIM_STATE_READY) {

                item.add(getString(R.string.msg_sim2));
                value.add("");
                item.add("");
                value.add(getString(R.string.msg_sim_state));
                flags2 = true;

            } else {
                item.add(getString(R.string.msg_sim2));
                value.add("");
                item.add("");
                value.add(getString(R.string.msg_sim_stateno));
            }
        }
        // Log.i("wangliangfu","MTK_GEMINI_SUPPORT = "+FeatureOption.MTK_GEMINI_SUPPORT);

//	      if(FeatureOption.MTK_GEMINI_ENHANCEMENT){
        if (FeatureOption.MTK_GEMINI_SUPPORT) {
            if (flags1 && flags2) {
                success_Button.setEnabled(true);
            }
        } else {
            if (flags1) {
                success_Button.setEnabled(true);
            }
        }

        list.setAdapter(new MyAdapter(this, item, value));
        final Handler handler1 = new Handler() {
            public void handleMessage(Message msg) {
                CameraTestStatus = 1;
                Intent miIntent = new Intent();
                if (FeatureOption.HX_FACTORYTEST_BATTERY) {
                    miIntent.setAction("com.ykq.intent.action.BATTERY_TEST");
                } else {
                    miIntent.setAction("com.ykq.intent.action.SPEAKER_TEST");
                }
                miIntent.putExtra("textall", true);
                startActivityForResult(miIntent, RESULT_OK);
                finish();
            }
        };
        Timer mTimer = new Timer();
        TimerTask mTask = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                Message msg = new Message();
                handler1.sendMessage(msg);

            }
        };
        if (flags1 == true && flags2 == true) {
//            Intent mIntent1 = getIntent();
//            boolean fl = mIntent1.getBooleanExtra("textall", false);
//            if (fl == true) {
//                mTimer.schedule(mTask, 1000);
//            }
        } else {
        }
    }

    @Override
    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "sim");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<String> items;
        private List<String> values;

        public MyAdapter(Context context, List<String> item, List<String> value) {

            mInflater = LayoutInflater.from(context);
            items = item;
            values = value;
        }


        public int getCount() {
            return items.size();
        }

        public Object getItem(int position) {
            return items.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.simple_simcard, null);

                holder = new ViewHolder();
                holder.text1 = (TextView) convertView.findViewById(R.id.myText1);
                holder.text2 = (TextView) convertView.findViewById(R.id.myText2);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text1.setText(items.get(position).toString());
            holder.text2.setText(values.get(position).toString());
            holder.text1.setTextSize(18);
            holder.text2.setTextSize(18);
            holder.text2.setTextColor(Color.BLUE);
            holder.text1.setTextColor(Color.GREEN);
            return convertView;
        }


        private class ViewHolder {

            TextView text1;
            TextView text2;
        }


    }


}
