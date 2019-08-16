package com.waterworld.factorytest.activitys;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

import java.io.IOException;

public class AssistMasterCamera extends FactoryActivity {


    private static final String TAG = Utils.TAG+"AssistMasterCamera" ;



    private Context mContext;

    private SurfaceHolder surfaceHolder;
    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private Button mSuccessBtn;

    private static  final int   AssistMasterCameraID = 2;
//    private static  final int   AssistFrontCameraID = 3;
    //
    private int findAssistBackCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        if( cameraCount  >= AssistMasterCameraID  ){
            return AssistMasterCameraID;
        }else {
            return -1;
        }
    }

    public Camera getCameraInstance(int cameraID) {
        Camera camera = null;
        try {
            camera = Camera.open(cameraID); // attempt to get a Camera instance
        } catch (Exception e) {
            //Camera is notavailable (in use or does not exist)
        }

        return camera; // returns nullif camera is unavailable
    }

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext  = getBaseContext();
        // Inflate the SurfaceView, set it as the main layout, and attach a listener
        setContentView(R.layout.master_camera);
        mSurfaceView = (SurfaceView) findViewById(R.id.mainSurfaceView);

        mSuccessBtn = findViewById( R.id.Button_Success);



    }

    protected  void onResume( ){
        super.onResume();
        Log.d(TAG, "onResume ");
        mCamera = getCameraInstance( findAssistBackCamera() );
        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback( mSurfaceHolderCallback );
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        }catch (Exception e){
            Log.d(TAG, "initAssisMasterCamera: "+e.toString());
        }
        if(mCamera != null) {
            try {
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
            } catch (RuntimeException e) {
                Log.d(TAG, "takePhotos: "+e.toString());
                Log.d(TAG, "takePhotos: "+Log.getStackTraceString(new Throwable()));

            }
        }
    }

    public void takeCapture(View view ) {
        try{
            mCamera.takePicture(null, null, pictureCallback);
        }catch (Exception e){
            Log.d(TAG, "takeCapture: ", new Throwable());
        }

    }

    public void onSuccessClick( View view){
        setResultBeforeFinish( Utils.SUCCESS);
        finish();
    }
    public void onFailClick( View view){
        setResultBeforeFinish( Utils.FAILED);
        finish();

    }
    final SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
        /** The camera device to use, or null if we haven't yet set a fixed surface size. */

        /** Whether we received a change callback after setting our fixed surface size. */

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // This is called every time the surface returns to the foreground

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "Surface destroyed");
            if (mCamera != null) {
                // 停止预览
                mCamera.stopPreview();
                // 释放摄像头
                mCamera.release();
                mCamera = null;
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // On the first invocation, width and height were automatically set to the view's size
            Log.d(TAG, "surfaceChanged: "+width+" "+height);
            Log.i(TAG, "Surface created");
            mCamera.setDisplayOrientation( 90 );
            // 重新开始预览
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d(TAG, "预览失败");
            }
        }
    };

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            // 停止预览
            Log.d(TAG, "onPictureTaken: ");
            mSuccessBtn.setEnabled( true );
            mCamera.stopPreview();
            mCamera.startPreview();
        }

    };


    public void finish() {
        super.finish();
//        setResultBeforeFinish( Coupling_test_result );
    }
//    static Size chooseBigEnoughSize(Size[] choices, int width, int height) {
//        // Collect the supported resolutions that are at least as big as the preview Surface
//        Log.d(TAG, "chooseBigEnoughSize: "+width+" "+height);
//        List<Size> bigEnough = new ArrayList<Size>();
//        for (Size option : choices) {
//            if (option.getWidth() >= width && option.getHeight() >= height) {
//                bigEnough.add(option);
//            }
//        }
//
//        // Pick the smallest of those, assuming we found any
//        if (bigEnough.size() > 0) {
//            return Collections.min(bigEnough, new CompareSizesByArea());
//        } else {
//            Log.e(TAG, "Couldn't find any suitable preview size");
//            return choices[0];
//        }
//    }

    @Override
    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "AssistCamera");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);

    }


    public void onBackPressed() {
        // TODO Auto-generated method stub
//		super.onBackPressed();
    }

}
