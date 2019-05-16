package com.abdullah_alsaad.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.abdullah_alsaad.JavaClass.QuestionAndAnswer;
import com.abdullah_alsaad.R;

import java.util.List;

public class QuestionAndAnswerAdapter extends ArrayAdapter<QuestionAndAnswer> {

    Context context;
    private List<QuestionAndAnswer> questionAndAnswerList;
    private Long parentId;
    private String type;

    public QuestionAndAnswerAdapter(Context context, List<QuestionAndAnswer> questionAndAnswerList, Long parentId, String type) {
        super(context, R.layout.topic_layout, questionAndAnswerList);
        this.context = context;
        this.questionAndAnswerList = questionAndAnswerList;
        this.parentId = parentId;
        this.type = type;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final QuestionAndAnswer questionAndAnswer = questionAndAnswerList.get(position);
        View view;
        if (null != convertView) {
            view = convertView;
        } else {
            LayoutInflater buckyInflater = LayoutInflater.from(getContext());
            view = buckyInflater.inflate(R.layout.topic_layout, parent, false);
        }

        TextView title = (TextView) view.findViewById(R.id.title);
        String toolbarTitle = "";
        if(!questionAndAnswer.getTile().isEmpty()){
            toolbarTitle = questionAndAnswer.getTile();
        }else{
            toolbarTitle = questionAndAnswer.getQuestion();
        }
        title.setText(toolbarTitle.length() <= 60 ? toolbarTitle : toolbarTitle.substring(toolbarTitle.length() - 60));

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        if(questionAndAnswer.getType().equals("RECORD")){
            view.findViewById(R.id.topicItemIcon).setBackgroundResource(R.drawable.ic_question_icon);
        }
        view.setBackgroundColor((position % 2 == 0) ?
                Color.parseColor("#FFEBF3FF") : Color.argb(0, 0, 0, 0));
        return view;

    }
}
