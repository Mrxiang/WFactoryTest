package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

import java.util.Random;


public class LoudSpeakerTest extends FactoryActivity  implements View.OnClickListener{

	private static final String TAG =Utils.TAG+"LoudSpeakerTest" ;
	private BroadcastReceiver 	mLoudSpeakerTestReceiver = null;
	private static int 			LoadSpeakerStatus = 0;
	private MediaPlayer 		mediaPlayer;
	private AudioManager 		audioManager;
	private int 				old_volume=-1;//add by yds

	private int  MusicArray[]={R.raw.male, R.raw.female, R.raw.qualsound};
	private int musicIndex;
	private int musicSeed=3;

	private Button     maleBtn;
	private Button     femaleBtn;
	private Button     pureBtn;
	private TextView     speakingText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.loud_speaker_test);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		if(audioManager.isWiredHeadsetOn()){

			String text = getString(R.string.msg_masse_no);
			View toastRoot = getLayoutInflater().inflate(R.layout.toast, null);
			TextView message = (TextView) toastRoot.findViewById(R.id.message);
			message.setText(text);

			Toast toastStart = new Toast(this);
			toastStart.setGravity(Gravity.BOTTOM, 0, 110);
			toastStart.setDuration(Toast.LENGTH_LONG);
			toastStart.setView(toastRoot);
			toastStart.show();
			registerHeadsetPlugReceiver();

		}else{
			loudSpeakerStart(  );
		}
//		fail_Button.setOnClickListener(this);
	}
	private void loudSpeakerStart( ){

		musicIndex = new Random().nextInt(musicSeed);
		Log.d(TAG, "loudSpeakerStart: "+musicIndex);
		old_volume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//add by yds

		audioManager.setSpeakerphoneOn(true);

		mediaPlayer = MediaPlayer.create(this, MusicArray[musicIndex]);
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
		mediaPlayer.setLooping( true );
		updateBackground();
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
			mediaPlayer.reset();
			mediaPlayer.release();
			audioManager.setSpeakerphoneOn(false);
			audioManager.setMode(AudioManager.MODE_NORMAL);
		}
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LoadSpeakerStatus = 0;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.male_btn:
				if( musicIndex == 0) {
					LoadSpeakerStatus = 1;
				}else{
					LoadSpeakerStatus = -1;
				}
				break;
			case R.id.female_btn:
				if( musicIndex == 1) {
					LoadSpeakerStatus = 1;
				}else{
					LoadSpeakerStatus = -1;
				}
				break;
			case R.id.pure_btn:
				if( musicIndex == 2) {
					LoadSpeakerStatus = 1;
				}else{
					LoadSpeakerStatus = -1;
				}
				break;
			case R.id.Button_Fail:
				LoadSpeakerStatus = -1;
				break;
			default:
				break;
		}
		setResultBeforeFinish( LoadSpeakerStatus );
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

	public void updateBackground( ){
		maleBtn = findViewById( R.id.male_btn);
		femaleBtn= findViewById( R.id.female_btn);
		pureBtn= findViewById( R.id.pure_btn);
		speakingText = findViewById( R.id.speaking_text);

		maleBtn.setBackground(new ColorDrawable(Color.GREEN));
		femaleBtn.setBackground(new ColorDrawable(Color.GREEN));
		pureBtn.setBackground(new ColorDrawable(Color.GREEN));
		speakingText.setText( R.string.speaking_text);
	}

	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onBackPressed: ");
	}
}
