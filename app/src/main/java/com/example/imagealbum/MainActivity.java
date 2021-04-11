package com.example.imagealbum;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageButton imgBtn1, imgBtn2, imgBtn3, imgBtn4, imgBtn5, imgBtn6;
    private GridView gridView;
    private List<Album> albumList;
    private CustomGridView adapter;

    String[] albumNames = {"Album1", "Album2", "Album3", "Album4", "Album5", "Album6", "Album7", "Album8"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Main Menu");
        String title = actionBar.getTitle().toString();
        gridView = findViewById(R.id.albumList);
        albumList = new ArrayList<Album>();

        for(String str: albumNames){
            albumList.add(new Album(str));
        }

        adapter = new CustomGridView(this, R.layout.custom_gridview_item, albumList);
        gridView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void showGallery(){
        Intent myIntent = new Intent(MainActivity.this, showAlbum.class);
        MainActivity.this.startActivity(myIntent);
    }

}