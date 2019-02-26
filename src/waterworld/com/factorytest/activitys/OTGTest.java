package waterworld.com.factorytest.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import waterworld.com.factorytest.R;

public class OTGTest extends Activity implements OnClickListener{

	private File file;
	private boolean isContinue = true;
	protected static boolean mCreateFirstTime;
	private TextView otgTv;
	private Button mBtFailed;
	private Button mBtOk;
	private final int READ_FILE = 215;
	public static int OTGTestStatus = 0;
	private final String  PATH = "/sys/class/switch/otg_state/state";
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case READ_FILE:
				readFile();
				if(isContinue){
					mHandler.sendEmptyMessageDelayed(READ_FILE, 1000);
				}
				break;
			}
		};
	};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_result_otg);
		setTitle(getString(R.string.title_activity_otg_test));
		file = new File(PATH);
		this.otgTv = (TextView) findViewById(R.id.otg_tv);
		this.mBtOk = (Button)findViewById(R.id.Button_Success);
		this.mBtOk.setOnClickListener(this);
		this.mBtFailed = (Button)findViewById(R.id.Button_Fail);
		this.mBtFailed.setOnClickListener(this);
	}
	
	private void readFile(){
		try {
			if(!file.exists()){
				Log.d("fff", "文件不存在");
				return;
			}
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] bytes = new byte[1024];  
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();  
            while (fileInputStream.read(bytes) != -1) {  
                arrayOutputStream.write(bytes, 0, bytes.length);  
            }  
            fileInputStream.close();  
            arrayOutputStream.close();  
            int intValue = new Integer(new String(arrayOutputStream.toByteArray()).trim()).intValue();
            Log.d("fff", ""+intValue);
            if(intValue==0){
            	otgTv.setText(getString(R.string.no_link_equipment));
            }else{
            	otgTv.setText(getString(R.string.link_equipment));
            }
		} catch (Exception e) {
			e.printStackTrace();
			 Log.d("fff", "Exception==="+e.toString());
		} 
	}

	public void onClick(View paramView)
	{
		isContinue = false;
		mHandler.removeMessages(READ_FILE);
		if (paramView.getId() == this.mBtOk.getId())
	    	{
		OTGTestStatus = 1;
		Intent mIntent = getIntent();
		boolean fl = mIntent.getBooleanExtra("textall", false);
		if(fl == true){
			Intent miIntent = new Intent();
			miIntent.setAction("com.ykq.intent.action.FMRADIO_TEST");
			miIntent.putExtra("textall", true);
			startActivityForResult(miIntent, RESULT_OK);
		}
		}else if(paramView.getId() == this.mBtFailed.getId()){
		OTGTestStatus = -1;
		Intent mIntent1 = getIntent();
		boolean fl1 = mIntent1.getBooleanExtra("textall", false);
		if(fl1 == true){
			Intent miIntent = new Intent();
			miIntent.setAction("com.ykq.intent.action.FMRADIO_TEST");
			miIntent.putExtra("textall", true);
			startActivityForResult(miIntent, RESULT_OK);
		}
		}
		finish();
		return;
	}
	
	protected void onResume() {
		super.onResume();
		isContinue = true;
		mHandler.sendEmptyMessage(READ_FILE);
	}
	
	protected void onPause() {
		super.onPause();
		isContinue = false;
	}
}
