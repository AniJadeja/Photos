package com.example.collapsingtoolbar.Activities;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collapsingtoolbar.Adapter.Adapter;
import com.example.collapsingtoolbar.Model.ImageModel;
import com.example.collapsingtoolbar.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbar;
    ConstraintLayout constraint;
    EditText search;
    InputMethodManager imm;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ImageModel> arrayList;
    ScrollView scrollView;
    Thread Task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Task = new Thread(this::init);
        Task.start();
        try {
            Task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Task = new Thread(this::startExe);
        Task.start();
        try {
            Task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clearSearch()
    {
        if (search.hasFocus()){
        search.clearFocus();
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);}
    }

    @Override
    protected void onResume() {
        super.onResume();

        Task = new Thread(this::fetchImages);
        Task.start();
        try {
            Task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    void init() {
        search = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new GridLayoutManager(MainActivity.this, 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        collapsingToolbar = findViewById(R.id.colap_toolbar);
        constraint = findViewById(R.id.constraint);
    }

    void startExe() {
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        final Typeface tf = ResourcesCompat.getFont(MainActivity.this, R.font.odin);
        collapsingToolbar.setCollapsedTitleTypeface(tf);
        collapsingToolbar.setExpandedTitleTypeface(tf);
        arrayList = new ArrayList<>();
        scrollView = findViewById(R.id.scrollView);
        recyclerView.setOnClickListener(v -> clearSearch());
    }


    private void fetchImages() {
        Uri uri;
        Cursor cursor;
        int column_index_data;




        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media._ID
        };

        String orderBy = MediaStore.Images.Media.DEFAULT_SORT_ORDER;

        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        new Thread(() -> {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String absoluteImagePath = cursor.getString(column_index_data);
                ImageModel ImageModel = new ImageModel();
                ImageModel.setPath(absoluteImagePath);
                arrayList.add(ImageModel);
            }

            runOnUiThread(() -> {
                Adapter Adapter = new Adapter(getApplicationContext(), arrayList, MainActivity.this);
                recyclerView.setAdapter(Adapter);
                cursor.close();
            });
        }
        ).start();
    }



}