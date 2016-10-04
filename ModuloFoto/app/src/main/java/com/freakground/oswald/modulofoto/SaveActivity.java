package com.freakground.oswald.modulofoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveActivity extends AppCompatActivity {

    private View mDecorView;
    public static boolean didSave;

    private EditText et;
    private Typeface custom_font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        mDecorView = getWindow().getDecorView();

        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        UiChangeListener();

        ((ImageView) findViewById(R.id.sPicture)).setImageURI(Uri.parse(RestActivity.pictureFile.toString()));

        custom_font = Typeface.createFromAsset(getAssets(), "fonts/aller.ttf");
        et = (EditText)findViewById(R.id.editText);
        et.setTypeface(custom_font);

    }

    @Override
    protected void onStart() {
        super.onStart();

        didSave = false;
    }

    public void deleteFile (View view) {
        RestActivity.pictureFile.delete();
        Intent intent = new Intent(SaveActivity.this, EndActivity.class);
        startActivity(intent);
    }

    public void saveFile (View view) {
        didSave = true;
        Bitmap first, second;

        first = BitmapFactory.decodeFile(RestActivity.pictureFile.getPath());

        second = setText(first);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(RestActivity.pictureFile);
            second.compress(Bitmap.CompressFormat.JPEG, 90, out); // bmp is your Bitmap instance
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

        Intent intent = new Intent(SaveActivity.this, EndActivity.class);
        startActivity(intent);
    }

    private Bitmap setText(Bitmap bmp1)
    {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);

        float size = 81f;

        Paint tPaint = new Paint();
        tPaint.setTextSize(size);
        tPaint.setColor(Color.BLACK);
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setTypeface(custom_font);
        tPaint.setAntiAlias(true);
        tPaint.setSubpixelText(true);

        float height = tPaint.measureText("yY");
        float width = tPaint.measureText(et.getText().toString());

        while(width > 540f){
            size --;
            tPaint.setTextSize(size);
            width = tPaint.measureText(et.getText().toString());
        }

        float x_coord = (bmp1.getWidth() - width)/2;
        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawText(et.getText().toString(), x_coord, 639f, tPaint);
        return bmOverlay;
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

    public void UiChangeListener()
    {
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
    }
}
