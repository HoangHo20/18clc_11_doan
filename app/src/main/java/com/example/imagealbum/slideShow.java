package com.example.imagealbum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.SliderView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class slideShow extends AppCompatActivity {
    private ArrayList<image> imageList;
    private SliderView sliderView;
    private SliderAdapter adapter;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int theme_id = Global.loadLastTheme(slideShow.this);
        if(theme_id == 0){
            setTheme(R.style.Theme_ImageAlbum);
        }
        else{
            setTheme(R.style.Theme_ImageAlbumDark);
        }

        setContentView(R.layout.activity_slide_show);

        innit();

        sliderView = findViewById(R.id.slider);
        adapter = new SliderAdapter(this, imageList);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setAutoCycle(true);
        sliderView.setScrollTimeInMillis(1500);
        // to start autocycle below method is used.
        sliderView.startAutoCycle();

    }

    private void innit(){
        intent = this.getIntent();

        String json = intent.getStringExtra("IMAGE");
        image[] images = new Gson().fromJson(json, image[].class);

        imageList = new ArrayList<image>(Arrays.asList(images));
    }
}