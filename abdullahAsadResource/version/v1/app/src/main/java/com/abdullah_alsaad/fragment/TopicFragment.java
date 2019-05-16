package com.abdullah_alsaad.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import com.abdullah_alsaad.JavaClass.Topic;
import com.abdullah_alsaad.OrmLiteDatabseHelper.DbPer;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.activities.FragmentsActivity;
import com.abdullah_alsaad.adapter.TopicAdapter;
import com.abdullah_alsaad.generic.AppUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopicFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private Toolbar toolbar;
    private TextView titleTxt;
    private ImageButton addBookBtn, addNewItemRecord;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AlertDialog alertDialog;
    private List<Topic> topicList;
    private ListAdapter topicAdapter;
    private ListView topicListView;
    private long parentId;
    private HashMap<String, Object> mapData = new HashMap<>();
    private String type = "PARENT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);
        titleTxt = (TextView) toolbar.findViewById(R.id.titleTxt);
        addBookBtn = (ImageButton) toolbar.findViewById(R.id.addBookBtn);
        addBookBtn.setOnClickListener(this);
        addNewItemRecord = (ImageButton) toolbar.findViewById(R.id.addNewItemRecord);
        addNewItemRecord.setOnClickListener(this);
        titleTxt.setText(R.string.lecture);
        topicListView = (ListView) view.findViewById(R.id.topicListView);
        String topicTitle = DbPer.getTopicTitle(getActivity(), parentId);
        if (parentId != 0) {
            titleTxt.setText(topicTitle);
            addBookBtn.setVisibility(View.VISIBLE);
            addNewItemRecord.setVisibility(View.VISIBLE);
        } else {
            addNewItemRecord.setVisibility(View.GONE);
        }
        displayLocalTopic();
        downloadMissingTopic();
        return view;
    }

    private void displayLocalTopic() {
        topicList = DbPer.getAllTopic(getActivity(), parentId);
        if (topicList != null) {
            displayTopicAdapter();
        } else {
            topicList = new ArrayList<>();
        }
    }

    private void downloadMissingTopic() {
        db.collection("Lecture").orderBy("id")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Topic topic = document.toObject(Topic.class);
                                topic.setStringId(document.getId());
                                topicList.add(topic);
                                DbPer.saveTopicLocally(getActivity(), topic);
                            }
                            displayLocalTopic();
                        } else {
                            Log.i("TopicFragment", "Error from downloadMissingTopic function");
                        }
                    }
                });
    }

    private void displayTopicAdapter() {
        if (topicList.size() > 0) {
            topicAdapter = new TopicAdapter(getActivity(), topicList, parentId, type);
            topicListView.setAdapter(topicAdapter);
            topicListView.invalidateViews();
            topicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    AppUtil.showToast(context,"ssd");
                    HashMap<String, Object> mapData;
                    mapData = new HashMap<>();
                    mapData.put("type", topicList.get(i).getType());
                    mapData.put("parentId", topicList.get(i).getId());
                    FragmentsActivity fragmentsActivity = (FragmentsActivity) (Activity) getActivity();
                    if (topicList.get(i).getType().equals("RECORD")) {
                        mapData.put("topic", topicList.get(i));
                        fragmentsActivity.callActivity("TopicItemActivity", mapData);
                    } else {
                        fragmentsActivity.callFragment("lecture", null, mapData);
                    }
                }
            });
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
                openAddTopicWindow("add_topic");
                break;
            case R.id.addNewItemRecord:
                openAddTopicWindow("add_topic_record");
                break;
            default:
        }
    }

    private void openAddTopicWindow(String type) {
        if (AppUtil.checkNetwork(getActivity()) != true) {
            AppUtil.showToast(getActivity(), R.string.noConnectionFound);
            return;
        }
        final EditText pdfURL, mp3URL;
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


        final EditText name = (EditText) viewAddRecord.findViewById(R.id.name);
        final Button save = viewAddRecord.findViewById(R.id.save);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Topic[] topicItem = new Topic[1];
                db.collection("Lecture").orderBy("id", Query.Direction.DESCENDING).limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        topicItem[0] = document.toObject(Topic.class);
                                    }
                                    saveToFireStore(new Topic(topicItem[0] != null ? topicItem[0].getId() : 0, "", name.getText().toString(), parentId, recordType, pdfURL != null ? pdfURL.getText().toString() : "", mp3URL != null ? mp3URL.getText().toString() : ""));
                                } else {
                                    Log.i("openAddTopicWindow func", "Error from downloadBookFromFireStore function");
                                }
                            }
                        });


            }
        });
        alertDialogBuilder.setView(viewAddRecord);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void saveToFireStore(final Topic topic) {
        final Topic newTopic = topic;
        newTopic.setId(topic.getId() + 1);
        Map<String, Object> topicMap = new HashMap<>();

//        if(topic.getType().equals("SUB-PARENT")){
//            newTopic.setParentId((int) topic.getId());
//        }else{
//            newTopic.setParentId(0);
//        }
        db.collection("Lecture")
                .add(newTopic)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        newTopic.setStringId(documentReference.getId());
                        DbPer.saveTopicLocally(getActivity(), newTopic);
                        Toast.makeText(getActivity(), R.string.topic_created, Toast.LENGTH_SHORT).show();
                        topicList.add(newTopic);
                        displayTopicAdapter();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), R.string.failed_please_try_again, Toast.LENGTH_SHORT).show();
                    }
                });
        alertDialog.dismiss();

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
