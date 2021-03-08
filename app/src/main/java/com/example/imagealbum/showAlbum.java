package com.example.imagealbum;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class showAlbum extends AppCompatActivity {
    private ImageView imgView1, imgView2, imgView3, imgView4, imgView5, imgView6,
            imgView7, imgView8, imgView9, imgView10, imgView11, imgView12,
            imgView13, imgView14, imgView15, imgView16, imgView17, imgView18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());


        imgView1 = findViewById(R.id.imageView1);
        imgView2 = findViewById(R.id.imageView2);
        imgView3 = findViewById(R.id.imageView3);
        imgView4 = findViewById(R.id.imageView4);
        imgView5 = findViewById(R.id.imageView5);
        imgView6 = findViewById(R.id.imageView6);

        imgView7 = findViewById(R.id.imageView7);
        imgView8 = findViewById(R.id.imageView8);
        imgView9 = findViewById(R.id.imageView9);
        imgView10 = findViewById(R.id.imageView10);
        imgView11 = findViewById(R.id.imageView11);
        imgView12 = findViewById(R.id.imageView12);

        imgView13 = findViewById(R.id.imageView13);
        imgView14 = findViewById(R.id.imageView14);
        imgView15 = findViewById(R.id.imageView15);
        imgView16 = findViewById(R.id.imageView16);
        imgView17 = findViewById(R.id.imageView17);
        imgView18 = findViewById(R.id.imageView18);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.showalbum_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
}