package waterworld.com.factorytest.activitys;

/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 */
/* MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import waterworld.com.factorytest.FactoryActivity;
import waterworld.com.factorytest.FactoryTestFeatureoption.FeatureOption;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import waterworld.com.factorytest.R;
import waterworld.com.factorytest.Utils;

//pingyh add start
//pingyh add end


public class BatteryTest extends FactoryActivity  implements OnClickListener {

	private int mLogRecordInterval = 10000;
	private File mLogFile;
	private boolean mIsRecording = false;

	private File BatteryTest = null;

	private TextView mStatus;
	private TextView mLevel;
	private TextView mScale;
	private TextView mHealth;
	private TextView mVoltage;
	private TextView mTemperature;
	private TextView mTechnology;
	private TextView mUptime;
	private EditText mIntervalEdit;
	private Button mLogRecord;
	//pingyh add start
	private TextView current_electronicl;
	private LinearLayout mElectroniclLayout;
	private TextView current_voltage;
	private LinearLayout mVoltageLayout;
    private int mChargerElectronic;
    private int mChargerVoltage;
	private Button success_Button;
	//pingyh add end

	private static final int EVENT_TICK = 1;
	private static final int EVENT_LOG_RECORD = 2;
        private static final int EVENT_CHR = 3; //zxs

	private String TAG = Utils.TAG+"EM-BatteryTest";
	
	static int BatteryTestStatus = 0;
	// yangkun add start 2018/03/19
	private boolean mIsCharging = false;
	// add end

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_TICK:
				updateBatteryStats();
				sendEmptyMessageDelayed(EVENT_TICK, 1000);
				break;
			}
		}

		private void updateBatteryStats() {
			// TODO Auto-generated method stub
			long uptime = SystemClock.elapsedRealtime();
			mUptime.setText(DateUtils.formatElapsedTime(uptime / 1000));
		}
	};

       	private Handler mChrHandler = new Handler() { //zxs 20170316
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_CHR:
				updateChargingStats();
				sendEmptyMessageDelayed(EVENT_CHR, 1000);
				break;
			}
		}

		private void updateChargingStats() {
			// TODO Auto-generated method stub
					//if(SystemProperties.get("ro.hx_battery_charger_test").equals("1")){
					String currentVoltage = readCurrentVoltage();
					if(!TextUtils.isEmpty(currentVoltage)) {
						mVoltageLayout.setVisibility(View.VISIBLE);
						if(Integer.parseInt(currentVoltage.trim())< 100){
							current_voltage.setText("0"+" mV");
						}else{
							current_voltage.setText(currentVoltage.trim()+" mV");
						}
					}
					
					String currentElectronicl = readCurrentAverage();
					if(!TextUtils.isEmpty(currentElectronicl)) {
						mElectroniclLayout.setVisibility(View.VISIBLE);
						current_electronicl.setText(currentElectronicl.trim()+" mA");
					}
					// yangkun modify start 2018/03/19
					if (currentVoltage != null && !currentVoltage.equals("")) {
						mChargerVoltage = Integer.parseInt(currentVoltage.trim());//arg1.getIntExtra("voltage", 0);//Integer.parseInt(arg1.getIntExtra("voltage", 0));
					}
					if (currentElectronicl != null && !currentElectronicl.equals("")) {
						mChargerElectronic = Integer.parseInt(currentElectronicl.trim());
					}
					// modify end
					Log.e(TAG,"pingyh mChargerVoltage:"+mChargerVoltage+"  mChargerElectronic:"+mChargerElectronic);
	                if (mChargerElectronic > 300) {
	                    success_Button.setEnabled(true);
	                } else {
	                    if (mChargerVoltage > 4100 && mChargerElectronic > 50) {
							success_Button.setEnabled(true);
	                    } else if (mIsCharging) { // yangkun add start 2018/03/19
							success_Button.setEnabled(true);
						} else {
	                    	success_Button.setEnabled(false);
	                    }
	                }
				}
		//}
	};

	/**
	 * Format a number of tenths-units as a decimal string without using a
	 * conversion to float. E.g. 347 -> "34.7"
	 */
	private final String tenthsToFixedString(int x) {
		int tens = x / 10;
		return new String("" + tens + "." + (x - 10 * tens));
	}

	/**
	 * Listens for intent broadcasts
	 */
	private IntentFilter mIntentFilter;
	private IntentFilter mIntentFilterSDCard;

	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED))
                        {
				int plugType = arg1.getIntExtra("plugged", 0);

				mLevel.setText("" + arg1.getIntExtra("level", 0));
				mScale.setText("" + arg1.getIntExtra("scale", 0));
				mVoltage.setText("" + arg1.getIntExtra("voltage", 0) + " "
						+ getString(R.string.battery_info_voltage_units));
				mTemperature.setText(""
						+ tenthsToFixedString(arg1
								.getIntExtra("temperature", 0))
						+ getString(R.string.battery_info_temperature_units));


				
				int status = arg1.getIntExtra("status",
						BatteryManager.BATTERY_STATUS_UNKNOWN);
				String statusString;
				if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
					statusString = getString(R.string.battery_info_status_charging);
					if (plugType > 0) {
						statusString = statusString
								+ " "
								+ getString((plugType == BatteryManager.BATTERY_PLUGGED_AC) ? R.string.battery_info_status_charging_ac
										: R.string.battery_info_status_charging_usb);
					}
					// yangkun add start 2018/03/19
					mIsCharging = true;
					// add end
				} else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
					statusString = getString(R.string.battery_info_status_discharging);
					// yangkun add start 2018/03/19
					mIsCharging = false;
					// add end
				} else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
					statusString = getString(R.string.battery_info_status_not_charging);
					// yangkun add start 2018/03/19
					mIsCharging = false;
					// add end
				} else if (status == BatteryManager.BATTERY_STATUS_FULL) {
					// yangkun add start 2018/04/14
					mIsCharging = true;
					// add end
					statusString = getString(R.string.battery_info_status_full);
				} else {
					statusString = getString(R.string.battery_info_status_unknown);
					// yangkun add start 2018/03/19
					mIsCharging = false;
					// add end
				}
				mStatus.setText(statusString);

				int health = arg1.getIntExtra("health",
						BatteryManager.BATTERY_HEALTH_UNKNOWN);
				String healthString;
				if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
					healthString = getString(R.string.battery_info_health_good);
				} else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
					healthString = getString(R.string.battery_info_health_overheat);
				} else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
					healthString = getString(R.string.battery_info_health_dead);
				} else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
					healthString = getString(R.string.battery_info_health_over_voltage);
				} else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
					healthString = getString(R.string.battery_info_health_unspecified_failure);
				} else {
					healthString = getString(R.string.battery_info_health_unknown);
				}
				mHealth.setText(healthString);
			}

		}
	};

	//pingyh add start
	public String readCurrentVoltage() {
		//String file = "/sys/class/power_supply/battery/ChargerVoltage";
                String file ="/sys/devices/platform/battery/Charging_Voltage";
		byte[] buffer = new byte[5];

		try {
			InputStream inStream = new FileInputStream(file);
			inStream.read(buffer);
			
			if (buffer != null) {					
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos.write(buffer);
				return baos.toString(); 
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}
	
	 public String readCurrentAverage() {
		//String file = "/sys/class/power_supply/battery/BatteryAverageCurrent";
                String file = "/sys/devices/platform/battery/Charging_Current";
		byte[] buffer = new byte[5];

		try {
			InputStream inStream = new FileInputStream(file);
			inStream.read(buffer);
			
			if (buffer != null) {					
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos.write(buffer);
				return baos.toString(); 
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}
	//pingyh add end
	
	private BroadcastReceiver mIntentReceiverSDCard = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			if (action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)
					|| action.equals(Intent.ACTION_MEDIA_REMOVED)
					|| action.equals(Intent.ACTION_MEDIA_EJECT)) {
				if (mIsRecording == false) {
					return;
				}
				mIsRecording = false;
				mLogHandler.removeMessages(EVENT_LOG_RECORD);

			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.battery_info);

		// create the IntentFilter that will be used to listen
		// to battery status broadcasts
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

		mIntentFilterSDCard = new IntentFilter();
		mIntentFilterSDCard.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		mIntentFilterSDCard.addAction(Intent.ACTION_MEDIA_REMOVED);
		mIntentFilterSDCard.addAction(Intent.ACTION_MEDIA_EJECT);
		mIntentFilterSDCard.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		mIntentFilterSDCard.addDataScheme("file");

		// check whether the sdcard exists, if yes, set up BatteryTest
		// directory, and if not, notify user to plug in it
		File sdcard = null;
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_REMOVED)) {
			sdcard = Environment.getExternalStorageDirectory();
			BatteryTest = new File(sdcard.getParent() + "/" + sdcard.getName()
					+ "/BatteryTest/");
			Log.e(TAG, sdcard.getParent() + "/" + sdcard.getName()
					+ "/BatteryTest/");
			if (!BatteryTest.isDirectory()) {
				BatteryTest.mkdirs();
			}

		}

		
		success_Button = (Button) findViewById(R.id.Button_Success);
		Button fail_Button = (Button) findViewById(R.id.Button_Fail);
		success_Button.setOnClickListener(this);
		fail_Button.setOnClickListener(this);
		
		if(SystemProperties.get("ro.hx_battery_charger_test").equals("1")){
			success_Button.setEnabled(false);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		mStatus = (TextView) findViewById(R.id.status);
		mLevel = (TextView) findViewById(R.id.level);
		mScale = (TextView) findViewById(R.id.scale);
		mHealth = (TextView) findViewById(R.id.health);
		mVoltage = (TextView) findViewById(R.id.voltage);
		mTemperature = (TextView) findViewById(R.id.temperature);
		mUptime = (TextView) findViewById(R.id.uptime);
		//pingyh add start
		if(SystemProperties.get("ro.hx_battery_charger_test").equals("1")){
			current_voltage = (TextView) findViewById(R.id.current_mv);
			mVoltageLayout = (LinearLayout) findViewById(R.id.current_voltage);
			current_electronicl = (TextView) findViewById(R.id.current_ma);
			mElectroniclLayout = (LinearLayout) findViewById(R.id.current_electronicl);
		}
		//pingyh add end
		if (mIntervalEdit == null) {
			Log.e(TAG, "clocwork worked...");
			// not return and let exception happened.
		}

		mHandler.sendEmptyMessageDelayed(EVENT_TICK, 1000);
               if(SystemProperties.get("ro.hx_battery_charger_test").equals("1")){ //zxs
                mChrHandler.sendEmptyMessageDelayed(EVENT_CHR, 10);
              }
		registerReceiver(mIntentReceiver, mIntentFilter);
		registerReceiver(mIntentReceiverSDCard, mIntentFilterSDCard);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeMessages(EVENT_TICK);
                mChrHandler.removeMessages(EVENT_CHR); //zxs

		// we are no longer on the screen stop the observers
		unregisterReceiver(mIntentReceiver);
		unregisterReceiver(mIntentReceiverSDCard);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.Button_Success:
			BatteryTestStatus = 1;
			break;
		case R.id.Button_Fail:
			BatteryTestStatus = -1;
			break;
		default:
			break;
		}
		// intent.getExtras()
		// startActivity(intent);
		setResultBeforeFinish( BatteryTestStatus );
		finish();
	}
		

	public Handler mLogHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_LOG_RECORD:
				Log.i(TAG, "Record one time");
				WriteCurrentBatteryInfo();
				sendEmptyMessageDelayed(EVENT_LOG_RECORD, mLogRecordInterval);
				break;
			}
		}

		private void WriteCurrentBatteryInfo() {
			String LogContent = "";
			LogContent = LogContent + mStatus.getText() + ", "
					+ mLevel.getText() + ", " + mScale.getText() + ", "
					+ mHealth.getText() + ", " + mVoltage.getText() + ", "
					+ mTemperature.getText() + ", " + mTechnology.getText()
					+ ", " + mUptime.getText() + "\n";

			try {
				FileWriter fileWriter = new FileWriter(mLogFile, true);
				fileWriter.write(LogContent);
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	};


	public void setResultBeforeFinish(int status) {
		Log.d(TAG, "setResultBeforeFinish: " + status);
		Intent intent = new Intent();
		intent.putExtra(Utils.NAME, "battery");
		intent.putExtra(Utils.TEST_RESULT, status);
		setResult(status, intent);
	}
}
