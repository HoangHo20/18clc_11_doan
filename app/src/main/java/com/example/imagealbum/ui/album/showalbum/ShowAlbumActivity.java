package com.example.imagealbum.ui.album.showalbum;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Gallery;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagealbum.Global;
import com.example.imagealbum.LocaleHelper;
import com.example.imagealbum.R;
import com.example.imagealbum.ui.album.AlbumModel;
import com.example.imagealbum.ui.album.database.AlbumEntity;
import com.example.imagealbum.ui.album.database.MediaEntity;
import com.example.imagealbum.viewImage;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowAlbumActivity extends AppCompatActivity {
    private AlbumEntity album;
    private AlbumModel model;
    private RecyclerView recyclerView;
    private ShowAlbumRecyclerAdapter recyclerAdapter;
    private List<MediaEntity> mediaList;
    private FloatingActionsMenu fab_menu;
    private FloatingActionButton image_add_fab, video_add_fab, password_fab, album_delete_fab, media_delete_fab;
    String[] projection =
            {MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA};

    String[] projection_video =
            {MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DATA};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int theme_id = Global.loadLastTheme(ShowAlbumActivity.this);
        if(theme_id == 0){
            setTheme(R.style.Theme_ImageAlbum);
        }
        else{
            setTheme(R.style.Theme_ImageAlbumDark);
        }

        setContentView(R.layout.show_album_activity);

        //Hide tool bar
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}

        Intent intent = getIntent();
        int albumID = intent.getIntExtra(Global.SHOW_ALBUM_EXTRA_ALBUM_ID, 0);

        model = new AlbumModel(this);
        album = model.getAlbumByID(albumID);

        init_button();
        load_media();
        init_recyclerview();
    }

    private void init_recyclerview() {
        recyclerView = findViewById(R.id.show_album_activity_recyclerview);

        recyclerAdapter = new ShowAlbumRecyclerAdapter(this, mediaList, album.isPrivate());
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, Global.ITEM_SIZE_GRID_LAYOUT_PORTRAIT));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab_menu.setEnabled(!fab_menu.isEnabled());
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || dy < 0 && fab_menu.isShown()) {
                    fab_menu.setEnabled(!fab_menu.isEnabled());
                }
            }
        });
    }

    private void load_media() {
        //TODO: load media
        mediaList = model.getMediaByAlbumID(album.getID());

        validateMediaList(mediaList);

        if (album.isPrivate()) {
            //TODO: LOAD Bitmap
        }
    }

    //If each Image/ Video path does not exist -> delete from database and List
    private void validateMediaList(List<MediaEntity> mediaList) {
        ArrayList<MediaEntity> list = new ArrayList<>();

        try {
            for (MediaEntity m : mediaList) {
                File f = new File(m.getPath());
                if (!f.exists()) {
                    list.add(m);
                }
            }

            for (MediaEntity m: list) {
                //delete from database
                model.deleteMedia(m);
                //delete from list
                mediaList.remove(m);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.error_load_album, Toast.LENGTH_SHORT).show();
        }
    }

    private void init_button() {
        fab_menu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        image_add_fab = (FloatingActionButton) findViewById(R.id.show_album_activity_add_image);
        video_add_fab = (FloatingActionButton) findViewById(R.id.show_album_activity_add_video);
        password_fab = (FloatingActionButton) findViewById(R.id.show_album_activity_password);
        album_delete_fab = (FloatingActionButton) findViewById(R.id.show_album_activity_delete_album);
        media_delete_fab = (FloatingActionButton) findViewById(R.id.show_album_activity_delete_media);

        //
        image_add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGalleryIntent(Global.IMAGE_TYPE);
            }
        });

        video_add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGalleryIntent(Global.VIDEO_TYPE);
            }
        });
    }

    public void launchGalleryIntent(int type) {
        Uri imageUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Intent intent;
        if (type == Global.IMAGE_TYPE) {
            intent = new Intent(Intent.ACTION_PICK, imageUri);

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, Global.REQUEST_GET_PICTURE);
        } else {
            intent = new Intent(Intent.ACTION_PICK, videoUri);

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, Global.REQUEST_GET_VIDEO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Global.REQUEST_GET_PICTURE) {
            if(resultCode == RESULT_OK && data != null) {
                if(data.getClipData() != null) {
                    ArrayList<Uri> uriList = new ArrayList<>();

                    ClipData clipData = data.getClipData();
                    int count = data.getClipData().getItemCount();

                    for(int i = 0; i < count; i++)
                        uriList.add(clipData.getItemAt(i).getUri());
                    //TODO: do something; here is your selected images

                    ArrayList<MediaEntity> mediaTemp = new ArrayList<>();
                    for (Uri u : uriList) {
                        Cursor cursor = this.getContentResolver().query(u, projection, null, null, null);
                        cursor.moveToFirst();

                        String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                        Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));

                        cursor.close();

                        mediaTemp.add(new MediaEntity(uri, absolutePathOfImage, Global.IMAGE_TYPE, album.getID()));
                    }

                    add_media_to_database(mediaTemp, Global.IMAGE_TYPE);
                } else if(data.getData() != null) {
                    String imagePath = data.getData().getPath();
                    //TODO: do something
                    add_media_to_database(imagePath, Global.IMAGE_TYPE);
                }
            }
        }

        if(requestCode == Global.REQUEST_GET_VIDEO) {
            if(resultCode == RESULT_OK && data != null) {
                if(data.getClipData() != null) {
                    ArrayList<Uri> uriList = new ArrayList<>();

                    ClipData clipData = data.getClipData();
                    int count = data.getClipData().getItemCount();

                    for(int i = 0; i < count; i++)
                        uriList.add(clipData.getItemAt(i).getUri());
                    //TODO: do something; here is your selected images

                    ArrayList<MediaEntity> mediaTemp = new ArrayList<>();
                    for (Uri u : uriList) {
                        Cursor cursor = this.getContentResolver().query(u, projection_video, null, null, null);
                        cursor.moveToFirst();

                        String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
                        Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));

                        cursor.close();

                        mediaTemp.add(new MediaEntity(uri, absolutePathOfImage, Global.VIDEO_TYPE, album.getID()));
                    }

                    add_media_to_database(mediaTemp, Global.VIDEO_TYPE);
                } else if(data.getData() != null) {
                    String imagePath = data.getData().getPath();
                    //TODO: do something
                    add_media_to_database(imagePath, Global.IMAGE_TYPE);
                }
            }
        }
    }

    private boolean isExistInMediaList(MediaEntity media) {
        for (MediaEntity m : mediaList) {
            if (m.getPath().equals(media.getPath())) {
                return true;
            }
        }

        return false;
    }

    private void add_media_to_database(ArrayList<MediaEntity> mediaTemp, int type) {
        if (mediaList != null) {
            for (MediaEntity m : mediaTemp) {
                if (!isExistInMediaList(m)) {
                    model.insertMedia(m);
                    mediaList.add(m);
                }
            }
        } else {
            model.insertMedia(mediaTemp);
        }

        recyclerAdapter.notifyDataSetChanged();
    }

    private void add_media_to_database (String mediaPath, int type) {
        if (type == Global.IMAGE_TYPE) {

        }

        if (type == Global.VIDEO_TYPE) {

        }
    }
}
