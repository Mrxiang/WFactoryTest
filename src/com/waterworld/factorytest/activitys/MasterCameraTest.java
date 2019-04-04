package com.waterworld.factorytest.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MasterCameraTest extends FactoryActivity {


    private Button success;
    private Button failed;
    private int    LED_test_result;
    private static final String TAG = Utils.TAG+"MasterCameraTest" ;
    private CameraManager mCameraManager;
    SurfaceView mSurfaceView;
    CameraDevice mCamera;
    ImageReader mCaptureBuffer;
    Handler mBackgroundHandler;
    HandlerThread mBackgroundThread;
    CameraCaptureSession mCaptureSession;
    private Context mContext;
    static final String CAPTURE_FILENAME_PREFIX = "cameratoo";

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext  = getBaseContext();
        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        mBackgroundThread = new HandlerThread("background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());

        // Inflate the SurfaceView, set it as the main layout, and attach a listener
        View layout = getLayoutInflater().inflate(R.layout.master_camera, null);
        setContentView(layout);
        mSurfaceView = (SurfaceView) layout.findViewById(R.id.mainSurfaceView);
        mSurfaceView.getHolder().addCallback(mSurfaceHolderCallback);

    }

//    public void onClickOnSurfaceView(View v) {
    public void takeCapture(View v) {
        if (mCaptureSession != null) {
            try {
                CaptureRequest.Builder requester =
                        mCamera.createCaptureRequest(mCamera.TEMPLATE_STILL_CAPTURE);
                requester.addTarget(mCaptureBuffer.getSurface());
                try {
                    // This handler can be null because we aren't actually attaching any callback
                    mCaptureSession.capture(requester.build(), /*listener*/null, /*handler*/null);
                } catch (CameraAccessException ex) {
                    Log.e(TAG, "Failed to file actual capture request", ex);
                }
                findViewById(R.id.RV_layout).setVisibility( View.VISIBLE);//
                findViewById( R.id.Button_capture).setVisibility( View.GONE );
            } catch (CameraAccessException ex) {
                Log.e(TAG, "Failed to build actual capture request", ex);
            }
        } else {
            Log.e(TAG, "User attempted to perform a capture outside our session");
        }

        // Control flow continues in mImageCaptureListener.onImageAvailable()
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
        private String mCameraId;

        /** Whether we received a change callback after setting our fixed surface size. */
        private boolean mGotSecondCallback;

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // This is called every time the surface returns to the foreground
            Log.i(TAG, "Surface created");
            mCameraId = null;
            mGotSecondCallback = false;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "Surface destroyed");
            holder.removeCallback(this);
            // We don't stop receiving callbacks forever because onResume() will reattach us
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // On the first invocation, width and height were automatically set to the view's size
            Log.d(TAG, "surfaceChanged: "+width+" "+height);
            if (mCameraId == null) {
                // Find the device's back-facing camera and set the destination buffer sizes
                try {
                    for (String cameraId : mCameraManager.getCameraIdList()) {
                        CameraCharacteristics cameraCharacteristics =
                                mCameraManager.getCameraCharacteristics(cameraId);
                        if (cameraCharacteristics.get(cameraCharacteristics.LENS_FACING) ==
                                CameraCharacteristics.LENS_FACING_BACK) {
                            Log.i(TAG, "Found a back-facing camera");
                            StreamConfigurationMap info = cameraCharacteristics
                                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                            // Bigger is better when it comes to saving our image
                            Size largestSize = Collections.max(
                                    Arrays.asList(info.getOutputSizes(ImageFormat.JPEG)),
                                    new CompareSizesByArea());

                            // Prepare an ImageReader in case the user wants to capture images
                            Log.i(TAG, "Capture size: " + largestSize);
                            mCaptureBuffer = ImageReader.newInstance(largestSize.getWidth(),
                                    largestSize.getHeight(), ImageFormat.JPEG, /*maxImages*/2);
                            mCaptureBuffer.setOnImageAvailableListener(
                                    mImageCaptureListener, mBackgroundHandler);

                            // Danger, W.R.! Attempting to use too large a preview size could
                            // exceed the camera bus' bandwidth limitation, resulting in
                            // gorgeous previews but the storage of garbage capture data.
                            Log.i(TAG, "SurfaceView size: "+ mSurfaceView.getWidth() + 'x' + mSurfaceView.getHeight());
                            Size optimalSize = chooseBigEnoughSize(
                                    info.getOutputSizes(SurfaceHolder.class), width, height);

                            // Set the SurfaceHolder to use the camera's largest supported size
                            Log.i(TAG, "Preview size: " + optimalSize);
                            SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
                            surfaceHolder.setFixedSize(optimalSize.getWidth(),
                                    optimalSize.getHeight());

                            mCameraId = cameraId;
                            return;

                            // Control flow continues with this method one more time
                            // (since we just changed our own size)
                        }
                    }
                } catch (CameraAccessException ex) {
                    Log.e(TAG, "Unable to list cameras", ex);
                }

                Log.e(TAG, "Didn't find any back-facing cameras");
                // This is the second time the method is being invoked: our size change is complete
            } else if (!mGotSecondCallback) {
                if (mCamera != null) {
                    Log.e(TAG, "Aborting camera open because it hadn't been closed");
                    return;
                }

                // Open the camera device
                try {
                    mCameraManager.openCamera(mCameraId, mCameraStateCallback,
                            mBackgroundHandler);
                } catch (CameraAccessException ex) {
                    Log.e(TAG, "Failed to configure output surface", ex);
                }
                mGotSecondCallback = true;

                // Control flow continues in mCameraStateCallback.onOpened()
            }
        }
    };

    final CameraDevice.StateCallback mCameraStateCallback =
            new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    Log.i(TAG, "Successfully opened camera"+camera.getId());
                    mCamera = camera;
                    try {
                        List<Surface> outputs = Arrays.asList(
                                mSurfaceView.getHolder().getSurface(), mCaptureBuffer.getSurface());
                        camera.createCaptureSession(outputs, mCaptureSessionListener,
                                mBackgroundHandler);
                    } catch (CameraAccessException ex) {
                        Log.e(TAG, "Failed to create a capture session", ex);
                    }

                    // Control flow continues in mCaptureSessionListener.onConfigured()
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    Log.e(TAG, "Camera was disconnected");
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    Log.e(TAG, "State error on device '" + camera.getId() + "': code " + error);
                }
    };


    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    final ImageReader.OnImageAvailableListener mImageCaptureListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    // Save the image once we get a chance
                    Log.d(TAG, "onImageAvailable: "+reader);
                    mBackgroundHandler.post(new CapturedImageSaver(reader.acquireNextImage()));

                    // Control flow continues in CapturedImageSaver#run()
                }
    };

    class CapturedImageSaver implements Runnable {
        /** The image to save. */
        private Image mCapture;

        public CapturedImageSaver(Image capture) {
            mCapture = capture;
        }

        @Override
        public void run() {
            try {
                // Choose an unused filename under the Pictures/ directory
                File file = File.createTempFile(CAPTURE_FILENAME_PREFIX, ".jpg",
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES));
                Log.d(TAG, "run: "+file.getAbsolutePath());
                try (FileOutputStream ostream = new FileOutputStream(file)) {
                    Log.i(TAG, "Retrieved image is" +
                            (mCapture.getFormat() == ImageFormat.JPEG ? "" : "n't") + " a JPEG");
                    ByteBuffer buffer = mCapture.getPlanes()[0].getBuffer();
                    Log.i(TAG, "Captured image size: " +
                            mCapture.getWidth() + 'x' + mCapture.getHeight());

                    // Write the image out to the chosen file
                    byte[] jpeg = new byte[buffer.remaining()];
                    buffer.get(jpeg);
                    ostream.write(jpeg);
                    Toast.makeText(mContext, mContext.getString(R.string.picture_saved_at), Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException ex) {
                    Log.e(TAG, "Unable to open output file for writing", ex);
                } catch (IOException ex) {
                    Log.e(TAG, "Failed to write the image to the output file", ex);
                }
            } catch (IOException ex) {
                Log.e(TAG, "Unable to create a new output file", ex);
            } finally {
                mCapture.close();
            }
        }
    }
    final CameraCaptureSession.StateCallback mCaptureSessionListener =
            new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    Log.i(TAG, "Finished configuring camera outputs");
                    mCaptureSession = session;

                    SurfaceHolder holder = mSurfaceView.getHolder();
                    if (holder != null) {
                        try {
                            // Build a request for preview footage
                            CaptureRequest.Builder requestBuilder =
                                    mCamera.createCaptureRequest(mCamera.TEMPLATE_PREVIEW);
                            requestBuilder.addTarget(holder.getSurface());
                            CaptureRequest previewRequest = requestBuilder.build();

                            // Start displaying preview images
                            try {
                                session.setRepeatingRequest(previewRequest, /*listener*/null,
                                        /*handler*/null);
                            } catch (CameraAccessException ex) {
                                Log.e(TAG, "Failed to make repeating preview request", ex);
                            }
                        } catch (CameraAccessException ex) {
                            Log.e(TAG, "Failed to build preview request", ex);
                        }
                    }
                    else {
                        Log.e(TAG, "Holder didn't exist when trying to formulate preview request");
                    }
                }

                @Override
                public void onClosed(CameraCaptureSession session) {
                    mCaptureSession = null;
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.e(TAG, "Configuration error on device '" + mCamera.getId());
                }
    };


    public void finish() {
        super.finish();
//        setResultBeforeFinish( Coupling_test_result );
    }
    static Size chooseBigEnoughSize(Size[] choices, int width, int height) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        Log.d(TAG, "chooseBigEnoughSize: "+width+" "+height);
        List<Size> bigEnough = new ArrayList<Size>();
        for (Size option : choices) {
            if (option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    @Override
    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "masterCamera");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);

    }

    protected void onPause() {
        super.onPause();
        try {
            // Ensure SurfaceHolderCallback#surfaceChanged() will run again if the user returns
            mSurfaceView.getHolder().setFixedSize(/*width*/0, /*height*/0);

            // Cancel any stale preview jobs
            if (mCaptureSession != null) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
        } finally {
            if (mCamera != null) {
                mCamera.close();
                mCamera = null;
            }
        }

        // Finish processing posted messages, then join on the handling thread
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
        } catch (InterruptedException ex) {
            Log.e(TAG, "Background worker thread was interrupted while joined", ex);
        }

        // Close the ImageReader now that the background thread has stopped
        if (mCaptureBuffer != null) mCaptureBuffer.close();
    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
//		super.onBackPressed();
        setResultBeforeFinish( LED_test_result );
        finish();
    }

}
