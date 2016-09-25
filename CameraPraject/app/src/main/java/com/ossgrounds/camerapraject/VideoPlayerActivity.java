package com.ossgrounds.camerapraject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by USER on 13/09/2016.
 */
public class VideoPlayerActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {


    private MediaController controller;
    private ArrayList<Video> videoList;
    private Random randomGenerator;
    private Uri vidUri;
    private VideoView vidView;
    private View decorView;
    //private ListView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //super.onStart();
        setContentView(R.layout.activity_video_player);

        decorView = getWindow().getDecorView();

        vidView = (VideoView)findViewById(R.id.videoView);
        videoList = new ArrayList<Video>();
        randomGenerator = new Random();
        getVideoList();
        int index = randomGenerator.nextInt(videoList.size());
        //get video
        Video playVideo = videoList.get(index);
        //get id
        long currVideo = playVideo.getID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                currVideo);

        try{
            vidUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/CameraProject/VID_"+ randomGenerator.nextInt(MainCamera.NumVideo) +".mp4");
            vidView.setVideoURI(vidUri);
            //setController();
            //vidView.setMediaController(controller);

            vidView.start();
        }
        catch(Exception e){
            Log.e("VIDEO SERVICE", "Error setting data source", e);
        }

    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int i) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private void setController(){
        controller = new MediaController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.videoView));
        controller.setEnabled(true);
    }

    private void playNext() {
        //play next
        int index = randomGenerator.nextInt(videoList.size());
        Video playVideo = videoList.get(index);
        long currVideo = playVideo.getID();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                currVideo);

        try{
            vidUri = Uri.parse(String.valueOf(trackUri));
            vidView.setVideoURI(vidUri);
            vidView.start();
        }
        catch(Exception e){
            Log.e("VIDEO SERVICE", "Error setting data source", e);
        }
    }
    private void playPrev() {
        //play prev
        int index = randomGenerator.nextInt(videoList.size());
        Video playVideo = videoList.get(index);
        long currVideo = playVideo.getID();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                currVideo);

        try{
            vidUri = Uri.parse(String.valueOf(trackUri));
            vidView.setVideoURI(vidUri);
            vidView.start();
        }
        catch(Exception e){
            Log.e("VIDEO SERVICE", "Error setting data source", e);
        }
    }

    public void getVideoList() {
        //retrieve song info
        ContentResolver videoResolver = getContentResolver();
        //??? uri acá?
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor videoCursor = videoResolver.query(videoUri, null, null, null, null);
        if(videoCursor!=null && videoCursor.moveToFirst()){
            //get columns
            int titleColumn = videoCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = videoCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);

            //add songs to list
            do {
                long thisId = videoCursor.getLong(idColumn);
                String thisTitle = videoCursor.getString(titleColumn);
                videoList.add(new Video(thisId, thisTitle));
            }while (videoCursor.moveToNext());
        }
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