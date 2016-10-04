package com.freakground.oswald.modulofoto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Oswald on 10/2/2016.
 */

public class SaveCounts {

    private static String count;
    private static final String resetValue = "r";


    public static void saveNumberOfReproductions(Context context){
        //Log.d("VALUE", "Count: " + count + "Content: " + context.getSharedPreferences(context.getString(R.string.prefKey), Context.MODE_PRIVATE).getInt(context.getString(R.string.numRep), 0));
        getCurrentCount(context);
        saveValue(context);
        //Log.d("VALUE", "Count: " + count + " Content: " + context.getSharedPreferences(context.getString(R.string.prefKey), Context.MODE_PRIVATE).getInt(context.getString(R.string.numRep), 0));
    }

    private static void saveValue (Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.prefKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        count = String.valueOf(Integer.valueOf(count) + 1);

        File file, local;
        FileOutputStream outputStream;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.archivoRep));
            local = new File(context.getExternalFilesDir(null), context.getString(R.string.archivoLocal));

            outputStream = new FileOutputStream(file);
            outputStream.write(count.getBytes());
            outputStream.close();

            outputStream = new FileOutputStream(local);
            outputStream.write(count.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor.putInt(context.getString(R.string.numRep), Integer.valueOf(count));
        editor.commit();
    }

    private static void getCurrentCount (Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.prefKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(count == null){
            getCountFromFile(context);
        }

        if(count.equals(resetValue)){
            editor.putInt(context.getString(R.string.numRep), 0);
            editor.commit();
            count = 0 + "";
        }else if(Integer.parseInt(count) < sharedPref.getInt(context.getString(R.string.numRep), 0)){
            count = String.valueOf(sharedPref.getInt(context.getString(R.string.numRep), 0));
        }
    }

    private static void getCountFromFile(Context context){
        File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File local = context.getFilesDir();
        File file = new File(sdcard, context.getString(R.string.archivoRep));

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            line = br.readLine();
            text.append(line);
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            e.printStackTrace();
        }

        if (text.toString().isEmpty()){
            file = new File(local, context.getString(R.string.archivoLocal));
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                line = br.readLine();
                text.append(line);
                br.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
                e.printStackTrace();
            }
        }

        count = text.toString().isEmpty() ? resetValue : text.toString();
    }

}
