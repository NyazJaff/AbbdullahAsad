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

import com.abdullah_alsaad.JavaClass.Topic;
import com.abdullah_alsaad.R;

import java.util.List;

public class RecordAdapter extends ArrayAdapter<Topic> {

    Context context;
    private List<Topic> topicList;
    private Long parentId;
    private String type;

    public RecordAdapter(Context context, List<Topic> topicList, Long parentId, String type) {
        super(context, R.layout.topic_layout, topicList);
        this.context = context;
        this.topicList = topicList;
        this.parentId = parentId;
        this.type = type;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Topic topic = topicList.get(position);
        View view;
        if (null != convertView) {
            view = convertView;
        } else {
            LayoutInflater buckyInflater = LayoutInflater.from(getContext());
            view = buckyInflater.inflate(R.layout.topic_layout, parent, false);
        }

        TextView name = (TextView) view.findViewById(R.id.title);
        name.setText(topic.getName());
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        if(topic.getType().equals("RECORD")){
            view.findViewById(R.id.topicItemIcon).setBackgroundResource(R.drawable.ic_new_item);
        }
        view.findViewById(R.id.layout_background).setBackgroundColor(context.getResources().getColor(R.color.appBackground));


        view.setBackgroundColor((position % 2 == 0) ?
                Color.parseColor("#FFEBF3FF") : Color.argb(0, 0, 0, 0));
        return view;

    }
}
