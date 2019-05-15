package com.abdullah_alsaad.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.abdullah_alsaad.JavaClass.BookItem;
import com.abdullah_alsaad.JavaClass.CommentABookmark;
import com.abdullah_alsaad.OrmLiteDatabseHelper.DbPer;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.adapter.CommentAdapter;

import java.util.List;

public class CommentFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private OnFragmentInteractionListener mListener;
    private List<CommentABookmark> commentAndBookmarkList;
    private ListView commentListView;
    private ListAdapter commentAdapter;
    private BookItem currentBookItem;
    private String type;
    public CommentFragment() {
    }
    public static CommentFragment newInstance(String param1, String param2) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments() != null) {
                currentBookItem = (BookItem) getArguments().getSerializable("data");
                type = (String) getArguments().getString("type");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        commentListView = (ListView) view.findViewById(R.id.commentListView);
        getCommentsAndBookmarks();
        return view;
    }

    private void getCommentsAndBookmarks() {
        commentAndBookmarkList = DbPer.getAllCommentAndBookmark(getActivity(), type,  currentBookItem.getId() );
        if(commentAndBookmarkList != null){
            displayCommentListAdapter();
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
    private void displayCommentListAdapter() {
        if(commentAndBookmarkList.size() > 0){
            commentAdapter = new CommentAdapter(getActivity(),commentAndBookmarkList,currentBookItem, type);
            commentListView.setAdapter(commentAdapter);
            commentListView.invalidateViews();
        }
    }
}
