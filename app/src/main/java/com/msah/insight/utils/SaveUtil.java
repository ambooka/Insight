package com.msah.insight.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;


import com.msah.insight.styles.toolbar.CustomToolbar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class SaveUtil {

    //@SuppressLint("SimpleDateFormat")
    public static void saveHtml(Activity activity, String html, String noteName) {


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CustomToolbar.REQ_VIDEO);

            }
            File dir = new File(activity.getExternalFilesDir(null) + File.separator + "Notes");
            if (!dir.exists()) {
                boolean createDir = dir.mkdir();
            }

            /**
             DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh_mm_ss");
             String time = dateFormat.format(new Date());
             String fileName = time.concat(".html");**/
            File file = new File(activity.getExternalFilesDir(null) + File.separator + "Notes" + File.separator + noteName);
            if (!file.exists()) {
                boolean isCreated = file.createNewFile();
                if (!isCreated) {
                    Toast.makeText(activity, "Cannot create file at ", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Toast.makeText(activity, noteName + " has been saved at ", Toast.LENGTH_LONG).show();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(html);
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            com.msah.insight.utils.Util.toast(activity, "Ran into error: " + e.getMessage());
        }
    }

}


