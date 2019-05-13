package com.abdullah_alsaad.generic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.abdullah_alsaad.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class AppUtil {

    public static void backupDatabase(Context context, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + databaseName + "";
                String backupDBPath = "Download/abdullah-alsaad.sqlite";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
//                    showToast(context, context.getString(R.string.databaseFileIsExported));
                } else {
                    showToast(context, context.getString(R.string.thereIsNothingToExport));
                }
            } else {
                showToast(context, context.getString(R.string.enableStoragePermission));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }
    public static void showToast(Context context, int text) {
        showToast(context, context.getString(text));
    }
    public static boolean checkNetwork(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static String getDevMode(String id){
        Boolean devMode = false;
        switch (id){
            case "BookItem" :
                return devMode == false ? "BookItem" :  ".BookItemDev";
            case "QandA" :
                return devMode == false ? "QandA" :  ".QandADev";
            case "Lecture" :
                return devMode == false ? "Lecture" :  ".LectureDev";
            case "QuestionAndAnswer" :
                return devMode == false ? "QuestionAndAnswer" :  ".QuestionAndAnswerDev";
            case "Radio" :
                return devMode == false ? "Radio" :  "Radio";
        }
        return "";
    }

    public static byte[] decodeFile(byte[] bookImageByte) {

        if (bookImageByte != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(bookImageByte, 0, bookImageByte.length);

            Bitmap resized = Bitmap.createScaledBitmap(bmp,(int)(bmp.getWidth()*0.2), (int)(bmp.getHeight()*0.2), true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] byteArray = stream.toByteArray();
            bmp.recycle();
            resized.recycle();
            return byteArray;
        }
        return null;
    }

    public static void openBrowser(Context context, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
}
