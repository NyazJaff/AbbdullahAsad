package com.abdullah_alsaad.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.abdullah_alsaad.JavaClass.BookItem;
import com.abdullah_alsaad.JavaClass.CommentABookmark;
import com.abdullah_alsaad.OrmLiteDatabseHelper.DbPer;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.activities.FragmentsActivity;
import com.abdullah_alsaad.generic.AppUtil;
import com.abdullah_alsaad.generic.StorageHandler;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PdfViewer extends Fragment implements OnPageChangeListener {

    private OnFragmentInteractionListener mListener;
    private PDFView pdfView;
    private BookItem bookItem;
    private Dialog alertDialog;
    private AppCompatImageButton btnBookmarkUncheck, btnBookmark, btnComment, btnBookmarkList, btnShare;
    private HashMap<String, Object> mapData = new HashMap<>();

    public PdfViewer() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);

        if (bookItem != null) {
            if (StorageHandler.pdfExists(bookItem.getId())) {
                File file = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "abdullahAlSaad/pdf/" + bookItem.getId() + ".pdf");
                pdfView = (PDFView) view.findViewById(R.id.pdfView);
                if (mapData != null) {
                    pdfView.fromFile(file)
                            .onPageChange(this)
                            .defaultPage((Integer) mapData.get("pageToOpen"))
                            .load();
                } else {
                    pdfView.fromFile(file)
                            .onPageChange(this)
                            .defaultPage(bookItem.getLastVisitPage())
                            .load();
                }

            }
            if (bookItem.getPdfDownloadPath() == null || bookItem.getPdfDownloadPath().equals("")) {
                bookItem.setPdfDownloadPath(Environment.getExternalStorageDirectory() +
                        File.separator + "abdullahAlSaad/pdf/" + bookItem.getId() + ".pdf");
                DbPer.saveBookItemToLocalDatabase(getActivity(), bookItem);
            }

            btnComment = (AppCompatImageButton) view.findViewById(R.id.btnComment);
            btnShare = (AppCompatImageButton) view.findViewById(R.id.btnShare);
            btnBookmark = (AppCompatImageButton) view.findViewById(R.id.btnBookmark);
//            btnBookmarkUncheck = (AppCompatImageButton) view.findViewById(R.id.btnBookmarkUncheck);
            btnBookmarkList = (AppCompatImageButton) view.findViewById(R.id.btnBookmarkList);

            btnBookmarkList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewCommentsAndBookmark();
                }
            });

            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sharePdf();
                }
            });

            btnBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveDeleteBookmark();
                }
            });

            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayAddCommentLayout();
                }
            });
//            if(getCurrentPageIfBookmarked() == null){
//                btnBookmarkUncheck.setVisibility(View.GONE);
//
//            }else{
//                btnBookmark.setVisibility(View.GONE);
//            }
        }
        return view;
    }

    private void sharePdf() {

        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "abdullahAlSaad/pdf/" + bookItem.getId() + ".pdf");

        Uri pictureUri = FileProvider.getUriForFile(
                getActivity(),
                getActivity().getApplicationContext()
                        .getPackageName() + ".provider", file);
//        Uri pictureUri = Uri.fromFile(file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
        shareIntent.setType("*/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.shareWithOthers)));
    }

    private void saveDeleteBookmark() {
        CommentABookmark commentABookmark = getCurrentPageIfBookmarked();
        if(null != pdfView) {
            if (commentABookmark == null) {
                CommentABookmark bookmark = new CommentABookmark(pdfView.getCurrentPage(), bookItem);
                if (DbPer.saveCommentABookmarkToLocalDatabase(getActivity(), bookmark) == true) {
                    AppUtil.showToast(getActivity(), R.string.bookmark_saved);
                    btnBookmark.setImageResource(R.drawable.ic_bookmark);
                    ///TODO change bookmark icon
                }
            } else {
                DbPer.deleteCommentBookmark(getActivity(), commentABookmark.getId());
                ///TODO change bookmark icon and delete toast
                AppUtil.showToast(getActivity(), R.string.bookmark_deleted);
                btnBookmark.setImageResource(R.drawable.ic_bookmark_uncheck);
            }
        }
    }

    public static PdfViewer newInstance(String param1, String param2) {
        PdfViewer fragment = new PdfViewer();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void saveLastVisitPage() {
        bookItem.setLastVisitPage(pdfView.getCurrentPage());
        DbPer.saveBookItemToLocalDatabase(getActivity(), bookItem);
    }

    private CommentABookmark getCurrentPageIfBookmarked() {
        if (bookItem.getCommentABookmarks() != null) {
            for (CommentABookmark commentABookmark : bookItem.getCommentABookmarks()) {
                if (commentABookmark.getPageNumber() == pdfView.getCurrentPage() && commentABookmark.getType().equals(CommentABookmark.BOOKMARK)) {
                    return commentABookmark;
                }
            }
        }
        return null;
    }

    private void saveComment(String commentText) {
        CommentABookmark comment = new CommentABookmark(commentText, pdfView.getCurrentPage(), bookItem);
        if (DbPer.saveCommentABookmarkToLocalDatabase(getActivity(), comment) == true) {
            AppUtil.showToast(getActivity(), R.string.comment_saved);
        }
        alertDialog.dismiss();
    }

    private void viewCommentsAndBookmark() {
        FragmentsActivity fragmentsActivity = (FragmentsActivity) (Activity) getActivity();
        fragmentsActivity.callFragment("ParentCommentAndBookmark", bookItem);

// View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_parent_comment_and_bookmark, null);
////        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
////        ViewPager viewPager = (ViewPager) popupView.findViewById(R.id.pager);
////
//////        TabLayout tabs = (TabLayout) popupView.findViewById(R.id.result_tabs);
//////        tabs.setupWithViewPager(viewPager);
////        popupWindow.showAsDropDown(popupView, 0, 0);
//

//        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View commentAndBookmarkView = getLayoutInflater().inflate(R.layout.fragment_parent_comment_and_bookmark, null);
//        alertDialog = new Dialog((Activity) getActivity());
//        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
////        BottomNavigationViewEx bnve = (BottomNavigationViewEx) commentAndBookmarkView.findViewById(R.id.bnve);
////        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
////            @Override
////            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
////                AppUtil.showToast(getActivity(), (String) item.getTitle());
////                return true;
////            }
////        });
//
//        alertDialog.setContentView(commentAndBookmarkView);
//        alertDialog.show();


    }

    private void displayAddCommentLayout() {
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View commentView = getLayoutInflater().inflate(R.layout.write_comment, null);
        final Button save, cancel;
        final EditText comment;
        alertDialog = new Dialog((Activity) getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        comment = (EditText) commentView.findViewById(R.id.comment);
        save = (Button) commentView.findViewById(R.id.save);
        cancel = (Button) commentView.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!comment.getText().toString().isEmpty()) {
                    saveComment(comment.getText().toString());
                }
            }
        });
        alertDialog.setContentView(commentView);
        alertDialog.show();
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
        saveLastVisitPage();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveLastVisitPage();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (getCurrentPageIfBookmarked() == null) {
            btnBookmark.setImageResource(R.drawable.ic_bookmark_uncheck);
        } else {
            btnBookmark.setImageResource(R.drawable.ic_bookmark);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveLastVisitPage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookItem = (BookItem) getArguments().getSerializable("data");
            mapData = (HashMap<String, Object>) getArguments().getSerializable("mapData");
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
