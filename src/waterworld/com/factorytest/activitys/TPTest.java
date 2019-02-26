package waterworld.com.factorytest.activitys;


import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import waterworld.com.factorytest.FactoryActivity;
import waterworld.com.factorytest.Utils;
import waterworld.com.factorytest.R;

//import com.mediatek.featureoption.FeatureOption;//used in android2.3
//import com.mediatek.common.featureoption.FeatureOption;

public class TPTest extends FactoryActivity {
	private final String TAG = "TPTest";
	static int CameraTestStatus = 0;
	 int CameraTestStatus1 = 0;
	 int CameraTestStatus2 = 0;
	 int CameraTestStatus3 = 0;
	 int CameraTestStatus4 = 0;
	 int CameraTestStatus5 = 0;
	int center;
	Canvas mcanvas;
	int innerCircle;
	int ringWidth;
	
	int point_x;
	int point_y;
	int radius = 0;
	 int alpha = 255;  
	boolean upFlag = false;

	@Override
	public void setResultBeforeFinish(int status) {
		Log.d(TAG, "setResultBeforeFinish: " + status);
		Intent intent = new Intent();
		intent.putExtra(Utils.NAME, "touch_panel");
		intent.putExtra(Utils.TEST_RESULT, status);
		setResult(status, intent);
	}

	//fangfengfan add start
	private enum Line{
		LINEONE,LINETOW,LINETHREE,LINEFOUR,LINEFIVE,EROOR
	}
	//fangfengfan add end

	//wangliangfu 20140424
	private final static String NEW_PAINT = "com.mediatek.FactoryTest.TPTest.new_paint";
 @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//full screen
		
		

			
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		int flag = getWindow().getDecorView().getSystemUiVisibility();

		//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED | flag );
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,	
				                  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//fangfengfan add start
		int[] windowWidthHeigh = getWindowWidthHeigh(TPTest.this);
        screenHeigh = windowWidthHeigh[0];
        screenWidth = windowWidthHeigh[1];
		Log.d(TAG, "screenHeigh=="+screenHeigh+"---screenWidth=="+screenWidth);
		if(SystemProperties.getBoolean("ro.hx_tp_test_xindiheng", false)){
			setContentView(new BlockView(TPTest.this)); 
		}else{
			setContentView(new RingView(this));
		}
		//fangfengfan add end			

		IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(NEW_PAINT);
        
        registerReceiver(mReceiver, commandFilter);

		//by yds start
		WindowManager windowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		screenHeigh = display.getHeight();
		screenWidth = display.getWidth();

		if ((blockWidth * (total_Column + 2) + (total_Column + 1) * distance) > screenWidth) {
			Toast.makeText(this, "Too many Column or blockwidth too long!",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		if ((blockHeigh * (total_Row + 2) + (total_Row + 1) * distance) > screenHeigh) {
			Toast.makeText(this, "Too many Row or blockheigh too long!",
					Toast.LENGTH_SHORT).show();
			finish();
		}

		generalBlockData();
		//by yds end

	} 

	//wangliangfu 20140424
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(NEW_PAINT)) {
					TPTest.this.setContentView(new BlockView(TPTest.this)); 		   
			}
		}
	};
 
  public void fullScreenChange() {
	  SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	  boolean fullScreen = mPreferences.getBoolean("fullScreen", false);
	  WindowManager.LayoutParams attrs = getWindow().getAttributes(); 
	  System.out.println("fullScreen的值:" + fullScreen);
	  if (fullScreen) {
	  attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	  getWindow().setAttributes(attrs); 
	  //取消全屏设置
	  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	  mPreferences.edit().putBoolean("fullScreen", false).commit() ;
	  } else {
	  attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN; 
	  getWindow().setAttributes(attrs); 
	  getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); 
	  mPreferences.edit().putBoolean("fullScreen", true).commit();
	  }
	  }
  
 protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		setContentView(R.layout.test_result_tp);
		
		setContentView(R.layout.test_result);
//		Button success = (Button)findViewById(R.id.Button_tpSuccess);
//		Button fail = (Button)findViewById(R.id.Button_tpFail);
		
		Button success = (Button)findViewById(R.id.Button_Success);
		Button fail = (Button)findViewById(R.id.Button_Fail);
		
		success.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        CameraTestStatus  = 1;
		        setResultBeforeFinish( CameraTestStatus );
		        finish();
			}
		});
		fail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        CameraTestStatus  = -1;
				setResultBeforeFinish( CameraTestStatus );
				finish();
			}
		});
		
	}


/**
  * Access to the screen size
  *
  * @param context
  * @return int[0]--height  int[1]--width
  */
 public static int[] getWindowWidthHeigh(Context context) {
	 int[] dis = null;
	 if (dis == null) {
		 dis = new int[2];
		 WindowManager windowManager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);//\u83b7\u53d6\u5c4f\u5e55\u7ba1\u7406\u5668
		 Display display = windowManager.getDefaultDisplay();//\u83b7\u53d6\u5c4f\u5e55\u7684\u5bbd\u9ad8
		 dis[0] = display.getHeight();
		 dis[1] = display.getWidth();
	 }
	 return dis;
 }


 
 
 public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
     CameraTestStatus  = -1;
	 setResultBeforeFinish( CameraTestStatus );
     onPause();
		
		
	}	
class RingView extends View {

	private final  Paint paint1;
	private final  Paint paint2;
	private final  Paint paint3;
	private final  Paint paint4;
	private final  Paint paint5;
	
	private final Context context;
	
	public RingView(Context context) {
		
		// TODO Auto-generated constructor stub
		this(context, null);
	}

	public RingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.paint1 = new Paint();
		this.paint1.setARGB(155, 167, 190, 206);
		this.paint1.setAntiAlias(true); //消除锯齿
		this.paint1.setStyle(Style.STROKE); //绘制空心圆

		this.paint2 = new Paint();
		this.paint2.setARGB(155, 167, 190, 206);
		this.paint2.setAntiAlias(true); //消除锯齿
		this.paint2.setStyle(Style.STROKE); //绘制空心圆

		this.paint3 = new Paint();
		this.paint3.setARGB(155, 167, 190, 206);
		this.paint3.setAntiAlias(true); //消除锯齿
		this.paint3.setStyle(Style.STROKE); //绘制空心圆

		this.paint4 = new Paint();
		this.paint4.setARGB(155, 167, 190, 206);
		this.paint4.setAntiAlias(true); //消除锯齿
		this.paint4.setStyle(Style.STROKE); //绘制空心圆

		this.paint5 = new Paint();
		this.paint5.setARGB(155, 167, 190, 206);
		this.paint5.setAntiAlias(true); //消除锯齿
		this.paint5.setStyle(Style.STROKE); //绘制空心圆

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		center = getWidth()/2;
		 innerCircle = dip2px(context, 33); //设置内圆半径
		 ringWidth = dip2px(context, 5); //设置圆环宽度
		mcanvas = canvas;



		alpha -= 3;
		canvas.drawColor(Color.GRAY);   //设置背景色
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);		//设置画笔颜色
		paint.setAlpha(alpha);			//设置透明度
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		if (upFlag) {
			++radius;
			if (radius > 26) {
				upFlag = false;
				radius = 0;
				alpha = 255;
			}
			if (radius == 18) {
				alpha = 100;
			}
			canvas.drawCircle(point_x, point_y, radius, paint); //画圆
			invalidate();
		}
		
		
		
		//绘制内圆
		
		this.paint1.setStrokeWidth(2);
		canvas.drawCircle(center,center/2, innerCircle, this.paint1);
		

		////////////////////////////////////////
		//绘制内圆
				
				this.paint2.setStrokeWidth(2);
				canvas.drawCircle(center/3,3*center/2, innerCircle, this.paint2);
				
		////////////
				
				//绘制内圆
				
				this.paint3.setStrokeWidth(2);
				canvas.drawCircle(center,3*center/2, innerCircle, this.paint3);
				
		////////////
				
				
				
				//绘制内圆
				
				this.paint4.setStrokeWidth(2);
				canvas.drawCircle(5*center/3,3*center/2, innerCircle, this.paint4);
				
		////////////
				
				
				
				//绘制内圆
				
				this.paint5.setStrokeWidth(2);
				canvas.drawCircle(center,5*center/2, innerCircle, this.paint5);
				
		
		super.onDraw(canvas);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		point_x = (int) event.getX();//获取点击位置
		point_y = (int) event.getY();
		if (event.getAction() == MotionEvent.ACTION_UP) {
			upFlag = true;
		}
		
		
		
		
		
		float X = event.getX();
		float Y = event.getY();
		Log.e("lyaotao", "X ="+X);
		Log.e("lyaotao", "Y ="+Y);
		
		if(((int)center-33)< X && X <((int)center+33) &&
				((int)center/2-43)< Y && Y <((int)center/2+43) ){
			CameraTestStatus1 = 1;
			this.paint1.setColor(Color.GREEN);
			Log.e("lyaotao", "CameraTestStatus1");
		}
		
		if(((int)center/3-33)< X && X <((int)center/3+33) &&
				((int)3*center/2-43)< Y && Y <((int)3*center/2+43) ){
			CameraTestStatus2 = 1;
			this.paint2.setColor(Color.GREEN);
			Log.e("lyaotao", "CameraTestStatus2");
		}
		
		if(((int)center-33)< X && X <((int)center+33) &&
				((int)3*center/2-43)< Y && Y <((int)3*center/2+43) ){
			CameraTestStatus3 = 1;
			this.paint3.setColor(Color.GREEN);
			Log.e("lyaotao", "CameraTestStatus3");
		}
		
		if(((int)5*center/3-33)< X && X <((int)5*center/3+33) &&
				((int)3*center/2-43)< Y && Y <((int)3*center/2+43) ){
			CameraTestStatus4 = 1;
			this.paint4.setColor(Color.GREEN);
			Log.e("lyaotao", "CameraTestStatus4");
		}
		
		if(((int)center - 33)< X && X <((int)center+33) &&
				((int)5*center/2-43)< Y && Y <((int)5*center/2+43) ){
			CameraTestStatus5 = 1;
			this.paint5.setColor(Color.GREEN);
			Log.e("lyaotao", "CameraTestStatus5");
		}
		
		if(CameraTestStatus1 == 1 && CameraTestStatus2 == 1 &&
				CameraTestStatus3 == 1 && CameraTestStatus4 == 1 && CameraTestStatus5 == 1){
				//wangliangfu 20140424 add
			Intent mIntent = new Intent(NEW_PAINT);
			sendBroadcast(mIntent);
		}
		invalidate();
//		return super.onTouchEvent(event);
		return true;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public  int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}



//by yds start
	private final int total_Row = SystemProperties.getInt("ro.tp_test_total_row", 2);// \D6м\E4\B5\C4\D0У\AC\CB\C4\D6ܲ\BB\CB\E3
	private final int total_Column = SystemProperties.getInt("ro.tp_test_total_col", 1);// \D6м\E4\B5\C4\C1У\AC\CB\C4\D6ܲ\BB\CB\E3
	private final int distance = 3;
	private final int blockWidth =  SystemProperties.getInt("ro.tp_test_blockwidth", 75);// 75;
	private final int blockHeigh =  SystemProperties.getInt("ro.tp_test_blockheigh", 63);// 63;

	private int screenWidth = 0;
	private int screenHeigh = 0;

	private int xBegin, yBegin;

	

	private List<RecArea> mUntouchArea = new ArrayList<RecArea>();
	
	BlockView mBlockView;

	// mBlockPaint.setColor(Color.GRAY);
	private void addBlock(int x1,int y1,int x2,int y2)
	{
		//boolean same=false;
		for (RecArea mRecArea : mUntouchArea) 
		{
			if(	(mRecArea.getx1()==x1)&&
				(mRecArea.gety1()==y1)&&
				(mRecArea.getx2()==x2)&&
				(mRecArea.gety2()==y2))
			{
				return;
			}
			
		}

		//if(false==same)
		mUntouchArea.add(new RecArea(x1, y1, x2, y2));
	}
	private void generalBlockData() {
		xBegin = (screenWidth % (blockWidth + distance) + distance) / 2;
		yBegin = (screenHeigh % (blockHeigh + distance) + distance) / 2;

		int xdis = (screenWidth - blockWidth * 2 - distance * 2 - xBegin * 2)
				/ (total_Column + 1);
		int ydis = (screenHeigh - blockHeigh * 2 - distance * 2 - yBegin * 2)
				/ (total_Row + 1);
		int i, j, x1, y1, x2, y2;
		int x, y;

		for (i = 1; i <= total_Column; i++)// \C4ڿ\F2\C1\D0
		{
			x = (blockWidth + distance) + i * xdis + xBegin;
			x1 = x - blockWidth / 2;
			x2 = x + blockWidth / 2;

			//block align start
			int x3=(x1-xBegin)/(blockWidth + distance);
			int x4=(x1-xBegin)%(blockWidth + distance);

			if(x4>(blockWidth/2))
			{
				x1=(blockWidth + distance)*(x3+1)+xBegin;
				x2=x1+blockWidth;
			}
			else
			{
				x1=(blockWidth + distance)*x3+xBegin;
				x2=x1+blockWidth;
			}
			//block align end
			for (j = 1;; j++) {
				y1 = (blockHeigh + distance) * j + yBegin;
				y2 = y1 + blockHeigh;

				if (y2 > (screenHeigh - blockHeigh - distance))
					break;
				else {
					addBlock(x1,y1,x2,y2);//mUntouchArea.add(new RecArea(x1, y1, x2, y2));
				}

			}
		}

		for (i = 1; i <= total_Row; i++)// \C4ڿ\F2\D0\D0
		{
			y = (blockHeigh + distance) + i * ydis + yBegin;
			y1 = y - blockHeigh / 2;
			y2 = y + blockHeigh / 2;

			//block align start
			int y3=(y1-yBegin)/(blockHeigh + distance);
			int y4=(y1-yBegin)%(blockHeigh + distance);

			if(y4>(blockHeigh/2))
			{
				y1=(blockHeigh + distance)*(y3+1)+yBegin;
				y2=y1+blockHeigh;
			}
			else
			{
				y1=(blockHeigh + distance)*y3+yBegin;
				y2=y1+blockHeigh;
			}
			//block align end
			for (j = 1;; j++) {
				x1 = (blockWidth + distance) * j + xBegin;
				x2 = x1 + blockWidth;

				if (x2 > (screenWidth - blockWidth - distance))
					break;
				else {
					addBlock(x1,y1,x2,y2);//mUntouchArea.add(new RecArea(x1, y1, x2, y2));
				}

			}
		}

		// top
		y1 = 0 + yBegin;
		y2 = y1 + blockHeigh;
		for (i = 0;; i++) {
			x1 = (blockWidth + distance) * i + xBegin;
			x2 = x1 + blockWidth;
			if (x2 > screenWidth)
				break;
			else
				addBlock(x1,y1,x2,y2);//mUntouchArea.add(new RecArea(x1, y1, x2, y2));
		}

		// right
		x1 = (blockWidth + distance) * (i - 1) + xBegin;
		x2 = x1 + blockWidth;
		for (i = 0;; i++) {
			y1 = (blockHeigh + distance) * i + yBegin;
			y2 = y1 + blockHeigh;

			if (y2 > screenHeigh)
				break;
			else
				addBlock(x1,y1,x2,y2);//mUntouchArea.add(new RecArea(x1, y1, x2, y2));
		}

		// bottom
		y1 = (blockHeigh + distance) * (i - 1) + yBegin;
		y2 = y1 + blockHeigh;
		for (i = 0;; i++) {
			x1 = (blockWidth + distance) * i + xBegin;
			x2 = x1 + blockWidth;
			if (x2 > screenWidth)
				break;
			else
				addBlock(x1,y1,x2,y2);//mUntouchArea.add(new RecArea(x1, y1, x2, y2));
		}

		// left
		x1 = 0 + xBegin;
		x2 = x1 + blockWidth;
		for (i = 0;; i++) {
			y1 = (blockHeigh + distance) * i + yBegin;
			y2 = y1 + blockHeigh;

			if (y2 > screenHeigh)
				break;
			else
				addBlock(x1,y1,x2,y2);//mUntouchArea.add(new RecArea(x1, y1, x2, y2));
		}

	}


	class BlockView extends View {

		Context mContext = null;
		Paint mBlockPaint = new Paint();
		private Path mPath[] = new Path[5];

		private final Paint mGesturePaint = new Paint();

		private int mPointID[] = { -1, -1, -1, -1, -1 };

		public BlockView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			mContext = context;

			mGesturePaint.setAntiAlias(true);
			mGesturePaint.setStyle(Style.STROKE);
			mGesturePaint.setStrokeWidth(5);
			// mGesturePaint.setColor(Color.WHITE);

			for (int i = 0; i < 5; i++) {
				mPath[i] = new Path();
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			int i;
			int[] mLineColor = { Color.RED, Color.CYAN, Color.BLUE,
					Color.YELLOW, Color.BLACK };
			for (i = 0; i < 5; i++) {
				mGesturePaint.setColor(mLineColor[i]);
				canvas.drawPath(mPath[i], mGesturePaint);
			}

			boolean isAllTouch = true;
			for (RecArea mRecArea : mUntouchArea) {
				if (mRecArea.isRectTouch())
					mBlockPaint.setColor(Color.GREEN);
				else {
					mBlockPaint.setColor(Color.GRAY);
					isAllTouch = false;
				}

				canvas.drawRect(mRecArea.getx1(), mRecArea.gety1(),
						mRecArea.getx2(), mRecArea.gety2(), mBlockPaint);

			}
			if (isAllTouch)
			{
				/*Toast.makeText(mContext, "Test Success!", Toast.LENGTH_SHORT)
						.show();
				finish();*/
				//fangfengfan add start
				if(SystemProperties.getBoolean("ro.hx_tp_test_xindiheng", false)){
					XLineView xLineView = new XLineView(TPTest.this);
					setContentView(xLineView);
					xLineView.setOnDrawSuccessListener(mOnDrawSuccessListener);
				}else{
					onPause();
				}
				//fangfengfan add end
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			//int point_x = (int) event.getX();
			//int point_y = (int) event.getY();
			int i=0;
			int pointerCount = event.getPointerCount();
			do{
				int point_x = (int) event.getX(i);
				int point_y = (int) event.getY(i);
				i++;
				for (RecArea mRecArea : mUntouchArea) {
					int x1 = mRecArea.getx1();
					int x2 = mRecArea.getx2();
					int y1 = mRecArea.gety1();
					int y2 = mRecArea.gety2();
					if ((mRecArea.isRectTouch() == false) && (point_x >= x1)
							&& (point_x <= x2) && (point_y >= y1)
							&& (point_y <= y2)) {
						mRecArea.setTouch();
						// postInvalidate();
						break;
					}
				}
				
			}while(i<pointerCount);
			// if(mUntouchArea.isEmpty())
			// Toast.makeText(mContext, "Test Success!",
			// Toast.LENGTH_SHORT).show();
			
			// int pointerid=event.getPointerId(0);
			int pid[] = { -1, -1, -1, -1, -1 };

			for (i = 0; i < pointerCount; i++) {
				float pointX = event.getX(i);
				float pointY = event.getY(i);

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					pid[i] = mPointID[i] = event.getPointerId(i);
					//Log.d("YDS", "down pid[" + i + "]=" + pid[i]);
					mPath[i].moveTo(pointX, pointY);
					break;
				case MotionEvent.ACTION_MOVE:
					int k = 0;
					pid[i] = event.getPointerId(i);
					//Log.d("YDS", "move pid[" + i + "]=" + pid[i]);
					for (k = 0; k < 5; k++) {
						if (mPointID[k] == pid[i]) {
							mPath[k].lineTo(pointX, pointY);
							break;
						}
					}

					if (5 == k)// means new point
					{
						for (k = 0; k < 5; k++) {
							if (mPointID[k] == -1) {
								mPointID[k] = pid[i];
								mPath[k].moveTo(pointX, pointY);
								break;
							}
						}

					}
					// mPath[i].lineTo(pointX, pointY);
					break;
				case MotionEvent.ACTION_UP:
					//Log.d("YDS", "up pid[" + i + "]=" + pid[i]);
					for (int j = 0; j < 5; j++)
						mPointID[j] = -1;
					break;
				default:
					//Log.d("YDS", "default" + event.getPointerId(i));
					break;
				}
			}

			if (MotionEvent.ACTION_MOVE == event.getAction()) {
				for (i = 0; i < 5; i++) {
					//Log.d("YDS A", " mPointID[" + i + "]=" + mPointID[i]);
					int j = 0;
					for (j = 0; j < pointerCount; j++) {
						if (mPointID[i] == pid[j])
							break;
					}
					if (j == pointerCount) {
						mPointID[i] = -1;
					}

					//Log.d("YDS", " mPointID[" + i + "]=" + mPointID[i]);
				}
			}
			postInvalidate();

			return true;
		}
	}

	
	class RecArea {
			private int x1, x2, y1, y2;
			private boolean isTouch = false;
	
			public RecArea(int X1, int Y1, int X2, int Y2) {
				x1 = X1;
				y1 = Y1;
				x2 = X2;
				y2 = Y2;
			}
	
			public int getx1() {
				return x1;
			}
	
			public int getx2() {
				return x2;
			}
	
			public int gety1() {
				return y1;
			}
	
			public int gety2() {
				return y2;
			}
	
			public boolean isRectTouch() {
				return isTouch;
			}
	
			public void setTouch() {
				isTouch = true;
			}
		}
//by yds end

//fangfengfan add start
private OnDrawSuccessListener mOnDrawSuccessListener = new OnDrawSuccessListener(){
	@Override
	public void onSuccess(int flag) {
		switch (flag) {
		case 0:
			HorizontalLineView horizontalLineView = new HorizontalLineView(TPTest.this);
			setContentView(horizontalLineView);
			horizontalLineView.setOnDrawSuccessListener(mOnDrawSuccessListener);
			break;
		case 1:
			VerticalLineView verticalLineView = new VerticalLineView(TPTest.this);
			setContentView(verticalLineView);
			verticalLineView.setOnDrawSuccessListener(mOnDrawSuccessListener);
			break;
		case 2:
			onPause();
			break;
		}
	}
};


class XLineView extends View{
	private final int DISTANCE = 25;
	private final int SART_POINT = 50;
	private  Paint paintGreen,paintBlue,paintRed,paintX;
	private Context context;
	private Path mPath;
	private float k0,k1,b0,b1,b2,b3;
	private int distance;
	private float startX,startY;
	private float downPointX,downPointY;
	private Line line;
	private float lineDistance;
	private boolean drawLineOneSuccess,drawLineTwoSuccess;
	private OnDrawSuccessListener mOnDrawSuccessListener;
	public XLineView(Context context) {
		super(context);
		setBackgroundColor(Color.GRAY);
		this.context = context;
		paintGreen = new Paint();
		paintGreen.setColor(Color.GREEN);
		paintGreen.setStyle(Style.STROKE);
		paintGreen.setAntiAlias(true); 
		paintX = paintGreen;
		
		paintBlue = new Paint();
		paintBlue.setColor(Color.BLUE);
		paintBlue.setStyle(Style.STROKE);
		paintBlue.setAntiAlias(true);
		
		paintRed = new Paint();
		paintRed.setColor(Color.RED);
		paintRed.setStyle(Style.STROKE);
		//paintRed.setStrokeWidth(20);
		paintRed.setAntiAlias(true); 
		
		mPath = new Path(); 
		
		distance = dip2px(context, DISTANCE);
		lineDistance = (float) Math.sqrt(Math.pow(screenHeigh,2)+Math.pow(screenWidth,2));
		
		k0 = (float)screenHeigh/screenWidth;
		//y = k0x
		b0 = (float) (-distance*Math.sqrt(Math.pow(k0,2)+1));
		b1 = (float) (distance*Math.sqrt(Math.pow(k0,2)+1));
		
		//y = k1x + screenHeigh;
		k1 = -k0;
		b2 = (float)(screenHeigh-distance*Math.sqrt(Math.pow(k1,2)+1));
		b3 = (float)(screenHeigh+distance*Math.sqrt(Math.pow(k1,2)+1));
		
		startX = dip2px(context, SART_POINT);
		startY = startX*k0;
		lineDistance = lineDistance - (float) Math.sqrt(Math.pow(startX,2)+Math.pow(startY,2))*3/2;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawLine(-b0/k0, 0, screenWidth, k0*screenWidth+b0, paintX);
		canvas.drawLine(0, b1, (screenHeigh-b1)/k0, screenHeigh, paintX);
		canvas.drawLine(0, b2, -b2/k1, 0, paintX);
		canvas.drawLine((screenHeigh-b3)/k1, screenHeigh, screenWidth, k1*screenWidth+b3, paintX);
		
		canvas.drawPath(mPath, paintX);
		
		if(drawLineOneSuccess && drawLineTwoSuccess){
			mOnDrawSuccessListener.onSuccess(0);
		}
		
		
		/**draw opposite angles line*/
//		canvas.drawLine(0, 0, screenWidth, screenHeigh, paintGreen);
//		canvas.drawLine(0, screenHeigh, screenWidth, 0, paintGreen);
		/**draw point*/
//		canvas.drawPoint(startX, startY, paintRed);//left top point
//		canvas.drawPoint(startX, screenHeigh-startY, paintRed);//left bottom point
//		canvas.drawPoint(screenWidth-startX, startY, paintRed);//right top point
//		canvas.drawPoint(screenWidth-startX, screenHeigh-startY, paintRed);//right bottom point
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float pointX =  event.getX();
		float pointY = event.getY();
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			downPointX = pointX;
			downPointY = pointY;
			mPath.moveTo(pointX, pointY);
			touchPointPosition(pointX, pointY);
			paintX = paintBlue;
			break;
		case MotionEvent.ACTION_MOVE:
			mPath.lineTo(pointX, pointY);
			containsPoint(pointX, pointY);
			break;
		case MotionEvent.ACTION_UP:
			if(line!= Line.EROOR){
				compareLineLenght(pointX, pointY);
			}else{
				resetState();
			}
			break;
		}
		postInvalidate();
		return true;
	}
	
	
	
	private void compareLineLenght(float pointX, float pointY) {
		float length = (float) Math.sqrt((pointX-downPointX)*(pointX-downPointX)
				+(pointY-downPointY)*(pointY-downPointY));
		Log.d(TAG, "length  == "+length+" -- lineDistance == "+lineDistance);
		if(length < lineDistance){
			Log.d(TAG, "compareLineLenght line is short");
			resetState();
		}else{
			if(line == Line.LINEONE){
				drawLineOneSuccess = true;
			}else if(line == Line.LINETOW){
				drawLineTwoSuccess = true;
			}
		}
	}

	private void touchPointPosition(float pointX, float pointY) {
		if(pointX<startX && pointY<startY 
				|| pointX>screenWidth-startX && pointY>screenHeigh-startY){
			Log.d(TAG, "point in line 1");
			line = Line.LINEONE;
		}else if(pointX<startX && pointY>screenHeigh-startY 
				|| pointX>screenWidth-startX && pointY<startY){
			Log.d(TAG, "point in line 2");
			line = Line.LINETOW;
		}else{
			line = Line.EROOR;
			Log.d(TAG, "point outside");
		}
		containsPoint(pointX,pointY);
	}

	private void containsPoint(float pointX, float pointY) {
		float d;
		if(line == Line.LINEONE){
			d = (float) Math.abs((k0*pointX-pointY)/Math.sqrt(k0*k0+1));
			if(d > distance){
				Log.d(TAG, "draw line 1 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.LINETOW){
			d = (float) Math.abs((k1*pointX-pointY+screenHeigh)/Math.sqrt(Math.pow(k1,2)+1));
			if(d > distance){
				Log.d(TAG, "draw line 2 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.EROOR){
			Log.d(TAG, "draw line EROOR");
		}
	}
	
	private void resetState(){
		mPath.rewind();
		drawLineOneSuccess = drawLineTwoSuccess = false;
		paintX = paintRed;
		Toast.makeText(context,context.getString(R.string.draw_line_error) , Toast.LENGTH_SHORT).show();
	}
	
	public void setOnDrawSuccessListener(OnDrawSuccessListener mOnDrawSuccessListener){
		this.mOnDrawSuccessListener = mOnDrawSuccessListener;
	}
}

class HorizontalLineView extends View{
	private final int SPACE = 40;
	private final int SART_POINT = 50;
	private Context context;
	private  Paint paintGreen,paintBlue,paintRed,paintX;
	private Path mPath;
	private float flag;
	private int space;
	private int startX;
	private float downPointX,downPointY;
	private OnDrawSuccessListener mOnDrawSuccessListener;
	private Line line;
	private float lineDistance;
	private boolean drawLineOneSuccess,drawLineTwoSuccess,drawLineThreeSuccess
					,drawLineFourSuccess,drawLineFiveSuccess;
	
	public HorizontalLineView(Context context) {
		super(context);
		setBackgroundColor(Color.GRAY);
		this.context = context;
		paintGreen = new Paint();
		paintGreen.setColor(Color.GREEN);
		paintGreen.setStyle(Style.STROKE);
		paintGreen.setAntiAlias(true); 
		paintX = paintGreen;
		
		paintBlue = new Paint();
		paintBlue.setColor(Color.BLUE);
		paintBlue.setStyle(Style.STROKE);
		paintBlue.setAntiAlias(true); 
		
		paintRed = new Paint();
		paintRed.setColor(Color.RED);
		paintRed.setStyle(Style.STROKE);
		//paintRed.setStrokeWidth(20);
		paintRed.setAntiAlias(true);
		
		mPath = new Path(); 
		
		space = dip2px(context,SPACE);
		
		flag = (float)(screenHeigh-5)/5;
		
		startX = dip2px(context, SART_POINT);
		lineDistance = screenWidth - startX*3/2;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		for(int i=0;i<6;i++){
			if(i==0){
				canvas.drawLine(0, space, screenWidth, space, paintX);
			}else if(i==5){
				canvas.drawLine(0, flag*i, screenWidth, flag*i, paintX);
			}else{
				canvas.drawLine(0, flag*i, screenWidth, flag*i, paintX);
				canvas.drawLine(0, flag*i+space, screenWidth, flag*i+space, paintX);
			}
		}
		
		canvas.drawPath(mPath, paintX);
		
		if(drawLineOneSuccess && drawLineTwoSuccess && drawLineThreeSuccess
				&& drawLineFourSuccess && drawLineFiveSuccess){
			Log.d(TAG, "do next");
			mOnDrawSuccessListener.onSuccess(1);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float pointX =  event.getX();
		float pointY = event.getY();
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			downPointX = pointX;
			downPointY = pointY;
			mPath.moveTo(pointX, pointY);
			touchPointPosition(pointX, pointY);
			paintX = paintBlue;
			break;
		case MotionEvent.ACTION_MOVE:
			mPath.lineTo(pointX, pointY);
			containsPoint(pointX, pointY);
			break;
		case MotionEvent.ACTION_UP:
			if(line!= Line.EROOR){
				compareLineLenght(pointX, pointY);
			}else{
				resetState();
			}
			break;
		}
		postInvalidate();
		return true;
	}
	
	private void compareLineLenght(float pointX, float pointY) {
		float length = (float) Math.sqrt((pointX-downPointX)*(pointX-downPointX)
				+(pointY-downPointY)*(pointY-downPointY));
		if(length < lineDistance){
			Log.d(TAG, "compareLineLenght line is short");
			resetState();
		}else{
			if(line == Line.LINEONE){
				drawLineOneSuccess = true;
			}else if(line == Line.LINETOW){
				drawLineTwoSuccess = true;
			}else if(line == Line.LINETHREE){
				drawLineThreeSuccess = true;
			}else if(line == Line.LINEFOUR){
				drawLineFourSuccess = true;
			}else if(line == Line.LINEFIVE){
				drawLineFiveSuccess = true;
			}
		}
	}
	private void resetState() {
		mPath.rewind();
		drawLineOneSuccess = drawLineTwoSuccess = drawLineThreeSuccess
		= drawLineFourSuccess = drawLineFiveSuccess = false;
		paintX = paintRed;
		Toast.makeText(context,context.getString(R.string.draw_line_error) , Toast.LENGTH_SHORT).show();
	}
	
	private void containsPoint(float pointX, float pointY) {
		if(line == Line.LINEONE){
			if(pointY>space){
				Log.d(TAG, "draw line 1 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.LINETOW){
			if(flag>pointY || pointY>flag+space){
				Log.d(TAG, "draw line 2 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.LINETHREE){
			if(2*flag>pointY || pointY>2*flag+space){
				Log.d(TAG, "draw line 3 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.LINEFOUR){
			if(3*flag>pointY || pointY>3*flag+space){
				Log.d(TAG, "draw line 4 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.LINEFIVE){
			if(4*flag>pointY || pointY>4*flag+space){
				Log.d(TAG, "draw line 5 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.EROOR){
			Log.d(TAG, "draw line EROOR");
		}
	}
	private void touchPointPosition(float pointX, float pointY) {
		if(pointX<startX || pointX>screenWidth-startX){
			if(pointY<space){
				line = Line.LINEONE;
				Log.d(TAG, "down line 1");
			}else if(flag<pointY && pointY<flag+space){
				line = Line.LINETOW;
				Log.d(TAG, "down line 2");
			}else if(2*flag<pointY && pointY<2*flag+space){
				line = Line.LINETHREE;
				Log.d(TAG, "down line 3");
			}else if(3*flag<pointY && pointY<3*flag+space){
				line = Line.LINEFOUR;
				Log.d(TAG, "down line 4");
			}else if(4*flag<pointY && pointY<4*flag+space){
				line = Line.LINEFIVE;
				Log.d(TAG, "down line 5");
			}else{
				line = Line.EROOR;
				Log.d(TAG, "down outside");
			}
		}else{
			line = Line.EROOR;
			Log.d(TAG, "down outside");
		}
	}

	public void setOnDrawSuccessListener(OnDrawSuccessListener mOnDrawSuccessListener){
		this.mOnDrawSuccessListener = mOnDrawSuccessListener;
	}
}

public class VerticalLineView extends View{
	private final int SART_POINT = 50;
	private final int SPACE = 20;
	private final String TAG = "fff";
	private  Paint paintGreen,paintBlue,paintRed,paintX;
	private Path mPath;
	private float flag;
	private int space;
	private Context context;
	private float downPointX,downPointY;
	private Line line;
	private float lineDistance;
	private int startY;
	private OnDrawSuccessListener mOnDrawSuccessListener;
	private boolean drawLineOneSuccess,drawLineTwoSuccess,drawLineThreeSuccess
	,drawLineFourSuccess,drawLineFiveSuccess;
	public VerticalLineView(Context context) {
		super(context);
		setBackgroundColor(Color.GRAY);
		this.context = context;
		paintGreen = new Paint();
		paintGreen.setColor(Color.GREEN);
		paintGreen.setStyle(Style.STROKE);
		paintGreen.setAntiAlias(true); 
		paintX = paintGreen;
		
		paintBlue = new Paint();
		paintBlue.setColor(Color.BLUE);
		paintBlue.setStyle(Style.STROKE);
		paintBlue.setAntiAlias(true); 
		
		paintRed = new Paint();
		paintRed.setColor(Color.RED);
		paintRed.setStyle(Style.STROKE);
		//paintRed.setStrokeWidth(20);
		paintRed.setAntiAlias(true); 
		
		mPath = new Path(); 
		
		flag = (float)(screenWidth-6)/5;
		space = dip2px(context,SPACE);
		startY = dip2px(context, SART_POINT);
		lineDistance = screenHeigh - startY*3/2;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for(int i=1;i<6;i++){
			canvas.drawLine(i*flag-space, 0, i*flag-space, screenHeigh, paintX);
			canvas.drawLine(i*flag, 0, i*flag, screenHeigh, paintX);
		}
		
		canvas.drawPath(mPath, paintX);
		canvas.drawPoint(flag-space, startY, paintRed);
		canvas.drawPoint(flag-space, screenHeigh-startY, paintRed);
		
		if(drawLineOneSuccess && drawLineTwoSuccess && drawLineThreeSuccess
				&& drawLineFourSuccess && drawLineFiveSuccess){
			Log.d(TAG, "do next");
			mOnDrawSuccessListener.onSuccess(2);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float pointX =  event.getX();
		float pointY = event.getY();
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			downPointX = pointX;
			downPointY = pointY;
			mPath.moveTo(pointX, pointY);
			touchPointPosition(pointX, pointY);
			paintX = paintBlue;
			break;
		case MotionEvent.ACTION_MOVE:
			mPath.lineTo(pointX, pointY);
			containsPoint(pointX, pointY);
			break;
		case MotionEvent.ACTION_UP:
			if(line!= Line.EROOR){
				compareLineLenght(pointX, pointY);
			}else{
				resetState();
			}
			break;
		}
		postInvalidate();
		return true;
	}
	
	private void compareLineLenght(float pointX, float pointY) {
		float length = (float) Math.sqrt((pointX-downPointX)*(pointX-downPointX)
				+(pointY-downPointY)*(pointY-downPointY));
		if(length < lineDistance){
			Log.d(TAG, "compareLineLenght line is short");
			resetState();
		}else{
			if(line == Line.LINEONE){
				drawLineOneSuccess = true;
			}else if(line == Line.LINETOW){
				drawLineTwoSuccess = true;
			}else if(line == Line.LINETHREE){
				drawLineThreeSuccess = true;
			}else if(line == Line.LINEFOUR){
				drawLineFourSuccess = true;
			}else if(line == Line.LINEFIVE){
				drawLineFiveSuccess = true;
			}
		}
	}
	
	private void resetState() {
		mPath.rewind();
		drawLineOneSuccess = drawLineTwoSuccess = drawLineThreeSuccess
		= drawLineFourSuccess = drawLineFiveSuccess = false;
		paintX = paintRed;
		Toast.makeText(context,context.getString(R.string.draw_line_error) , Toast.LENGTH_SHORT).show();
	}
	
	private void containsPoint(float pointX, float pointY) {
		if(line == Line.LINEONE){
			if(pointX>flag-space){
				Log.d(TAG, "draw line 1 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.LINETOW){
			if(flag>pointX || pointX>2*flag-space){
				Log.d(TAG, "draw line 2 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.LINETHREE){
			if(2*flag>pointX || pointX>3*flag-space){
				Log.d(TAG, "draw line 3 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.LINEFOUR){
			if(3*flag>pointX || pointX>4*flag-space){
				Log.d(TAG, "draw line 4 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.LINEFIVE){
			if(4*flag>pointX || pointX>5*flag-space){
				Log.d(TAG, "draw line 5 outside");
				line = Line.EROOR;
			}
		}else if(line == Line.EROOR){
			Log.d(TAG, "draw line EROOR");
		}
	}

	private void touchPointPosition(float pointX, float pointY) {
		if(pointY<startY || pointY>screenHeigh-startY){
			if(pointX<flag-space){
				line = Line.LINEONE;
				Log.d(TAG, "down line 1");
			}else if(flag<pointX && pointX<2*flag-space){
				line = Line.LINETOW;
				Log.d(TAG, "down line 2");
			}else if(2*flag<pointX && pointX<3*flag-space){
				line = Line.LINETHREE;
				Log.d(TAG, "down line 3");
			}else if(3*flag<pointX && pointX<4*flag-space){
				line = Line.LINEFOUR;
				Log.d(TAG, "down line 4");
			}else if(4*flag<pointX && pointX<5*flag-space){
				line = Line.LINEFIVE;
				Log.d(TAG, "down line 5");
			}else{
				line = Line.EROOR;
				Log.d(TAG, "down outside");
			}
		}else{
			line = Line.EROOR;
			Log.d(TAG, "down outside");
		}
	}
	
	public void setOnDrawSuccessListener(OnDrawSuccessListener mOnDrawSuccessListener){
		this.mOnDrawSuccessListener = mOnDrawSuccessListener;
	}
}


public interface OnDrawSuccessListener {
	void onSuccess(int flag);
}

public	int dip2px(Context context, float dpValue) {
	final float scale = context.getResources().getDisplayMetrics().density;
	return (int) (dpValue * scale + 0.5f);
}


//fangfengfan add end



/////////////////////////////////////////////////////////////////////

@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();

}

@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	CameraTestStatus  = -1;
}

}



