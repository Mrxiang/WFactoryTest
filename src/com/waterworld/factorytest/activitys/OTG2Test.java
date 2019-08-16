package com.waterworld.factorytest.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;

public class OTG2Test extends FactoryActivity  {

	private static final String TAG = Utils.TAG+"OTGTest";
	private File file;
	private boolean isContinue = true;
	protected static boolean mCreateFirstTime;
	private TextView otgTv;
	private Button mBtFailed;
	private Button mBtOk;
	private final int READ_FILE = 215;
	public static int OTGTestStatus = 0;
	private UsbReceiver mUsbReceiver;
	private UsbManager usbManager;
	public static final String ACTION_USB_DEVICE_ATTACHED ="android.hardware.usb.action.USB_DEVICE_ATTACHED"; //对应的是USB设备插入时候的广播
	public static final String ACTION_USB_DEVICE_DETACHED ="android.hardware.usb.action.USB_DEVICE_DETACHED"; //对应的USB设备拔出的时候的广播

	private StorageManager mStorageManager;

	private StringBuilder stringBuilder;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_result_otg2);
		this.otgTv = (TextView) findViewById(R.id.otg_tv);
		this.mBtOk = (Button)findViewById(R.id.Button_Success);
		this.mBtFailed = (Button)findViewById(R.id.Button_Fail);

		stringBuilder = new StringBuilder();
		stringBuilder.append( getResources().getString( R.string.insert_usb_device ));
		otgTv.setText( stringBuilder +"\n");

		registerUSBDeviceReceiver();



	}

	private StorageEventListener mListener = new StorageEventListener(){
		public void onUsbMassStorageConnectionChanged(boolean connected) {
			Log.d(TAG, " onUsbMassStorageConnectionChanged "+connected +"\n");
		}


		public void onStorageStateChanged(String path, String oldState, String newState) {
			Log.d(TAG, " onStorageStateChanged "+path +"\n");
		}

		public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
			Log.d(TAG, " onVolumeStateChanged "+vol +"\n");
			onVolumeStateChangedInternal( vol );
		}

		public void onVolumeRecordChanged(VolumeRecord rec) {
			Log.d(TAG, " onVolumeRecordChanged "+rec +"\n");
		}

		public void onVolumeForgotten(String fsUuid) {
			Log.d(TAG, " onVolumeForgotten "+fsUuid +"\n");
		}

		public void onDiskScanned(DiskInfo disk, int volumeCount) {
			Log.d(TAG, " onDiskScanned "+disk +"\n");
			if( disk.size <0 ){
//				setResultBeforeFinish( Utils.FAILED );
//				finish();
				stringBuilder.append( getResources().getString( R.string.please_inset_sdcard)+"\n");
				otgTv.setText( stringBuilder);
			}
		}

		public void onDiskDestroyed(DiskInfo disk) {
			Log.d(TAG, " onDiskDestroyed "+disk +"\n");
		}
	};
	protected void onResume() {
		super.onResume();
	}

	protected void onPause() {
		super.onPause();
	}

	@Override
	public void setResultBeforeFinish(int status) {
		Log.d(TAG, "setResultBeforeFinish: " + status);
		Intent intent = new Intent();
		intent.putExtra(Utils.NAME, "otg2");
		intent.putExtra(Utils.TEST_RESULT, status);
		setResult(status, intent);

	}

	private void onVolumeStateChangedInternal( VolumeInfo info){
		Log.d(TAG, "onVolumeStateChangedInternal: type:"+info.getType()+"\n" );
		if( info.getType() == VolumeInfo.TYPE_PUBLIC ){
			Log.d(TAG,  "  state:"+info.getState()+"\n");
			if( info.getState() == VolumeInfo.STATE_CHECKING ){
				Log.d(TAG, " checking"+"\n");
				otgTv.setText( getResources().getString( R.string.checking) );

			}else if(info.getState() == VolumeInfo.STATE_UNMOUNTED ){
				Log.d(TAG, " unmounted"+"\n");
				otgTv.setText( getResources().getString( R.string.unmounted) );

			}else  if( info.getState() == VolumeInfo.STATE_MOUNTED || info.getState() == VolumeInfo.STATE_MOUNTED_READ_ONLY ){
				Log.d(TAG," success"+"\n");
				showOtgStorage( info );

			}else{
				Log.d(TAG, " failed" +"\n");
				otgTv.setText( getResources().getString( R.string.failed) );
				setResultBeforeFinish( Utils.FAILED);
				finish();
			}


		}
	}
	
	private void showOtgStorage( VolumeInfo volumeInfo){
		VolumeRecord record = mStorageManager.findRecordByUuid( volumeInfo.getFsUuid());
		DiskInfo  diskInfo = volumeInfo.getDisk();
		Log.d(TAG, diskInfo.getDescription().toString() );
		if( !record.isSnoozed() || !diskInfo.isAdoptable() ){
			Log.d(TAG,"success"+"\n");
			otgTv.setText( getResources().getString( R.string.pass) );
			setResultBeforeFinish( Utils.SUCCESS);
			finish();
		}
	}


	protected void onDestory(){
		super.onDestroy();
	}

	private void registerUSBDeviceReceiver() {
//		mOTGReceiver = new OTGReceiver();
		mUsbReceiver = new UsbReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction( ACTION_USB_DEVICE_ATTACHED );
		intentFilter.addAction( ACTION_USB_DEVICE_DETACHED );
		registerReceiver(mUsbReceiver, intentFilter);
	}

	public  class UsbReceiver extends  BroadcastReceiver{

		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive: "+intent.getAction());
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				otgTv.setText( context.getResources().getString( R.string.usb_device_attached) );
				mStorageManager = (StorageManager) getSystemService("storage");
				mStorageManager.registerListener( mListener);
			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				otgTv.setText( context.getResources().getString( R.string.usb_device_detected) );
				mStorageManager = (StorageManager) getSystemService("storage");
				mStorageManager.unregisterListener( mListener);
			}
		}
	}


}
