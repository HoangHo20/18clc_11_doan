package com.example.imagealbum;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class viewImage extends AppCompatActivity {
    private static int SEND_INFO = 1;
    private image image;
    private ImageView imgView;
    private Intent intent;
    private int pos;
    private ImageView detailBtn;
    private ImageView setWallpaperBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image);

        innit();

        detailBtn = findViewById(R.id.toolBar_imageView_detailBtn);
        setWallpaperBtn = findViewById(R.id.toolBar_imageView_setWallpaperlBtn);

        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(viewImage.this, showImageInfo.class);
                sendIntent.putExtra("IMAGE", image.toJson());
                startActivityForResult(sendIntent, SEND_INFO);
            }
        });

        setWallpaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap selectedImage = getBitMap();
                if (selectedImage == null){
                    Toast.makeText(viewImage.this, R.string.viewImage_nullBitmap, Toast.LENGTH_SHORT).show();
                }
                else{
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    try{
                        wallpaperManager.setBitmap(selectedImage);
                        Toast.makeText(viewImage.this, R.string.set_wallpaper_success, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(viewImage.this, R.string.set_wallpaper_fail, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }


    private void innit(){
        intent = this.getIntent();
        String data = intent.getStringExtra("IMAGE");
        pos = Integer.parseInt(intent.getStringExtra("POS"));
        image = new Gson().fromJson(data, image.class);

        imgView = findViewById(R.id.viewImage_image);

        Bitmap selectedImage = this.getBitMap();
        if(selectedImage != null){
            imgView.setImageBitmap(selectedImage);
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        else{
            Toast.makeText(viewImage.this, R.string.viewImage_nullBitmap, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_INFO){
            if (resultCode == Activity.RESULT_OK){
                String str = data.getStringExtra("IMAGE");
                if (str != null){
                    this.image = new Gson().fromJson(str, image.class);
                }
            }
        }
    }

    private void response(){
        intent.putExtra("IMAGE", this.image.toJson());
        intent.putExtra("POS", String.valueOf(pos));
        setResult(Activity.RESULT_OK, intent);
    }

    private Bitmap getBitMap(){
        Bitmap selectedImage = null;
        try{
            Uri uri = image.getImage_URI();
            InputStream imageStream = getContentResolver().openInputStream(uri);
            selectedImage = BitmapFactory.decodeStream(imageStream);
            imgView.setImageBitmap(selectedImage);
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return selectedImage;
    }

    @Override
    public void onBackPressed() {
        response();
        finish();
    }
}