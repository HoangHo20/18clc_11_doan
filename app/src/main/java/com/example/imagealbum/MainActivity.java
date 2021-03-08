package com.example.imagealbum;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private ImageButton imgBtn1, imgBtn2, imgBtn3, imgBtn4, imgBtn5, imgBtn6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Main Menu");
        String title = actionBar.getTitle().toString();

        imgBtn1 = findViewById(R.id.imageButton1);
        imgBtn2 = findViewById(R.id.imageButton2);
        imgBtn3 = findViewById(R.id.imageButton3);
        imgBtn4 = findViewById(R.id.imageButton4);
        imgBtn5 = findViewById(R.id.imageButton5);
        imgBtn6 = findViewById(R.id.imageButton6);

        imgBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });

        imgBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });

        imgBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });

        imgBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });

        imgBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });

        imgBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });

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