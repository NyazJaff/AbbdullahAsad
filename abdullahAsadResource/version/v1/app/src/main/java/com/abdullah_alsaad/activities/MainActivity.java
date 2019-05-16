package com.abdullah_alsaad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abdullah_alsaad.R;
import com.abdullah_alsaad.fragment.TopicItem;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private PDFView pdfView;
    private ImageButton btnTelegram, btnYoutube, btnTwitter, btnFacebook, btnBook, btnBookVoice, btnQuestion, btnLive, btnProfile, btnAdmin, btnLogout;
    private ProgressBar progressBar;
    private AlertDialog dialogLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTelegram = (ImageButton) findViewById(R.id.btnTelegram);
        btnYoutube = (ImageButton) findViewById(R.id.btnYoutube);
        btnTwitter = (ImageButton) findViewById(R.id.btnTwitter);
        btnFacebook = (ImageButton) findViewById(R.id.btnFacebook);
        btnBook = (ImageButton) findViewById(R.id.btnBook);
        btnBookVoice = (ImageButton) findViewById(R.id.btnBookVoice);
        btnQuestion = (ImageButton) findViewById(R.id.btnQuestion);
        btnLive = (ImageButton) findViewById(R.id.btnLive);
        btnProfile = (ImageButton) findViewById(R.id.btnProfile);
        btnAdmin = (ImageButton) findViewById(R.id.btnAdmin);
        btnLogout = (ImageButton) findViewById(R.id.btnLogout);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        btnBook.setOnClickListener(this);
        btnLive.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnQuestion.setOnClickListener(this);

        btnBookVoice.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            btnAdmin.setVisibility(View.VISIBLE);
            btnAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginUser();
                }
            });
        } else {
            btnLogout.setVisibility(View.VISIBLE);
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    openHomeActivity();
                }
            });
        }

//        progressBar.setVisibility(View.VISIBLE);
//        Download task = new Download();
//        task.execute("http://abdullah-asad.com/wp-content/uploads/2017/05/tadaber-qor2an.pdf");

//        pdfView = (PDFView) findViewById(R.id.pdfView);
//        pdfView.fromAsset("file2.pdf")
//                .onPageChange(this)
//                .load();

//        pdfView.getCurrentPage();
//
//        pdfView.loadPages();
//        Button button = (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateDefaultPage();
//            }
//        });
//        Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try  {
//                    sendNotification();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();
    }

//    public void sendNotification(){
//        try {
//            String jsonResponse;
//
//            URL url = new URL("https://onesignal.com/api/v1/notifications");
//            HttpURLConnection con = (HttpURLConnection)url.openConnection();
//            con.setUseCaches(false);
//            con.setDoOutput(true);
//            con.setDoInput(true);
//
//            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            con.setRequestProperty("Authorization", "Basic NjA0ZGFjZTUtYjI4MS00MzZiLWI0NjYtNzg0NmU5NzdhMGUz");
//            con.setRequestMethod("POST");
//
//            String strJsonBody = "{"
//                    +   "\"app_id\": \"2f1f542a-643d-4608-beb3-c277535b3e74\","
//                    +   "\"included_segments\": [\"All\"],"
//                    +   "\"data\": {\"foo\": \"bar\"},"
//                    +   "\"contents\": {\"en\": \" ajaxژێر پرد\"}"
//                    + "}";
//
//
//            System.out.println("strJsonBody:\n" + strJsonBody);
//
//            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
//            con.setFixedLengthStreamingMode(sendBytes.length);
//
//            OutputStream outputStream = con.getOutputStream();
//            outputStream.write(sendBytes);
//
//            int httpResponse = con.getResponseCode();
//            System.out.println("httpResponse: " + httpResponse);
//
//            if (  httpResponse >= HttpURLConnection.HTTP_OK
//                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
//                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
//                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
//                scanner.close();
//            }
//            else {
//                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
//                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
//                scanner.close();
//            }
//            System.out.println("jsonResponse:\n" + jsonResponse);
//
//        } catch(Throwable t) {
//            t.printStackTrace();
//        }
//    }

//    public void updateDefaultPage(){
//        pdfView.fromAsset("file2.pdf")
//                .onPageChange(this)
//                .defaultPage(20)
//                .load();
//
//        pdfView.setBackgroundColor(Color.YELLOW);
//    }

    @Override
    public void onClick(View view) {
        Intent myIntent = null;
        switch (view.getId()) {
            case R.id.btnBook:
                myIntent = new Intent(MainActivity.this, FragmentsActivity.class);
                myIntent.putExtra("fragment", "Book");
                break;
            case R.id.btnLive:
                myIntent = new Intent(MainActivity.this, LiveStream.class);
                myIntent.putExtra("fragment", "btnLive");
                break;
            case R.id.btnProfile:
                myIntent = new Intent(MainActivity.this, AboutShikh.class);
                myIntent.putExtra("fragment", "btnProfile");
                break;
            case R.id.btnBookVoice:
                myIntent = new Intent(MainActivity.this, FragmentsActivity.class);
                myIntent.putExtra("fragment", "lecture");
                break;
            case R.id.btnQuestion:
                myIntent = new Intent(MainActivity.this, TopicItem.class);
                myIntent.putExtra("fragment", "mp3PlayerListener");
                break;
            default:
        }

        MainActivity.this.startActivity(myIntent);
    }

    private void LoginUser() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View login = getLayoutInflater().inflate(R.layout.admin_login, null);
        final EditText email = (EditText) login.findViewById(R.id.name);
        final EditText password = (EditText) login.findViewById(R.id.password);
        Button loginBtn = (Button) login.findViewById(R.id.save);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginInputValidation(email, password) == true) {
                    loginUser(email, password);
                }
            }
        });
        alertDialog.setView(login);
        dialogLogin = alertDialog.create();
        dialogLogin.show();

    }

    private void loginUser(EditText email, EditText password) {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        mAuth.signInWithEmailAndPassword(emailString.toLowerCase(), passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            openHomeActivity();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.failedToLogin, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void openHomeActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private boolean loginInputValidation(EditText email, EditText password) {
        View focusView = null;
        boolean inputComplete = true;
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        if (passwordString.isEmpty()) {
            password.setError(getString(R.string.password_required));
            focusView = password;
            inputComplete = false;
        }

        if (emailString.isEmpty()) {
            email.setError(getString(R.string.email_required));
            focusView = email;
            inputComplete = false;
        }
        if (focusView != null) {
            focusView.requestFocus();
        }

        return inputComplete;
    }

//    public class Download extends AsyncTask<String, String, String> {
//        //TODO check permission
//
//        public Download() {
//        }
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            System.out.println("Starting download");
//        }
//
//        @Override
//        protected String doInBackground(String... f_url) {
//
//            int count;
//            try {
//                String root = Environment.getExternalStorageDirectory().toString();
//
//                System.out.println("Downloading");
//                URL url = new URL(f_url[0]);
//
//                URLConnection conection = url.openConnection();
//                conection.connect();
//                // getting file length
//                int lenghtOfFile = conection.getContentLength();
//
//                // input stream to read file - with 8k buffer
//                InputStream input = new BufferedInputStream(url.openStream(), 8192);
//                // Output stream to write file
//                OutputStream output = new FileOutputStream(root+"/Download/downloadedfile.pdf");
//                byte data[] = new byte[1024];
//                long total = 0;
//
//
//                while ((count = input.read(data)) != -1) {
//                    total += count;
//
//                    // writing data to file
//                    output.write(data, 0, count);
//
//                }
//                // flushing output
//                output.flush();
//
//                // closing streams
//                output.close();
//                input.close();
//
//            } catch (Exception e) {
//                Log.e("Error: ", e.getMessage());
//            }
//
//            return null;
//
//        }
//
//        /**
//         * After completing background task
//         * **/
//        @Override
//        protected void onPostExecute(String file_url) {
//            System.out.println("Downloaded");
//            progressBar.setVisibility(View.INVISIBLE);
//
//        }
//    }

}
