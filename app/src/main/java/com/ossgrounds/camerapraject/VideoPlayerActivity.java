package com.ossgrounds.camerapraject;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayerActivity extends Activity {
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by USER on 13/09/2016.
 */
public class VideoPlayerActivity extends AppCompatActivity {

    int i = 0;
    List<String> videoPathes = new ArrayList<String>();


    String TAG = "MyVideoPlayer";

    private VideoView mVideoView;
    private EditText mPath;
    private Button mPlay;
    private Button mPause;
    private Button mBack;
    private Button mNext;
    private String current;



    /*protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);
    }*/

    @Override
    protected void onStart() {
        super.onStart();

        mVideoView = (VideoView) findViewById(R.id.MyVideoView);
        videoPathes.add(Environment.getExternalStorageDirectory().getAbsolutePath()+"/CameraPraject/*");

        //mVideoView.setVideoPath("http://www.ebookfrenzy.com/android_book/movie.mp4");

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "Duration = " +
                        mVideoView.getDuration());
            }
        });

        mPlay = (Button) findViewById(R.id.playButton);
        mPause = (Button) findViewById(R.id.pauseButton);
        mBack = (Button) findViewById(R.id.backButton);
        mNext = (Button) findViewById(R.id.nextButton);

        mPlay.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                playVideo();
            }
        });
        mPause.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (mVideoView != null) {
                    mVideoView.pause();
                }
            }
        });
        mNext.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (mVideoView.canSeekForward()) {
                    mVideoView.seekTo(i);
                }
            }
        });
        mPlay.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (mVideoView.canSeekBackward()) {
                    //current = null;
                    mVideoView.seekTo(i);
                }
            }
        });

        runOnUiThread(new Runnable(){
            public void run() {
                playVideo();

            }

        });


        mVideoView.setVideoPath(videoPathes.get(i));
        mVideoView.start();

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(final MediaPlayer mp) {
                Random random = new Random();
                i = (i + 1) % videoPathes.size();
                i = random.nextInt(videoPathes.size());

                mVideoView.setVideoPath(videoPathes.get(i));
                mVideoView.start();
            }
        });

    }

    private void playVideo() {
        try {
            final String path = mPath.getText().toString();
            Log.v(TAG, "path: " + path);
            if (path == null || path.length() == 0) {
                Toast.makeText(this, "File URL/path is empty",
                        Toast.LENGTH_LONG).show();

            } else {
                // If the path has not changed, just start the media player
                if (path.equals(current) && mVideoView != null) {
                    mVideoView.start();
                    mVideoView.requestFocus();
                    return;
                }
                current = path;
                mVideoView.setVideoPath(getDataSource(path));
                mVideoView.start();
                mVideoView.requestFocus();

            }
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
            if (mVideoView != null) {
                mVideoView.stopPlayback();
            }
        }
    }

    private String getDataSource(String path) throws IOException {
        if (!URLUtil.isNetworkUrl(path)) {
            return path;
        } else {
            URL url = new URL(path);
            URLConnection cn = url.openConnection();
            cn.connect();
            InputStream stream = cn.getInputStream();
            if (stream == null)
                throw new RuntimeException("stream is null");
            File temp = File.createTempFile("mediaplayertmp", "dat");
            temp.deleteOnExit();
            String tempPath = temp.getAbsolutePath();
            FileOutputStream out = new FileOutputStream(temp);
            byte buf[] = new byte[128];
            do {
                int numread = stream.read(buf);
                if (numread <= 0)
                    break;
                out.write(buf, 0, numread);
            } while (true);
            try {
                stream.close();
            } catch (IOException ex) {
                Log.e(TAG, "error: " + ex.getMessage(), ex);
            }
            return tempPath;
        }
    }
}
