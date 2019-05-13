package com.abdullah_alsaad.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.abdullah_alsaad.JavaClass.BookItem;
import com.abdullah_alsaad.JavaClass.CommentABookmark;
import com.abdullah_alsaad.OrmLiteDatabseHelper.DbPer;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.activities.FragmentsActivity;
import com.abdullah_alsaad.fragment.ParentCommentAndBookmark;

import java.util.HashMap;
import java.util.List;

public class CommentAdapter extends ArrayAdapter<CommentABookmark> {

    Context context;
    private List<CommentABookmark> commentList;
    private BookItem bookItem;
    private String type;
    public CommentAdapter(Context context, List<CommentABookmark> commentList, BookItem bookItem, String type) {
        super(context, R.layout.view_comment_layout, commentList);
        this.context = context;
        this.commentList = commentList;
        this.bookItem = bookItem;
        this.type = type;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        final CommentABookmark currentComment = commentList.get(position);
        View view;
        if (null != convertView) {
            view = convertView;
        } else {
            LayoutInflater buckyInflater = LayoutInflater.from(getContext());
            view = buckyInflater.inflate(R.layout.view_comment_layout, parent, false);
        }

        TextView bookDetailsTxt = (TextView) view.findViewById(R.id.bookDetailsTxt);
        TextView commentTxt = (TextView) view.findViewById(R.id.commentTxt);
        ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);

        if(type.equals(ParentCommentAndBookmark.COMMENT)){
            commentTxt.setText(currentComment.getComment());
        }else{
            commentTxt.setText(String.format(context.getString(R.string.page),currentComment.getPageNumber()));
            btnDelete.setBackgroundResource(R.drawable.ic_bookmark);
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentList.remove(position);
                DbPer.deleteCommentBookmark(context, currentComment.getId());
                notifyDataSetChanged();
            }
        });

        bookDetailsTxt.setText(String.format(context.getString(R.string.bookItemSmallPrint),bookItem.getName(),currentComment.getPageNumber()));
        bookDetailsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPdfViewFragment(currentComment);
            }
        });
        commentTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPdfViewFragment(currentComment);
            }
        });

        return view;
    }

    private void openPdfViewFragment(CommentABookmark currentComment ) {
        HashMap<String, Object> mapData = new HashMap<>();
        mapData.put("pageToOpen",currentComment.getPageNumber());
        FragmentsActivity fragmentsActivity = (FragmentsActivity) (Activity) context;
        fragmentsActivity.callFragment("PdfViewer", bookItem,mapData);
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private CommentABookmark currentComment;
        public MyAsyncTask (CommentABookmark currentComment){
            this.currentComment = currentComment;
        }
        @Override
        protected Void doInBackground(Void... params) {
            DbPer.deleteCommentBookmark(context, currentComment.getId());

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            notifyDataSetChanged();
        }
    }

}
