package com.abdullah_alsaad.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.abdullah_alsaad.JavaClass.BookItem;
import com.abdullah_alsaad.JavaClass.CommentABookmark;
import com.abdullah_alsaad.OrmLiteDatabseHelper.DbPer;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.activities.FragmentsActivity;
import com.abdullah_alsaad.generic.AppUtil;
import com.abdullah_alsaad.generic.StorageHandler;
import com.abdullah_alsaad.generic.pdfium.PdfDocument;
import com.abdullah_alsaad.generic.pdfium.PdfiumCore;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PdfViewerCopy extends Fragment implements OnPageChangeListener {

    private OnFragmentInteractionListener mListener;
    private PDFView pdfView;
    private BookItem bookItem;
    private Dialog alertDialog;
    private AppCompatImageButton btnBookmarkUncheck, btnBookmark, btnComment, btnBookmarkList, btnShare;
    private HashMap<String, Object> mapData = new HashMap<>();

    private static final String TAG = "MyClassName";

    private PdfiumCore mPdfCore;

    private PdfDocument mPdfDoc = null;
    private FileInputStream mDocFileStream = null;

    private GestureDetector mSlidingDetector;
    private ScaleGestureDetector mZoomingDetector;

    private int mCurrentPageIndex = 0;
    private int mPageCount = 0;

    private SurfaceHolder mPdfSurfaceHolder;
    private boolean isSurfaceCreated = false;

    private final Rect mPageRect = new Rect();
    private final RectF mPageRectF = new RectF();
    private final Rect mScreenRect = new Rect();
    private final Matrix mTransformMatrix = new Matrix();
    private boolean isScaling = false;
    private boolean isReset = true;


    private final ExecutorService mPreLoadPageWorker = Executors.newSingleThreadExecutor();
    private final ExecutorService mRenderPageWorker = Executors.newSingleThreadExecutor();

    private Runnable mRenderRunnable;

    public PdfViewerCopy() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);


        mPdfCore = new PdfiumCore(this);

        mSlidingDetector = new GestureDetector(this, new SlidingDetector());
        mZoomingDetector = new ScaleGestureDetector(this, new ZoomingDetector());

        Intent intent = getIntent();
        Uri fileUri;
        if( (fileUri = intent.getData()) == null){
            finish();
            return ;
        }

        mRenderRunnable = new Runnable() {
            @Override
            public void run() {
                loadPageIfNeed(mCurrentPageIndex);

                resetPageFit(mCurrentPageIndex);
                mPdfCore.renderPage(mPdfDoc, mPdfSurfaceHolder.getSurface(), mCurrentPageIndex,
                        mPageRect.left, mPageRect.top,
                        mPageRect.width(), mPageRect.height());

                mPreLoadPageWorker.submit(new Runnable() {
                    @Override
                    public void run() {
                        loadPageIfNeed(mCurrentPageIndex + 1);
                        loadPageIfNeed(mCurrentPageIndex + 2);
                    }
                });
            }
        };

        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.view_surface_main);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                isSurfaceCreated = true;
                updateSurface(holder);
                if (mPdfDoc != null) {
                    mRenderPageWorker.submit(mRenderRunnable);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.w(TAG, "Surface Changed");
                updateSurface(holder);
                if(mPdfDoc != null){
                    mRenderPageWorker.submit(mRenderRunnable);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                isSurfaceCreated = false;
                Log.w(TAG, "Surface Destroy");
            }
        });

        try{
            mDocFileStream = new FileInputStream(fileUri.getPath());

            mPdfDoc = mPdfCore.newDocument(mDocFileStream.getFD());
            Log.d("Main", "Open Document");

            mPageCount = mPdfCore.getPageCount(mPdfDoc);
            Log.d(TAG, "Page Count: " + mPageCount);

        }catch(IOException e){
            e.printStackTrace();
            Log.e("Main", "Data uri: " + fileUri.toString());
        }

        return view;
    }


    public static PdfViewerCopy newInstance(String param1, String param2) {
        PdfViewerCopy fragment = new PdfViewerCopy();
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


    private class SlidingDetector extends GestureDetector.SimpleOnGestureListener {

        private boolean checkFlippable(){
            return ( mPageRect.left >= mScreenRect.left &&
                    mPageRect.right <= mScreenRect.right );
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
            if(!isSurfaceCreated) return false;
            Log.d(TAG, "Drag");

            distanceX *= -1f;
            distanceY *= -1f;

            if( (mPageRect.left <= mScreenRect.left && mPageRect.right <= mScreenRect.right && distanceX < 0) ||
                    (mPageRect.right >= mScreenRect.right && mPageRect.left >= mScreenRect.left && distanceX > 0) )
                distanceX = 0f;
            if( (mPageRect.top <= mScreenRect.top && mPageRect.bottom <= mScreenRect.bottom && distanceY < 0) ||
                    (mPageRect.bottom >= mScreenRect.bottom && mPageRect.top >= mScreenRect.top && distanceY > 0) )
                distanceY = 0f;

            //Portrait restriction
            if(isReset && mScreenRect.width() < mScreenRect.height()) distanceX = distanceY = 0f;
            if(isReset && mScreenRect.height() <= mScreenRect.width()) distanceX = 0f;

            if(distanceX == 0f && distanceY == 0f) return false;

            Log.d(TAG, "DistanceX: " + distanceX);
            Log.d(TAG, "DistanceY: " + distanceY);
            mPageRect.left += distanceX;
            mPageRect.right += distanceX;
            mPageRect.top += distanceY;
            mPageRect.bottom += distanceY;

            mPdfCore.renderPage(mPdfDoc, mPdfSurfaceHolder.getSurface(), mCurrentPageIndex,
                    mPageRect.left, mPageRect.top,
                    mPageRect.width(), mPageRect.height());

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            if(!isSurfaceCreated) return false;
            if(velocityX == 0f) return false;

            if(!checkFlippable()){
                Log.d(TAG, "Not flippable");
                return false;
            }

            if(velocityX < -200f){ //Forward
                if(mCurrentPageIndex < mPageCount - 1){
                    Log.d(TAG, "Flip forward");
                    mCurrentPageIndex++;
                    Log.d(TAG, "Next Index: " + mCurrentPageIndex);

                    mRenderPageWorker.submit(mRenderRunnable);
                }
                return true;
            }

            if(velocityX > 200f){ //Backward
                Log.d(TAG, "Flip backward");
                if(mCurrentPageIndex > 0){
                    mCurrentPageIndex--;
                    Log.d(TAG, "Next Index: " + mCurrentPageIndex);

                    mRenderPageWorker.submit(mRenderRunnable);
                }
                return true;
            }

            return false;
        }
    }
    private class ZoomingDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float mAccumulateScale = 1f;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector){
            isScaling = true;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector){
            if(!isSurfaceCreated) return false;


            mAccumulateScale *= detector.getScaleFactor();
            mAccumulateScale = Math.max(1f, mAccumulateScale);
            float scaleValue = (mAccumulateScale > 1f)? detector.getScaleFactor() : 1f;
            mTransformMatrix.setScale(scaleValue, scaleValue,
                    detector.getFocusX(), detector.getFocusY());
            mPageRectF.set(mPageRect);

            mTransformMatrix.mapRect(mPageRectF);

            rectF2Rect(mPageRectF, mPageRect);

            mPdfCore.renderPage(mPdfDoc, mPdfSurfaceHolder.getSurface(), mCurrentPageIndex,
                    mPageRect.left, mPageRect.top,
                    mPageRect.width(), mPageRect.height());

            isReset = false;

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector){
            if(mAccumulateScale == 1f && !mScreenRect.contains(mPageRect)){
                resetPageFit(mCurrentPageIndex);

                mPdfCore.renderPage(mPdfDoc, mPdfSurfaceHolder.getSurface(), mCurrentPageIndex,
                        mPageRect.left, mPageRect.top,
                        mPageRect.width(), mPageRect.height());
            }

            isScaling = false;
        }
    }

    @Override
    public void onDestroy(){
        try{
            if(mPdfDoc != null && mDocFileStream != null){
                mPdfCore.closeDocument(mPdfDoc);
                Log.d("Main", "Close Document");

                mDocFileStream.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            super.onDestroy();
        }
    }

}
