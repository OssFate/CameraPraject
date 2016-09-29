package com.freakground.oswald.modulofoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.File;

public class WelcomeActivity extends AppCompatActivity {

    private View mDecorView;
    private Handler mHandler;
    private ViewSwitcher mSwitcher;
    private File[] mFile;

    private String mPath;

    private ImageView mFirst;
    private ImageView mSecond;
    private BitmapFactory.Options bmOptions;

    final private int mDelay = 4000; //milliseconds
    final private int mW = 586;
    final private int mH = 713;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mPath = Environment.getExternalStorageDirectory().toString() + "/Pictures/Test";

        mDecorView = getWindow().getDecorView();

        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        File f = new File(mPath);
        mFile = f.listFiles();

        mFirst = (ImageView) findViewById(R.id.firstImage);
        mSecond = (ImageView) findViewById(R.id.secondImage);

        bmOptions = new BitmapFactory.Options();

        mFirst.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mFile[(int) (Math.random() * mFile.length)].getPath(), bmOptions), mW, mH, true));
        //((ImageView) findViewById(R.id.secondImage)).setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file[1].getPath()), 586, 713, true));
        //((ImageView) findViewById(R.id.firstImage)).setImageURI( Uri.parse(file[(int) (Math.random() * file.length)].getPath()));
        //((ImageView) findViewById(R.id.secondImage)).setImageURI(Uri.parse(file[(int) (Math.random() * file.length)].getPath()));

        mSwitcher = (ViewSwitcher) findViewById(R.id.switcher);
        mHandler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mHandler.postDelayed(new Runnable() {
            public void run() {
                //do something
                if (mSwitcher.getDisplayedChild() == 0) {
                    //Log.d("HANDLER!", "IM STILL RUNNING 1");
                    mSecond.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mFile[(int) (Math.random() * mFile.length)].getPath(), bmOptions), mW, mH, true));
                    mSwitcher.showNext();
                } else {
                    //Log.d("HANDLER!", "IM STILL RUNNING 2");
                    mFirst.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mFile[(int) (Math.random() * mFile.length)].getPath(), bmOptions), mW, mH, true));
                    mSwitcher.showPrevious();
                }
                mHandler.postDelayed(this, mDelay);
            }
        }, mDelay);
    }

    public void changeActivity(View view) {
        System.out.println("LOL");
        Intent intent = new Intent(WelcomeActivity.this, SaveActivity.class);
        this.finish();
        mHandler.removeCallbacksAndMessages(null);
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
