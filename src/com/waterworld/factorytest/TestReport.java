package com.waterworld.factorytest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.mediatek.pingbo.PingTeeUtil;
import com.waterworld.factorytest.ext.ScrollListView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.android.internal.telephony.ITelephony;

//add by yds start
//add by yds end

public class TestReport extends Activity {

	private static final String TAG = Utils.TAG+"TestReport";
	private 	TextView 			tv_banben_value;
	private 	ScrollListView  	tv_true;
	private 	ScrollListView 		tv_false;
	private 	TextView 			testall;

	private 	GroupAdapter 	passedAdapter;
	private 	GroupAdapter 	failedAdapter;

	private     ArrayList<Integer>  passedList;
	private     ArrayList<Integer>  faildList;

	private 	ITelephony iTelephony;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.test_result_all);

		tv_banben_value = (TextView)findViewById(R.id.tv_banben_value);
		tv_banben_value.setText(SystemProperties.get("ro.build.display.id", "unKnow"));

		TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		TextView tv_jiaozhun_choose = (TextView)findViewById(R.id.tv_jiaozhun_choose);
		try {
			Method getITelephonyMethod = TelephonyManager.class.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			iTelephony = (ITelephony) getITelephonyMethod.invoke(tm, (Object[]) null);

			//it works? not sure.
			if("10".equals(SystemProperties.get("gsm.serial").substring(SystemProperties.get("gsm.serial").length() -2, SystemProperties.get("gsm.serial").length())) ||
					"10P".equals(SystemProperties.get("gsm.serial").substring(SystemProperties.get("gsm.serial").length() -3, SystemProperties.get("gsm.serial").length())) ||
					"10".equals(SystemProperties.get("gsm.serial").substring(SystemProperties.get("gsm.serial").length() -3, SystemProperties.get("gsm.serial").length()-1)) ||
					"10P".equals(SystemProperties.get("gsm.serial").substring(SystemProperties.get("gsm.serial").length() -4, SystemProperties.get("gsm.serial").length()-1))){
				tv_jiaozhun_choose.setText(R.string.msg_shi);
				tv_jiaozhun_choose.setTextColor(Color.GREEN);
			}else{
				tv_jiaozhun_choose.setText(R.string.msg_fou);
				tv_jiaozhun_choose.setTextColor(Color.RED);
			}

		} catch (Exception e) {
			// TODO: handle exception
			tv_jiaozhun_choose.setText(R.string.msg_fou);
			tv_jiaozhun_choose.setTextColor(Color.RED);
			e.printStackTrace();
		}


		testall = (TextView)findViewById(R.id.tv_testall_choose);

		if(FactoryDatas.allPassed( ) ){
			testall.setText(R.string.msg_shi);
			testall.setTextColor(Color.GREEN);
		}
		else{
			testall.setText(R.string.msg_fou);
			testall.setTextColor(Color.RED);
		}
		tv_true = findViewById( R.id.tv_true);
		tv_false = findViewById( R.id.tv_false);

		passedList = new ArrayList<Integer>();
		FactoryDatas.getPassedBeans( passedList);
		passedAdapter = new GroupAdapter( this, passedList, 0  );
		tv_true.setAdapter( passedAdapter );

		faildList = new ArrayList<Integer>();
		FactoryDatas.getFailedBeans( faildList);
		failedAdapter = new GroupAdapter( this, faildList, 1  );
		tv_false.setAdapter(failedAdapter);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();

	}

	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	class  GroupAdapter extends  BaseAdapter
	{
		private  List<Integer >  list;
		private Context context;
		private int flags ;
		public GroupAdapter(Context context,List<Integer> list,int flag)
		{
			this.list=list;
			this.context=context;
			this.flags=flag;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView=LayoutInflater.from(context).inflate(R.layout.simple_all, null);
			TextView tv=(TextView)convertView.findViewById(R.id.tv);
			tv.setText(context.getResources().getString(list.get(position)));
			if(flags == 0){
				tv.setTextColor(Color.GREEN);
			}else if(flags == 1){
				tv.setTextColor(Color.RED);
			}
			return convertView;
		}
	}

}
