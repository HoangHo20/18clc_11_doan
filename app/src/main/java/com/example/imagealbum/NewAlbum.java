package com.example.imagealbum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class NewAlbum extends AppCompatActivity {
    private TextView name;
    private Button createBtn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_album);

        name = findViewById(R.id.newAlbum_name);
        createBtn = findViewById(R.id.newAlbum_createBtn);

        intent = getIntent();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = name.getText().toString();
                System.out.println(text);
                if (!text.isEmpty()){
                    intent.putExtra("NAME", text);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }


}