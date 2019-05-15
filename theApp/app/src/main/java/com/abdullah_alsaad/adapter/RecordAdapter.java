package com.abdullah_alsaad.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Recycler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdullah_alsaad.JavaClass.Topic;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.activities.FragmentsActivity;

import java.util.HashMap;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    Context context;
    private List<Topic> topicList;
    private Long parentId;
    private String type;
    public String FIREBASE_TABLE_NAME = "";

    public RecordAdapter(Context context, List<Topic> topicList, Long parentId, String type, String firebase_table_name) {
        this.context = context;
        this.topicList = topicList;
        this.parentId = parentId;
        this.type = type;
        this.FIREBASE_TABLE_NAME = firebase_table_name;

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
        final Topic topic = topicList.get(position);

        holder.title.setText(topic.getName());

        if(topic.getType().equals("RECORD")){
            holder.topicItemIcon.setBackgroundResource(R.drawable.ic_new_item);
        }
//        holder.layout_background.setBackgroundColor(context.getResources().getColor(R.color.appBackground));
        holder.parentView.setBackgroundColor((position % 2 == 0) ?
                Color.parseColor("#FFEBF3FF") : Color.argb(0, 0, 0, 0));

        holder.parentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> mapData;
                        mapData = new HashMap<>();
                        mapData.put("type", topic.getType());
                        mapData.put("parentId", topic.getId());
                        mapData.put("firebaseTableName", FIREBASE_TABLE_NAME);
                        FragmentsActivity fragmentsActivity = (FragmentsActivity) (Activity) context;
                        if (topic.getType().equals("RECORD")) {
                            mapData.put("topic", topic);
                            fragmentsActivity.callActivity("TopicItemActivity", mapData);
                        } else {
                            fragmentsActivity.callFragment("lecture", null, mapData);
                        }
            }
        });
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    //    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        final Topic topic = topicList.get(position);
//        View view;
//        if (null != convertView) {
//            view = convertView;
//        } else {
//            LayoutInflater buckyInflater = LayoutInflater.from(getContext());
//            view = buckyInflater.inflate(R.layout.topic_layout, parent, false);
//        }
//
//        TextView name = (TextView) view.findViewById(R.id.title);
//        name.setText(topic.getName());
////        view.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////
////            }
////        });
//        if(topic.getType().equals("RECORD")){
//            view.findViewById(R.id.topicItemIcon).setBackgroundResource(R.drawable.ic_new_item);
//        }
//        view.findViewById(R.id.layout_background).setBackgroundColor(context.getResources().getColor(R.color.appBackground));
//
//
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
