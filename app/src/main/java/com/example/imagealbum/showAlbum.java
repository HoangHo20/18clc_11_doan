package com.example.imagealbum;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class showAlbum extends AppCompatActivity {
    ArrayList<image> imageList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    private static int SEND_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_album);
        Intent intent = getIntent();


        recyclerView = findViewById(R.id.imagegallery);

        adapter = new RecyclerAdapter(imageList, showAlbum.this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_showalbum, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.actionBar_showAlbum_addBtn:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, 2);
                break;
            case R.id.actionBar_showAlbum_deleteBtn:
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri URI = item.getUri();
                        this.getImageFilePath(URI);
                    }
                }
                else if (data.getData() != null) {
                    Uri URI = data.getData();
                    this.getImageFilePath(URI);
                }
            }
        }
        else if (requestCode == SEND_IMAGE){
            if (resultCode == RESULT_OK){
                String img = data.getStringExtra("IMAGE");
                image new_img = new Gson().fromJson(img, image.class);
                int pos = Integer.parseInt(data.getStringExtra("POS"));
                imageList.set(pos, new_img);
                adapter.notifyDataSetChanged();
            }
        }
    }






    private String getImageFilePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

                ArrayList<String> imageInfo = getImageInfo(uri);

                if (absolutePathOfImage != null) {
                    this.addImage(uri, absolutePathOfImage, imageInfo);
                } else {
                    this.addImage(uri, String.valueOf(uri), imageInfo);
                }
            }
        }
        return null;
    }


    private ArrayList<String> getImageInfo(Uri uri){
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        ArrayList<String> stringArrayList = new ArrayList<>();

        stringArrayList.add(returnCursor.getString(nameIndex));
        stringArrayList.add(Long.toString(returnCursor.getLong(sizeIndex)));

        return stringArrayList;
    }

    private void addImage(Uri URI, String path,  ArrayList<String> imageInfo){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd");
        LocalDate localDate = LocalDate.now();
        String date = dtf.format(localDate);

        System.out.println(date);

        boolean isDuplicate = false;

        image image = new image(URI, Long.parseLong(imageInfo.get(1)), imageInfo.get(0), "Unknow", date, path);

        for(image temp: imageList){
            if (temp.getImage_URI().equals(URI)){
                isDuplicate = true;
                break;
            }
        }

        if(!isDuplicate){
            imageList.add(image);
            System.out.println(image.toJson());
            adapter.notifyDataSetChanged();
        }
    }

}