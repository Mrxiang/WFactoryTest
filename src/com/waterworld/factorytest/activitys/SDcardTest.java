package com.waterworld.factorytest.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.FactoryDatas;
import com.waterworld.factorytest.FactoryTestFeatureoption.FeatureOption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
//import com.mediatek.common.featureoption.FeatureOption;
import com.waterworld.factorytest.R;

import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

public class SDcardTest extends FactoryActivity {


    private static final String TAG = Utils.TAG + "SDcardTest";
    private TextView hasSDTextView;

    private TextView SDPathTextView;
    static boolean flags1 = false;
    private TextView FILESpathTextView;

    private TextView createFileTextView;

    private TextView readFileTextView;

    private TextView deleteFileTextView;

    private TextView Button_Success;
    private TextView Button_Fail;

    private FileHelper helper;

    static int SDcardTestStatus = 0;

    private static final File DATA_DIRECTORY
            = getDirectory("ANDROID_DATA", "/data");

    byte[] mBuffer = new byte[1024];

    private StorageManager mStorageManager;


    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.sdcard_test);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        File path = getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();

        long availMem = readAvailMem();
        long allMem = readAllMem();

        TextView textView0 = (TextView) findViewById(R.id.textView0);
        final TextView textView1 = (TextView) findViewById(R.id.textView1);
        final TextView textView2 = (TextView) findViewById(R.id.textView2);
        final TextView textView3 = (TextView) findViewById(R.id.textView3);
        final TextView textView4 = (TextView) findViewById(R.id.textView4);

        final TextView textView0_result = (TextView) findViewById(R.id.textView0_result);
        final TextView tv_T_result = (TextView) findViewById(R.id.tv_T_result);


        textView0.setText(getString(R.string.values_memoryjie));

        if(FactoryDatas.parseSDCardFakeValue( this )){
            textView1.setText(getString(R.string.values_memoryzong) + "4GB");
            textView3.setText(getString(R.string.values_memorynei) + "64GB");

        }else{
            textView1.setText(getString(R.string.values_memoryzong) + formatFileSize(this, allMem));
//            textView3.setText(getString(R.string.values_memorynei) + formatFileSize(this, totalBlocks * blockSize));
            textView3.setText(getString(R.string.values_memorynei) + formatFileSize(this, getAllStorageSize(getBaseContext())));

        }
        textView2.setText(getString(R.string.values_memorysheng) + formatFileSize(this, availMem));
        textView4.setText(getString(R.string.values_memorymeisheng) + formatFileSize(this, availableBlocks * blockSize));
//	    RunningState mState;

        hasSDTextView = (TextView) findViewById(R.id.hasSDTextView);

//       SDPathTextView = (TextView) findViewById(R.id.SDPathTextView); 

//       FILESpathTextView = (TextView) findViewById(R.id.FILESpathTextView); 

        createFileTextView = (TextView) findViewById(R.id.createFileTextView);

//       readFileTextView = (TextView) findViewById(R.id.readFileTextView); 

        deleteFileTextView = (TextView) findViewById(R.id.deleteFileTextView);


        helper = new FileHelper(getApplicationContext());


//       hasSDTextView.setText(getString(R.string.values_sd1) + helper.hasSD()); 

//       SDPathTextView.setText(getString(R.string.values_sd2) + helper.getSDPATH()); 

//       FILESpathTextView.setText(getString(R.string.values_sd3) + helper.getFILESPATH()); 

        Button_Success = (TextView) findViewById(R.id.Button_Success);
        Button_Fail = (TextView) findViewById(R.id.Button_Fail);
        Button_Success.setEnabled(false);
        Button_Success.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                SDcardTestStatus = 1;
                setResultBeforeFinish(SDcardTestStatus);
                finish();
            }
        });
        Button_Fail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SDcardTestStatus = -1;
                setResultBeforeFinish(SDcardTestStatus);
                finish();
            }
        });


        try {
            if (getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName())
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if (FeatureOption.MTK_2SDCARD_SWAP)  // wanghe 2013-09-07
            {
                File createFile = helper.createSDFile("test.txt");

                if (null != createFile) {
                    createFileTextView.setText(getString(R.string.values_sd4)

                            + createFile.getAbsolutePath());
                }
            } else {
                createFileTextView.setText(getString(R.string.values_sd4)

                        + helper.createSDFile("test.txt").getAbsolutePath());
            }

        } catch (IOException e) {

            e.printStackTrace();
            createFileTextView.setText(getString(R.string.values_sd4) + "");

        } catch (Exception e) {

            e.printStackTrace();
        }

//       readFileTextView.setText(getString(R.string.values_sd6)+ helper.readSDFile("test.txt"));

        deleteFileTextView.setText(getString(R.string.values_sd5)

                + helper.deleteSDFile("test.txt"));
        hasSDTextView.setText(getString(R.string.values_sd1) + helper.hasSD());

//        if ((getString(R.string.values_sd5) + "true").equals(deleteFileTextView.getText().toString())) {
        if (helper.hasSD()) {
            tv_T_result.setText(R.string.Button_Status_Success);
            tv_T_result.setTextColor(Color.GREEN);
            flags1 = true;
        } else {
            tv_T_result.setText(R.string.Button_Status_Fail);
            tv_T_result.setTextColor(Color.RED);
        }
        if ((!(getString(R.string.values_memoryzong) + "").equals(textView1.getText())) &&
                (!(getString(R.string.values_memorysheng) + "").equals(textView2.getText())) &&
                (!(getString(R.string.values_memorynei) + "").equals(textView3.getText())) &&
                (!(getString(R.string.values_memorymeisheng) + "").equals(textView4.getText()))) {
            textView0_result.setText(R.string.Button_Status_Success);
            textView0_result.setTextColor(Color.GREEN);
        } else {
            textView0_result.setText(R.string.Button_Status_Fail);
            textView0_result.setTextColor(Color.RED);
        }


//        if ((getString(R.string.values_sd5) + "true").equals(deleteFileTextView.getText().toString()) &&
        if (helper.hasSD() &&
                (!(getString(R.string.values_memoryzong) + "").equals(textView1.getText())) &&
                (!(getString(R.string.values_memorysheng) + "").equals(textView2.getText())) &&
                (!(getString(R.string.values_memorynei) + "").equals(textView3.getText())) &&
                (!(getString(R.string.values_memorymeisheng) + "").equals(textView4.getText()))) {
            Button_Success.setEnabled(true);

        } else {
        }

        if ("1".equals(SystemProperties.get("ro.yk676_cf2_bsh18_hdp_tplink"))) {
           if( helper.hasSD() ){
                SDcardTestStatus = Utils.SUCCESS;
                setResultBeforeFinish( SDcardTestStatus );
                finish();
           }else{
               SDcardTestStatus = Utils.FAILED;
               setResultBeforeFinish( SDcardTestStatus );
               finish();
           }
        }
    }
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
//	super.onBackPressed();
    }

    public static File getDataDirectory() {
        return DATA_DIRECTORY;
    }

    static File getDirectory(String variableName, String defaultPath) {
        String path = System.getenv(variableName);
        return path == null ? new File(defaultPath) : new File(path);
    }


    public static String formatFileSize(Context context, long number) {
        return formatFileSize(context, number, false);
    }

    private static String formatFileSize(Context context, long number, boolean shorter) {
        if (context == null) {
            return "";
        }

        float result = number;
        int suffix = com.android.internal.R.string.byteShort;
        if (result > 900) {
            suffix = com.android.internal.R.string.kilobyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = com.android.internal.R.string.megabyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = com.android.internal.R.string.gigabyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = com.android.internal.R.string.terabyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = com.android.internal.R.string.petabyteShort;
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 10) {
            if (shorter) {
                value = String.format("%.1f", result);
            } else {
                value = String.format("%.2f", result);
            }
        } else if (result < 100) {
            if (shorter) {
                value = String.format("%.0f", result);
            } else {
                value = String.format("%.2f", result);
            }
        } else {
            value = String.format("%.0f", result);
        }
        return context.getResources().
                getString(com.android.internal.R.string.fileSizeSuffix,
                        value, context.getString(suffix));
    }


    private long extractMemValue(byte[] buffer, int index) {
        while (index < buffer.length && buffer[index] != '\n') {
            if (buffer[index] >= '0' && buffer[index] <= '9') {
                int start = index;
                index++;
                while (index < buffer.length && buffer[index] >= '0'
                        && buffer[index] <= '9') {
                    index++;
                }
                String str = new String(buffer, 0, start, index - start);
                return ((long) Integer.parseInt(str)) * 1024;
            }
            index++;
        }
        return 0;
    }

    private boolean matchText(byte[] buffer, int index, String text) {
        int N = text.length();
        if ((index + N) >= buffer.length) {
            return false;
        }
        for (int i = 0; i < N; i++) {
            if (buffer[index + i] != text.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private long readAvailMem() {
        try {
            long memFree = 0;
            long memCached = 0;
            FileInputStream is = new FileInputStream("/proc/meminfo");
            int len = is.read(mBuffer);
            is.close();
            final int BUFLEN = mBuffer.length;
            for (int i = 0; i < len && (memFree == 0 || memCached == 0); i++) {
                if (matchText(mBuffer, i, "MemFree")) {
                    i += 7;
                    memFree = extractMemValue(mBuffer, i);
                } else if (matchText(mBuffer, i, "Cached")) {
                    i += 6;
                    memCached = extractMemValue(mBuffer, i);
                }
                while (i < BUFLEN && mBuffer[i] != '\n') {
                    i++;
                }
            }
            return memFree + memCached;
        } catch (java.io.FileNotFoundException e) {
        } catch (IOException e) {
        }
        return 0;
    }

    private long readAllMem() {
        try {

            long memALL = 0;
            FileInputStream is = new FileInputStream("/proc/meminfo");
            int len = is.read(mBuffer);
            is.close();
            final int BUFLEN = mBuffer.length;
            for (int i = 0; i < len && (memALL == 0); i++) {
                if (matchText(mBuffer, i, "MemTotal")) {
                    i += 8;
                    memALL = extractMemValue(mBuffer, i);
                }
                while (i < BUFLEN && mBuffer[i] != '\n') {
                    i++;
                }
            }
            return memALL;
        } catch (java.io.FileNotFoundException e) {
        } catch (IOException e) {
        }
        return 0;
    }


    @Override
    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "sdcard");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }
    public long getAllStorageSize( Context context ){
        mStorageManager = context.getSystemService(StorageManager.class);

        return mStorageManager.getPrimaryStorageSize();

    }
}

