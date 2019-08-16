package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

//import com.mediatek.common.featureoption.FeatureOption;

//import com.mediatek.FactoryTest.LoudSpeakerTest.ToastUtil;
public class SpeakerTest extends FactoryActivity implements View.OnClickListener {

    private BroadcastReceiver mSpeakerTestReceiver = null;
    static int SpeakerTestStatus = 0;
    private static String TAG = Utils.TAG +"SpeakerTest";
    MediaPlayer mediaPlayer;
    AudioManager audioManager;

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

        setContentView(R.layout.speaker_test);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//		     audioManager.setSpeakerphoneOn(false);
//		     audioManager.setWiredHeadsetOn(false);

        if (audioManager.isWiredHeadsetOn()) {

            String text = getString(R.string.msg_masse_no);

            //ToastUtil mToastUtil = new ToastUtil();
            // mToastUtil.showMessage(SpeakerTest.this, text);
            View toastRoot = getLayoutInflater().inflate(R.layout.toast, null);
            TextView message = (TextView) toastRoot.findViewById(R.id.message);
            message.setText(text);

            Toast toastStart = new Toast(this);
            toastStart.setGravity(Gravity.BOTTOM, 0, 110);
                toastStart.setDuration(Toast.LENGTH_LONG);
            toastStart.setView(toastRoot);
            toastStart.show();

//		    	 Toast.makeText(SpeakerTest.this, text, Toast.LENGTH_SHORT).show();
            SpeakerTestStatus = -1;

            registerHeadsetPlugReceiver();

        } else {
            speakerStart();
        }

    }

    //wlf copy from MediaPlayer:create() 201040618.
    private MediaPlayer createMediaPlayer(int resid) {
        try {
            AssetFileDescriptor afd = getResources().openRawResourceFd(resid);
            if (afd == null) return null;

            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            //wangliangfu add .
            mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

            mp.prepare();
            return mp;
        } catch (IOException ex) {
//            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (SecurityException ex) {
//            Log.d(TAG, "create failed:", ex);
            // fall through
        }
        return null;
    }


    private void speakerStart() {
//        musicIndex = new Random().nextInt(musicSeed);
        musicIndex = 2;
        Log.d(TAG, "loudSpeakerStart: "+musicIndex);

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(false);

        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FLAG_PLAY_SOUND);//AudioManager.FLAG_PLAY_SOUND);//wlf add 20130820
        //audioManager.adjustStreamVolume (AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);

        AudioSystem.setMasterVolume(1.0f);
        mediaPlayer = createMediaPlayer( MusicArray[musicIndex] );
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        updateBackground();
    }

    private void registerHeadsetPlugReceiver() {
        mSpeakerTestReceiver = new SpeakerTestReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(mSpeakerTestReceiver, intentFilter);
    }

    @Override
    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "speaker");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }

    public class SpeakerTestReceiver extends HeadsetPlugReceiver {

        private static final String TAG = "SpeakerTestReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {
                    speakerStart();
                    // Toast.makeText(context, "headset not connected", Toast.LENGTH_SHORT).show();
                } else if (intent.getIntExtra("state", 0) == 1) {
                    // Toast.makeText(context, "headset connected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (null != mSpeakerTestReceiver) {
            unregisterReceiver(mSpeakerTestReceiver);
        }
        super.onDestroy();
        if (mediaPlayer != null && audioManager != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        SpeakerTestStatus = 0;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.male_btn:
                if( musicIndex == 0) {
                    SpeakerTestStatus = 1;
                }else{
                    SpeakerTestStatus = -1;
                }
                break;
            case R.id.female_btn:
                if( musicIndex == 1) {
                    SpeakerTestStatus = 1;
                }else{
                    SpeakerTestStatus = -1;
                }
                break;
            case R.id.pure_btn:
                if( musicIndex == 2) {
                    SpeakerTestStatus = 1;
                }else{
                    SpeakerTestStatus = -1;
                }
                break;
            case R.id.Button_Fail:
                SpeakerTestStatus = -1;
                break;
            default:
                break;
        }
        setResultBeforeFinish( SpeakerTestStatus );
        finish();
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


}
