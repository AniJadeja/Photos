package com.example.collapsingtoolbar.utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.collapsingtoolbar.Model.MediaModel;

import java.util.ArrayList;

public class FetchMedia {

    Activity activity;
    private static ArrayList<MediaModel> arrayList;

    private static Uri uri = null;
    private static Cursor cursor;
    private static int column_index_data;
    private static String[] projection = null;
    private static String orderBy = null;
    public static int Images = 1;
    public static int Videos = 2;
    public FetchMedia(Activity activity){
        this.activity =activity;
        arrayList = new ArrayList<>();
    }


    public ArrayList<MediaModel> fetchMedia(int media) {

        if (media == Images) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Images.Media._ID};
            orderBy = MediaStore.Images.Media.DEFAULT_SORT_ORDER;
        }
        else if (media == Videos)
        {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Video.Media._ID};
            orderBy = MediaStore.Images.Media.DEFAULT_SORT_ORDER;
        }

        cursor = activity.getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);

        new Thread(() -> {
            while (cursor.moveToNext()) {
                Log.d("FetchImages(): ", " Started");
                long mediaId = cursor.getLong(column_index_data);
                Uri uriMedia = Uri.withAppendedPath(uri, "" + mediaId);
                MediaModel mediaModel = new MediaModel();
                mediaModel.setUri(uriMedia);
                arrayList.add(mediaModel);
            }
            cursor.close();
            Log.d("FetchImages(): ", " Ended");

        }).start();
        return arrayList;
    }

}
