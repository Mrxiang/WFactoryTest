package waterworld.com.factorytest.activitys;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//zzk
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera.ShutterCallback;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.graphics.Color;
import android.graphics.Point;

import java.util.List;

import android.util.DisplayMetrics;

import waterworld.com.factorytest.R;
import waterworld.com.factorytest.FactoryActivity;
import waterworld.com.factorytest.FactoryTestFeatureoption.FeatureOption;
import waterworld.com.factorytest.Utils;
//zzk

//zzk

public class CameraTest extends FactoryActivity implements View.OnClickListener, SurfaceHolder.Callback {

    static int CameraTestStatus = 0;
    Timer mTimer;
    private Camera m_Camera;
    boolean flag = true;
    TimerTask mTask;
    Parameters mParameters;
    TextView tv_shanguang;
    TextView tv_shexiang;
    //zzk
    private static final String TAG = Utils.TAG + "CameraTest";
    private static final int NUMBER_OF_FLASH = 2;
    private Camera mCamera;
    private ImageView mTakePictureBtn;
    private ImageView mSwitchCameraBtn;
    private boolean isPreview = false;
    private SurfaceView mPreviewSV = null; //\u9884\u89c8SurfaceView
    private SurfaceHolder mySurfaceHolder = null;
    private Camera myCamera = null;
    private Bitmap mBitmap = null;
    private AutoFocusCallback myAutoFocusCallback = null;
    private boolean FlashLightStatus = false;
    private Button success_Button;
    private Size mPreviewSize;
    private boolean isSystemCameraRun = false;
    //zzk
    private boolean flag_is_shanguang = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
//		setContentView(R.layout.test_result);
        setContentView(R.layout.test_result_camera_front);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		Intent PhoneTestIntent =  new Intent("android.media.action.IMAGE_CAPTURE");
        success_Button = (Button) findViewById(R.id.Button_Success);
        Button fail_Button = (Button) findViewById(R.id.Button_Fail);
        //zzk add start
        mTakePictureBtn = (ImageView) findViewById(R.id.btn_capture);
        mSwitchCameraBtn = (ImageView) findViewById(R.id.btn_capturer);
        mPreviewSV = (SurfaceView) findViewById(R.id.previewSV);
        mPreviewSV.setVisibility(View.GONE);
        //zzk add end
        success_Button.setOnClickListener(this);
        fail_Button.setOnClickListener(this);
        success_Button.setEnabled(false);

        //tv_shanguang = (TextView) findViewById(R.id.tv_shanguang);
        tv_shexiang = (TextView) findViewById(R.id.tv_shexiang);
        try {
            //m_Camera = Camera.open();
            //mParameters = m_Camera.getParameters();
            //mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            //m_Camera.setParameters(mParameters);
            //m_Camera.startPreview();
        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText(CameraTest.this, getString(R.string.msg_camera_erro), Toast.LENGTH_SHORT).show();
        }

        final Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                if (mParameters != null) {
                    //mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                }
                if (m_Camera != null) {
                    //m_Camera.release();
                }
                isSystemCameraRun = true;

                mTimer.cancel();
                //tv_shanguang.setText(R.string.msg_shanguang_ok);
                tv_shexiang.setVisibility(View.VISIBLE);
                flag_is_shanguang = false;
                try {
                    Intent PhoneTestIntent = new Intent();
                    PhoneTestIntent.setClassName("com.mediatek.camera", "com.mediatek.camera.CameraLauncher");
                    startActivity(PhoneTestIntent);
                } catch (Exception eeee) {
                    try {
                        Intent PhoneTestIntent = new Intent();
                        //				PhoneTestIntent.setClassName("com.mediatek.camera", "com.mediatek.camera.Camera");
                        // PhoneTestIntent.setClassName("com.mediatek.camera", "com.mediatek.camera.VideoCamera");
                        // wanghe 2013-08-07 for factory test camera
                        PhoneTestIntent.setClassName("com.android.gallery3d", "com.android.camera.VideoCamera");
                        PhoneTestIntent.putExtra("lyaotao", 1);
                        startActivity(PhoneTestIntent);
                    } catch (Exception e) {
                        try {
                            Intent PhoneTestIntent = new Intent();
                            PhoneTestIntent.setClassName("com.google.android.GoogleCamera", "com.android.camera.CameraLauncher");
                            PhoneTestIntent.putExtra("lyaotao", 1);
                            startActivity(PhoneTestIntent);
                        } catch (Exception ee) {
                            try {
                                Intent PhoneTestIntent = new Intent();
                                PhoneTestIntent.setClassName("com.yunos.camera", "com.yunos.camera.CameraActivity");
                                PhoneTestIntent.putExtra("lyaotao", 1);
                                startActivity(PhoneTestIntent);
                            } catch (Exception eee) {
                                CameraTestStatus = -1;
                                finish();
                            }
                        }
                    }
                }

                //tv_shanguang.setText(R.string.msg_shexiang_ok);
                //tv_shexiang.setVisibility(View.GONE);

                super.handleMessage(msg);
            }
        };


        mTimer = new Timer();
        mTask = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                mHandler.sendMessage(message);
                Log.e("lyaotao", "12345");
            }
        };
        if (android.os.SystemProperties.getBoolean("ro.hx_flashlight_camera", false)) {
            //mTimer.schedule(mTask, 0, 1000);
            //zzk
            mSwitchCameraBtn.setVisibility(View.GONE);
            mTakePictureBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mTakePictureBtn.setEnabled(false);
                    if (android.os.SystemProperties.getBoolean("ro.hx_flashlight_always_on", false)) {
                        keepTorch();
                    } else {
                        flashBegin();
                    }
                    mTakePictureBtn.setVisibility(View.GONE);
                    SystemClock.sleep(2000);
                    FlashLightStatus = true;
                    tv_shexiang.setVisibility(View.GONE);
                    if (android.os.SystemProperties.getBoolean("ro.hx_open_system_back_camera", false)) {
                        mTimer.schedule(mTask, 0, 1000);
                    } else {
                        openCustomCamera();
                    }
                }
            });


            //zzk
        } else {
            if (android.os.SystemProperties.getBoolean("ro.hx_open_system_back_camera", false)) {
                mTimer.schedule(mTask, 0, 1000);
            } else {
                openCustomCamera();
            }
        }
        mSwitchCameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isPreview && myCamera != null) {
                    //zjw add for open flashled by 20170719
                    Parameters mParameters = myCamera.getParameters();
                    mParameters.setFlashMode(mParameters.FLASH_MODE_ON);
                    myCamera.setParameters(mParameters);
                    //zjw add end for open flashled by 20170719
                    myCamera.takePicture(myShutterCallback, null, myJpegCallback);
                    mSwitchCameraBtn.setEnabled(false);
                }
            }
        });
    }

    private void openCustomCamera() {
        //flag_is_shanguang = false;
        Log.d(TAG, "openCustomCamera: ");
        mTakePictureBtn.setVisibility(View.GONE);
        tv_shexiang.setVisibility(View.GONE);
        mPreviewSV.setVisibility(View.VISIBLE);
        mSwitchCameraBtn.setVisibility(View.VISIBLE);
        mySurfaceHolder = mPreviewSV.getHolder();
        mySurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);//translucent\u534a\u900f\u660e transparent\u900f\u660e
        mySurfaceHolder.addCallback(CameraTest.this);
        mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        myAutoFocusCallback = new AutoFocusCallback() {

            public void onAutoFocus(boolean success, Camera camera) {
                // TODO Auto-generated method stub
                if (success)//success\u8868\u793a\u5bf9\u7126\u6210\u529f
                {
                    Log.i(TAG, "myAutoFocusCallback: success...");
                    //myCamera.setOneShotPreviewCallback(null);

                } else {
                    //\u672a\u5bf9\u7126\u6210\u529f
                    Log.i(TAG, "myAutoFocusCallback: \u5931\u8d25\u4e86...");

                }


            }
        };
    }

    //zzk add
    private void flashBegin() {
        try {
            for (int i = 0; i < NUMBER_OF_FLASH; i++) {
                openFlash();
                SystemClock.sleep(100);
                closeFlash();
                SystemClock.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void keepTorch() {
        openFlash();
        SystemClock.sleep(3000);
        closeFlash();
    }

    private void openFlash() {
        m_Camera = Camera.open();
        m_Camera.startPreview();
        Parameters mParameters = m_Camera.getParameters();
        mParameters.setFlashMode(mParameters.FLASH_MODE_TORCH);
        m_Camera.setParameters(mParameters);
    }

    private void closeFlash() {
        Parameters mParameters = m_Camera.getParameters();
        mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        m_Camera.setParameters(mParameters);
        m_Camera.release();
        m_Camera = null;
        mParameters = null;
    }

    //zzk end
    protected void onResume() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onResume: ");
        if (isSystemCameraRun) {
            success_Button.setEnabled(true);
        }
        if (!flag_is_shanguang)
        //		tv_shanguang.setText(R.string.msg_shexiang_ok);
        {
            tv_shexiang.setVisibility(View.VISIBLE);
            tv_shexiang.setText(R.string.msg_shexiang_ok);
            mSwitchCameraBtn.setVisibility(View.GONE);
            mTakePictureBtn.setVisibility(View.GONE);
        } else if (FlashLightStatus == false && android.os.SystemProperties.getBoolean("ro.hx_flashlight_camera", false)) {
            //zzk add start
            tv_shexiang.setVisibility(View.VISIBLE);
            tv_shexiang.setText(R.string.msg_shanguang_ok);
            tv_shexiang.setTextColor(Color.RED);
            //zzk add start
        }
        super.onResume();
//		CameraTestStatus = 0;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.Button_Success:
                CameraTestStatus = 1;
                if (mTimer != null) {
                    mTimer.cancel();
                }
                //zzk
                if (mySurfaceHolder != null) {
                    mySurfaceHolder.removeCallback(CameraTest.this);
                    mySurfaceHolder = null;
                }
                if (myAutoFocusCallback != null) {
                    myAutoFocusCallback = null;
                }
                if (myCamera != null) {
                    myCamera.setPreviewCallback(null);
                    myCamera.stopPreview();
                    myCamera.release();
                    myCamera = null;
                }
                //zzk
                break;
            case R.id.Button_Fail:
                CameraTestStatus = -1;
                if (mTimer != null) {
                    mTimer.cancel();
                }
                break;
            default:
                break;
        }
        //intent.getExtras()
//		startActivity(intent);
        isSystemCameraRun = false;
        setResultBeforeFinish(CameraTestStatus);
        finish();
    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
//		super.onBackPressed();
    }

    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub

        if (mParameters != null) {
            mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        }
        if (m_Camera != null) {
            m_Camera.release();

        }

        super.onStop();
    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onDestroy: ");
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mParameters != null) {
            mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        }
        if (m_Camera != null) {
            m_Camera.release();
        }

        super.onDestroy();
    }

    //zzk add start
    /*\u4e0b\u9762\u4e09\u4e2a\u662fSurfaceHolder.Callback\u521b\u5efa\u7684\u56de\u8c03\u51fd\u6570*/
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    // \u5f53SurfaceView/\u9884\u89c8\u754c\u9762\u7684\u683c\u5f0f\u548c\u5927\u5c0f\u53d1\u751f\u6539\u53d8\u65f6\uff0c\u8be5\u65b9\u6cd5\u88ab\u8c03\u7528
    {
        // TODO Auto-generated method stub
        Log.i(TAG, "SurfaceHolder.Callback:surfaceChanged!");
        initCamera();

    }


    public void surfaceCreated(SurfaceHolder holder)
    // SurfaceView\u542f\u52a8\u65f6/\u521d\u6b21\u5b9e\u4f8b\u5316\uff0c\u9884\u89c8\u754c\u9762\u88ab\u521b\u5efa\u65f6\uff0c\u8be5\u65b9\u6cd5\u88ab\u8c03\u7528\u3002
    {
        // TODO Auto-generated method stub
        myCamera = Camera.open();
        try {
            myCamera.setPreviewDisplay(mySurfaceHolder);
            Log.i(TAG, "SurfaceHolder.Callback: surfaceCreated!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            if (null != myCamera) {
                myCamera.release();
                myCamera = null;
            }
            e.printStackTrace();
        }


    }


    public void surfaceDestroyed(SurfaceHolder holder)
    //\u9500\u6bc1\u65f6\u88ab\u8c03\u7528
    {
        // TODO Auto-generated method stub
        Log.i(TAG, "SurfaceHolder.Callback\uff1aSurface Destroyed");
        if (null != myCamera) {
            myCamera.setPreviewCallback(null); /*\u5728\u542f\u52a8PreviewCallback\u65f6\u8fd9\u4e2a\u5fc5\u987b\u5728\u524d\u4e0d\u7136\u9000\u51fa\u51fa\u9519\u3002
			\u8fd9\u91cc\u5b9e\u9645\u4e0a\u6ce8\u91ca\u6389\u4e5f\u6ca1\u5173\u7cfb*/

            myCamera.stopPreview();
            isPreview = false;
            myCamera.release();
            myCamera = null;
        }

    }

    //\u521d\u59cb\u5316\u76f8\u673a
    public void initCamera2() {
        Log.d(TAG, "initCamera: ");
        if (isPreview) {
            myCamera.stopPreview();
        }
        if (null != myCamera) {
            Parameters myParam = myCamera.getParameters();
            //			//\u67e5\u8be2\u5c4f\u5e55\u7684\u5bbd\u548c\u9ad8
            //			WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            //			Display display = wm.getDefaultDisplay();
            //			Log.i(tag, "\u5c4f\u5e55\u5bbd\u5ea6\uff1a"+display.getWidth()+" \u5c4f\u5e55\u9ad8\u5ea6:"+display.getHeight());
            //zzk add
            double targetRatio = (double) 4 / (double) 3;
            double aspectTolerance = 0.02;
            List<Size> supported = myParam.getSupportedPictureSizes();
            Log.i(TAG, "wlf frontcameratest supported = " + supported);
            if (supported == null)
                return;
            int width = 320, height = 240;
            for (Size candidate : supported) {
                width = candidate.width;
                height = candidate.height;
                double ratio = (double) width / (double) height;
                if (Math.abs(ratio - targetRatio) < aspectTolerance) {
                    myParam.setPictureSize(width, height);
                    myParam.setPreviewSize(width, height);
                    //mParameters.setPreviewSize(height, width);
                    Log.i(TAG, "wlf frontcameratest mParameters width = " + width + ", height = " + height);
                    break;
                }
            }
            mPreviewSize = myParam.getPreviewSize();

            Point point = new Point();
            {
                CameraTest.this.getWindowManager().getDefaultDisplay().getSize(point);
            }

            //mSurfaceView.getLayoutParams().width = point.x;
            //mSurfaceView.getLayoutParams().height = (int)(width * targetRatio);
            //mSurfaceView.getLayoutParams().height = point.x * mPreviewSize.width / mPreviewSize.height;

            //by yds start
            LayoutParams lp = mPreviewSV.getLayoutParams();
            lp.height = point.x * mPreviewSize.width / mPreviewSize.height;
            lp.width = point.x;
            mPreviewSV.setLayoutParams(lp);
            //zzk add
            myParam.setPictureFormat(PixelFormat.JPEG);//\u8bbe\u7f6e\u62cd\u7167\u540e\u5b58\u50a8\u7684\u56fe\u7247\u683c\u5f0f
            //\u8bbe\u7f6e\u5927\u5c0f\u548c\u65b9\u5411\u7b49\u53c2\u6570

            //myParam.setPictureSize(1280, 960);
            //myParam.setPreviewSize(960, 720);
            //myParam.set("rotation", 90);
            myCamera.setDisplayOrientation(90);

            if (FeatureOption.HX_CAM_FOCUS_MODE_COMP) {
                List<String> supportedFocusModes = myParam.getSupportedFocusModes();
                if (supportedFocusModes.size() == 1) {
                    myParam.setFocusMode(Parameters.FOCUS_MODE_FIXED);
                } else {
                    myParam.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
            } else {
                myParam.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            myCamera.setParameters(myParam);
            myCamera.startPreview();
            myCamera.autoFocus(myAutoFocusCallback);
            isPreview = true;
        }
    }

    public void initCamera() {
        Log.d(TAG, "initCamera: ");
        if (isPreview) {
            myCamera.stopPreview();
        }
        if (null != myCamera) {
            Camera.Parameters params = myCamera.getParameters();

            List<Size> supportedPictureSizes
                    = params.getSupportedPictureSizes();
            List<Size> supportedPreviewSizes
                    = params.getSupportedPreviewSizes();

            if (supportedPictureSizes != null &&
                    supportedPreviewSizes != null &&
                    supportedPictureSizes.size() > 0 &&
                    supportedPreviewSizes.size() > 0) {

                //2.x
                Size pictureSize = supportedPictureSizes.get(0);

                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);

                int maxWidth = display.getWidth();
                int maxHeight = display.getHeight();
                if (maxWidth > 0 && maxHeight > 0) {
                    for (Size size : supportedPictureSizes) {
                        if (maxWidth >= size.width && maxHeight >= size.height) {
                            pictureSize = size;
                            break;
                        }
                    }
                }

                Size previewSize = getOptimalPreviewSize(
                        supportedPreviewSizes,
                        display.getWidth(),
                        display.getHeight());

                params.setPictureSize(pictureSize.width, pictureSize.height);
                params.setPreviewSize(previewSize.width, previewSize.height);

            }
            myCamera.setDisplayOrientation(90);
            myCamera.setParameters(params);
            myCamera.startPreview();
            myCamera.autoFocus(myAutoFocusCallback);
            isPreview = true;
        }
    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /*\u4e3a\u4e86\u5b9e\u73b0\u62cd\u7167\u7684\u5feb\u95e8\u58f0\u97f3\u53ca\u62cd\u7167\u4fdd\u5b58\u7167\u7247\u9700\u8981\u4e0b\u9762\u4e09\u4e2a\u56de\u8c03\u53d8\u91cf*/
    ShutterCallback myShutterCallback = new ShutterCallback()
            //\u5feb\u95e8\u6309\u4e0b\u7684\u56de\u8c03\uff0c\u5728\u8fd9\u91cc\u6211\u4eec\u53ef\u4ee5\u8bbe\u7f6e\u7c7b\u4f3c\u64ad\u653e\u201c\u5494\u5693\u201d\u58f0\u4e4b\u7c7b\u7684\u64cd\u4f5c\u3002\u9ed8\u8ba4\u7684\u5c31\u662f\u5494\u5693\u3002
    {

        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");

        }
    };
    PictureCallback myRawCallback = new PictureCallback()
            // \u62cd\u6444\u7684\u672a\u538b\u7f29\u539f\u6570\u636e\u7684\u56de\u8c03,\u53ef\u4ee5\u4e3anull
    {

        public void onPictureTaken(byte[] data, Camera camera) {

            Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };
    PictureCallback myJpegCallback = new PictureCallback()
            //\u5bf9jpeg\u56fe\u50cf\u6570\u636e\u7684\u56de\u8c03,\u6700\u91cd\u8981\u7684\u4e00\u4e2a\u56de\u8c03
    {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Toast.makeText(CameraTest.this, getString(R.string.picture_saved_at), Toast.LENGTH_SHORT).show();
            success_Button.setEnabled(true);
            if (null != data) {
                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//data\u662f\u5b57\u8282\u6570\u636e\uff0c\u5c06\u5176\u89e3\u6790\u6210\u4f4d\u56fe
                myCamera.stopPreview();
                isPreview = false;
            }
            //\u8bbe\u7f6eFOCUS_MODE_CONTINUOUS_VIDEO)\u4e4b\u540e\uff0cmyParam.set("rotation", 90)\u5931\u6548\u3002\u56fe\u7247\u7adf\u7136\u4e0d\u80fd\u65cb\u8f6c\u4e86\uff0c\u6545\u8fd9\u91cc\u8981\u65cb\u8f6c\u4e0b
            Matrix matrix = new Matrix();
            matrix.postRotate((float) 90.0);
            Bitmap rotaBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
            //\u4fdd\u5b58\u56fe\u7247\u5230sdcard
            if (null != rotaBitmap) {
                saveJpeg(rotaBitmap);
            }

            //\u518d\u6b21\u8fdb\u5165\u9884\u89c8
            myCamera.startPreview();
            isPreview = true;
            mSwitchCameraBtn.setEnabled(true);
        }
    };

    /*\u7ed9\u5b9a\u4e00\u4e2aBitmap\uff0c\u8fdb\u884c\u4fdd\u5b58*/
    public void saveJpeg(Bitmap bm) {
        String savePath = "/mnt/sdcard/rectPhoto/";
        File folder = new File(savePath);
        if (!folder.exists()) //\u5982\u679c\u6587\u4ef6\u5939\u4e0d\u5b58\u5728\u5219\u521b\u5efa
        {
            folder.mkdir();
        }
        long dataTake = System.currentTimeMillis();
        String jpegName = savePath + dataTake + ".jpg";
        Log.i(TAG, "saveJpeg:jpegName--" + jpegName);
        //File jpegFile = new File(jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);

            //			//\u5982\u679c\u9700\u8981\u6539\u53d8\u5927\u5c0f(\u9ed8\u8ba4\u7684\u662f\u5bbd960�\u9ad81280),\u5982\u6539\u6210\u5bbd600�\u9ad8800
            //			Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);

            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            Log.i(TAG, "saveJpeg\uff1a\u5b58\u50a8\u5b8c\u6bd5\uff01");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "saveJpeg:\u5b58\u50a8\u5931\u8d25\uff01");
            e.printStackTrace();
        }
    }

    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "back_camera");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }

}


