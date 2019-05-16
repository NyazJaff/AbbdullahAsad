package com.abdullah_alsaad.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.abdullah_alsaad.JavaClass.BookItem;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.fragment.Book;
import com.abdullah_alsaad.fragment.ParentCommentAndBookmark;
import com.abdullah_alsaad.fragment.PdfViewer;
import com.abdullah_alsaad.fragment.TopicFragment;
import com.abdullah_alsaad.generic.AppUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FragmentsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar toolbar;
    private ImageButton btnHomeActivity, addBookBtn;
    private AlertDialog dialogAddBook;
    private FirebaseFirestore db;
    private TextView title;
    private FirebaseAuth mAuth;
    private HashMap<String, Object> mapData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        context = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        title = (TextView) toolbar.findViewById(R.id.titleTxt);
        toolbar.findViewById(R.id.addNewItemRecord).setVisibility(View.GONE);

        btnHomeActivity = (ImageButton) toolbar.findViewById(R.id.homeBtn);
        addBookBtn = (ImageButton) toolbar.findViewById(R.id.addBookBtn);
        btnHomeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callHomeActivity();
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            addBookBtn.setVisibility(View.VISIBLE);
            addBookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addBook();
                }
            });
        }
        mAuth = FirebaseAuth.getInstance();
        if( mAuth.getCurrentUser() == null ){
            toolbar.findViewById(R.id.addBookBtn).setVisibility(View.GONE);

        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        db = FirebaseFirestore.getInstance();
        mToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.naviagtion_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        String fragmentName = intent.getStringExtra("fragment");


        mapData = new HashMap<>();
        mapData.put("type", "PARENT");
        mapData.put("parentId", new Long(0));
        if (fragmentName.equals("lecture")) {
            mapData.put("firebaseTableName", "lecture");
            callFragment(fragmentName, null, mapData);
        } else if (fragmentName.equals("speech")) {
            mapData.put("firebaseTableName", "speech");
            callFragment(fragmentName, null, mapData);
        } else if (fragmentName.equals("questionAndAnswer")) {
            mapData.put("firebaseTableName", "questionAndAnswer");
            callFragment(fragmentName, null, mapData);
        }else {
            callFragment(fragmentName);
        }
    }

    private void callHomeActivity() {
        Intent myIntent = new Intent(FragmentsActivity.this, MainActivity.class);
        FragmentsActivity.this.startActivity(myIntent);
    }

    public void callActivity(String activity, HashMap<String, Object> mapData) {
        Intent myIntent = null;

        switch (activity) {
            case "MainActivity":
                myIntent = new Intent(FragmentsActivity.this, MainActivity.class);
                break;
            case "TopicItemActivity":
                myIntent = new Intent(FragmentsActivity.this, TopicItemActivity.class);
                break;
        }
        if (myIntent != null) {
            if(mapData != null){
                myIntent.putExtra("mapData", mapData);
            }
            startActivity(myIntent);
        }
    }

    private void addBook() {
        if (AppUtil.checkNetwork(context) != true) {
            AppUtil.showToast(context, R.string.noConnectionFound);
            return;
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View addBook = getLayoutInflater().inflate(R.layout.add_book_layout, null);

        final EditText name = (EditText) addBook.findViewById(R.id.title);
        final EditText pdfURL = (EditText) addBook.findViewById(R.id.password);
        final EditText imageURL = (EditText) addBook.findViewById(R.id.imageURL);
        Button save = (Button) addBook.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs(name, pdfURL, imageURL) != false) {

                    final BookItem[] bookItem = new BookItem[1];
                    db.collection(AppUtil.getDevMode("BookItem")).orderBy("id", Query.Direction.DESCENDING).limit(1)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            bookItem[0] = document.toObject(BookItem.class);
                                        }
                                        saveToFireStore(name.getText().toString(), pdfURL.getText().toString(), imageURL.getText().toString(), bookItem[0] != null ? bookItem[0].getId() : 0);
                                    } else {
                                        Log.i("downloadBook func", "Error from downloadBookFromFireStore function");

                                    }
                                }
                            });
                }
            }
        });

        alertDialog.setView(addBook);
        dialogAddBook = alertDialog.create();
        dialogAddBook.show();
    }

    private boolean validateInputs(EditText name, EditText pdfURL, EditText imageURL) {
        View focusView = null;
        boolean inputComplete = true;
        String nameString = name.getText().toString();
        String pdfURLString = pdfURL.getText().toString();
        String imageURLString = imageURL.getText().toString();

        if (imageURLString.isEmpty()) {
            imageURL.setError(getString(R.string.error_imageURL_required));
            focusView = imageURL;
            inputComplete = false;
        }
        if (pdfURLString.isEmpty()) {
            pdfURL.setError(getString(R.string.error_pdfURL_required));
            focusView = pdfURL;
            inputComplete = false;
        }
        if (nameString.isEmpty()) {
            name.setError(getString(R.string.error_name_book_required));
            focusView = name;
            inputComplete = false;
        }
        if (focusView != null) {
            focusView.requestFocus();
        }

        return inputComplete;
    }

    private void saveToFireStore(String name, String pdfURL, String imageURL, long id) {
        final Date date = Calendar.getInstance().getTime();
        Map<String, Object> bookMap = new HashMap<>();
        bookMap.put("createdTime", date);
        bookMap.put("name", name);
        bookMap.put("pdfURL", pdfURL);
        bookMap.put("imageURL", imageURL);
        bookMap.put("id", id + 1);

        db.collection(AppUtil.getDevMode("BookItem"))
                .add(bookMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.book_created), Toast.LENGTH_SHORT).show();
                        dialogAddBook.dismiss();
//                        questionBodyTxt.setText("");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    @Override
    public void onBackPressed() {
//        boolean b = getSupportFragmentManager().findFragmentById(R.id.fragment_place).isVisible();
        int b = getSupportFragmentManager().getBackStackEntryCount();
        Log.i("getBackStackEntryCount", b + "");
        if (b > 1) {
            super.onBackPressed();
        } else {
            callHomeActivity();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent myIntent = null;
        boolean activityCall = false;
        switch (item.getItemId()) {
            case R.id.book:
                callFragment("Book");
                break;
            case R.id.lecture:
                mapData.put("firebaseTableName", "lecture");
                callFragment("lecture", null, mapData);
                break;
            case R.id.speech:
                mapData.put("firebaseTableName", "speech");
                callFragment("speech", null, mapData);
                break;
            case R.id.questionAndAnswer:
                mapData.put("firebaseTableName", "questionAndAnswer");
                callFragment("questionAndAnswer", null, mapData);
                break;
            case R.id.live:
                myIntent = new Intent(FragmentsActivity.this, LiveStream.class);
                myIntent.putExtra("fragment", "btnLive");
                activityCall = true;
                break;
            case R.id.about:
                myIntent = new Intent(FragmentsActivity.this, AboutShikh.class);
                myIntent.putExtra("fragment", "btnProfile");
                activityCall = true;
                break;
            default:
                return false;
        }
        mDrawerLayout.closeDrawers();
        if(activityCall){
            FragmentsActivity.this.startActivity(myIntent);
        }
        return true;
    }

    public void callFragment(String fragmentName) {
        callFragment(fragmentName, null);
    }

    public void callFragment(String fragmentName, Object data) {
        callFragment(fragmentName, data, null);
    }

    public void callFragment(String fragmentName, Object data, HashMap<String, Object> mapData) {
        FrameLayout fl = (FrameLayout) findViewById(R.id.main_activity_frame);
        fl.removeAllViews();
        Fragment myFragment = null;
        Class fragmentClass = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle arguments = new Bundle();
        switch (fragmentName) {
            case "PdfViewer":
                fragmentClass = PdfViewer.class;
                break;
            case "Book":
                fragmentClass = Book.class;
                title.setText(R.string.books);
                break;
            case "ParentCommentAndBookmark":
                fragmentClass = ParentCommentAndBookmark.class;
                break;
            case "lecture":
                fragmentClass = TopicFragment.class;
                break;
            case "speech":
                fragmentClass = TopicFragment.class;
                break;
            case "questionAndAnswer":
                fragmentClass = TopicFragment.class;
                break;
        }

        if (fragmentClass != null) {
            try {
                myFragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (mapData != null) {
                arguments.putSerializable("mapData", (Serializable) mapData);
            }
            if (data != null) {
                arguments.putSerializable("data", (Serializable) data);
                myFragment.setArguments(arguments);
                arguments.putSerializable("mapData", (Serializable) mapData);
                fragmentManager.beginTransaction().replace(R.id.main_activity_frame,
                        myFragment)
                        .addToBackStack(fragmentName)
                        .commit();
            }
//            else if(fragmentName.equals("lecture")) {
//                myFragment.setArguments(arguments);
//                fragmentManager.beginTransaction().replace(R.id.main_activity_frame,
//                        myFragment)
//                        .commit();
//            }
            else {
                myFragment.setArguments(arguments);
                fragmentManager.beginTransaction().replace(R.id.main_activity_frame,
                        myFragment)
                        .addToBackStack(fragmentName)
                        .commit();
            }
        }
    }
}
