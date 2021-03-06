package com.example.imagealbum;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class showAlbum extends AppCompatActivity {
    private ArrayList<image> imageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private static int SEND_IMAGE = 1;
    private static int SLIDE_SHOW = 3;
    private ImageView addBtn;
    private ImageView deleteBtn;
    private ImageView slideShowBtn;
    private ImageView doneBtn;
    private boolean inSlideShow = false;
    private boolean inDeleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_album);
        Intent intent = getIntent();


        recyclerView = findViewById(R.id.imagegallery);
        addBtn = findViewById(R.id.actionBar_showAlbum_addBtn);
        deleteBtn = findViewById(R.id.actionBar_showAlbum_deleteBtn);
        slideShowBtn = findViewById(R.id.actionBar_showAlbum_slideShowBtn);
        doneBtn = findViewById(R.id.actionBar_showAlbum_doneBtn);

        adapter = new RecyclerAdapter(imageList, showAlbum.this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(adapter);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, 2);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inDeleteMode){
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_selected);
                    addBtn.setVisibility(View.INVISIBLE);
                    slideShowBtn.setVisibility(View.INVISIBLE);
                    doneBtn.setVisibility(View.VISIBLE);
                    inDeleteMode = !inDeleteMode;
                    adapter.setInSelectionMode(true);
                }
                else{
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_unselect);
                    addBtn.setVisibility(View.VISIBLE);
                    slideShowBtn.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.INVISIBLE);
                    inDeleteMode = !inDeleteMode;
                    adapter.setInSelectionMode(false);
                    adapter.deSelectedAll();
                }
            }
        });

        slideShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inSlideShow){
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_selected);
                    addBtn.setVisibility(View.INVISIBLE);
                    deleteBtn.setVisibility(View.INVISIBLE);
                    doneBtn.setVisibility(View.VISIBLE);
                    inSlideShow = !inSlideShow;
                    adapter.setInSelectionMode(true);
                }
                else{
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_unselect);
                    addBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.INVISIBLE);
                    inSlideShow = !inSlideShow;
                    adapter.setInSelectionMode(false);
                    adapter.deSelectedAll();
                }

            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inSlideShow){
                    ArrayList<image> selectedImgs = getSelectedImg();
                    if (selectedImgs.size() > 1){
                        String json = new Gson().toJson(selectedImgs);
                        Intent intent = new Intent(showAlbum.this, slideShow.class);
                        intent.putExtra("IMAGE", json);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(showAlbum.this, R.string.no_photo_selected_slideShow, Toast.LENGTH_SHORT).show();
                    }
                }
                else if(inDeleteMode){
                    ArrayList<Integer> selectedImagesIndex = adapter.getSelectedImages();
                    if(!selectedImagesIndex.isEmpty()){
                        for(int index: selectedImagesIndex){
                            imageList.remove(index);
                        }

                    }
                }
                adapter.notifyDataSetChanged();
                if(inSlideShow){
                    deleteBtn.setVisibility(View.VISIBLE);
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_unselect);
                    inSlideShow = !inSlideShow;
                }
                else if(inDeleteMode){
                    slideShowBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_unselect);
                    inDeleteMode = !inDeleteMode;
                }
                addBtn.setVisibility(View.VISIBLE);
                doneBtn.setVisibility(View.INVISIBLE);
                adapter.setInSelectionMode(false);
            }
        });

    }


    private ArrayList<image> getSelectedImg(){
        ArrayList<image> res = new ArrayList<>();

        ArrayList<Integer> selectedImagesIndex = adapter.getSelectedImages();
        for(int index: selectedImagesIndex){
            res.add(imageList.get(index));
        }

        return res;
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


        boolean isDuplicate = false;

        for(image temp: imageList){
            if (temp.getImage_name().equals(imageInfo.get(0))){
                isDuplicate = true;
                break;
            }
        }

        if(!isDuplicate){
            imageList.add(new image(URI, Long.parseLong(imageInfo.get(1)), imageInfo.get(0), "Unknow", date, path));
            adapter.notifyDataSetChanged();
        }
    }

}