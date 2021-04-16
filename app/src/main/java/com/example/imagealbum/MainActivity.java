package com.example.imagealbum;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int CREATE_ALBUM_CODE = 1;
//    private static int SHOW_ALBUM_DETAIL = 2;

    private ImageButton imgBtn1, imgBtn2, imgBtn3, imgBtn4, imgBtn5, imgBtn6;
    private GridView gridView;
    private List<Album> albumList;
    private CustomGridView adapter;
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

    // Adapter for GridView
    private class CustomGridView extends BaseAdapter {
        private Context context;
        private int idLayout;
        private List<Album> listAlbum;
        private int positionSelect = -1;
        private boolean inDeleteMode = false;

        public CustomGridView(Context context, int idLayout, List<Album> listAlbum) {
            this.context = context;
            this.idLayout = idLayout;
            this.listAlbum= listAlbum;
        }

        public void changeDeleteMode(){
            this.inDeleteMode = !this.inDeleteMode;
        }

        @Override
        public int getCount() {
            if (listAlbum.size() != 0 && !listAlbum.isEmpty()) {
                return listAlbum.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            GridView grid = (GridView)parent;
            int size = grid.getColumnWidth();

            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.custom_gridview_item, null);
            }

            TextView albumName = (TextView) convertView.findViewById(R.id.imageButton_text);
            ImageButton imageBtn = (ImageButton) convertView.findViewById(R.id.imageButton);
            imageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(inDeleteMode){
                        listAlbum.remove(position);
                        notifyDataSetChanged();
                    }
                    else{
                        showGallery();
                    }
                }
            });
            albumName.setText(listAlbum.get(position).getName());

            return convertView;

        }

    }
}