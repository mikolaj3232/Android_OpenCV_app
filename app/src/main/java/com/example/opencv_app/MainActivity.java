package com.example.opencv_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.hardware.camera2.*;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.net.Uri;
import android.view.WindowManager;
import android.widget.Button;
import android.hardware.Camera.PictureCallback;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
private static final int PICK_IMAGE = 100;
    private static final String TAG = "MainActivity";
    private JavaCameraView mOpenCvCameraView;
    private static final int REQUEST_CODE = 123;
    private Button mButton;
    private Mat mRgba;
    private Mat mByte;
    private Mat imGgray, imgCandy;
    CameraBridgeViewBase.CvCameraViewFrame inputFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       // setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.tc);
        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mButton.setOnClickListener(new View.OnClickListener() { // take a picture
            @Override
            public void onClick(View v) {

            }
        });


    }
    public void opengallery(View v){
        Intent g = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(g, PICK_IMAGE);
      //  Uri u = g.getData();
      //  Intent intent = new Intent(this, Edit_Picture.class);
       // intent.putExtra("image", u.toString());
       // startActivity(intent);
    }
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
              Uri u = data.getData();
              Intent intent = new Intent(this, Edit_Picture.class);
             intent.putExtra("image", u.toString());
             startActivity(intent);
        }
}
    BaseLoaderCallback mBaseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                {
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
        Imgproc.cvtColor(mRgba, imGgray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(imGgray, mByte, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 35, 5);
        return mByte; // this is m Binary image
    }

}
