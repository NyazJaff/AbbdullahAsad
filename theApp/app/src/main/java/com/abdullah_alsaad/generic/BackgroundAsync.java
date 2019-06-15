package com.abdullah_alsaad.generic;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.abdullah_alsaad.OrmLiteDatabseHelper.DbPer;
import com.abdullah_alsaad.activities.TopicItemActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class BackgroundAsync extends AsyncTask<String, String, String> {
    //TODO check permission
    Long id;
    String format;
    Context context;
    ProgressBar progressBar;
    String downloadingFile = "";

    public BackgroundAsync(long id, String format) {
        this.id = id;
        this.format = format;
    }

    public BackgroundAsync(long id, String format, Context context, ProgressBar progressBar) {
        this.id = id;
        this.format = format;
        this.context = context;
        this.progressBar = progressBar;
    }

    public BackgroundAsync(Context context, long id, String format, String downloadingFile) {
        this.id = id;
        this.format = format;
        this.context = context;
        this.downloadingFile = downloadingFile;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        System.out.println("Starting download");
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
//            System.out.println("Downloading");
            URL url = new URL(f_url[0]);

            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();
            if(lenghtOfFile > 0) {
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                byte data[] = new byte[1124];
                long total = 0;
                if(downloadingFile.equals("bookImage")){
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // writing data to file
                        byteArrayOutputStream.write(data, 0, count);
                    }
                    // flushing output
                    byte[] logoImagedata = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.flush();
                    // closing streams
                    byteArrayOutputStream.close();
                    DbPer.insertBookImage(context,id,logoImagedata);
                }else{
                    // Output stream to write file
                    File folder = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "abdullahAlSaad/"+format);
                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdirs();
                    }
                    if (success) {
                        OutputStream output = new FileOutputStream(root + "/abdullahAlSaad/"+ format +"/" + id + "." + format);
                        while ((count = input.read(data)) != -1) {
                            total += count;
                            // writing data to file
                            output.write(data, 0, count);
                        }
                        // flushing output
                        output.flush();
                        // closing streams
                        output.close();
                }
                    input.close();
                }
            }
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if(context != null){
            AppUtil.showToast(context,values[0]);
        }
    }

    /**
     * After completing background task
     **/
    @Override
    protected void onPostExecute(String file_url) {
//        System.out.println("Downloaded");
        if(progressBar!=null){
            progressBar.setVisibility(View.GONE);
        }
        if(context!=null && !downloadingFile.equals("bookImage")){
            TopicItemActivity topicItemActivity = (TopicItemActivity)context;
            topicItemActivity.pdfSetup();
            topicItemActivity.resetDownloadInProgress();
        }
    }
    @Override
    protected void onCancelled() {
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "/abdullahAlSaad/"+ format +"/" + id + "." + format);
        if (folder.exists()) {
            folder.delete();
        }
        super.onCancelled();

    }
}

