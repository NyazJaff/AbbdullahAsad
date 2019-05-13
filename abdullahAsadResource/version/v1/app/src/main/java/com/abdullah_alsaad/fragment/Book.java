package com.abdullah_alsaad.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.abdullah_alsaad.JavaClass.BookItem;
import com.abdullah_alsaad.OrmLiteDatabseHelper.DbPer;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.adapter.PdfGridAdapter;
import com.abdullah_alsaad.generic.AppUtil;
import com.abdullah_alsaad.generic.BackgroundAsync;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Book extends Fragment {
    public Book() {
    }

    private Context context;
    private ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<BookItem> bookItemsList;
    private View view;
    private ProgressDialog nDialog;
    private GridView gridView;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_books, container, false);
        context = getActivity();
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        bookItemsList = new ArrayList<>();
        gridView = (GridView) view.findViewById(R.id.gridView);
        fragmentManager = getActivity().getSupportFragmentManager();
        AppUtil.backupDatabase(context, "abdullah-alsaad.sqlite");
        permission_check();
        return view;
    }

    private void displayBooks() {
        nDialog = new ProgressDialog(getActivity());
        nDialog.setMessage(getString(R.string.inProgress));
        nDialog.setTitle(R.string.loadingSettings);
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();

        Integer lastInsertedValue = DbPer.getBookItemLastItemInserted(getActivity());
        if (lastInsertedValue != null) {
            bookItemsList = DbPer.getAllBookItems(getActivity());
            if (bookItemsList != null) {
                displayGridAdapter();
            }
            downloadMissingBooks(lastInsertedValue);
        } else {
            downloadBookFromFireStore();
        }
    }

    private void permission_check() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                return;
            }
        }

        initialize();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialize();
                } else {
                    permission_check();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void initialize() {
        displayBooks();
    }

    private void downloadMissingBooks(Integer lastInsertedBookId) {
        db.collection("BookItem").whereGreaterThan("id", lastInsertedBookId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.VISIBLE);
                            for (DocumentSnapshot document : task.getResult()) {
                                BookItem bookItem = document.toObject(BookItem.class);
                                bookItem.setStringId(document.getId());
                                bookItemsList.add(bookItem);
                                DbPer.saveBookItemToLocalDatabase(context, bookItem);
                                BackgroundAsync backgroundAsync = new BackgroundAsync(bookItem.getId(), "png");
                                backgroundAsync.execute(bookItem.getImageURL());
                            }
                            displayGridAdapter();
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            Log.i("downloadBook func", "Error from downloadBookFromFireStore function");
                        }
                    }
                });
    }

    private void downloadBookFromFireStore() {
        db.collection("BookItem").orderBy("id")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                BookItem bookItem = document.toObject(BookItem.class);
                                bookItem.setStringId(document.getId());
                                bookItemsList.add(bookItem);
                                BackgroundAsync backgroundAsync = new BackgroundAsync(bookItem.getId(), "png");
                                backgroundAsync.execute(bookItem.getImageURL());
                                DbPer.saveBookItemToLocalDatabase(context, bookItem);
                            }
                            displayGridAdapter();
                        } else {
                            Log.i("downloadBook func", "Error from downloadBookFromFireStore function");
                        }
                    }
                });
    }

    private void displayGridAdapter() {
        if (bookItemsList.size() > 0) {
            PdfGridAdapter adapter = new PdfGridAdapter(getActivity(), bookItemsList, fragmentManager, progressBar, Book.this);
            gridView.setAdapter(adapter);
        }
        nDialog.dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
