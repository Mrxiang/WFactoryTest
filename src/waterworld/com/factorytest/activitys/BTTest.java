package waterworld.com.factorytest.activitys;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import waterworld.com.factorytest.FactoryActivity;
import waterworld.com.factorytest.R;
import waterworld.com.factorytest.Utils;

public class BTTest extends FactoryActivity implements OnClickListener {


    private static final String TAG = Utils.TAG + "BTTest";
    static int CameraTestStatus = 0;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    IntentFilter filter1;
    IntentFilter filter2;
    SimpleAdapter mAdapter;
    String name;
    String address;
    short rssi;
    ListView lv;
    Button success_Button;
    BluetoothAdapter adapter;
    Timer mTimer;
    static boolean flags;
    static boolean flags1 = false;
    static boolean flags2 = false;
    private List<Map<String, Object>> list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_result_bt);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        adapter = BluetoothAdapter.getDefaultAdapter();
        filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        list = new ArrayList<Map<String, Object>>();

        if (adapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
            Toast.makeText(BTTest.this, getString(R.string.msg_btwait), Toast.LENGTH_SHORT).show();
            finish();
        }


        if (adapter.isEnabled()) {
            flags = true;
        } else {
            flags = false;
            adapter.enable();
        }
//		      Button bt_on = (Button)findViewById(R.id.Button_bton);
//		      Button bt_off = (Button)findViewById(R.id.Button_btoff);
        lv = (ListView) findViewById(R.id.list_address);

        success_Button = (Button) findViewById(R.id.Button_Success);
        Button fail_Button = (Button) findViewById(R.id.Button_Fail);
        success_Button.setOnClickListener(this);
        fail_Button.setOnClickListener(this);
        success_Button.setEnabled(false);


        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {

                if (adapter.isEnabled()) {
                    //adapter.enable();
                    adapter.startDiscovery();
                    registerReceiver(mReceiver, filter1);


                } else {



                }


            }
        };
        mTimer = new Timer();
        TimerTask mTask = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                Message msg = new Message();

                handler.sendMessage(msg);
                Log.e("lyaotao", "222");
            }
        };

        //mTimer.schedule(mTask, 2000, 1000);
        mTimer.schedule(mTask, 4000);//by yds

//	}
    }

    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
//		super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        mTimer.cancel();
        if (!flags) {
            if (adapter != null) {
                adapter.disable();
            }
        } else {
            if (adapter != null) {
                adapter.enable();
            }
        }

        super.onDestroy();
    }

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        CameraTestStatus = -1;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.Button_Success:
                CameraTestStatus = 1;
                if (!flags) {
                    adapter.disable();
                    adapter.cancelDiscovery();
                }
                if (mTimer != null) {
                    mTimer.cancel();
                }
                break;
            case R.id.Button_Fail:
                CameraTestStatus = -1;
                if (!flags) {
                    adapter.disable();
                    adapter.cancelDiscovery();
                }
                if (mTimer != null) {
                    mTimer.cancel();
                }
                break;
            default:
                break;
        }
        setResultBeforeFinish( CameraTestStatus );
        finish();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//		             if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                name = device.getName();
                address = device.getAddress();
                rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
//		            	                } 

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", name);
                map.put("address", address);
                map.put("rssi", rssi);
                if (name != null) {
                    if (name.equals("BT-1") || name.equals("BT-2")
                            || name.equals("BT-3") || name.equals("BT-4")
                            || name.equals("BT-5") || name.equals("BT-6")
                            || name.equals("BT-7") || name.equals("BT-8")
                            || name.equals("BT-9") || name.equals("BT-10")) {
                        flags1 = true;
                    }
                    if (rssi >= -60) {
                        flags2 = true;
                    }
                }
                list.add(map);
                mAdapter = new SimpleAdapter(BTTest.this, list, R.layout.simple,
                        new String[]{"name", "address", "rssi"},
                        new int[]{R.id.tv_name_value, R.id.tv_address_value, R.id.tv_rssi_value});
                lv.setAdapter(mAdapter);
            }
            if (!"".equals(name) && !"".equals(address)) {
                if (list.size() != 0) {
                    success_Button.setEnabled(true);
                    if ((flags1 == true) && (true == flags2)) {
                        Intent mIntent1 = getIntent();
                        boolean fl = mIntent1.getBooleanExtra("textall", false);
                        if (fl == true) {
                            CameraTestStatus = 1;
                            if (!flags) {
                                adapter.disable();
                                adapter.cancelDiscovery();
                            }
                            if (mTimer != null) {
                                mTimer.cancel();
                            }
                            Intent miIntent = new Intent();
                            miIntent.setAction("com.ykq.intent.action.PHONE_TEST");
                            miIntent.putExtra("textall", true);
                            startActivityForResult(miIntent, RESULT_OK);
                            finish();
                        }
                    }
//		        	CameraTestStatus  = 1;
                } else {
//		        	CameraTestStatus  = -1;
                }
            }

        }
    };

    public void setResultBeforeFinish( int status ){
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "bluetooth");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }
}

