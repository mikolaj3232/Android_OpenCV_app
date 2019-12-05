package com.example.opencv_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;


public class Edit_Picture extends AppCompatActivity  {
Uri u ;
ImageView iv;
ImageView iv2;
static{
    if(OpenCVLoader.initDebug()){
        Log.i("opencv","Init ok");
    }
    else{
        Log.i("opencv","Init fail");
    }
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__picture);
        iv=findViewById(R.id.imageView);
        Intent intent = getIntent();
        String ipath=intent.getStringExtra("image");
        u= Uri.parse(ipath);
        iv.setImageURI(u);
        iv2=findViewById(R.id.imageView2);
    }
    public void convert(View v){

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),u);
            Mat obj = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC4);
            Utils.bitmapToMat(bmp, obj);
            Imgproc.cvtColor(obj, obj, Imgproc.COLOR_BGR2GRAY);
           // Utils.bitmapToMat(bmp, obj);
            Utils.matToBitmap(obj,bmp);
            iv2.setImageBitmap(bmp) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
