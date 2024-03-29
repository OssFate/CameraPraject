package com.ossgrounds.camerapraject;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainCamera extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_SD = 101;
    private SharedPreferences sharedPref;


    public static SharedPreferences.Editor editor;
    public static int NumVideo;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public void clickLul(View view){
        System.out.println("Hello this is doge, and this is our video");
        System.out.println(NumVideo);

        editor.putInt(getString(R.string.numVid), NumVideo);
        editor.commit();
        //setContentView(R.layout.activity_video_player);
        Intent lePlayButton = new Intent(this, VideoPlayerActivity.class);
        startActivity(lePlayButton);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_camera);

        sharedPref = this.getSharedPreferences(getString(R.string.prefKey), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        NumVideo = sharedPref.getInt(getString(R.string.numVid), R.string.defaultNumVid);
        System.out.println(NumVideo);
        if(NumVideo > 10000){
            NumVideo = 0;
            editor.putInt(getString(R.string.numVid), NumVideo);
            editor.commit();
        }
        System.out.println(NumVideo);

        // Request permissions for android 6
        // We are not going to use this anymore, is here cuz reasons, but main build will be focus
        // on android 5, so we gotta allow permissions on the phone itself, and code like nothing
        // happens.

        /*
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_WRITE_SD);

        */
        // start the image capture Intent

    }

    public void cameraPlay(View view){
        editor.putInt(getString(R.string.numVid), NumVideo);
        editor.commit();

        Random rand = new Random();
        Uri myUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/CameraProject/VID_"+
                rand.nextInt(MainCamera.NumVideo) +".mp4"); // initialize Uri here
        System.out.println(myUri.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW );
        intent.setDataAndType(myUri, "video/*");
        startActivity(intent);
    }

    public void cameraRec(View view){
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO); // create a file to save the image
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        System.out.println(fileUri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE); // create a file to save the image
    }

    public void cameraPic(View view){
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        System.out.println(fileUri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE); // create a file to save the image
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraProject");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("CameraProject", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "PIC_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ MainCamera.NumVideo + ".mp4");
            MainCamera.NumVideo ++;
        } else {
            return null;
        }

        return mediaFile;
    }
}
