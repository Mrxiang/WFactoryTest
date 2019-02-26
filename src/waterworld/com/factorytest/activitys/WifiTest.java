package waterworld.com.factorytest.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import waterworld.com.factorytest.FactoryActivity;
import waterworld.com.factorytest.R;
import waterworld.com.factorytest.Utils;

public class WifiTest extends FactoryActivity {
    private static final String TAG = Utils.TAG + "WifiTest";
    /**
     * Called when the activity is first created.
     */
    private TextView allNetWork;
    private Button scan;
    private Button start;
    private Button stop;
    private Button check;
    private WifiAdmin mWifiAdmin;
    static int CameraTestStatus = 0;
    Button success_Button;
    private List<ScanResult> list;
    private ScanResult mScanResult;
    private StringBuffer sb = new StringBuffer();
    Timer mTimer;
    static boolean flags;
    static boolean flags1 = false;
    static boolean flags2 = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_result_wifi);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mWifiAdmin = new WifiAdmin(WifiTest.this);
        if (mWifiAdmin.isWifiOpen()) {
            flags = true;
            mWifiAdmin.openWifi();
        } else {
            flags = false;
            mWifiAdmin.openWifi();
        }
        init();
    }

    public void init() {
        allNetWork = (TextView) findViewById(R.id.allNetWork);
        success_Button = (Button) findViewById(R.id.Button_Success);
        Button fail_Button = (Button) findViewById(R.id.Button_Fail);
        success_Button.setEnabled(false);
        success_Button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CameraTestStatus = 1;
                setResultBeforeFinish( CameraTestStatus );
                finish();
            }
        });
        fail_Button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CameraTestStatus = -1;
                setResultBeforeFinish( CameraTestStatus );
                finish();
            }
        });


        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (mWifiAdmin.isWifiOpen()) {
                    if (sb != null) {
                        sb = new StringBuffer();
                    }
                    mWifiAdmin.startScan();
                    list = mWifiAdmin.getWifiList();
                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            mScanResult = list.get(i);
                            sb = sb.append(mScanResult.BSSID + "\n").append(mScanResult.SSID + "\n")
                                    .append(mScanResult.capabilities + "\n").append(mScanResult.frequency + "\n")
                                    .append(mScanResult.level + "\n\n");
                            if (mScanResult.SSID.equals("WIFI-1") || mScanResult.SSID.equals("WIFI-2")
                                    || mScanResult.SSID.equals("WIFI-3") || mScanResult.SSID.equals("WIFI-4")
                                    || mScanResult.SSID.equals("WIFI-5") || mScanResult.SSID.equals("WIFI-6")
                                    || mScanResult.SSID.equals("WIFI-7") || mScanResult.SSID.equals("WIFI-8")
                                    || mScanResult.SSID.equals("WIFI-9") || mScanResult.SSID.equals("WIFI-10")) {
                                flags1 = true;
                            }
                            if (mScanResult.level >= -60) {
                                flags2 = true;
                            }
                        }
                        allNetWork.setText(getString(R.string.values_wifidang) + "\n" + sb.toString());
                    }
//				    	mWifiAdmin.closeWifi();
                    if (!allNetWork.getText().equals(getString(R.string.values_dangqian))) {
                        success_Button.setEnabled(true);
                        if (flags1 == true && flags2 == true) {

                            Intent mIntent1 = getIntent();
                            boolean fl = mIntent1.getBooleanExtra("textall", false);
                            if (fl == true) {
                                Intent miIntent = new Intent();
                                miIntent.setAction("com.ykq.intent.action.BT_TEST");
                                miIntent.putExtra("textall", true);
                                startActivityForResult(miIntent, RESULT_OK);
                            }
                            finish();
                        }

                    } else {
                    }

                } else {
//							allNetWork.setText(getString(R.string.values_dangqian));
//							mTimer.cancel();
//							Intent mIntent = getIntent();
//							boolean fl = mIntent.getBooleanExtra("textall", false);
//							if(fl == true){
//								Intent miIntent = new Intent();
//								miIntent.setAction("com.ykq.intent.action.BT_TEST");
//								miIntent.putExtra("textall", true);
//								startActivityForResult(miIntent, RESULT_OK);
//								
//							}
//							finish();
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
                Log.e("lyaotao", "111");
            }
        };

        mTimer.schedule(mTask, 2000, 1000);

    }

    public void getAllNetWorkList() {
        if (sb != null) {
            sb = new StringBuffer();
        }
        mWifiAdmin.startScan();
        list = mWifiAdmin.getWifiList();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                mScanResult = list.get(i);
                sb = sb.append(mScanResult.BSSID + "\n").append(mScanResult.SSID + "\n")
                        .append(mScanResult.capabilities + "\n").append(mScanResult.frequency + "\n")
                        .append(mScanResult.level + "\n\n");
            }
            allNetWork.setText(getString(R.string.values_wifidang) + "\n" + sb.toString());
        }
    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (!flags) {
            if (mWifiAdmin != null) {
                mWifiAdmin.closeWifi();
            }
        } else {
            if (mWifiAdmin != null) {
                mWifiAdmin.openWifi();
            }
        }

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

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        CameraTestStatus = -1;
    }


    public class WifiAdmin {
        private WifiManager mWifiManager;
        private WifiInfo mWifiInfo;
        private List<ScanResult> mWifiList;
        private List<WifiConfiguration> mWifiConfigurations;
        WifiLock mWifiLock;

        public WifiAdmin(Context context) {
            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mWifiInfo = mWifiManager.getConnectionInfo();
        }

        public void openWifi() {
            if (!mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
            }
        }

        public void closeWifi() {
            mWifiManager.setWifiEnabled(false);
        }

        public boolean isWifiOpen() {
            return mWifiManager.isWifiEnabled();
        }

        public int checkState() {
            return mWifiManager.getWifiState();
        }

        public void acquireWifiLock() {
            mWifiLock.acquire();
        }

        public void releaseWifiLock() {
            if (mWifiLock.isHeld()) {
                mWifiLock.acquire();
            }
        }

        public void createWifiLock() {
            mWifiLock = mWifiManager.createWifiLock("test");
        }

        public List<WifiConfiguration> getConfiguration() {
            return mWifiConfigurations;
        }

        public void connetionConfiguration(int index) {
            if (index > mWifiConfigurations.size()) {
                return;
            }
            mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
        }

        public void startScan() {
            mWifiManager.startScan();
            mWifiList = mWifiManager.getScanResults();
            mWifiConfigurations = mWifiManager.getConfiguredNetworks();
        }

        public List<ScanResult> getWifiList() {
            return mWifiList;
        }

        public StringBuffer lookUpScan() {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mWifiList.size(); i++) {
                sb.append("Index_" + new Integer(i + 1).toString() + ":");
                sb.append((mWifiList.get(i)).toString()).append("\n");
            }
            return sb;
        }

        public String getMacAddress() {
            return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
        }

        public String getBSSID() {
            return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
        }

        public int getIpAddress() {
            return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
        }

        public int getNetWordId() {
            return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
        }

        public String getWifiInfo() {
            return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
        }

        public void addNetWork(WifiConfiguration configuration) {
            int wcgId = mWifiManager.addNetwork(configuration);
            mWifiManager.enableNetwork(wcgId, true);
        }

        public void disConnectionWifi(int netId) {
            mWifiManager.disableNetwork(netId);
            mWifiManager.disconnect();
        }
    }


    public void setResultBeforeFinish(int status ){
        Log.d(TAG, "setResultBeforeFinish: "+status );
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "wifi");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }
}




