package com.waterworld.factorytest.activitys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.waterworld.factorytest.FactoryActivity;
import com.waterworld.factorytest.FactoryTestFeatureoption;
import com.waterworld.factorytest.R;
import com.waterworld.factorytest.Utils;

import java.util.List;

public class SecondFlashTest extends FactoryActivity{

    private Camera mCamera;
    private int    SECOND_FLASH_test_result;
    private int    cameraIndex;
    private static final String TAG = Utils.TAG+"SecondFlashTest" ;
    private String mCameraId;

    private  CameraManager mCameraManager;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.master_flash_test);

        openFlash(  );

        showDialog( );

    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setCancelable( false)
                .setMessage(getString(R.string.sure_secondflash))
                .setNegativeButton(R.string.msg_fou,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SECOND_FLASH_test_result = Utils.FAILED;
                                setResultBeforeFinish( SECOND_FLASH_test_result );
                                closeFlash();
                                finish();
                            }
                        })
                .setPositiveButton(R.string.msg_shi,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                SECOND_FLASH_test_result = Utils.SUCCESS;
                                setResultBeforeFinish( SECOND_FLASH_test_result );
                                closeFlash();
                                finish();

                            }
                        })
                .create()
                .show();

    }

    private void openFlash( ) {
        Log.d(TAG, "openFlash: ");

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = getCameraId();

            mCameraManager.setTorchMode(mCameraId, true);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't set torch mode", e);
        }
    }
    private String getCameraId() throws CameraAccessException {
        String[] ids = mCameraManager.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
            Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
            if (flashAvailable != null && flashAvailable
                    && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                return id;
            }
        }
        return null;
    }



    private void closeFlash() {


        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraManager.setTorchMode(mCameraId, false);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't set torch mode" + e.toString());
        }

    }

    public void finish() {
        super.finish();
//        setResultBeforeFinish( Coupling_test_result );
    }

    @Override
    public void setResultBeforeFinish(int status) {
        Log.d(TAG, "setResultBeforeFinish: " + status);
        Intent intent = new Intent();
        intent.putExtra(Utils.NAME, "secondFlash");
        intent.putExtra(Utils.TEST_RESULT, status);
        setResult(status, intent);

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
//		super.onBackPressed();
        setResultBeforeFinish( SECOND_FLASH_test_result );
        finish();


    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onDestroy: ");
        if (mCamera != null) {
            mCamera.release();
        }

        super.onDestroy();
    }

}
