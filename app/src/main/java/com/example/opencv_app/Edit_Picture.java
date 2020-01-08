package com.example.opencv_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
int menu_option=0;
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
       // iv2=findViewById(R.id.imageView2);
    }
    public void convert(View v){

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),u);
            Mat obj = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC4);
            Utils.bitmapToMat(bmp, obj);
           // Imgproc.cvtColor(obj, obj, Imgproc.COLOR_BGR2GRAY);
            switch(menu_option){
                case 1:{
                    Imgproc.cvtColor(obj, obj, Imgproc.COLOR_RGB2YUV);
                   break;
                }
                case 2:{
                    Imgproc.cvtColor(obj, obj, Imgproc.COLOR_RGB2HSV);
                    break;
                }
                case 3:{
                    Imgproc.cvtColor(obj, obj, Imgproc.COLOR_RGB2Luv);
                     break;
                }
                default:{
                    Imgproc.cvtColor(obj, obj, Imgproc.COLOR_BGR2GRAY);
                    break;
                }
            }
           // Utils.bitmapToMat(bmp, obj);
            Utils.matToBitmap(obj,bmp);
            iv.setImageBitmap(bmp) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void save(View v){
        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title" , "OpenCV");
       }
    public void  restart(View v){
        iv.setImageURI(u);
    }
    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.m2, menu);
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
}
