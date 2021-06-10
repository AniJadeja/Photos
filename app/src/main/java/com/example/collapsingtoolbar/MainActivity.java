package com.example.collapsingtoolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
        runOnUiThread(()->{
            collapsingToolbar = findViewById(R.id.colap_toolbar);
            constraint = findViewById(R.id.recyclerview);
            constraint.setOnClickListener(v -> {
                search.clearFocus();
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            });
        });




        final Typeface tf = ResourcesCompat.getFont(MainActivity.this, R.font.odin);
        collapsingToolbar.setCollapsedTitleTypeface(tf);
        collapsingToolbar.setExpandedTitleTypeface(tf);


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
    }
}