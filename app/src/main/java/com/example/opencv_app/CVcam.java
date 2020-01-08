package com.example.opencv_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CVcam extends AppCompatActivity  implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final int PICK_IMAGE = 100;
    private static final String TAG = "MainActivity";
    private JavaCameraView mOpenCvCameraView;
    private static final int REQUEST_CODE = 123;
    private Button mButton;
    private Mat mRgba;
    private volatile Mat mByte;
    private volatile Mat imGgray, imgCandy;
    private Mat mRgbaF;
    private Mat  mRgbaT;
    CameraBridgeViewBase.CvCameraViewFrame inputFrame;
    int menu_option=0;
    //var for frot camera
    Thread t1;
    private Button mButton2;
    int cam_index=0;
    boolean rd=false;
    // end var
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cvcam);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //   findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(CVcam.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        mButton = (Button) findViewById(R.id.button2);
        mButton2 = (Button) findViewById(R.id.button3);
        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(0);
        mButton.setOnClickListener(new View.OnClickListener() { // take a picture
            @Override
            public void onClick(View v) {
                try {
                    ts();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cam_index == 0) {
                    cam_index = 1;
                    mOpenCvCameraView.disableView();
                    mOpenCvCameraView.setCameraIndex(cam_index);
                    mOpenCvCameraView.enableView();
                } else {
                    cam_index = 0;
                    mOpenCvCameraView.disableView();
                    mOpenCvCameraView.setCameraIndex(cam_index);
                    mOpenCvCameraView.enableView();
                }
            }
        });
    }
    // Toast
    public void ts() {
        mOpenCvCameraView.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = null; //Mat x=mByte;
                try { if(mByte.width()<=0&& mByte.height()<=0){}
                else{
                    bmp = Bitmap.createBitmap(mByte.cols(), mByte.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(mByte, bmp);}
                } catch (CvException e) {
                    Log.d(TAG, e.getMessage());
                }
                MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "Title" , "OpenCV");
                mByte.release();

            }
        });
        Toast.makeText(this, "Saving file", Toast.LENGTH_LONG).show();

    }
    // END Toast
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //   int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//           if (id == R.id.action_settings) {
//             return true;
//           }
        switch (item.getItemId()){
            case R.id.menu_main_setting: {
                Toast.makeText(this, "This is working", Toast.LENGTH_LONG).show();
                menu_option=0;
                break;
            }
            case R.id.menu_main_setting2: {
                Toast.makeText(this, "This is working", Toast.LENGTH_LONG).show();
                menu_option=1;
                break;
            }
            case R.id.menu_main_setting3: {
                Toast.makeText(this, "This is working", Toast.LENGTH_LONG).show();
                menu_option=2;
                break;
            }
            case R.id.menu_main_setting4: {
                Toast.makeText(this, "This is working", Toast.LENGTH_LONG).show();
                menu_option=3;
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    BaseLoaderCallback mBaseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                { // try
                    rd=true;
                    //end
                    mOpenCvCameraView.enableView();
                    break;
                }
                default:
                {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };
    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug() == true) {
            Log.i(TAG, "opencv loaded");
            mBaseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.i(TAG, "opencn not loade");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mBaseLoaderCallback);
        }
    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        //
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        //
        mByte = new Mat(height, width, CvType.CV_8UC4);
        imGgray = new Mat(height, width, CvType.CV_8UC4);
        imgCandy = new Mat(height, width, CvType.CV_8UC1);
    }
    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        // START
        // Rotate mRgba 90 degrees
        if(cam_index==0) {
            Core.transpose(mRgba, mRgbaT);
            Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0, 0, 0);
            Core.flip(mRgbaF, mRgba, 1);
        }
        else {
            Core.transpose(mRgba, mRgbaT);
            Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0, 0, 0);
            Core.flip(mRgbaF, mRgba, -1);
        }
        //END

        switch(menu_option){
            case 1:{
                Imgproc.cvtColor(mRgba, mByte, Imgproc.COLOR_RGB2YUV);
                // Imgproc.adaptiveThreshold(imGgray, mByte, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 35, 5);
                break;
            }
            case 2:{
                Imgproc.cvtColor(mRgba, mByte, Imgproc.COLOR_RGB2HSV);
                // Imgproc.adaptiveThreshold(imGgray, mByte, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 35, 5);
                break;
            }
            case 3:{
                Imgproc.cvtColor(mRgba, mByte, Imgproc.COLOR_RGB2Luv);
                // Imgproc.adaptiveThreshold(imGgray, mByte, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 35, 5);
                break;
            }
            default:{
                Imgproc.cvtColor(mRgba, imGgray, Imgproc.COLOR_RGB2GRAY);
                Imgproc.adaptiveThreshold(imGgray, mByte, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 35, 5);
                break;
            }
        }
//        Imgproc.cvtColor(mRgba, imGgray, Imgproc.COLOR_RGB2GRAY);
//        Imgproc.adaptiveThreshold(imGgray, mByte, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 35, 5);
        return mByte; // this is m Binary image
    }
}

