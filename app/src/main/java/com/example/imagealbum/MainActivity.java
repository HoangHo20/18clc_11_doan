package com.example.imagealbum;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageButton imgBtn1, imgBtn2, imgBtn3, imgBtn4, imgBtn5, imgBtn6;
    private GridView gridView;
    private List<Album> albumList;
    private CustomGridView adapter;
    private static int CREATE_ALBUM_CODE = 1;
    private boolean inDeleteMode = false;

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


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(inDeleteMode){
                    albumList.remove(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if (requestCode == CREATE_ALBUM_CODE && resultCode == Activity.RESULT_OK){
                Album newAlb = new Album(data.getStringExtra("NAME"));
                boolean flag = true;
                for(Album album: albumList){
                    if (album.checkDuplicate(newAlb)){
                        new AlertDialog.Builder(MainActivity.this).
                                setIcon(android.R.drawable.ic_delete)
                                .setTitle("Can't create new Album")
                                .setMessage("Duplicate name")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                    }
                                })
                                .show();
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    albumList.add(newAlb);
                    adapter.notifyDataSetChanged();
                    System.out.println(albumList.size());
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_mainactivity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.actionBar_mainActi_addBtn:
                Intent intent = new Intent(MainActivity.this, NewAlbum.class);
                startActivityForResult(intent, CREATE_ALBUM_CODE);
                break;
            case R.id.actionBar_mainActi_deleteBtn:
                inDeleteMode = !inDeleteMode;
                adapter.changeDeleteMode();
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showGallery(){
        Intent myIntent = new Intent(MainActivity.this, showAlbum.class);
        MainActivity.this.startActivity(myIntent);
    }



}