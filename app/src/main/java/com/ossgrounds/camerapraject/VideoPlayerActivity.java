package com.ossgrounds.camerapraject;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by USER on 13/09/2016.
 */
public class VideoPlayerActivity extends Activity {

    String TAG = "MyVideoPlayer";

    @Override
    protected void onStart() {
        super.onStart();

        final VideoView videoView = (VideoView) findViewById(R.id.MyVideoView);

        videoView.setVideoPath("http://www.ebookfrenzy.com/android_book/movie.mp4");

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "Duration = " +
                        videoView.getDuration());
            }
        });

        videoView.start();

    }
}
