package com.example.collapsingtoolbar.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.collapsingtoolbar.Adapter.Adapter;
import com.example.collapsingtoolbar.Model.MediaModel;
import com.example.collapsingtoolbar.R;
import com.example.collapsingtoolbar.utils.FetchMedia;

import java.util.ArrayList;


public class AllVideosFragment extends Fragment {



    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<MediaModel> arrayList;
    FetchMedia fetchMedia;
    Thread Task;

    public AllVideosFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        Task = new Thread(this::init);
        Task.start();
        try {
            Task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Task = new Thread(this::fetchImages);
        Task.start();
        try {
            Task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_photos, container, false);
    }

    void init() {
        recyclerView = getView().findViewById(R.id.recyclerview);
        layoutManager = new GridLayoutManager(getActivity(), 4);
        arrayList = new ArrayList<>();
        fetchMedia = new FetchMedia(getActivity());
    }




    private void fetchImages() {
        arrayList = fetchMedia.fetchMedia(FetchMedia.Videos);
        new Thread(() -> {
            getActivity().runOnUiThread(() -> {
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                Adapter Adapter = new Adapter(getActivity().getApplicationContext(), arrayList, getActivity());
                recyclerView.setAdapter(Adapter);
                Log.d("FetchImages(): ", " RecyclerView Adapter attached");
            });
        }
        ).start();
    }


}