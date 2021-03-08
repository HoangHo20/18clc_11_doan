package com.example.imagealbum;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Encrypt extends AppCompatActivity {
    private EditText password, reEnter_password;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);


        password = findViewById(R.id.password1);
        reEnter_password = findViewById(R.id.password2);
        confirmBtn = findViewById(R.id.confirm_button);


        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}