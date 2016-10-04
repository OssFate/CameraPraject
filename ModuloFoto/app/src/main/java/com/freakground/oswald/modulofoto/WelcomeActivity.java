package com.freakground.oswald.modulofoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.File;

public class WelcomeActivity extends AppCompatActivity {

    //State
    private View mDecorView;
    private final Handler mHandler = new Handler();
    final private int mDelay = 5000; //milliseconds
    final private int mW = 575;
    final private int mH = 686;

    //SlideShow
    private ViewSwitcher mSwitcher;
    private ImageView mFirst;
    private ImageView mSecond;
    private BitmapFactory.Options bmOptions;
    private Bitmap bm1;
    private Bitmap bm2;

    //File Location
    private File[] mFile;
    private String mPath;

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

        mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + getString(R.string.pathFotos);
        File f = new File(mPath);
        mFile = f.listFiles();

        mFirst = (ImageView) findViewById(R.id.firstImage);
        mSecond = (ImageView) findViewById(R.id.secondImage);

        bmOptions = new BitmapFactory.Options();

        bm1 = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mFile[(int) (Math.random() * mFile.length)].getPath(), bmOptions), mW, mH, true);
        bm2 = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mFile[(int) (Math.random() * mFile.length)].getPath(), bmOptions), mW, mH, true);
        mFirst.setImageBitmap(bm1);

        mSwitcher = (ViewSwitcher) findViewById(R.id.switcher);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mHandler.postDelayed(new Runnable() {
            public void run() {
                //do something
                if (mSwitcher.getDisplayedChild() == 0) {
                    bm2 = null;
                    bm1 = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mFile[(int) (Math.random() * mFile.length)].getPath(), bmOptions), mW, mH, true);
                    mSecond.setImageBitmap(bm1);
                    mSwitcher.showNext();
                } else {
                    bm1 = null;
                    bm2 = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mFile[(int) (Math.random() * mFile.length)].getPath(), bmOptions), mW, mH, true);
                    mFirst.setImageBitmap(bm2);
                    mSwitcher.showPrevious();
                }
                mHandler.postDelayed(this, mDelay);
            }
        }, mDelay);
    }

    public void changeActivity(View view) {
        //finish();
        findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        Intent intent = new Intent(WelcomeActivity.this, RestActivity.class);
        startActivity(intent);
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
