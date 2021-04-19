package com.example.imagealbum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

public class showImageInfo extends AppCompatActivity {
    private image image;
    private EditText uri;
    private EditText name;
    private EditText size;
    private EditText date;
    private EditText location;
    private Button btn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_info);

        intent = this.getIntent();
        String data = intent.getStringExtra("IMAGE");
        image = new Gson().fromJson(data, image.class);

        uri = findViewById(R.id.showImageInfo_uri);
        name = findViewById(R.id.showImageInfo_name);
        size = findViewById(R.id.showImageInfo_size);
        date = findViewById(R.id.showImageInfo_date);
        location = findViewById(R.id.showImageInfo_location);
        btn = findViewById(R.id.showImageInfo_btnDone);

        uri.setText("URI: " + image.getImage_URI());
        name.setText("Name: " + image.getImage_name());
        size.setText("Size: " + String.valueOf(image.getImage_size()));
        date.setText("Date: " + image.getDate());
        location.setText(image.getLocation());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resturnResult();
                finish();
            }
        });
    }


    private void resturnResult(){
        image.setLocation(location.getText().toString());
        intent.putExtra("IMAGE", image.toJson());
        setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void onBackPressed() {
        resturnResult();
        finish();
    }
}