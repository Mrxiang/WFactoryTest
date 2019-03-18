package com.waterworld.factorytest.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.FactoryTestFeatureoption.FeatureOption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

//import android.os.YKQBaseUtil;

public class FrontCameraTest extends FactoryActivity implements OnClickListener {
    private static final String TAG = Utils.TAG + "FrontCameraTest";
    private SurfaceView mSurfaceView = null;
    private SurfaceView mSurfaceView1 = null;
    private SurfaceHolder mSurfaceHolder = null;
    private SurfaceHolder mSurfaceHolder1 = null;
    private ImageButton takePictureButton = null;
    private Camera mCamera;
    static int FrontCameraTestStatus = 0;
    static int Main2CameraTestStatus = 0;
    // 记录保存的是第几张图片
    private int whichPicture = 0;
    Timer mTimer;
    private int pl;
    private int flg = 0;
    // wlf add start
    private ImageButton mFlashSwitch;
    Parameters mParameters;
    private Handler mHandler;
    private Runnable mWaitingEvent;

    private ImageView flash_btn;
    private int NUMBER_OF_FLASH = 2;
    private Size mPreviewSize;
    public static String mCameraId = "1";
    public static boolean mCameraMain2Text = true;
    // end.
    private boolean isSystemCameraRun = false;//zzk add

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        setContentView(R.layout.test_result_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        success_Button = (Button) findViewById(R.id.Button_Success);
        // zzk add
        flashmsg = (TextView) findViewById(R.id.flash_msg);
        flash_btn = (ImageView) findViewById(R.id.btn_capture);
        flash_btn.setVisibility(View.GONE);
        flashmsg.setVisibility(View.GONE);
        // zzk add
        Button fail_Button = (Button) findViewById(R.id.Button_Fail);
        success_Button.setOnClickListener(this);
        fail_Button.setOnClickListener(this);
        success_Button.setEnabled(false);
        takePictureButton = (ImageButton) this
                .findViewById(R.id.Button_capture);
        mSurfaceView = (SurfaceView) this.findViewById(R.id.surfaceview);
        // mSurfaceView1 = (SurfaceView)this.findViewById(R.id.surfaceview1);
        if (android.os.SystemProperties.getBoolean(
                "ro.hx_flashlight_face_camera", false)) {
            flash_btn.setVisibility(View.VISIBLE);
            flashmsg.setVisibility(View.VISIBLE);
            mSurfaceView.setVisibility(View.GONE);
            flash_btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (android.os.SystemProperties.getBoolean(
                            "ro.hx_flashlight_always_on", false)) {
                        keepTorch();
                    } else {
                        flashBegin();
                    }
                    SystemClock.sleep(2000);
                    flash_btn.setVisibility(View.GONE);
                    flashmsg.setVisibility(View.GONE);
                    if (android.os.SystemProperties.getBoolean(
                            "ro.hx_system_front_camera", false)) {
                        openSystemFrontCamera();
                    } else {
                        openCustomFrontCamera();
                    }

                }
            });
        } else {
            if (android.os.SystemProperties.getBoolean(
                    "ro.hx_system_front_camera", false)) {
                openSystemFrontCamera();
            } else {
                openCustomFrontCamera();
            }
        }

        mFlashSwitch = (ImageButton) findViewById(R.id.Button_flash);// wlf
        if (!android.os.SystemProperties.getBoolean(
                "ro.hx_sub_flashlight_support", false)) {
            mFlashSwitch.setVisibility(View.GONE);
        }

        // 拍照按钮监听
        takePictureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 拍照
                mCamera.takePicture(null, null, pictureCallback);
                takePictureButton
                        .setImageResource(R.drawable.btn_shutter_photo_disable_focus);
                takePictureButton.setEnabled(false);
            }
        });

    }

    // zzk add start
    private void openSystemFrontCamera() {

        try {
            isSystemCameraRun = true;
            Intent PhoneTestIntent = new Intent();
            PhoneTestIntent.setClassName("com.mediatek.camera",
                    "com.mediatek.camera.CameraLauncher");
            PhoneTestIntent.putExtra("front_camera", 1);
            startActivity(PhoneTestIntent);
        } catch (Exception e) {
            //add by cjg
            if (FeatureOption.HX_DEPUTY_CAMERA_TEST) {
				/*if(YKQBaseUtil.readEnvPartition("CMainId") == 1){
					FrontCameraTestStatus = -1;
				}else{
					Main2CameraTestStatus = -1;
				}*/
            } else {
                FrontCameraTestStatus = -1;
            }
            //add end
            finish();
        }
    }

    private void openCustomFrontCamera() {
        mSurfaceView.setVisibility(View.VISIBLE);

        mSurfaceHolder = mSurfaceView.getHolder();

        mSurfaceHolder.addCallback(new SurfaceHolderCallback());

        // 设置缓冲区类型
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

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
        mCamera = Camera.open(1);
        mCamera.startPreview();
        Parameters mParameters = mCamera.getParameters();
        mParameters.setFlashMode(mParameters.FLASH_MODE_TORCH);
        mCamera.setParameters(mParameters);
    }

    private void closeFlash() {
        Parameters mParameters = mCamera.getParameters();
        mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(mParameters);
        mCamera.release();
        mCamera = null;
        mParameters = null;
    }

    // zzk end
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
    }

    public void onAttachedToWindow() {
        // TODO Auto-generated method stub

        super.onAttachedToWindow();
        // this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (mCamera != null) {
            // 停止预览
            mCamera.stopPreview();
            // 释放摄像头
            mCamera.release();
            mCamera = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onDestroy();
    }

    protected void onResume() {
        if (isSystemCameraRun) {
            success_Button.setEnabled(true);
        }
        if (mCamera != null) {
            // 鍋滄棰勮
            mCamera.stopPreview();
            // 閲婃斁鎽勫儚澶?
            mCamera.release();
            mCamera = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onResume();
        // CameraTestStatus = 0;
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.Button_Success:
                FrontCameraTestStatus = 1;
                break;
            case R.id.Button_Fail:
                FrontCameraTestStatus = -1;
                break;
            default:
                break;
        }
        setResultBeforeFinish(FrontCameraTestStatus);
        isSystemCameraRun = false;
        finish();
    }

    private class SurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            // 打开摄像头
            int CammeraIndex = FindFrontCamera();
            if (CammeraIndex == -1) {
                CammeraIndex = FindBackCamera();
            }
            try {
                mCamera = Camera.open(1);
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (RuntimeException e) {
                //add by cjg
                if (FeatureOption.HX_DEPUTY_CAMERA_TEST) {
					/*if(YKQBaseUtil.readEnvPartition("CMainId") == 1){
						FrontCameraTestStatus = -1;
					}else{
						Main2CameraTestStatus = -1;
					}*/
                } else {
                    FrontCameraTestStatus = -1;
                }
                //add end
                finish();
                return;
            }
            // 获取摄像头参数
            // Camera.Parameters
            mParameters = mCamera.getParameters();
            // wlf add start
            double targetRatio = (double) 4 / (double) 3;
            mParameters.setRotation(270);
            double aspectTolerance = 0.02;
//            List<Size> supported = mParameters.getSupportedPictureSizes();
            List<Size> supported = mParameters.getSupportedPreviewSizes();
            Log.i(TAG, "wlf frontcameratest supported = " + supported);
            if (supported == null)
                return;
            int width = 320, height = 240;
            for (Size candidate : supported) {
                width = candidate.width;
                height = candidate.height;
                double ratio = (double) width / (double) height;
                if (Math.abs(ratio - targetRatio) < aspectTolerance) {
                    mParameters.setPictureSize(width, height);
                    mParameters.setPreviewSize(width, height);
                    // mParameters.setPreviewSize(height, width);
                    Log.i(TAG, "wlf frontcameratest mParameters width = "
                            + width + ", height = " + height);
                    break;
                }
            }
            // mParameters.setPictureSize(width, height);
            mPreviewSize = mParameters.getPreviewSize();

            Point point = new Point();
            {
                FrontCameraTest.this.getWindowManager().getDefaultDisplay()
                        .getSize(point);
            }

            // mSurfaceView.getLayoutParams().width = point.x;
            // mSurfaceView.getLayoutParams().height = (int)(width *
            // targetRatio);
            // mSurfaceView.getLayoutParams().height = point.x *
            // mPreviewSize.width / mPreviewSize.height;

            // by yds start
            ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
            lp.height = point.x * mPreviewSize.width / mPreviewSize.height;
            lp.width = point.x;
            mSurfaceView.setLayoutParams(lp);
            // by yds end
            /*
             * DisplayMetrics dm = new DisplayMetrics();
             * getWindowManager().getDefaultDisplay().getMetrics(dm);
             * mSurfaceHolderRvLayout.getLayoutParams().height =
             * //dm.widthPixels; dm.widthPixels * 5 / 16; // dm.widthPixels *
             * mPreviewSize.height / mPreviewSize.width;
             * android.util.Log.e(TAG,
             * "mPreviewSize = ("+mPreviewSize.width+", "
             * +mPreviewSize.height+"), height = "
             * +mSurfaceHolderRvLayout.getLayoutParams().height
             * +", width = "+dm.widthPixels );
             */
            // wlf add end.
            // 设置图片格式
            mCamera.setDisplayOrientation(90);// 270
            mParameters.setPictureFormat(PixelFormat.JPEG);

            mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH); // wlf
            // 20151014
            mCamera.setParameters(mParameters);
            // 开始预览
            mCamera.startPreview();
            mHandler = new Handler();
            mWaitingEvent = new Runnable() {
                @Override
                public void run() {
                    mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                    mFlashSwitch
                            .setImageDrawable(getDrawable(R.drawable.ic_flash_off_holo_light));
                    // mFlashSwitch.setBackgroundResource(R.drawable.bk_true);
                    if (mCamera != null)// error occur one timer,do not know
                        // why,so add it
                        mCamera.setParameters(mParameters);
                }
            };
            mHandler.postDelayed(mWaitingEvent, 3000);
            mFlashSwitch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String flashMode = mParameters.getFlashMode();
                            if (flashMode.equals("torch")) {
                                mParameters
                                        .setFlashMode(Parameters.FLASH_MODE_OFF);
                                // mFlashSwitch.setBackgroundResource(R.drawable.bk_true);
                                mFlashSwitch
                                        .setImageDrawable(getDrawable(R.drawable.ic_flash_off_holo_light));
                            } else {
                                mParameters
                                        .setFlashMode(Parameters.FLASH_MODE_TORCH);
                                // mFlashSwitch.setBackgroundResource(R.drawable.bk_fail);
                                mFlashSwitch
                                        .setImageDrawable(getDrawable(R.drawable.ic_flash_on_holo_light));
                            }
                            mCamera.setParameters(mParameters);
                        }
                    });
                }
            });
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            if (mCamera != null) {

                mHandler.removeCallbacks(mWaitingEvent);
                Log.i(TAG, "surfaceDestroyed called.");
                // 停止预览
                mCamera.stopPreview();
                // 释放摄像头
                mCamera.release();
                mCamera = null;
            }
        }

    }

    // 拍照回调
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            // 停止预览
            mCamera.stopPreview();
            success_Button.setEnabled(true);
            Bitmap mBitmap;
            mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            // 文件路径和文件名
            File pictureFile = new File(
                    Environment.getExternalStorageDirectory(), "camera"
                    + Integer.toString(whichPicture) + ".jpg");

            try {
                FileOutputStream mFileOutputStream = new FileOutputStream(
                        pictureFile);
                // 将图像数据压入文件
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 75,
                        mFileOutputStream);
                try {
                    // 关闭输出流
                    mFileOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // displayToast("保存成功！");
            displayToast(getString(R.string.picture_saved_at));
            whichPicture++;
            takePictureButton.setImageResource(R.drawable.btn_shutter_photo);
            takePictureButton.setEnabled(true);
            // 开始预览
            mCamera.startPreview();
        }

    };
    private TextView flashmsg;
    private Button success_Button;

    // 显示Toast函数
    private void displayToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private int FindBackCamera() {
        int cameraCount = 0;
        CameraInfo cameraInfo = new CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    private int FindFrontCamera() {
        int cameraCount = 0;
        CameraInfo cameraInfo = new CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number
        //add by cjg
        if (FeatureOption.HX_DEPUTY_CAMERA_TEST) {
			/*if(mCameraId.equals("1")){
				YKQBaseUtil.writeConfigPartition("CMainId",mCameraId);
			}else{
				YKQBaseUtil.writeConfigPartition("CMainId",mCameraId);
			}*/
        }
        //add end
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "front_camera");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);
    }
}
