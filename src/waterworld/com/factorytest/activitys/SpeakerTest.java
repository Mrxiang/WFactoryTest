package waterworld.com.factorytest.activitys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
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

import waterworld.com.factorytest.FactoryActivity;
import waterworld.com.factorytest.R;
import waterworld.com.factorytest.Utils;

//import com.mediatek.common.featureoption.FeatureOption;

//import com.mediatek.FactoryTest.LoudSpeakerTest.ToastUtil;
public class SpeakerTest extends FactoryActivity implements View.OnClickListener {

    private BroadcastReceiver mSpeakerTestReceiver = null;
    static int CameraTestStatus = 0;
    private static String TAG = Utils.TAG +"SpeakerTest";
    MediaPlayer mediaPlayer;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_result);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//		     audioManager.setSpeakerphoneOn(false);
//		     audioManager.setWiredHeadsetOn(false);

        if (audioManager.isWiredHeadsetOn()) {
            Intent mIntent = getIntent();
            boolean fl = mIntent.getBooleanExtra("textall", false);

            String text = getString(R.string.msg_masse_no);

            //ToastUtil mToastUtil = new ToastUtil();
            // mToastUtil.showMessage(SpeakerTest.this, text);
            View toastRoot = getLayoutInflater().inflate(R.layout.toast, null);
            TextView message = (TextView) toastRoot.findViewById(R.id.message);
            message.setText(text);

            Toast toastStart = new Toast(this);
            toastStart.setGravity(Gravity.BOTTOM, 0, 110);
            if (fl) {
                toastStart.setDuration(Toast.LENGTH_LONG);
            } else {
                toastStart.setDuration(Toast.LENGTH_SHORT);
            }
            toastStart.setView(toastRoot);
            toastStart.show();

//		    	 Toast.makeText(SpeakerTest.this, text, Toast.LENGTH_SHORT).show();
            CameraTestStatus = -1;

					/*if(fl == true){
						Intent miIntent = new Intent();
						miIntent.setAction("com.ykq.intent.action.HALL_TEST");
						miIntent.putExtra("textall", true);
						startActivityForResult(miIntent, RESULT_OK);
					}*/

            if (!fl) {
                finish();
                return;
            }
            registerHeadsetPlugReceiver();

        } else {
            speakerStart();
        }

        Button success_Button = (Button) findViewById(R.id.Button_Success);
        Button fail_Button = (Button) findViewById(R.id.Button_Fail);
        success_Button.setOnClickListener(this);
        fail_Button.setOnClickListener(this);
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
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(false);

        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FLAG_SHOW_UI);//AudioManager.FLAG_PLAY_SOUND);//wlf add 20130820
        //audioManager.adjustStreamVolume (AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);

        AudioSystem.setMasterVolume(1.0f);
        mediaPlayer = createMediaPlayer(R.raw.testaudio_music);

        mediaPlayer.setLooping(true);
	   /*   Uri myUri1 = Uri.parse("file:///data/data/test.mp3");
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.setDataSource(getApplicationContext(), myUri1);
        } catch (Exception e) {}
	
	mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
	    try{
	    	 mediaPlayer.prepare();
	     }catch (Exception e) {
			// TODO: handle exception
		 }*/
        mediaPlayer.start();

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
        setResultBeforeFinish( CameraTestStatus );
        finish();
    }
	
}
