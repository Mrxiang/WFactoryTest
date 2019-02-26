package waterworld.com.factorytest.activitys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import waterworld.com.factorytest.FactoryActivity;
import waterworld.com.factorytest.R;
import waterworld.com.factorytest.Utils;

public class LoudSpeakerTest extends FactoryActivity  implements View.OnClickListener{

	private static final String TAG =Utils.TAG+"LoudSpeakerTest" ;
	private BroadcastReceiver mLoudSpeakerTestReceiver = null;
	static int CameraTestStatus = 0;
	MediaPlayer mediaPlayer;
	AudioManager audioManager;
	private int old_volume=-1;//add by yds
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test_result);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		 audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		 if(audioManager.isWiredHeadsetOn()){
			 Intent mIntent1 = getIntent();
			 boolean fl1 = mIntent1.getBooleanExtra("textall", false);
				
	    	 String text = getString(R.string.msg_masse_no);
	    	// ToastUtil mToastUtil = new ToastUtil();
	    	// mToastUtil.showMessage(LoudSpeakerTest.this, text);
//	    	 Toast.makeText(LoudSpeakerTest.this, text, Toast.LENGTH_SHORT).show();
		 		View toastRoot = getLayoutInflater().inflate(R.layout.toast, null);
				TextView message = (TextView) toastRoot.findViewById(R.id.message);
				message.setText(text);

				Toast toastStart = new Toast(this);
				toastStart.setGravity(Gravity.BOTTOM, 0, 110);
				if (fl1){
					toastStart.setDuration(Toast.LENGTH_LONG);
				}else{
					toastStart.setDuration(Toast.LENGTH_SHORT);
				}
				toastStart.setView(toastRoot);
				toastStart.show();
				
				/*if(fl1 == true){
					Intent miIntent = new Intent();
					miIntent.setAction("com.ykq.intent.action.VIBRATE_TEST");
					miIntent.putExtra("textall", true);
					startActivityForResult(miIntent, RESULT_OK);
				}*/
				if (!fl1){
					CameraTestStatus = -1;
					finish();
			    	return;
				}
				registerHeadsetPlugReceiver();
				  	 
	     }else{
	    	 loudSpeakerStart();
	     }
	     Button success_Button =(Button) findViewById(R.id.Button_Success);
	     Button fail_Button = (Button) findViewById(R.id.Button_Fail);
	     success_Button.setOnClickListener(this);
	     fail_Button.setOnClickListener(this);
	}
	private void loudSpeakerStart(){
		
		old_volume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//add by yds
		
	     audioManager.setSpeakerphoneOn(true);
	     
	     mediaPlayer = MediaPlayer.create(this, R.raw.testaudio_music);
	     audioManager.setWiredHeadsetOn(false);
	     audioManager.setMicrophoneMute(false);
		 audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 
		 		audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);//AudioManager.FLAG_SHOW_UI);
	    

	     try{
	    	 mediaPlayer.prepare();
	     }catch (Exception e) {
			// TODO: handle exception
		 }
	     mediaPlayer.start();
	}

	private void registerHeadsetPlugReceiver() {
		mLoudSpeakerTestReceiver = new LoudSpeakerTestReceiver(); 
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(mLoudSpeakerTestReceiver, intentFilter);
	}



	public class LoudSpeakerTestReceiver extends HeadsetPlugReceiver {

    	private static final String TAG = "LoudSpeakerTestReceiver";

    	@Override
    	public void onReceive(Context context, Intent intent) {
    		  if (intent.hasExtra("state")){
    			   if (intent.getIntExtra("state", 0) == 0){
    				   loudSpeakerStart();
    				  // Toast.makeText(context, "headset not connected", Toast.LENGTH_SHORT).show();
    			   }
    			   else if (intent.getIntExtra("state", 0) == 1){
    				  // Toast.makeText(context, "headset connected", Toast.LENGTH_SHORT).show();
    			   }
    		  }
    	}
    }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//by yds start
		if(old_volume!=-1)
		{
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 
		 		old_volume, AudioManager.FLAG_PLAY_SOUND);
		}
		//by yds end
		
    	if (null != mLoudSpeakerTestReceiver){
    		unregisterReceiver(mLoudSpeakerTestReceiver);    		
    	}
		super.onDestroy();
		if(mediaPlayer != null){
		mediaPlayer.stop();
		audioManager.setSpeakerphoneOn(false);
		audioManager.setMode(AudioManager.MODE_NORMAL);
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
		switch (v.getId()) {
		case R.id.Button_Success:
			CameraTestStatus  = 1;
			break;
		case R.id.Button_Fail:
			CameraTestStatus = -1;
			break;
		default:
			break;
		}
		setResultBeforeFinish( CameraTestStatus );
		finish();
	}
	

	@Override
	public void setResultBeforeFinish(int status) {
		Log.d(TAG, "setResultBeforeFinish: " + status);
		Intent intent = new Intent();
		intent.putExtra(Utils.NAME, "loud_speaker");
		intent.putExtra(Utils.TEST_RESULT, status);
		setResult(status, intent);
	}
}
