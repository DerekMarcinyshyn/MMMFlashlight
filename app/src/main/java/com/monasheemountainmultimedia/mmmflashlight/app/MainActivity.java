package com.monasheemountainmultimedia.mmmflashlight.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageButton;

import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private Camera camera;
    private boolean isFlashOn = false;
    Parameters params;
    SurfaceHolder mHolder;
    SurfaceView preview;
    ImageButton btnSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        setContentView(R.layout.activity_main);

        // flashlight switch button
        btnSwitch = (ImageButton) findViewById(R.id.btnSwitch);

        Context context = this;
        PackageManager pm = context.getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("ERROR", "Device has no camera!");
            return;
        }

        // init the surface view
        setSurfaceView();

        // get the camera
        camera = Camera.open();
        params = camera.getParameters();
        Log.v("MMM: ", "Camera.open() and params: " + params);

        // set initial state of button
        toggleButtonImage();

        btnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    // turn off flashlight
                    turnOffFlash();
                } else {
                    // turn on flashlight
                    turnOnFlash();
                }
            }
        });
    }

    private void setSurfaceView() {
        preview = (SurfaceView) this.findViewById(R.id.PREVIEW);
        mHolder = preview.getHolder();
        mHolder.addCallback(this);
        mHolder.setKeepScreenOn(true);
    }

    /** Turn on the Flashlight using FLASH_MODE_TORCH */
    private void turnOnFlash() {
        if (!isFlashOn) {
            // sanity check
            if (camera == null || params == null) {
                Log.v("MMM: ", "turnOnFlash - camera and param are null");
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);

            camera.setParameters(params);
            try {
                camera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                Log.e("ERROR", "IOException camera.setPreviewDisplay.");
            }

            camera.startPreview();
            isFlashOn = true;

            // toggle image to show state
            toggleButtonImage();
        }
    }

    /** Turn off flashlight with FLASH_MODE_OFF */
    private void turnOffFlash() {
        if (isFlashOn) {
            // sanity check
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);

            camera.setParameters(params);
            camera.stopPreview();

            isFlashOn = false;

            // toggle image to show state
            toggleButtonImage();
        }
    }

    /** Toggle the button image */
    private void toggleButtonImage() {
        if (isFlashOn) {
            btnSwitch.setImageResource(R.drawable.button_on);
        } else {
            btnSwitch.setImageResource(R.drawable.button_off);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOffFlash();
    }

    @Override
    protected void onPause() {
        super.onPause();
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        turnOffFlash();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // set the preview display of the camera to the holder surface view
        try {
            camera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            //e.printStackTrace();
            Log.e("ERROR", "IOException camera.setPreviewDisplay(mHolder);");
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
    }
}
