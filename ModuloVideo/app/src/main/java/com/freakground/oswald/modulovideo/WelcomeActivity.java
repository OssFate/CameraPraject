package com.freakground.oswald.modulovideo;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    // States
    private final Handler mHandler = new Handler();
    private final int mDelayRecord = 20000;
    private final int mIdleTime = 30000;

    private boolean oneTime = false;
    private boolean isRecording = false;
    public static boolean didSave;

    // Views
    private AnimationDrawable mConteo;
    private View mDecorView;

    // Save
    public final String TAG = "MODULOVIDEO";
    private File mFile;

    // Camera
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(WelcomeActivity.this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    @Override
    protected void onStart() {
        super.onStart();

        didSave = false;

        Log.d("LAL", "does this run again?");

        loadConteo();
        ImageView iv = (ImageView) findViewById(R.id.conteo);
        mConteo = (AnimationDrawable) iv.getBackground();

        mFile = getOutputMediaFile();

        mHandler.postDelayed(idleApp, mIdleTime);

        loadG3();
        loadG4();
    }

    // Did you like it
    private void loadG3(){
        findViewById(R.id.g3_fondo).setBackground(getDrawable(R.drawable.g3_fondo));
        findViewById(R.id.g3_texto).setBackground(getDrawable(R.drawable.g3_texto));
        findViewById(R.id.g3No).setBackground(getDrawable(R.drawable.g3_no));
        findViewById(R.id.g3Si).setBackground(getDrawable(R.drawable.g3_si));
    }

    // Wanna record again?
    private void loadG4(){
        findViewById(R.id.g4Fondo).setBackground(getDrawable(R.drawable.g4_fondo));
        findViewById(R.id.g4Texto).setBackground(getDrawable(R.drawable.g4_texto));
        findViewById(R.id.g4Si).setBackground(getDrawable(R.drawable.g3_si));
        findViewById(R.id.g4No).setBackground(getDrawable(R.drawable.g3_no));
    }

    private void loadConteo () {
        findViewById(R.id.conteo).setBackground(getDrawable(R.drawable.conteo20));
        findViewById(R.id.touchToEnd).setBackground(getDrawable(R.drawable.terminargrabacion));
    }

    ////////////////***** RUNNABLES  **********//////////////////////////////

    private Runnable endRecord = new Runnable() {
        @Override
        public void run() {
            if(isRecording) {
                findViewById(R.id.recording).setVisibility(View.GONE);
                mConteo.stop();
                mConteo.selectDrawable(0);

                mMediaRecorder.stop();  // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                mCamera.lock();

                isRecording = false;

                mHandler.removeCallbacks(idleApp);
                mHandler.postDelayed(idleApp, mIdleTime);
                findViewById(R.id.g3).setVisibility(View.VISIBLE);
            }
        }
    };

    private Runnable idleApp = new Runnable() {
        @Override
        public void run() {
            mFile.delete();
            returnInit(null);
        }
    };

    public void fastRecord(View view) {
        mHandler.removeCallbacks(endRecord);
        mHandler.postDelayed(endRecord, 10);
    }

    public void startRecording(View view) {
        if (prepareVideoRecorder()) {
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording
            findViewById(R.id.g2).setVisibility(View.INVISIBLE);
            mMediaRecorder.start();

            findViewById(R.id.recording).setVisibility(View.VISIBLE);
            mConteo.start();

            isRecording = true;

            mHandler.removeCallbacks(idleApp);
            mHandler.postDelayed(idleApp, mIdleTime);
            mHandler.postDelayed(endRecord, mDelayRecord);
        } else {
            // prepare didn't work, release the camera
            releaseMediaRecorder();
            // inform user
        }
    }

    public void recordAgain(View view) {
        findViewById(R.id.g4).setVisibility(View.INVISIBLE);
        findViewById(R.id.g2).setVisibility(View.VISIBLE);

        mHandler.removeCallbacks(idleApp);
        mHandler.postDelayed(idleApp, mIdleTime);
    }

    public void didntLikeIt(View view) {
        if(!oneTime) {
            oneTime = !oneTime;
            findViewById(R.id.g3).setVisibility(View.INVISIBLE);
            findViewById(R.id.g4).setVisibility(View.VISIBLE);
            mHandler.removeCallbacks(idleApp);
            mHandler.postDelayed(idleApp, mIdleTime);
        } else {
            leaveAppNotSave(view);
        }
    }

    public void returnInit(View view) {
        finish();
        Intent intent = new Intent(WelcomeActivity.this, IntroActivity.class);
        startActivity(intent);
    }

    public void leaveAppSave(View view){
        didSave = true;
        mHandler.removeCallbacksAndMessages(null);
        finish();
        Intent intent = new Intent(WelcomeActivity.this, EndActivity.class);
        startActivity(intent);
    }

    public void leaveAppNotSave(View view){
        mFile.delete();
        mHandler.removeCallbacksAndMessages(null);
        finish();
        Intent intent = new Intent(WelcomeActivity.this, EndActivity.class);
        startActivity(intent);
    }

    ///////////////////// UN TOUCHABLE  //////////////////////////////////////////////////////

    private boolean prepareVideoRecorder(){

        //mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(mFile.toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        //mMediaRecorder.setVideoSize(640, 480);

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance

            Camera.Parameters params = c.getParameters();
            List<Camera.Size> size = params.getSupportedPreviewSizes();
            Camera.Size s = size.get(6);
            params.setPreviewSize(s.width, s.height);
            ///*
            Log.d("Camera", params.getPreviewSize().width + " , " + params.getPreviewSize().height);
            /*
            for (Camera.Size l: params.getSupportedPreviewSizes()
                    ) {
                Log.d("MyError", "W: " + l.width + " H: " + l.height);
            }*/
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            c.setParameters(params);
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(){
        String sdcard1 = "/storage/sdcard1";
        File mediaStorageDir = new File(sdcard1, getString(R.string.pathVideosGuardar));
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
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
