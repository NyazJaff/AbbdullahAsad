package com.abdullah_alsaad.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdullah_alsaad.JavaClass.QuestionAndAnswer;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.activities.FragmentsActivity;

import java.util.HashMap;
import java.util.List;

public class QuestionAndAnswerAdapter extends RecyclerView.Adapter<QuestionAndAnswerAdapter.ViewHolder> {

    Context context;
    private List<QuestionAndAnswer> questionAndAnswerList;
    private Long parentId;
    private String type;
    public String FIREBASE_TABLE_NAME = "questionAndAnswer";


    public QuestionAndAnswerAdapter(Context context, List<QuestionAndAnswer> questionAndAnswerList, Long parentId, String type) {
        this.context = context;
        this.questionAndAnswerList = questionAndAnswerList;
        this.parentId = parentId;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).
                inflate(R.layout.topic_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final QuestionAndAnswer questionAndAnswer = questionAndAnswerList.get(position);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        holder.title.setText(questionAndAnswer.getTile());

        String toolbarTitle = "";
        if(!questionAndAnswer.getTile().isEmpty()){
            toolbarTitle = questionAndAnswer.getTile();
        }else{
            toolbarTitle = questionAndAnswer.getQuestion();
        }
        holder.title.setText(toolbarTitle.length() <= 60 ? toolbarTitle : toolbarTitle.substring(toolbarTitle.length() - 60));

        if(questionAndAnswer.getType().equals("RECORD")){
            holder.topicItemIcon.setImageResource(R.drawable.ic_question_icon);
        }
        holder.parentView.setBackgroundColor((position % 2 == 0) ?
                Color.parseColor("#FFEBF3FF") : Color.argb(0, 0, 0, 0));

        holder.parentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> mapData;
                mapData = new HashMap<>();
                mapData.put("type", questionAndAnswer.getType());
                mapData.put("parentId", questionAndAnswer.getId());
                mapData.put("firebaseTableName", FIREBASE_TABLE_NAME);
                FragmentsActivity fragmentsActivity = (FragmentsActivity) (Activity) context;
                if (questionAndAnswer.getType().equals("RECORD")) {
                    mapData.put("questionAndAnswer", questionAndAnswer);
                    fragmentsActivity.callActivity("TopicItemActivity", mapData);  //TODO change activity maybe
                } else {
                    fragmentsActivity.callFragment("questionAndAnswer", null, mapData);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return questionAndAnswerList.size();
    }

//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        final QuestionAndAnswer questionAndAnswer = questionAndAnswerList.get(position);
//        View view;
//        if (null != convertView) {
//            view = convertView;
//        } else {
//            LayoutInflater buckyInflater = LayoutInflater.from(getContext());
//            view = buckyInflater.inflate(R.layout.topic_layout, parent, false);
//        }
//
//        TextView title = (TextView) view.findViewById(R.id.title);
//        String toolbarTitle = "";
//        if(!questionAndAnswer.getTile().isEmpty()){
//            toolbarTitle = questionAndAnswer.getTile();
//        }else{
//            toolbarTitle = questionAndAnswer.getQuestion();
//        }
//        title.setText(toolbarTitle.length() <= 60 ? toolbarTitle : toolbarTitle.substring(toolbarTitle.length() - 60));
//
////        view.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////
////            }
////        });
//        if(questionAndAnswer.getType().equals("RECORD")){
//            view.findViewById(R.id.topicItemIcon).setBackgroundResource(R.drawable.ic_question_icon);
//        }
//        view.setBackgroundColor((position % 2 == 0) ?
//                Color.parseColor("#FFEBF3FF") : Color.argb(0, 0, 0, 0));
//        return view;
//
//    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private View parentView;
        private ImageView topicItemIcon;
        public ViewHolder(@NonNull View view){
            super(view);
            this.parentView = view;
            this.topicItemIcon = (ImageView) view.findViewById(R.id.topicItemIcon);
            this.title = (TextView) view.findViewById(R.id.title);

        }
    }
}
