package com.abdullah_alsaad.activities;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.abdullah_alsaad.JavaClass.QandA;
import com.abdullah_alsaad.JavaClass.RadioSetting;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.adapter.LiveQuestionAdapter;
import com.abdullah_alsaad.generic.AppUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveStream extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private static final int RECOVERY_REQUEST = 1;
    private static final String YOUTUBE = "YOUTUBE";
    private static final String MIXLR = "MIXLR";
    private YouTubePlayerView youTubeView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private YouTubePlayer youTubePlayer;
    private RadioSetting radioSetting = null;
    private ImageButton homeBtn, shareBtn, editBtn, questionTableBtn;
    private Button questionSendBtn;
    private EditText questionBodyTxt, dateSearch;
    private TextView txtTile, askQuestionTxt;
    private Dialog alertDialog;
    private DatePickerDialog dpd;
    private ListView listViw;
    private View row;
    private Date dateSelected, currentDate;
    private AlertDialog dialogQuestion;
    private List<QandA> questionAndAnswerList;
    private DateFormat formatter;
    private ProgressDialog loadDialog;
    private int year, month, day;
    private Switch liveSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        getIntent().setAction("Already created");
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        shareBtn = (ImageButton) findViewById(R.id.shareBtn);
        questionTableBtn = (ImageButton) findViewById(R.id.questionTableBtn);
        editBtn = (ImageButton) findViewById(R.id.editBtn);
        questionSendBtn = (Button) findViewById(R.id.questionSendBtn);
        questionBodyTxt = (EditText) findViewById(R.id.question);
        txtTile = (TextView) findViewById(R.id.txtTile);
        askQuestionTxt = (TextView) findViewById(R.id.askQuestionTxt);
        liveSwitch = (Switch) findViewById(R.id.liveSwitch);
        homeBtn.setOnClickListener(this);
        questionSendBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        questionAndAnswerList = new ArrayList<>();
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        loadDialog = new ProgressDialog(LiveStream.this);
        getSettings();

//        WebView bookingView = (WebView) findViewById(R.id.webview);
//        bookingView.getSettings().setJavaScriptEnabled(true);
//        bookingView.setWebViewClient(new WebViewClient());
//        bookingView.loadUrl("file:///android_asset/mixler.html");
    }

    private void getSettings() {
        if (AppUtil.checkNetwork(LiveStream.this) != true) {
            AppUtil.showToast(LiveStream.this, R.string.noConnectionFound);
            return;
        }
        db.collection(AppUtil.getDevMode("Radio")).document("Setting").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    radioSetting = document.toObject(RadioSetting.class);
                    youTubeView.initialize(radioSetting.getApiKey(), LiveStream.this);
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        setSwitchChange();
                    }
                    if (radioSetting.isLive() == true) {
                        askQuestionTxt.setVisibility(View.VISIBLE);
                        questionBodyTxt.setVisibility(View.VISIBLE);
                        questionSendBtn.setVisibility(View.VISIBLE);
                        findViewById(R.id.liveYoutubeMixlerHolder).setVisibility(View.VISIBLE);
                        setMixlrViewClickListener();


                    }
                    txtTile.setText(radioSetting.getTitle());
                }
            }
        });
    }

    private void setMixlrViewClickListener(){
        findViewById(R.id.mixlrIcon).setOnClickListener(this);
        findViewById(R.id.mixlrTxt).setOnClickListener(this);
    }
    private void setSwitchChange() {
        editBtn.setVisibility(View.VISIBLE);
        questionTableBtn.setVisibility(View.VISIBLE);
        liveSwitch.setVisibility(View.VISIBLE);
        editBtn.setOnClickListener(this);
        questionTableBtn.setOnClickListener(this);

        liveSwitch.setChecked(radioSetting.isLive());
        liveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean switchState) {
                Toast.makeText(getApplicationContext(), R.string.settingsUpdated, Toast.LENGTH_SHORT).show();
                Map<String, Object> setting = new HashMap<>();
                setting.put("live", switchState);
                db.collection(AppUtil.getDevMode("Radio"))
                        .document("Setting")
                        .set(setting, SetOptions.merge());

                openLiveStreamActivity();
            }

        });
    }

    private void openLiveStreamActivity() {
        Intent intent = new Intent(LiveStream.this, LiveStream.class);
        startActivity(intent);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            this.youTubePlayer = youTubePlayer;
            loadYoutubeVideo();
        }
    }

    private boolean inputValidation() {
        View focusView = null;
        boolean inputComplete = true;
        String questionBodyTxtString = questionBodyTxt.getText().toString();
        if (questionBodyTxtString.isEmpty()) {
            questionBodyTxt.setError(getString(R.string.missingInputField));
            focusView = questionBodyTxt;
            inputComplete = false;
        }
        if (focusView != null) {
            focusView.requestFocus();
        }
        return inputComplete;
    }

    public void sendQuestion() {
        if (AppUtil.checkNetwork(LiveStream.this) != true) {
            AppUtil.showToast(LiveStream.this, R.string.noConnectionFound);
            return;
        }
        if (inputValidation() == true) {
            Date date = Calendar.getInstance().getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            Map<String, Object> user = new HashMap<>();
            user.put("question", questionBodyTxt.getText().toString());
            user.put("createdTime", day + "/" + month + "/" + year);


            db.collection(AppUtil.getDevMode("QandA"))
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            questionBodyTxt.setText("");
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.question_sent), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    private void loadYoutubeVideo() {
        this.youTubePlayer.loadVideo(getFormatedYoutubeLiveUrl());
        youTubePlayer.play();
    }

    private String getFormatedYoutubeLiveUrl(){
        String[] url = radioSetting.getYoutubeUrl().split("watch\\?v=");
        if(url.length > 1){
            return url[1];
        }else{
            return ".";
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    protected void onResume() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            restartIntent();
        }

        super.onResume();
    }

    private void restartIntent() {
        String action = getIntent().getAction();
        // Prevent endless loop by adding a unique action, don't restart if action is present
        if (action == null || !action.equals("Already created")) {
            Log.v("Example", "Force restart");
            Intent intent = new Intent(this, LiveStream.class);
            startActivity(intent);
            finish();
        }
        // Remove the unique action so the next time onResume is called it will restart
        else
            getIntent().setAction(null);

        super.onResume();
    }

    @Override
    public void onClick(View view) {
        Intent myIntent = null;
        switch (view.getId()) {
            case R.id.homeBtn:
                myIntent = new Intent(LiveStream.this, MainActivity.class);
                LiveStream.this.startActivity(myIntent);
                break;
            case R.id.questionSendBtn:
                sendQuestion();
                break;
            case R.id.shareBtn:
                shareLive();
                break;
            case R.id.questionTableBtn:
                currentDate = Calendar.getInstance().getTime();
                try {
                    currentDate = formatter.parse(formatter.format(currentDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (radioSetting != null) {
                    loadDialog();
                    getCurrentQuestions(currentDate);
                }
                break;
            case R.id.editBtn:
                if (radioSetting != null) {
                    openEditView();
                }
                break;
            case R.id.mixlrIcon:
            case R.id.mixlrTxt:
                AppUtil.openBrowser(getApplicationContext(),"http://mixlr.com/aalsaad/");
                break;
            default:
        }
    }

    private void loadDialog() {
        loadDialog.setMessage(getString(R.string.loading));
        loadDialog.setTitle(R.string.loadingData);
        loadDialog.setIndeterminate(false);
        loadDialog.setCancelable(true);
        loadDialog.show();
    }

    private void getCurrentQuestions(Date date) {
        loadDialog();

        questionAndAnswerList.clear();
        CollectionReference usersRef = db.collection(AppUtil.getDevMode("QandA"));
        Query query = usersRef
                .whereGreaterThanOrEqualTo("createdTime", date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        db.collection(AppUtil.getDevMode("QandA")).whereEqualTo("createdTime", day + "/" + month + "/" + year)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                QandA item = document.toObject(QandA.class);
                                item.setId(document.getId());
                                questionAndAnswerList.add(item);
                            }
                            openQuestionView(day + "/" + month + "/" + year);
                        } else {
                            Log.i("LiveStream", "Error from getCurrentQuestions function");
                        }
                    }
                });
    }

    private void openQuestionView(String createdTime) {
        LiveQuestionAdapter adapter;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        row = getLayoutInflater().inflate(R.layout.live_view_question, null);
        listViw = (ListView) row.findViewById(R.id.questionAndAnswerListView);
        dateSearch = (EditText) row.findViewById(R.id.dateSearch);

        dateSearch.setText(createdTime);


        adapter = new LiveQuestionAdapter(LiveStream.this, questionAndAnswerList);
        listViw.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        dateSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateAndSearch();
            }
        });
        dateSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    setDateAndSearch();
                }
            }
        });

        alertDialog.setView(row);
        dialogQuestion = alertDialog.create();
        dialogQuestion.show();
        loadDialog.dismiss();

//        View commentView = getLayoutInflater().inflate(R.layout.live_view_question, null);
//        alertDialog = new Dialog(LiveStream.this);
//        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//
//        alertDialog.setContentView(commentView);
//
//
//        alertDialog.show();

//        alertDialog = new Dialog(LiveStream.this);
//        alertDialog.setContentView(R.layout.live_view_question);
////        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////        alertDialog.setContentView(questionView);
//        alertDialog.show();
//
//        questionAndAnswerListView = alertDialog.findViewById(R.id.questionAndAnswerListView);
//
//        if(questionAndAnswerList.size() > 0){
//
//            questionAndAnswerListView.setAdapter(new LiveQuestionAdapter(LiveStream.this,questionAndAnswerList));
//            questionAndAnswerListView.invalidateViews();
//        }
//
    }

    private void setDateAndSearch() {
        Calendar now = Calendar.getInstance();
        now.get(Calendar.YEAR);
        now.get(Calendar.MONTH);
        now.get(Calendar.DAY_OF_MONTH - 1);

        dpd = DatePickerDialog.newInstance(
                LiveStream.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void openEditView() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        row = getLayoutInflater().inflate(R.layout.edit_live, null);
        final EditText title = row.findViewById(R.id.title);
        final EditText youtubeUrl = row.findViewById(R.id.youtubeUrl);
        Button save = row.findViewById(R.id.save);

        title.setText(radioSetting.getTitle());
        youtubeUrl.setText(radioSetting.getYoutubeUrl());
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> setting = new HashMap<>();
                setting.put("title", title.getText().toString());
                setting.put("youtubeUrl", youtubeUrl.getText().toString());

                db.collection("Radio")
                        .document("Setting")
                        .set(setting, SetOptions.merge());
                restartIntent();
                dialogQuestion.dismiss();
            }
        });
        alertDialog.setView(row);
        dialogQuestion = alertDialog.create();
        dialogQuestion.show();
    }

    private void shareLive() {
        String text = String.format(getApplicationContext().getString(R.string.shareLive), getApplicationContext().getString(R.string.liveStreamText) + "\n\n" + txtTile.getText().toString() , getApplicationContext().getString(R.string.appLink));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.shareWithOthers)));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            int month = monthOfYear + 1;
            dateSelected = sdf.parse(dayOfMonth + "/" + month + "/" + year);
            dialogQuestion.dismiss();
            getCurrentQuestions(dateSelected);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
