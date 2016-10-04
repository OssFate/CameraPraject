package com.freakground.oswald.modulo1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import yellow5a5.actswitchanimtool.ActSwitchAnimTool;

public class VideoActivity extends AppCompatActivity {

    private View decorView;
    private VideoView videoView;

    private final Handler handler = new Handler();
    private final int mDelay = 500;

    private int mStopPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);


        decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        new ActSwitchAnimTool(this)
                .receiveIntent(getIntent())
                .setAnimType(ActSwitchAnimTool.MODE_SHRINK)
                .target(findViewById(R.id.imagePause2))
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        videoView = (VideoView) findViewById(R.id.videoVideo);
        videoView.setVideoURI(Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + getString(R.string.videoInstitucional)));

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            public void onCompletion(MediaPlayer mp){
                System.out.println("It ended!");

                SaveCounts.saveNumberOfReproductions(VideoActivity.this);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(VideoActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                }, mDelay);
            }
        });

        videoView.start();
    }

    public void playNPause(View view){
        //System.out.println("is playing? = " + videoView.isPlaying());
        view.setVisibility(View.INVISIBLE);

        if(videoView.isPlaying()){
            findViewById(R.id.imagePlay).setVisibility(View.VISIBLE);
            mStopPosition = videoView.getCurrentPosition();
            videoView.pause();
        } else {
            //System.out.println("Resume!! " + mStopPosition);
            findViewById(R.id.imagePause2).setVisibility(View.VISIBLE);
            videoView.seekTo(mStopPosition);
            videoView.start();
        }
    }

    /*

    public void resetVideo (View view) {
        findViewById(R.id.imagePause2).setVisibility(View.VISIBLE);
        findViewById(R.id.imagePlay).setVisibility(View.INVISIBLE);
        videoView.seekTo(0);
        videoView.start();
    }

    */

    public void resetModule (View view) {
        findViewById(R.id.imagePause2).setVisibility(View.VISIBLE);
        findViewById(R.id.imagePlay).setVisibility(View.INVISIBLE);
        videoView.stopPlayback();
        finish();
        Intent intent = new Intent(VideoActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

}
