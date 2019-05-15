package com.abdullah_alsaad.activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abdullah_alsaad.JavaClass.QuestionAndAnswer;
import com.abdullah_alsaad.JavaClass.Topic;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.generic.AppUtil;
import com.abdullah_alsaad.generic.BackgroundAsync;
import com.abdullah_alsaad.generic.StorageHandler;
import com.abdullah_alsaad.receiver.OnClearFromRecentService;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopicItemActivity extends AppCompatActivity implements OnPageChangeListener {
    private Notification.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews;
    private Toolbar toolbar;
    private Context context;
    private PDFView pdfView;
    private Button playBtn, button_back_ten_seconds, button_back_five_seconds;
    private SeekBar positionBar;
    private TextView elapsedTimeLabel, remainingTimeLabel, questionAnswerTextView, title;
    private MediaPlayer mp;
    private int totalTime;
    private Topic topic;
    private QuestionAndAnswer questionAndAnswer;
    private ProgressBar progressbar;
    private boolean mp3SetupComplete;
    private HashMap<String, Object> mapData;
    private Handler mHandler;
    private boolean downloadInProgress = false;
    public String FIREBASE_TABLE_NAME = "", fileStringId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_item);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.app_bar_back_nav);
        title = (TextView) toolbar.findViewById(R.id.titleTxt);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        permission_check();
//        questionAnswerSetup();
    }

    public void pdfSetup() {
        if (StorageHandler.pdfExists(Integer.valueOf(fileStringId))) {
            findViewById(R.id.pdfView).setVisibility(View.VISIBLE);
            findViewById(R.id.blurredPdfImage).setVisibility(View.GONE);
            findViewById(R.id.pdfDownloadBtn).setVisibility(View.GONE);
            File file = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "abdullahAlSaad/pdf/" + fileStringId + ".pdf");
            pdfView = (PDFView) findViewById(R.id.pdfView);
//            pdfView.setBackgroundColor(Color.RED);
            pdfView.fromFile(file)
                    .onPageChange(this)
                    .defaultPage(0)
                    .load();
            pdfView.zoomTo(1);
//            pdfView.setBackgroundResource(R.drawable.header_bg_tran);
//            pdfView.setBackgroundColor(Color.RED);
//            pdfView.setBackground(Color.RED);
            return;
        }
        findViewById(R.id.pdfView).setVisibility(View.GONE);
        findViewById(R.id.blurredPdfImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadPDF();
            }
        });
        findViewById(R.id.pdfDownloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadPDF();
            }
        });
    }

    private void downloadPDF() {
        if (AppUtil.checkNetwork(context) == true) {
            if (topic.getPdfURL() != null && !topic.getPdfURL().isEmpty()) {
                downloadFile("pdf", topic.getPdfURL());
//                AppUtil.showToast(context, "downloading file");
            }
        } else {
            AppUtil.showToast(context, getString(R.string.connectToNetworkToDownloadFileForFirstTime));
        }
    }

    private void mp3Setup() {
        playBtn = (Button) findViewById(R.id.playBtn);
        button_back_ten_seconds = (Button) findViewById(R.id.button_back_ten_seconds);
        button_back_five_seconds = (Button) findViewById(R.id.button_back_five_seconds);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBtnClick();
            }
        });

        if (StorageHandler.mp3Exists(Integer.valueOf(fileStringId))) {
            File file = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "abdullahAlSaad/mp3/" + fileStringId + ".mp3");

            elapsedTimeLabel = (TextView) findViewById(R.id.elapsedTimeLabel);
            remainingTimeLabel = (TextView) findViewById(R.id.remainingTimeLabel);

            Uri fileUri = Uri.fromFile(file);
            mp = MediaPlayer.create(context, fileUri);
            if (mp != null) {
                mp.seekTo(0);
            }

            totalTime = mp.getDuration();

            positionBar = (SeekBar) findViewById(R.id.positionBar);
            positionBar.setMax(totalTime);
            positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser == true) {
                        mp.seekTo(progress);
                        positionBar.setProgress(progress);
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            TopicItemActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int currentPossition = mp.getCurrentPosition();
                        positionBar.setProgress(currentPossition);
                        String elapseTime = createTimeLabel(currentPossition);
                        elapsedTimeLabel.setText(elapseTime);
                        String remainingTime = createTimeLabel(totalTime - currentPossition);
                        remainingTimeLabel.setText(remainingTime);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            });

            button_back_ten_seconds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int num = mp.getCurrentPosition();
                    mp.seekTo(num - 11000);
                }
            });
            button_back_five_seconds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int num = mp.getCurrentPosition();
                    mp.seekTo(num - 6000);
                }
            });
            mp3SetupComplete = true;
            mp3NotificationSetup();
        }
    }

    public void resetDownloadInProgress() {
        downloadInProgress = false;
    }

    public void playBtnClick() {
        if (StorageHandler.mp3Exists(Integer.valueOf(fileStringId))) {
            if (mp3SetupComplete != true) {
                mp3Setup();
            }
            if (!mp.isPlaying()) {
                mp.start();
                playBtn.setBackgroundResource(R.drawable.ic_pause_button);
            } else {
                mp.pause();
                playBtn.setBackgroundResource(R.drawable.ic_play_button);
            }
        } else if (AppUtil.checkNetwork(context) == true) {
            if (FIREBASE_TABLE_NAME.equals("questionAndAnswer")) {
                if (questionAndAnswer.getMp3URL() != null && !questionAndAnswer.getMp3URL().isEmpty()) {
                    downloadFile("mp3", questionAndAnswer.getMp3URL());
                }
            } else {
                if (topic.getMp3URL() != null && !topic.getMp3URL().isEmpty()) {
                    downloadFile("mp3", topic.getMp3URL());
                }
            }
        } else {
            AppUtil.showToast(context, getString(R.string.connectToNetworkToDownloadFileForFirstTime));
        }
    }

    private void downloadFile(String format, String url) {

        if (downloadInProgress == false) {
            progressbar.setVisibility(View.VISIBLE);
            AppUtil.showToast(context, getString(R.string.downloading));
            BackgroundAsync backgroundAsync = new BackgroundAsync(Integer.valueOf(fileStringId), format, context, progressbar);
            backgroundAsync.execute(url);
            downloadInProgress = true;
        } else {
            AppUtil.showToast(context, getString(R.string.downloadInProgress));
        }
    }
    private void mp3NotificationSetup() {
        if (mp != null) {
            remoteViews = new RemoteViews(getPackageName(), R.layout.custom_mp3_notification);
            remoteViews.setImageViewResource(R.id.app_icon, R.mipmap.ic_launcher);
            remoteViews.setTextViewText(R.id.title, "test");
            notification_id = (int) System.currentTimeMillis();
            Intent button_intent = new Intent("button_clicked");
            button_intent.putExtra("id", notification_id);
            PendingIntent p_button_intent = PendingIntent.getBroadcast(context, 123, button_intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.button, p_button_intent);
        }
    }
    private void showNotificationBar() {
        if (mp != null) {
            Intent notification_intent = new Intent(context, TopicItemActivity.class);
            notification_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 123, notification_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            builder = new Notification.Builder(getApplicationContext());
            String title;
            if(FIREBASE_TABLE_NAME.equals("questionAndAnswer")){
                title = getTitleForQuestionAndAnswer();
            }else {
                title = topic.getName();
            }
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setContentText(title)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(123, builder.build());
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }

    private void destroyNotification() {
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        destroyNotification();
//        Toast.makeText(context, "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Toast.makeText(context, "onPause", Toast.LENGTH_SHORT).show();
        if (mp != null && mp.isPlaying()) {
            showNotificationBar();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        destroyNotification();
        if (mp != null) {
            mp.pause();
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialize();
                } else {
                    permission_check();
                }
                return;
            }
        }
    }

    private void permission_check() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                return;
            }
        }
        initialize();
    }

    public Spanned questionAnswerSetup(String question, String answer) {
//        String a = "Question:\\n test test test test test Answer: \\n This is a #sample #twitter text of @tom_cruise with a link http://www.google.com";
        String questionTitle = getString(R.string.question)+ ":";
        String answerTitle = getString(R.string.answer)+ ":";
        String a = String.format(context.getString(R.string.questionAndAnswerBody)
                , "<br/>" + questionTitle + "<br />"
                , question.replaceAll("\\n", "<br />") + "<br/><br/>"
                , answerTitle + "<br/>"
                , answer.replaceAll("\\n", "<br />"));


//        Pattern p = Pattern.compile(question);
//        Matcher m = p.matcher(INPUT);

        Pattern questionPattern = Pattern.compile(questionTitle);
        Pattern titlePattern = Pattern.compile(answerTitle);

        StringBuffer sb = new StringBuffer(a.length());
        Matcher o = titlePattern.matcher(a);

        while (o.find()) {
            o.appendReplacement(sb, "<font color=\"#428eff\">" + o.group(0) + "</font>");
        }
        o.appendTail(sb);

        Matcher n = questionPattern.matcher(sb.toString());
        sb = new StringBuffer(sb.length());

        while (n.find()) {
            n.appendReplacement(sb, "<font color=\"#ff4081\" >" + n.group(0) + "</font>");
        }
        n.appendTail(sb);
        findViewById(R.id.pdfView).setVisibility(View.GONE);
        findViewById(R.id.blurredPdfImage).setVisibility(View.GONE);
        findViewById(R.id.pdfDownloadBtn).setVisibility(View.GONE);

        return (Html.fromHtml(sb.toString()));
//        questionAnswer = findViewById(R.id.questionAnswer);
//        questionAnswer.setText(Html.fromHtml(sb.toString()));
    }

    private void initialize() {
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = getIntent();
        mapData = (HashMap<String, Object>) intent.getSerializableExtra("mapData");
        if (mapData != null) {
            FIREBASE_TABLE_NAME = (String) mapData.get("firebaseTableName");
            if (FIREBASE_TABLE_NAME.equals("questionAndAnswer")) {
                if (mapData.get("questionAndAnswer") != null) {
                    questionAndAnswer = (QuestionAndAnswer) mapData.get("questionAndAnswer");
                    String toolbarTitle;
                    toolbarTitle = getTitleForQuestionAndAnswer();
                    title.setText(toolbarTitle.length() <= 60 ? toolbarTitle : toolbarTitle.substring(toolbarTitle.length() - 60));

                    findViewById(R.id.blurredPdfImage).setVisibility(View.GONE);
                    findViewById(R.id.pdfDownloadBtn).setVisibility(View.GONE);
                    fileStringId = questionAndAnswer.getParentId() + "" + questionAndAnswer.getId();
                    if (questionAndAnswer.getMp3URL() != null && !questionAndAnswer.getMp3URL().isEmpty()) {
                        flipImages();
                        mp3Setup();
                    } else {
                        findViewById(R.id.mp3ViewConstraint).setVisibility(View.GONE);
                    }
                    questionAnswerTextView = (TextView) findViewById(R.id.questionAnswer);
                    questionAnswerTextView.setText(questionAnswerSetup(questionAndAnswer.getQuestion(), questionAndAnswer.getAnswer()));
                }
            } else {
                if (mapData.get("topic") != null) {
                    topic = (Topic) mapData.get("topic");
                    title.setText(topic.getName());
                    fileStringId = topic.getParentId() + "" + topic.getId();
                    if (topic.getMp3URL() != null && !topic.getMp3URL().isEmpty()) {
                        flipImages();
                        mp3Setup();
                    } else {
                        findViewById(R.id.mp3ViewConstraint).setVisibility(View.GONE);
                    }

                    if (topic.getPdfURL() != null && !topic.getPdfURL().isEmpty()) {
                        pdfSetup();
                    } else {
                        findViewById(R.id.pdfView).setVisibility(View.GONE);
                        findViewById(R.id.blurredPdfImage).setVisibility(View.GONE);
                        findViewById(R.id.pdfDownloadBtn).setVisibility(View.GONE);
                    }
                    findViewById(R.id.questionAnswer).setVisibility(View.GONE);

                }
            }
        }
    }

    private String getTitleForQuestionAndAnswer() {
        String toolbarTitle;
        if (!questionAndAnswer.getTile().isEmpty()) {
            toolbarTitle = questionAndAnswer.getTile();
        } else {
            toolbarTitle = questionAndAnswer.getQuestion().substring(0, Math.min(questionAndAnswer.getQuestion().length(), 35));
        }
        return toolbarTitle;
    }

    private void flipImages() {
        if (!Locale.getDefault().getLanguage().equals("en")) {
            findViewById(R.id.button_back_five_seconds).setScaleX(-1);
            findViewById(R.id.button_back_ten_seconds).setScaleX(-1);
        }
    }
}
