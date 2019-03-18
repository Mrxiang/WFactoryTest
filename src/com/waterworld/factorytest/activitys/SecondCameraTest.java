package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

public class SecondCameraTest extends FactoryActivity implements View.OnClickListener{

	private static final String TAG = Utils.TAG+"SecondCameraTest";
	public static int SecondCameraTestStatus = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.test_result_second_camera_front);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Button success_Button =(Button) findViewById(R.id.Button_Success);
	    Button fail_Button = (Button) findViewById(R.id.Button_Fail);
		
	    success_Button.setOnClickListener(this);
	    fail_Button.setOnClickListener(this);

		showRemindDilog();	
	}
	private void openSystemCamera(){
		try{
			Intent PhoneTestIntent = new Intent();
			PhoneTestIntent.setClassName("com.mediatek.camera", "com.android.camera.CameraLauncher");
			startActivity(PhoneTestIntent);
		} catch (Exception eeee) {
			try{
				Intent PhoneTestIntent = new Intent();
				PhoneTestIntent.setClassName("com.android.gallery3d", "com.android.camera.VideoCamera");
				PhoneTestIntent.putExtra("lyaotao", 1);
				startActivity(PhoneTestIntent);
			} catch (Exception e) {
				try{
					Intent PhoneTestIntent = new Intent();
					PhoneTestIntent.setClassName("com.google.android.GoogleCamera", "com.android.camera.CameraLauncher");
					PhoneTestIntent.putExtra("lyaotao", 1);
					startActivity(PhoneTestIntent);
				} catch(Exception ee) {
					try{
						Intent PhoneTestIntent = new Intent();
						PhoneTestIntent.setClassName("com.yunos.camera", "com.yunos.camera.CameraActivity");
						PhoneTestIntent.putExtra("lyaotao", 1);
						startActivity(PhoneTestIntent);
					} catch(Exception eee) {
						try{
							Intent PhoneTestIntent = new Intent();
							PhoneTestIntent.setClassName("com.mediatek.camera", "com.mediatek.camera.CameraLauncher");
							PhoneTestIntent.putExtra("lyaotao", 1);
							startActivity(PhoneTestIntent);
						} catch(Exception ex) {
							SecondCameraTestStatus = -1;
							finish();
						}
					}
				}
			}
		}
	}

	private void showRemindDilog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.remind);
        builder.setMessage(R.string.remind_content);
		builder.setPositiveButton(com.android.internal.R.string.ok, new DialogInterface.OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog, int which) {
				  dialog.dismiss();
				  openSystemCamera();
			  }
		  });
		builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        switch (v.getId()) {
			case R.id.Button_Success:
				SecondCameraTestStatus  = 1;
				break;
			case R.id.Button_Fail:
				SecondCameraTestStatus = -1;
				break;
			default:
				break;
		}
		setResultBeforeFinish( SecondCameraTestStatus );
		finish();
	}

	@Override
	public void setResultBeforeFinish(int status) {
		Log.d(TAG, "setResultBeforeFinish: " + status);
		Intent intent = new Intent();
		intent.putExtra(Utils.NAME, "secondCamera");
		intent.putExtra(Utils.TEST_RESULT, status);
		setResult(status, intent);

	}
}
