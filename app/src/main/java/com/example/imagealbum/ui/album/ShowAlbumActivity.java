package com.example.imagealbum.ui.album;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imagealbum.Global;
import com.example.imagealbum.ui.album.database.AlbumEntity;

public class ShowAlbumActivity extends AppCompatActivity {
    private AlbumEntity album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        album = (AlbumEntity) intent.getSerializableExtra(Global.SHOW_ALBUM_EXTRA_SERIALIZE_NAME);


    }
}
