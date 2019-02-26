package waterworld.com.factorytest.activitys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import waterworld.com.factorytest.FactoryActivity;
import waterworld.com.factorytest.R;
import waterworld.com.factorytest.Utils;

public class MicTest extends FactoryActivity implements OnClickListener {
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REPLAY = 215; //fangfengfan add
    private static final String TAG = Utils.TAG + "MicTest";
    private static String mFileName = null;
    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;
    private PlayButton mPlayButton = null;
    private MediaPlayer mPlayer = null;
    private BroadcastReceiver mMicTestReceiver = null;

    private Button success_Button = null;
    private Button fail_Button = null;
    //fangfengfan add start
    private Button replay_Button = null;
    private Button key_Button = null;
    private Button reconding_Button = null;
    //fangfengfan add end

    private Handler mmmyHandler = null;
    private Handler mmmyyHandler = null;
    private Handler resetHandler = null;
    private int old_volume = -1;
    static int CameraTestStatus = 0;
    TextView tx_mic;
    TextView tx_loud;
    TextView tx_sp;
    TextView tx_mic_e;
    AudioManager audioManager;
    Timer mTimer;
    int flags = 0;
    Timer mTimer1;
    int flags1 = 0;
    MediaPlayer mediaPlayer;

    //fangfengfan add start
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REPLAY:
                    stopPlaying();
                    replay_Button.setEnabled(true);
                    tx_mic_e.setText(R.string.values_mic_e3);
                    break;
            }
        }

        ;
    };
    //fangfengfan add end

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();

        success_Button.setEnabled(true);
        fail_Button.setEnabled(true);

        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        //fangfengfan add start
        //replay_Button.setVisibility(View.VISIBLE);
        //fangfengfan add end
    }

    private void startRecording() {
        //fangfengfan add start
        //replay_Button.setVisibility(View.GONE);
        File file = new File(mFileName);
        if (file.exists()) {
            file.delete();
        }
        //fangfengfan add end
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        success_Button.setEnabled(false);
        //fail_Button.setEnabled(false);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        try {
            mRecorder.start();
        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText(MicTest.this, getString(R.string.msg_sdcard_new),
                    Toast.LENGTH_SHORT).show();
            CameraTestStatus = -1;

        }
    }

    private void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {

                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    public MicTest() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();

        mFileName += "/audiorecordtest.3gp";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case 79:
                //add your code here
                key_Button.setVisibility(View.GONE);

        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // 构造界面
        setContentView(R.layout.test_result_mic);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        success_Button = (Button) findViewById(R.id.Button_Success);
        fail_Button = (Button) findViewById(R.id.Button_Fail);
        success_Button.setOnClickListener(this);
        fail_Button.setOnClickListener(this);
        audioManager = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        success_Button.setEnabled(false);
//		tx_mic = (TextView) findViewById(R.id.tx_mic);
//		tx_loud = (TextView) findViewById(R.id.tx_loud);
        tx_mic_e = (TextView) findViewById(R.id.tx_mic_e);
//		tx_sp = (TextView) findViewById(R.id.tx_sp);
        tx_mic_e.setTextColor(Color.GREEN);

        //fangfengfan add start
        replay_Button = (Button) findViewById(R.id.Button_Replay);
        key_Button = (Button) findViewById(R.id.Button_key);
        reconding_Button = (Button) findViewById(R.id.Button_reconding);
        replay_Button.setEnabled(false);

        replay_Button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                reconding_Button.setEnabled(false);
                replay_Button.setEnabled(false);
                startPlaying();
                tx_mic_e.setText(R.string.values_mic_e2);
                mTimer1 = new Timer();
                TimerTask mTask1 = new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Message message = new Message();
                        mmmyyHandler.sendMessage(message);
                    }
                };
                mTimer1.schedule(mTask1, 4000);
            }
        });
        reconding_Button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                reconding_Button.setEnabled(false);
                replay_Button.setEnabled(false);
                tx_mic_e.setVisibility(View.VISIBLE);
                tx_mic_e.setText(R.string.values_mic_e);
                onRecord(true);
                mTimer1 = new Timer();
                TimerTask mTask1 = new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Message message = new Message();
                        mmmyHandler.sendMessage(message);
                    }
                };
                mTimer1.schedule(mTask1, 4000);
                //startRecording();

            }
        });
        //fangfengfan add end
        resetHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                //flags1 = 1;
                //tx_mic_e.setText(R.string.values_mic_e1);
                if (null != mTimer1)
                    mTimer1.cancel();
                onRecord(false);
                onPlay(false);
                tx_mic_e.setText(R.string.msg_masse);
                tx_mic_e.setTextColor(Color.BLACK);
                tx_mic_e.setVisibility(View.VISIBLE);
                replay_Button.setEnabled(false);
                reconding_Button.setEnabled(false);
                super.handleMessage(msg);
            }
        };
        mmmyHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                flags1 = 1;
                tx_mic_e.setText(R.string.values_mic_e1);
                onRecord(false);
                replay_Button.setEnabled(true);
                reconding_Button.setEnabled(true);
                mTimer1.cancel();
                super.handleMessage(msg);
            }

        };

        mmmyyHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                onPlay(false);
                tx_mic_e.setText(R.string.values_mic_e3);
                tx_mic_e.setTextColor(Color.BLACK);
                replay_Button.setEnabled(true);
                reconding_Button.setEnabled(true);
                super.handleMessage(msg);
            }

        };
        if (!audioManager.isWiredHeadsetOn()) {
            Intent mIntent1 = getIntent();
            boolean fl1 = mIntent1.getBooleanExtra("textall", false);

            final String text = getString(R.string.msg_masse);

            //ToastUtil mToastUtil = new ToastUtil();
            //mToastUtil.showMessage(MicTest.this, text);
            View toastRoot = getLayoutInflater().inflate(R.layout.toast, null);
            TextView message = (TextView) toastRoot.findViewById(R.id.message);
            message.setText(text);

            Toast toastStart = new Toast(this);
            toastStart.setGravity(Gravity.BOTTOM, 0, 110);
            if (fl1) {
                toastStart.setDuration(Toast.LENGTH_LONG);
            } else {
                toastStart.setDuration(Toast.LENGTH_SHORT);
            }
            toastStart.setView(toastRoot);
            toastStart.show();

            CameraTestStatus = -1;

			/*if(fl1 == true){
				Intent miIntent = new Intent();
				miIntent.setAction("com.ykq.intent.action.PHONEMIC_TEST");
				miIntent.putExtra("textall", true);
				startActivityForResult(miIntent, RESULT_OK);
			}*/
            if (!fl1) {
                finish();
                return;
            }
            reconding_Button.setEnabled(false);
            replay_Button.setEnabled(false);
            //registerHeadsetPlugReceiver();
        }
        registerHeadsetPlugReceiver();
    }

    private void MicStart() {

        tx_mic_e.setVisibility(View.VISIBLE);
        success_Button.setEnabled(true);
        onRecord(true);

        mTimer1 = new Timer();
        TimerTask mTask1 = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (flags1 == 0) {
                    Message message = new Message();
                    mmmyHandler.sendMessage(message);
                } else if (flags1 == 1) {
                    flags1 = 2;
                    Message message = new Message();
                    mmmyyHandler.sendMessage(message);
                    mTimer1.cancel();
                }
            }
        };
        mTimer1.schedule(mTask1, 4000, 4000);
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
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

    protected void onDestroy() {
        // // TODO Auto-generated method stub
        mHandler.removeMessages(REPLAY);//fangfengfan add
        if (null != mMicTestReceiver) {
            unregisterReceiver(mMicTestReceiver);
        }
        if (old_volume != -1) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    old_volume, AudioManager.FLAG_PLAY_SOUND);
        }
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mTimer1 != null) {
            mTimer1.cancel();
        }
        if (audioManager != null) {
            audioManager.setSpeakerphoneOn(false);
        }
        File file = new File(mFileName);
        file.delete();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            CameraTestStatus = 1;
        } else {
            CameraTestStatus = -1;
        }

        super.onResume();
    }

    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Activity暂停时释放录音和播放对象
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void registerHeadsetPlugReceiver() {
        mMicTestReceiver = new MicTestReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(mMicTestReceiver, intentFilter);
    }

    public class MicTestReceiver extends HeadsetPlugReceiver {

        private static final String TAG = "MicTestReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                //Log.d("YDS","state="+intent.getIntExtra("state", 0));
                if (intent.getIntExtra("state", 0) == 0) {
                    // Toast.makeText(context, "headset not connected", Toast.LENGTH_SHORT).show();
                    //Log.d("YDS","AA");
                    if (!audioManager.isWiredHeadsetOn())
                        resetHandler.sendEmptyMessage(0);

                } else if (intent.getIntExtra("state", 0) == 1) {
                    //MicStart();
                    //Log.d("YDS","BB");
                    old_volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                    reconding_Button.setEnabled(true);
                    tx_mic_e.setVisibility(View.INVISIBLE);
                    //unregisterReceiver(mMicTestReceiver);
                    //mMicTestReceiver=null;
                    // Toast.makeText(context, "headset connected", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "mic");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }
}
