package com.waterworld.factorytest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.mediatek.FactoryTest.FactoryTestFeatureoption.FeatureOption;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HardwareInformationActivity extends Activity implements View.OnClickListener{
	private String TAG = "HardwareInformationShowActivity";
	private TextView mFLASHInformation;
	private TextView mCPUInformation;
	private TextView mLCMInformation;
	private TextView mTPInformation;
	private TextView mFrontCameraInformation;
	private TextView mBackCaremaInformation;
	private TextView mBuildVersionTextView;
	private RelativeLayout mFingerprintIcLayout;
	private TextView mFingerprintIcContent;
	private String PROPERTY_CUSTOM_BUILD_VERSION="ro.build.display.id";
	private Button button_success;
	private Button button_fail;
	static int HardwareInforTestStatus = 0;
	
	//fangfengfan add start for read tp version
	private static final boolean HX_SHOW_TP_VERSION = SystemProperties.getBoolean("ro.hx_show_tp_version", false);
	private static final boolean HX_SHOW_FINGERPRINT_IC = SystemProperties.getBoolean("ro.hx_show_fingerprint_ic", false);
	//fangfengfan add end

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hardware_info_layout);
		init();

	}

	private void init() {
		mCPUInformation = (TextView) findViewById(R.id.hardware_cpu_info);
		mCPUInformation.setText(SystemProperties.get("ro.mediatek.platform",null));

		mLCMInformation = (TextView) findViewById(R.id.hardware_lcm_info);
		mTPInformation = (TextView) findViewById(R.id.hardware_tp_info);
		mFrontCameraInformation = (TextView) findViewById(R.id.hardware_front_camera_info);
		mBackCaremaInformation = (TextView) findViewById(R.id.hardware_back_camera_info);
		mBuildVersionTextView=(TextView)findViewById(R.id.build_version);
		mFLASHInformation = (TextView)findViewById(R.id.hardware_flash_info);
		mFingerprintIcContent = (TextView)findViewById(R.id.fingerprint_ic_content);
		mFingerprintIcLayout = (RelativeLayout)findViewById(R.id.fingerprint_ic_layout);
		mBuildVersionTextView.setText(getCustomBuildVersion());

		ReadFlashIC();
		ReadLcmIC();
		ReadCamIC();
		//fangfengfan add start for read tp version
		//if(HX_SHOW_TP_VERSION){
		//	ReadTPVersion();
		//} else {
		//	ReadTPIC();
		//}

		ReadTPIC();
		ReadTPVersion();
		
		if(HX_SHOW_FINGERPRINT_IC){
			mFingerprintIcLayout.setVisibility(View.VISIBLE);
			ReadFingerPrintIC();
		}
		//fangfengfan add end
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setAction("com.ykq.intent.action.FACTORY_TEST_MAIN");
		setResult(RESULT_OK, intent);
		switch (v.getId()) {
		case R.id.Button_Success:
			HardwareInforTestStatus = 1;
			Intent mIntent = getIntent();
			boolean fl = mIntent.getBooleanExtra("textall", false);
			if(fl == true){
				Intent miIntent = new Intent();
				miIntent.setAction("com.ykq.intent.action.ALL_TEST");
				miIntent.putExtra("textall", true);
				startActivityForResult(miIntent, RESULT_OK);
			}
			break;
		case R.id.Button_Fail:
			HardwareInforTestStatus = -1;
			Intent mIntent1 = getIntent();
			boolean fl1 = mIntent1.getBooleanExtra("textall", false);
			if(fl1 == true){
				Intent miIntent = new Intent();
				miIntent.setAction("com.ykq.intent.action.ALL_TEST");
				miIntent.putExtra("textall", true);
				startActivityForResult(miIntent, RESULT_OK);
			}
			break;
		default:
			break;
		}
		finish();
	}

	public void ReadFlashIC() 
	{
		File file = new File("/system/flashinfo");
		String str = null;
		try {
		    InputStream is = new FileInputStream(file);
		    InputStreamReader input = new InputStreamReader(is, "UTF-8");
		    BufferedReader reader = new BufferedReader(input);
		    while ((str = reader.readLine()) != null) {
			mFLASHInformation.append(str);
			mFLASHInformation.append("\n");
			Log.i(TAG, "zrl debug, read cmdline = " + str);
		    }

		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	}

	public String getCustomBuildVersion(){
		String mCustomBuildVersionString="Unknown";

		mCustomBuildVersionString = SystemProperties.get(PROPERTY_CUSTOM_BUILD_VERSION,
					"Unknown");
		
		return mCustomBuildVersionString;
	}

	private String getCPUInformation() {
		String mHardwareCpuInformation = "Unknown";
		String cpuInfo = getCPU();
		if (cpuInfo != null) {
			String cpuMaxFreq = getMaxCpuFreq();
			if ((cpuMaxFreq != null) && (!cpuMaxFreq.equals(""))
					&& (!cpuMaxFreq.equals("N/A"))) {
				cpuMaxFreq = getCpuFreqString(Integer.parseInt(cpuMaxFreq));

				return cpuInfo + " " + cpuMaxFreq;
			} else {
				return cpuInfo;
			}
		} else {
			return mHardwareCpuInformation;
		}

	}

	public static String getCPU() {
		String str1 = "/proc/cpuinfo";
		String str2;
		String[] arrayOfString;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();

			arrayOfString = str2.split(":");
			arrayOfString[1] = arrayOfString[1].trim();

			localBufferedReader.close();

		} catch (IOException e) {
			return null;
		}
		return arrayOfString[1];
	}

	public static String getCpuFreqString(int fValue) {
		fValue = fValue / 1000;
		// Log.i(TAG, "getCpuFreqString---------->" + fValue + "MHZ");
		return fValue + "MHZ";
	}

	public static String getMaxCpuFreq() {
		StringBuilder result = new StringBuilder();
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat",
					"/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result.append(new String(re));
				
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();

			result.delete(0, result.length());
			result.append("N/A");

		}
		return result.toString().trim();
	}

	public void ReadTPIC() {
		String file_tp_info = "/proc/driver/tp_info";
		byte[] buffer_info = new byte[128];

		try {
			InputStream inStream = new FileInputStream(file_tp_info);
			inStream.read(buffer_info);

			if (buffer_info != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos.write(buffer_info);
				String mTPInfo = baos.toString();
				// Log.v(TAG, "read file_tp_info = " + mTPInfo);
				mTPInformation.append("IC:       ");
				mTPInformation.append(mTPInfo);
				mTPInformation.append("\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//fangfengfan add start for read tp version
	public void ReadTPVersion() {
		String file_tp_version = "/proc/driver/tp_version";
  
		byte[] buffer_version = new byte[128];
		try {
			InputStream inStream_version = new FileInputStream(file_tp_version);
			inStream_version.read(buffer_version);
			
			if (buffer_version != null) {					
				ByteArrayOutputStream baos_version = new ByteArrayOutputStream();
				baos_version.write(buffer_version);
				String fullString_version = baos_version.toString(); 
				
				Log.i(TAG, "debug, read file_tp_version = " + fullString_version);
				mTPInformation.append("Version:   ");
				mTPInformation.append(fullString_version);
				mTPInformation.append("\n");
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}

	public void ReadFingerPrintIC(){
		String file_fingerprint_info = "/proc/driver/fingerprint_info";
		byte[] buffer_info = new byte[128];
		try {
			InputStream inStream = new FileInputStream(file_fingerprint_info);
			//while ((byteread = inStream.read(buffer)) != -1) {
			//byteread = inStream.readline(buffer);
			inStream.read(buffer_info);
			
			if (buffer_info != null) {					
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos.write(buffer_info);
				String fullString = baos.toString(); 
				
				Log.i(TAG, "debug, read file_fingerprint_info = " + fullString);
				mFingerprintIcContent.setText(fullString);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	//fangfengfan add end

       public void ReadLcmIC() 
	{
		byte[] buffer = new byte[600]; //[255];  // changed by wanghe 2013-08-08 from 230 to 255

		String file_lcm_info = "/proc/driver/lcm_info";
		try {
			InputStream inStream = new FileInputStream(file_lcm_info);
			inStream.read(buffer);
			
			if (buffer != null) {					
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos.write(buffer);
				String fullString = baos.toString(); 
				
				Log.i(TAG, "debug, read file_lcm_info = " + fullString);

				mLCMInformation.setText(fullString);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}			

	}

	public void ReadCamIC() {

		String file_cam_main = "/proc/driver/camera_info_main";
		String file_cam_sub = "/proc/driver/camera_info_sub";

		byte[] buffer_main = new byte[50];
		byte[] buffer_sub = new byte[50];
		try {
			InputStream inStream = new FileInputStream(file_cam_main);
			inStream.read(buffer_main);

			if (buffer_main != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos.write(buffer_main);
				String mBackCameraInfo = baos.toString();
				// Log.v(TAG, "read file_cam_main = " + mBackCameraInfo);
				mBackCaremaInformation.setText(mBackCameraInfo);
			}

			InputStream inStream_sub = new FileInputStream(file_cam_sub);
			inStream_sub.read(buffer_sub);

			if (buffer_sub != null) {
				ByteArrayOutputStream baos_sub = new ByteArrayOutputStream();
				baos_sub.write(buffer_sub);
				String mFrontCameraInfo = baos_sub.toString();
				// Log.v(TAG, "read file_cam_sub = " + mFrontCameraInfo);
				mFrontCameraInformation.setText(mFrontCameraInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

