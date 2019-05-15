package com.abdullah_alsaad.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.abdullah_alsaad.JavaClass.QandA;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.generic.AppUtil;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LiveQuestionAdapter extends ArrayAdapter<QandA> implements View.OnClickListener{
    Context context;
    private List<QandA> adapterList;

    FirebaseFirestore db;

    public LiveQuestionAdapter(Context context, List<QandA> adapterList) {
        super(context, R.layout.view_comment_layout, adapterList);
        this.context = context;
        this.adapterList = adapterList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        final QandA currentItem = adapterList.get(position);
        View view;
        if (null != convertView) {
            view = convertView;
        } else {
            LayoutInflater buckyInflater = LayoutInflater.from(getContext());
            view = buckyInflater.inflate(R.layout.view_comment_layout, parent, false);
        }

        TextView questionTxt = (TextView) view.findViewById(R.id.commentTxt);
        TextView bookDetailsTxt = (TextView) view.findViewById(R.id.bookDetailsTxt);
        ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
        questionTxt.setText(currentItem.getQuestion());
        bookDetailsTxt.setVisibility(View.INVISIBLE);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterList.remove(position);
                db.collection(AppUtil.getDevMode("QandA")).document(currentItem.getId()).delete();
                notifyDataSetChanged();
            }
        });
        return view;
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnDelete :

                break;
            default :
        }

    }
}
