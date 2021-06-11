package com.example.collapsingtoolbar.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.collapsingtoolbar.Adapter.Adapter;
import com.example.collapsingtoolbar.Model.ImageModel;
import com.example.collapsingtoolbar.R;
import com.example.collapsingtoolbar.utils.Dialog;
import com.google.android.material.appbar.AppBarLayout;
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

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> runOnUiThread(()->
        {
            try {
                fetchImages();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })).start();
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
        search.clearFocus();
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

    }


    private void fetchImages() throws InterruptedException {
        Uri uri;
        Cursor cursor;
        int column_index_data, thumb;

        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Thumbnails.DATA};

        String orderBy = MediaStore.Images.Media.CD_TRACK_NUMBER;

        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        thumb = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        Task = new Thread(
                () -> {
                    while (cursor.moveToNext()) {
                        String absoluteImagePath = cursor.getString(column_index_data);
                        ImageModel ImageModel = new ImageModel();
                        ImageModel.setPath(absoluteImagePath);
                        ImageModel.setThumbnail(cursor.getString(thumb));
                        arrayList.add(ImageModel);
                    }
                }
        );
        Task.start();
        Task.join();


        Adapter Adapter = new Adapter(getApplicationContext(), arrayList, MainActivity.this);
        recyclerView.setAdapter(Adapter);
        cursor.close();
    }

}