package com.example.collapsingtoolbar.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.collapsingtoolbar.Adapter.Adapter;
import com.example.collapsingtoolbar.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class MainActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView constraint;
    EditText search;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Adapter adapter;
    int[] arr;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runOnUiThread(this::init);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new Thread(()->constraint.setOnClickListener(v -> {
            search.clearFocus();
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }));

    }

    void init() {
        search = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(arr);
        /*recyclerView.setAdapter(adapter);*/
        recyclerView.setHasFixedSize(true);
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        final Typeface tf = ResourcesCompat.getFont(MainActivity.this, R.font.odin);
        collapsingToolbar = findViewById(R.id.colap_toolbar);
        constraint = findViewById(R.id.recyclerview);
        collapsingToolbar.setCollapsedTitleTypeface(tf);
        collapsingToolbar.setExpandedTitleTypeface(tf);
    }
}