package com.freakground.oswald.modulofoto;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RestActivity extends AppCompatActivity {

    //State
    private View mDecorView;
    private final Handler mHandler = new Handler();
    private final int mIdleDelay = 30000;
    private final int mDelay = 5000;
    private boolean oneShot = false;

    //Images change
    private AnimationDrawable mConteo;
    private AnimationDrawable mFlash;

    //Current file
    public static File pictureFile;

    //Camera control
    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout mCameraPreview;

    private Camera.ShutterCallback mShutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            findViewById(R.id.flash).setVisibility(View.VISIBLE);
            mFlash.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.flash).setVisibility(View.GONE);
                    mFlash.stop();
                    mFlash.selectDrawable(0);
                }
            }, 1000);
        }
    };

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Log.d("PICTURE", pictureFile.toString());
            if (pictureFile == null){
                Log.d("FOTO", "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("FOTO", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("FOTO", "Error accessing file: " + e.getMessage());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);

        mDecorView = getWindow().getDecorView();

        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        // Create an instance of Camera
        mCamera = getCameraInstance();
        //mCamera.setDisplayOrientation(180);
        mPreview = new CameraPreview(this, mCamera);
        mCameraPreview = (FrameLayout) findViewById(R.id.camera_preview);
        mCameraPreview.addView(mPreview);

    }

    @Override
    protected void onStart() {
        super.onStart();

        loadAnimations();
        ImageView ivConteo = (ImageView) findViewById(R.id.conteo);
        mConteo = (AnimationDrawable) ivConteo.getBackground();
        mConteo.setExitFadeDuration(200);
        ImageView ivFlash = (ImageView) findViewById(R.id.flash);
        mFlash  = (AnimationDrawable) ivFlash.getBackground();

        pictureFile = getOutputMediaFile();

        mHandler.postDelayed(autoPhoto, 10000);

        loadF3();
        loadF4();
        loadF5();
    }

    //Taking picture
    private void loadF3 () {
        findViewById(R.id.f3Der).setBackground(getDrawable(R.drawable.f3_der));
        findViewById(R.id.f3Izq).setBackground(getDrawable(R.drawable.f3_izq));
    }

    //You liked it?
    private void loadF4 () {
        findViewById(R.id.f4Fondo).setBackground(getDrawable(R.drawable.f4_fondo));
        findViewById(R.id.f4Confirmacion).setBackground(getDrawable(R.drawable.f4_confirmacion));
        findViewById(R.id.f4Like).setBackground(getDrawable(R.drawable.f4_like));
        findViewById(R.id.f4Si).setBackground(getDrawable(R.drawable.f4_si));
        findViewById(R.id.f4No).setBackground(getDrawable(R.drawable.f4_no));
    }

    //Again?
    private void loadF5 () {
        findViewById(R.id.f5Izq).setBackground(getDrawable(R.drawable.f3_izq));
        findViewById(R.id.f5Der).setBackground(getDrawable(R.drawable.f3_der));
        findViewById(R.id.f5Fondo).setBackground(getDrawable(R.drawable.f5_fondo));
        findViewById(R.id.f5Texto).setBackground(getDrawable(R.drawable.f5_texto));
        findViewById(R.id.f5No).setBackground(getDrawable(R.drawable.f4_no));
        findViewById(R.id.f5Si).setBackground(getDrawable(R.drawable.f4_si));
    }

    private void loadAnimations() {
        findViewById(R.id.flash).setBackground(getDrawable(R.drawable.flash));
        findViewById(R.id.conteo).setBackground(getDrawable(R.drawable.conteo));
    }
    /////////////// Runnables /////////////////////////////////////////////
    Runnable autoPhoto = new Runnable() {
        @Override
        public void run() {
            goToTakePicture(null);
        }
    };

    Runnable idleApp = new Runnable() {
        @Override
        public void run() {
            pictureFile.delete();
            returnInit(null);
        }
    };

    Runnable takePicture = new Runnable() {
        @Override
        public void run() {
            findViewById(R.id.conteo).setVisibility(View.GONE);
            mConteo.stop();
            mConteo.selectDrawable(0);

            mCamera.takePicture(mShutter, null, mPicture);

            mHandler.removeCallbacks(idleApp);
            mHandler.postDelayed(idleApp, mIdleDelay);
            findViewById(R.id.f3).setVisibility(View.GONE);
            mCameraPreview.setTranslationX(213f);
            findViewById(R.id.f4).setVisibility(View.VISIBLE);
        }
    };

    ////////////////////////////////////////////////////////////

    public void goToTakePicture(View view){
        mHandler.removeCallbacks(autoPhoto);
        findViewById(R.id.f2).setVisibility(View.GONE);
        mCameraPreview.setTranslationX(-25f);
        findViewById(R.id.f3).setVisibility(View.VISIBLE);
        findViewById(R.id.conteo).setVisibility(View.VISIBLE);
        mConteo.start();

        mHandler.removeCallbacks(idleApp);
        mHandler.postDelayed(idleApp, mIdleDelay);
        mHandler.postDelayed(takePicture, mDelay);
    }

    public void takeOther (View view){
        findViewById(R.id.f5).setVisibility(View.GONE);
        findViewById(R.id.f2).setVisibility(View.VISIBLE);

        mCameraPreview.setTranslationX(-200f);

        mHandler.postDelayed(autoPhoto, 10000);
    }

    public void wannaTakeOther(View view){
        if(!oneShot) {
            oneShot = !oneShot;
            findViewById(R.id.f4).setVisibility(View.GONE);
            reEnableCamera();
            findViewById(R.id.f5).setVisibility(View.VISIBLE);
            mHandler.removeCallbacks(idleApp);
            mHandler.postDelayed(idleApp, mIdleDelay);
        } else {
            leaveAppDontSave(view);
        }
    }

    public void leaveAppSave(View view){
        createMarker();
        mCamera.release();
        mHandler.removeCallbacksAndMessages(null);
        //finish();
        Intent intent = new Intent(RestActivity.this, SaveActivity.class);
        startActivity(intent);
    }

    public void leaveAppDontSave (View view){
        pictureFile.delete();
        mCamera.release();
        mHandler.removeCallbacksAndMessages(null);
        finish();
        Intent intent = new Intent(RestActivity.this, EndActivity.class);
        startActivity(intent);
    }

    public void returnInit(View view){
        finish();
        mCamera.release();
        Intent intent = new Intent(RestActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }

    public void reEnableCamera(){
        mCamera.release();
        mCamera = null;
        mCamera = getCameraInstance();
        mPreview = null;
        mPreview = new CameraPreview(this, mCamera);
        mCameraPreview.removeAllViews();
        mCameraPreview.addView(mPreview);
    }

    public void createMarker () {
        File frame = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + getString(R.string.pathFrame) );

        Bitmap first, second, third;
        first = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(pictureFile.getPath()), 833, 500, true);
        second = BitmapFactory.decodeFile(frame.getPath());

        third = overlayMark(second, first);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(pictureFile);
            third.compress(Bitmap.CompressFormat.JPEG, 90, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap overlayMark(Bitmap bmp1, Bitmap bmp2)
    {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);

        canvas.drawBitmap(bmp2, -140f, 40, null);
        canvas.drawBitmap(bmp1, 0, 0, null);
        return bmOverlay;
    }

    ///////////////// UNTOUCHABLE

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance

            Camera.Parameters params = c.getParameters();
            List<Camera.Size> size = params.getSupportedPictureSizes();
            Camera.Size s = size.get(7);
            params.setPictureSize(s.width, s.height);
            List<Camera.Size> size2 = params.getSupportedPreviewSizes();
            Camera.Size s2 = size2.get(6);
            params.setPreviewSize(s2.width, s2.height);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            c.setParameters(params);
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "alqueria/fotos");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
