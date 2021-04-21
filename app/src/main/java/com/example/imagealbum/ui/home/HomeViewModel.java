package com.example.imagealbum.ui.home;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.imagealbum.image;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    String[] projection = {MediaStore.MediaColumns.DATA};

    private MutableLiveData<ArrayList<image>> imageLiveData;
    private ArrayList<image> images;

    public HomeViewModel() {
        imageLiveData = new MutableLiveData<>();
        images = new ArrayList<>();
    }

    public MutableLiveData<ArrayList<image>> getImageMutableLiveData() {
        return imageLiveData;
    }

    private void init() {

    }

    public void loadImageFromDevice(Context context) {
        images.clear();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            image imageFile = new image(Uri.parse(absolutePathOfImage));
            images.add(imageFile);
        }
        cursor.close();

        //update live data
        imageLiveData.setValue(images);
    }
}