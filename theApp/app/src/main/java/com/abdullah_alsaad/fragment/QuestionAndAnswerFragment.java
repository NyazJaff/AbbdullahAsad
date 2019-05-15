package com.abdullah_alsaad.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.abdullah_alsaad.R;
import com.google.firebase.auth.FirebaseAuth;

public class QuestionAndAnswerFragment extends Fragment implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView titleTxt;
    private ImageButton addBookBtn, addNewItemRecord;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);
        titleTxt = (TextView) toolbar.findViewById(R.id.titleTxt);
        addBookBtn = (ImageButton) toolbar.findViewById(R.id.addBookBtn);
        addBookBtn.setOnClickListener(this);
        addNewItemRecord = (ImageButton) toolbar.findViewById(R.id.addNewItemRecord);
        addNewItemRecord.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        return  view;

    }

    public QuestionAndAnswerFragment() {
    }



    @Override
    public void onClick(View view) {

    }
}
