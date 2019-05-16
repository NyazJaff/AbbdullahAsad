package com.abdullah_alsaad.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.abdullah_alsaad.JavaClass.QuestionAndAnswer;
import com.abdullah_alsaad.JavaClass.Topic;
import com.abdullah_alsaad.OrmLiteDatabseHelper.DbPer;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.activities.FragmentsActivity;
import com.abdullah_alsaad.adapter.QuestionAndAnswerAdapter;
import com.abdullah_alsaad.adapter.RecordAdapter;
import com.abdullah_alsaad.generic.AppUtil;
import com.abdullah_alsaad.generic.SharedPreferenceObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class TopicFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private Toolbar toolbar;
    private TextView titleTxt;
    private ImageButton addBookBtn, addNewItemRecord;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AlertDialog alertDialog;
    private List<Topic> topicList;
    private List<QuestionAndAnswer> questionAndAnswerList;
    private ListAdapter listAdapter;
    private RecyclerView recordListView;
    private long parentId;
    private HashMap<String, Object> mapData = new HashMap<>();
    private String type = "PARENT";
    private FirebaseAuth mAuth;
    private ProgressDialog nDialog;
    private boolean fireBaseSaveInProgress = false;
    public String FIREBASE_TABLE_NAME = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);
        titleTxt = (TextView) toolbar.findViewById(R.id.titleTxt);
        addBookBtn = (ImageButton) toolbar.findViewById(R.id.addBookBtn);
        addBookBtn.setOnClickListener(this);
        addNewItemRecord = (ImageButton) toolbar.findViewById(R.id.addNewItemRecord);
        addNewItemRecord.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        recordListView = (RecyclerView) view.findViewById(R.id.topicListView);
        String toolbarTitle = "";

        if (parentId != 0) {
            if(FIREBASE_TABLE_NAME.equals("questionAndAnswer")){
                addNewItemRecord.setImageResource(R.drawable.ic_question_icon);
//                addNewItemRecord.setBackground();
                QuestionAndAnswer questionAndAnswer = DbPer.getQuestionAndAnswerTitle(getActivity(), parentId);
                if(!questionAndAnswer.getTile().isEmpty()){
                    toolbarTitle = questionAndAnswer.getTile();
                }else{
                    toolbarTitle = questionAndAnswer.getQuestion().substring(0, Math.min(questionAndAnswer.getQuestion().length(), 35));
                }
            }else {
                toolbarTitle = DbPer.getTopicTitle(getActivity(), parentId);
            }

            titleTxt.setText(toolbarTitle.length() <= 60 ? toolbarTitle : toolbarTitle.substring(toolbarTitle.length() - 60));
            addBookBtn.setVisibility(View.VISIBLE);
            addNewItemRecord.setVisibility(View.VISIBLE);
        } else {
            if (FIREBASE_TABLE_NAME.equals("lecture")) {
                titleTxt.setText(R.string.lectures);
            } else if (FIREBASE_TABLE_NAME.equals("speech")) {
                titleTxt.setText(R.string.speech);
            } else if (FIREBASE_TABLE_NAME.equals("questionAndAnswer")) {
                titleTxt.setText(R.string.questionAndAnswer);
            }
            addNewItemRecord.setVisibility(View.GONE);
        }

        if (mAuth.getCurrentUser() == null) {
            addBookBtn.setVisibility(View.GONE);
            addNewItemRecord.setVisibility(View.GONE);
        }

        if (FIREBASE_TABLE_NAME.equals("questionAndAnswer")) {
            displayLocalQuestionAndAnswer();
            downloadMissingRecord("QuestionAndAnswer");
        } else {
            displayLocalTopic();
            downloadMissingRecord("Lecture");
        }
        return view;
    }

    private void displayLocalQuestionAndAnswer() {
        questionAndAnswerList = DbPer.getAllQuestionAndAnswer(getActivity(), parentId);
        if (questionAndAnswerList != null) {
            displayRecordAdapter();
        } else {
            questionAndAnswerList = new ArrayList<>();
        }
    }

    private void displayLocalTopic() {
        topicList = DbPer.getAllTopic(getActivity(), parentId, FIREBASE_TABLE_NAME);
        if (topicList != null) {
            displayRecordAdapter();
        } else {
            topicList = new ArrayList<>();
        }
    }

    private void downloadMissingRecord(String firebaseTableName) {
        nDialog = new ProgressDialog(getActivity());
        nDialog.setMessage(getString(R.string.inProgress));
        nDialog.setTitle(R.string.loadingData);
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        if (parentId == 0 && AppUtil.checkNetwork(getActivity())) {
            nDialog.show();
        }

        Integer lastInsertedValue;
        if(FIREBASE_TABLE_NAME.equals("questionAndAnswer")){
            lastInsertedValue = DbPer.getQuestionAndAnswerLastItemInserted(getActivity());
        }else {
            lastInsertedValue = DbPer.getTopicItemLastItemInserted(getActivity());
        }

        if (lastInsertedValue != null) {
            db.collection(AppUtil.getDevMode(firebaseTableName)).whereGreaterThan("id", lastInsertedValue)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(FIREBASE_TABLE_NAME.equals("questionAndAnswer")){
                                    fireBaseQuestionAndAnswerResultHandler(task);
                                }else {
                                    fireBaseTopicResultHandler(task);
                                }
                            } else {
                                Log.i("TopicFragment", "Error from downloadMissingRecord function");
                            }
                        }
                    });
        } else {
            db.collection(AppUtil.getDevMode(firebaseTableName)).orderBy("id")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(FIREBASE_TABLE_NAME.equals("questionAndAnswer")){
                                    fireBaseQuestionAndAnswerResultHandler(task);
                                }else {
                                    fireBaseTopicResultHandler(task);
                                }
                            } else {
                                Log.i("TopicFragment", "Error from downloadMissingRecord function");
                            }
                        }
                    });
        }
    }

    private void fireBaseQuestionAndAnswerResultHandler(@NonNull Task<QuerySnapshot> task) {
        for (DocumentSnapshot document : task.getResult()) {
            QuestionAndAnswer questionAndAnswer = document.toObject(QuestionAndAnswer.class);
            questionAndAnswerList.add(questionAndAnswer);
            DbPer.saveQuestionAndAnswerLocally(getActivity(), questionAndAnswer);
        }
//        topicL/istView.invalidateViews();
        if (nDialog.isShowing()) {
            nDialog.dismiss();
        }
        displayLocalQuestionAndAnswer();
    }

    private void fireBaseTopicResultHandler(@NonNull Task<QuerySnapshot> task) {
        for (DocumentSnapshot document : task.getResult()) {
            Topic topic = document.toObject(Topic.class);
            topic.setStringId(document.getId());
            topicList.add(topic);
            DbPer.saveTopicLocally(getActivity(), topic);
        }
//        topicL/istView.invalidateViews();
        if (nDialog.isShowing()) {
            nDialog.dismiss();
        }
        displayLocalTopic();
    }

    private void displayRecordAdapter() {

        if (FIREBASE_TABLE_NAME.equals("questionAndAnswer")) {
            if (questionAndAnswerList.size() > 0) {
                recordListView.setAdapter(new QuestionAndAnswerAdapter(getActivity(), questionAndAnswerList, parentId, type));
                recordListView.setLayoutManager(new LinearLayoutManager(getActivity()));
//                recordListView.invalidateViews();
//                recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        HashMap<String, Object> mapData;
//                        mapData = new HashMap<>();
//                        mapData.put("type", questionAndAnswerList.get(i).getType());
//                        mapData.put("parentId", questionAndAnswerList.get(i).getId());
//                        mapData.put("firebaseTableName", FIREBASE_TABLE_NAME);
//                        FragmentsActivity fragmentsActivity = (FragmentsActivity) (Activity) getActivity();
//                        if (questionAndAnswerList.get(i).getType().equals("RECORD")) {
//                            mapData.put("questionAndAnswer", questionAndAnswerList.get(i));
//                            fragmentsActivity.callActivity("TopicItemActivity", mapData);  //TODO change activity maybe
//                        } else {
//                            fragmentsActivity.callFragment("questionAndAnswer", null, mapData);
//                        }
//                    }
//                });
            }
        } else {
            if (topicList.size() > 0) {
                recordListView.setAdapter(new RecordAdapter(getActivity(), topicList, parentId, type, FIREBASE_TABLE_NAME));
                recordListView.setLayoutManager(new LinearLayoutManager(getActivity()));

//                listAdapter = new RecordAdapter(getActivity(), topicList, parentId, type);
//                recordListView.setAdapter(listAdapter);
//                recordListView.invalidateViews();
//                recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                    AppUtil.showToast(context,"ssd");
//                        HashMap<String, Object> mapData;
//                        mapData = new HashMap<>();
//                        mapData.put("type", topicList.get(i).getType());
//                        mapData.put("parentId", topicList.get(i).getId());
//                        mapData.put("firebaseTableName", FIREBASE_TABLE_NAME);
//                        FragmentsActivity fragmentsActivity = (FragmentsActivity) (Activity) getActivity();
//                        if (topicList.get(i).getType().equals("RECORD")) {
//                            mapData.put("topic", topicList.get(i));
//                            fragmentsActivity.callActivity("TopicItemActivity", mapData);
//                        } else {
//                            fragmentsActivity.callFragment("lecture", null, mapData);
//                        }
//                    }
//                });
            }
        }
    }

    public TopicFragment() {
    }

    public static TopicFragment newInstance(String param1, String param2) {
        TopicFragment fragment = new TopicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mapData = (HashMap<String, Object>) getArguments().getSerializable("mapData");

            parentId = (long) mapData.get("parentId");
            FIREBASE_TABLE_NAME = (String) mapData.get("firebaseTableName");
            type = (String) mapData.get("type");
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addBookBtn:
                if(FIREBASE_TABLE_NAME.equals("questionAndAnswer")){
                    openAddQuestionAndAnswerWindow("add_question_section");
                }else {
                    openAddTopicWindow("add_topic");
                }
                break;
            case R.id.addNewItemRecord:
                if(FIREBASE_TABLE_NAME.equals("questionAndAnswer")){
                    openAddQuestionAndAnswerWindow("add_question_section_record");
                }else {
                    openAddTopicWindow("add_topic_record");
                }
                break;
            default:
        }
    }

    private void openAddQuestionAndAnswerWindow(String type) {
        if (AppUtil.checkNetwork(getActivity()) != true) {
            AppUtil.showToast(getActivity(), R.string.noConnectionFound);
            return;
        }
        final EditText title, question, answer, mp3URL;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        View viewAddRecord = getLayoutInflater().inflate(R.layout.add_question_record, null);
        title = viewAddRecord.findViewById(R.id.title);
        question = viewAddRecord.findViewById(R.id.question);
        answer = viewAddRecord.findViewById(R.id.answer);
        mp3URL = viewAddRecord.findViewById(R.id.mp3URL);
        final Button save = viewAddRecord.findViewById(R.id.save);

        final String recordType;
        if (type.equals("add_question_section")) {
            recordType = "PARENT";
            question.setVisibility(View.GONE);
            answer.setVisibility(View.GONE);
            mp3URL.setVisibility(View.GONE);
        } else {
            recordType = "RECORD";
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fireBaseSaveInProgress == false) {
                    fireBaseSaveInProgress = true;
                    final QuestionAndAnswer[] questionAndAnswers = new QuestionAndAnswer[1];
                    db.collection(AppUtil.getDevMode("QuestionAndAnswer")).orderBy("id", Query.Direction.DESCENDING).limit(1)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            questionAndAnswers[0] = document.toObject(QuestionAndAnswer.class);
                                        }
                                        saveQuestionToFireStore(new QuestionAndAnswer(questionAndAnswers[0] != null ? questionAndAnswers[0].getId() : 0, title.getText().toString(), parentId, recordType,question.getText().toString(),answer.getText().toString(), mp3URL.getText().toString()));
                                    } else {
                                        Log.i("openAddTopicWindow func", "Error from downloadBookFromFireStore function");
                                    }
                                }
                            });
                } else {
                    AppUtil.showToast(getActivity(), getString(R.string.saveInProgress));
                }
            }
        });
        alertDialogBuilder.setView(viewAddRecord);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void saveQuestionToFireStore(QuestionAndAnswer questionAndAnswer){
        final QuestionAndAnswer newQuestionAndAnswer = questionAndAnswer;
        newQuestionAndAnswer.setId(questionAndAnswer.getId() + 1);
        db.collection(AppUtil.getDevMode("QuestionAndAnswer"))
                .add(newQuestionAndAnswer)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        DbPer.saveQuestionAndAnswerLocally(getActivity(), newQuestionAndAnswer);
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.topic_created), Toast.LENGTH_SHORT).show();
                        questionAndAnswerList.add(newQuestionAndAnswer);
                        displayRecordAdapter();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.failed_please_try_again), Toast.LENGTH_SHORT).show();
                    }
                });
        fireBaseSaveInProgress = false;
        alertDialog.dismiss();
    }

    private void openAddTopicWindow(String type) {
        if (AppUtil.checkNetwork(getActivity()) != true) {
            AppUtil.showToast(getActivity(), R.string.noConnectionFound);
            return;
        }
        final EditText name, pdfURL, mp3URL;
        SharedPreferenceObject sharedPreferenceObject = loadSettings("SharedPreferenceObject");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        View viewAddRecord = null;
        final String recordType;

        if (type.equals("add_topic")) {
            mp3URL = null;
            pdfURL = null;
            viewAddRecord = getLayoutInflater().inflate(R.layout.add_topic, null);
            recordType = "PARENT";
        } else {
            viewAddRecord = getLayoutInflater().inflate(R.layout.add_topic_record, null);
            pdfURL = (EditText) viewAddRecord.findViewById(R.id.pdfURL);
            mp3URL = (EditText) viewAddRecord.findViewById(R.id.mp3URL);
            recordType = "RECORD";
        }
        name = (EditText) viewAddRecord.findViewById(R.id.title);
        if (sharedPreferenceObject != null && recordType.equals("RECORD")) {
            pdfURL.setText(sharedPreferenceObject.getPdfURL());
            mp3URL.setText(sharedPreferenceObject.getMp3URL());
            name.setText(sharedPreferenceObject.getName());
        }
        final Button save = viewAddRecord.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fireBaseSaveInProgress == false) {
                    fireBaseSaveInProgress = true;
                    final Topic[] topicItem = new Topic[1];
                    db.collection(AppUtil.getDevMode("Lecture")).orderBy("id", Query.Direction.DESCENDING).limit(1)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            topicItem[0] = document.toObject(Topic.class);
                                        }
                                        saveToFireStore(new Topic(topicItem[0] != null ? topicItem[0].getId() : 0, "", name.getText().toString(), parentId, recordType, pdfURL != null ? pdfURL.getText().toString() : "", mp3URL != null ? mp3URL.getText().toString() : "", FIREBASE_TABLE_NAME));
                                        if (recordType.equals("RECORD")) {
                                            updatePreference(name.getText().toString(), mp3URL != null ? mp3URL.getText().toString() : "", pdfURL != null ? pdfURL.getText().toString() : "");
                                        }
                                    } else {
                                        Log.i("openAddTopicWindow func", "Error from downloadBookFromFireStore function");
                                    }
                                }
                            });
                } else {
                    AppUtil.showToast(getActivity(), getString(R.string.saveInProgress));
                }
            }
        });
        alertDialogBuilder.setView(viewAddRecord);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updatePreference(String name, String mp3URL, String pdfURL) {
        SharedPreferenceObject sharedPreferenceObject = new SharedPreferenceObject();
        if (loadSettings("SharedPreferenceObject") != null) {
            sharedPreferenceObject = loadSettings("SharedPreferenceObject");
        }

        sharedPreferenceObject.setName(name);
        sharedPreferenceObject.setMp3URL(mp3URL);
        sharedPreferenceObject.setPdfURL(pdfURL);

        SharedPreferences mPrefs = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(sharedPreferenceObject);
        prefsEditor.putString("SharedPreferenceObject", json);
        prefsEditor.commit();

    }

    private SharedPreferenceObject loadSettings(String key) {
        SharedPreferences mPrefs = getActivity().getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(key, "");
        SharedPreferenceObject sharedPreferenceObject = gson.fromJson(json, SharedPreferenceObject.class);
        return sharedPreferenceObject;
    }

    private void saveToFireStore(final Topic topic) {
        final Topic newTopic = topic;
        newTopic.setId(topic.getId() + 1);
        Map<String, Object> topicMap = new HashMap<>();
        db.collection(AppUtil.getDevMode("Lecture"))
                .add(newTopic)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        newTopic.setStringId(documentReference.getId());
                        DbPer.saveTopicLocally(getActivity(), newTopic);
                        topicList.add(newTopic);
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.topic_created), Toast.LENGTH_SHORT).show();
                        displayRecordAdapter();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.failed_please_try_again), Toast.LENGTH_SHORT).show();
                    }
                });
        fireBaseSaveInProgress = false;
        alertDialog.dismiss();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
