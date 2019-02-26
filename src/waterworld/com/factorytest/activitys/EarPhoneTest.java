package waterworld.com.factorytest.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import waterworld.com.factorytest.R;

public class EarPhoneTest extends Activity implements View.OnClickListener{

	static int CameraTestStatus = 0;
	MediaPlayer mediaPlayer;
	private boolean flag=true;
		@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.test_result);
		     final AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
//		     audioManager.setMicrophoneMute(true);
		if(audioManager.isWiredHeadsetOn()){
				if(android.os.SystemProperties.getBoolean("ro.factorytest_EarPhone", false)){
					mediaPlayer = MediaPlayer.create(this, R.raw.test1);
					}
		        
     			else{
		     		mediaPlayer = MediaPlayer.create(this, R.raw.testaudio); 
                     }
		     mediaPlayer.start();
		     }else{
		    	 new AlertDialog.Builder(this)
		    	 .setTitle(R.string.msg_title)
		    	 .setMessage(R.string.msg_masse)
		    	 .setPositiveButton(R.string.msg_ok, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(audioManager.isWiredHeadsetOn()){
							if(android.os.SystemProperties.getBoolean("ro.factorytest_EarPhone", false)){
								mediaPlayer = MediaPlayer.create(EarPhoneTest.this, R.raw.test1);
															}
		        
     							else{
		     						mediaPlayer = MediaPlayer.create(EarPhoneTest.this, R.raw.testaudio); }
						     mediaPlayer.start();
						     }else{
						    	 Intent intent = new Intent(); 
						     intent.setAction("com.ykq.intent.action.FACTORY_TEST_MAIN");
						     setResult(RESULT_OK,intent);
						     CameraTestStatus = -1;
						     Intent mIntent = getIntent();
								boolean fl = mIntent.getBooleanExtra("textall", false);
								if(fl == true){
									Intent miIntent = new Intent();
									if(android.os.SystemProperties.getBoolean("ro.factorytest_EarPhone", false)){
										miIntent.setAction("com.ykq.intent.action.ALL_TEST");
										miIntent.putExtra("textall", true);
										startActivityForResult(miIntent, RESULT_OK);
									}
									else{
										miIntent.setAction("com.ykq.intent.action.KEY_TEST");
										miIntent.putExtra("textall", true);
										startActivityForResult(miIntent, RESULT_OK);
									}
								}
						     
						     finish();
						     }
					}
				})
				.create().show();
		     }
	       Button success_Button =(Button) findViewById(R.id.Button_Success);
	       Button fail_Button = (Button) findViewById(R.id.Button_Fail);
	       success_Button.setOnClickListener(this);
	       fail_Button.setOnClickListener(this);
	}
	/*
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

  case 79:
   //add your code here
		if(flag==true){
		mediaPlayer.pause();
		flag=false;}
		else if(flag==false){
		mediaPlayer.start();
		flag=true;}
   
        }
        return super.onKeyUp(keyCode, event);
    }
    */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mediaPlayer != null){
			
		mediaPlayer.stop();
		}
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		CameraTestStatus = 0;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        Intent intent = new Intent(); 
        intent.setAction("com.ykq.intent.action.FACTORY_TEST_MAIN");
        setResult(RESULT_OK,intent);
		switch (v.getId()) {
		case R.id.Button_Success:
			CameraTestStatus  = 1;
			Intent mIntent = getIntent();
			boolean fl = mIntent.getBooleanExtra("textall", false);
			if(fl == true){
				Intent miIntent = new Intent();
				if(android.os.SystemProperties.getBoolean("ro.factorytest_EarPhone", false)){
				miIntent.setAction("com.ykq.intent.action.ALL_TEST");
				miIntent.putExtra("textall", true);
				startActivityForResult(miIntent, RESULT_OK);}
				else{
				miIntent.setAction("com.ykq.intent.action.KEY_TEST");
				miIntent.putExtra("textall", true);
				startActivityForResult(miIntent, RESULT_OK);
							}
			}
			break;
		case R.id.Button_Fail:
			CameraTestStatus = -1;
			Intent mIntent1 = getIntent();
			boolean fl1 = mIntent1.getBooleanExtra("textall", false);
			if(fl1 == true){
				Intent miIntent = new Intent();
				if(android.os.SystemProperties.getBoolean("ro.factorytest_EarPhone", false)){
				miIntent.setAction("com.ykq.intent.action.ALL_TEST");
				miIntent.putExtra("textall", true);
				startActivityForResult(miIntent, RESULT_OK);}
				else{
				miIntent.setAction("com.ykq.intent.action.KEY_TEST");
				miIntent.putExtra("textall", true);
				startActivityForResult(miIntent, RESULT_OK);
							}
			}
			break;
		default:
			break;
		}
		//intent.getExtras()
//		startActivity(intent);
		finish();
	}

}
