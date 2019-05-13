package com.abdullah_alsaad.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abdullah_alsaad.R;
import com.abdullah_alsaad.activities.FragmentsActivity;


public class TopicItem extends Fragment {
    private Toolbar toolbar;
    private OnFragmentInteractionListener mListener;
    private Notification.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private Button playBtn;
    SeekBar positionBar;
    TextView elapsedTimeLabel, remainingTimeLabel;
    private MediaPlayer mp;
    private int totalTime;
    private RemoteViews remoteViews;
    public TopicItem() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecture_item, container, false);

        toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);
        ImageButton addBookBtn = (ImageButton) toolbar.findViewById(R.id.addBookBtn);
        ImageButton addNewItemRecord = (ImageButton) toolbar.findViewById(R.id.addNewItemRecord);
        addBookBtn.setVisibility(View.GONE);
        addNewItemRecord.setVisibility(View.GONE);
        playBtn = (Button) view.findViewById(R.id.playBtn);
        elapsedTimeLabel = (TextView) view.findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = (TextView) view.findViewById(R.id.remainingTimeLabel);

        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getActivity().getPackageName(),R.layout.custom_mp3_notification);

        remoteViews.setImageViewResource(R.id.app_icon,R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.title, "test");
        notification_id = (int) System.currentTimeMillis();
        Intent button_intent = new Intent("button_clicked");
        button_intent.putExtra("id",notification_id);
        PendingIntent p_button_intent = PendingIntent.getBroadcast(getActivity(),123,button_intent,0);
        remoteViews.setOnClickPendingIntent(R.id.button, p_button_intent);


        view.findViewById(R.id.button_back_five_seconds).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notification_intent = new Intent (getActivity(), FragmentsActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),0, notification_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
                builder = new Notification.Builder(getActivity());
                builder.setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentText("tttttt")
                        .setContentIntent(pendingIntent);
                notificationManager.notify(123, builder.build());
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playBtnClick();
            }
        });
        mp = MediaPlayer.create(getActivity(),R.raw.fat_12_027);
//        mp.setLooping(true);
        mp.seekTo(0);
//        mp.setVolume(0.5f,0.5f);
        totalTime = mp.getDuration();

        positionBar = (SeekBar) view.findViewById(R.id.positionBar);
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mp.seekTo(progress);
                positionBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp!=null){
                    try{
                        Message msg =  new Message();
                        msg.what = mp.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    }catch (InterruptedException e){

                    }
                }
            }
        }).start();


        return view;
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
           int currentPossition = msg.what;
           positionBar.setProgress(currentPossition);
           String elapseTime = createTimeLabel(currentPossition);
           elapsedTimeLabel.setText(elapseTime);
           String remainingTime = createTimeLabel(totalTime-currentPossition);
           remainingTimeLabel.setText(remainingTime);
           return true;
        }
    });

    public String createTimeLabel(int time){
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000  % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }

    public void playBtnClick(){
        if(!mp.isPlaying()){
            mp.start();
            playBtn.setBackgroundResource(R.drawable.ic_pause_button);
        }else {
            mp.pause();
            playBtn.setBackgroundResource(R.drawable.ic_play_button);
        }
    }


    public static TopicItem newInstance(String param1, String param2) {
        TopicItem fragment = new TopicItem();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
