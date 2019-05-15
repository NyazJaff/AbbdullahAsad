package com.abdullah_alsaad.generic;

import android.os.Environment;

import java.io.File;

public class StorageHandler {


    public static boolean pdfExists(long id){
        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "abdullahAlSaad/pdf/" + id + ".pdf");
        boolean success = true;
        if (file.exists()) {
            return true;
        }
        return false;
    }
    public static boolean mp3Exists(long id){
        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "abdullahAlSaad/mp3/" + id + ".mp3");
        boolean success = true;
        if (file.exists()) {
            return true;
        }
        return false;
    }

}
