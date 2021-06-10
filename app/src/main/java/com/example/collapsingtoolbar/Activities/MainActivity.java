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
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(this::init).start();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    void init() {
        search = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new GridLayoutManager(MainActivity.this,4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        final Typeface tf = ResourcesCompat.getFont(MainActivity.this, R.font.odin);
        collapsingToolbar = findViewById(R.id.colap_toolbar);
        constraint = findViewById(R.id.constraint);
        collapsingToolbar.setCollapsedTitleTypeface(tf);
        collapsingToolbar.setExpandedTitleTypeface(tf);
        arrayList = new ArrayList<>();
        search.clearFocus();
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        getPermission();
    }

    private void getPermission() {
        new Thread(()-> runOnUiThread(() ->{
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 101);
            }
            else {
                new Thread(() -> runOnUiThread(this::fetchImages)).start();

            }
        })).start();

    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new Handler(Looper.myLooper()).post(this::fetchImages);
            }
            else {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchImages(){
        Uri uri;
        Cursor cursor;
        int column_index_data,thumb;

        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Thumbnails.DATA};

        String orderBy = MediaStore.Images.Media.CD_TRACK_NUMBER;

        cursor = getApplicationContext().getContentResolver().query(uri,projection,null,null,orderBy+" DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        thumb = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext())
        {
            String absoluteImagePath = cursor.getString(column_index_data);
            ImageModel ImageModel = new ImageModel();
            ImageModel.setPath(absoluteImagePath);
            ImageModel.setThumbnail(cursor.getString(thumb));
            arrayList.add(ImageModel);
        }

        Adapter Adapter = new Adapter(getApplicationContext(),arrayList,MainActivity.this);
        recyclerView.setAdapter(Adapter);
        cursor.close();
    }
}