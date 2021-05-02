package com.example.imagealbum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class showImageInfo extends AppCompatActivity {
    private image image;
    private EditText path;
    private EditText name;
    private EditText size;
    private EditText date;
    private EditText location;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int theme_id = Global.loadLastTheme(showImageInfo.this);
        if(theme_id == 0){
            setTheme(R.style.Theme_ImageAlbum);
        }
        else{
            setTheme(R.style.Theme_ImageAlbumDark);
        }
        setContentView(R.layout.activity_show_image_info);

        intent = this.getIntent();
        String data = intent.getStringExtra("IMAGE");
        image = new Gson().fromJson(data, image.class);

        path = findViewById(R.id.showImageInfo_path);
        name = findViewById(R.id.showImageInfo_name);
        size = findViewById(R.id.showImageInfo_size);
        date = findViewById(R.id.showImageInfo_date);
        location = findViewById(R.id.showImageInfo_location);

        path.setText("Path: " + image.getPath());
        name.setText("Name: " + image.getImage_name());
        size.setText("Size: " + String.valueOf(image.getImage_size()));
        date.setText("Date: " + image.getDate());

        String address = getAddress();
        if(!address.equals("")){
            location.setText("Location: " + address);
        }
        else{
            location.setText("Location: Unknown");
        }

    }

    public String getAddress(){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        ArrayList<Double> coord = getCoordinate(showImageInfo.this);

        if(!coord.isEmpty()){
            try{
                addresses = geocoder.getFromLocation(coord.get(1), coord.get(0), 1);
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                String res = "";
                if(city != null){
                    res += city;
                }

                if(country != null && city != null){
                    res += ", " + country;
                }
                else if(country != null && city == null){
                    res += country;
                }
                return res;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return "";
    }

    public ArrayList<Double> getCoordinate(Context context) {
        String[] projection = {MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media.LATITUDE};
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{image.getPath()};
        double longitude = Double.MAX_VALUE;
        double latitude = Double.MAX_VALUE;

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            longitude = c.getDouble(c.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LONGITUDE));
            latitude = c.getDouble(c.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LATITUDE));
        } else {
            // File not found in media store D
        }
        c.close();

        ArrayList<Double> res = new ArrayList<>();
        if (longitude != Double.MAX_VALUE && latitude != Double.MAX_VALUE){
            res.add(longitude);
            res.add(latitude);
        }

        return res;
    }

}