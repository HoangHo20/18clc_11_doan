package com.example.imagealbum.ui.album.showalbum;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagealbum.Global;
import com.example.imagealbum.LocaleHelper;
import com.example.imagealbum.R;
import com.example.imagealbum.ui.album.AlbumEncrypt;
import com.example.imagealbum.ui.album.AlbumFragment;
import com.example.imagealbum.ui.album.AlbumModel;
import com.example.imagealbum.ui.album.EnterPasswordDialog;
import com.example.imagealbum.ui.album.SetPasswordDialog;
import com.example.imagealbum.ui.album.database.AlbumEntity;
import com.example.imagealbum.ui.album.database.MediaEntity;
import com.example.imagealbum.viewImage;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ShowAlbumActivity extends AppCompatActivity implements EnterPasswordDialog.EnterPasswordDialogListener, SetPasswordDialog.SetPasswordDialogListener {
    private AlbumEntity album;
    private AlbumModel model;
    private RecyclerView recyclerView;
    private ShowAlbumRecyclerAdapter recyclerAdapter;
    private List<MediaEntity> mediaList;
    private FloatingActionsMenu fab_menu;
    private View toolbarView;
    private Button cancel_button, done_button;
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

        recyclerAdapter = new ShowAlbumRecyclerAdapter(this, mediaList, album);
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
            loadDecryptBitmapOfMedia(mediaList);
        }
    }

    //If each Image/ Video path does not exist -> delete from database and List
    private void validateMediaList(List<MediaEntity> mediaList) {
        ArrayList<MediaEntity> listUnavailable = new ArrayList<>();

        try {
            for (MediaEntity m : mediaList) {
                if (!isFileExist(m.getPath())) {
                    listUnavailable.add(m);
                }
            }

            for (MediaEntity m : listUnavailable) {
                //delete from database
                model.deleteMedia(m);
                //delete from list
                mediaList.remove(m);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.error_load_album, Toast.LENGTH_SHORT).show();
        }
    }

    private void init_cancel_done_toolbar() {
        toolbarView = findViewById(R.id.cancel_done_toolbar);
        cancel_button = (Button) findViewById(R.id.cancel_button);
        done_button = (Button) findViewById(R.id.done_button);

        toolbarView.setVisibility(View.VISIBLE);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerAdapter.setInSelectedMode(Global.SELECTED_MODE_OFF);
                recyclerAdapter.deSelectedAll();
                toolbarView.setVisibility(View.GONE);
            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarView.setVisibility(View.GONE);
                askDeleteSelectedMedia();
            }
        });
    }

    private void init_button() {
        //Action buttons
        fab_menu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        image_add_fab = (FloatingActionButton) findViewById(R.id.show_album_activity_add_image);
        video_add_fab = (FloatingActionButton) findViewById(R.id.show_album_activity_add_video);
        password_fab = (FloatingActionButton) findViewById(R.id.show_album_activity_password);
        album_delete_fab = (FloatingActionButton) findViewById(R.id.show_album_activity_delete_album);
        media_delete_fab = (FloatingActionButton) findViewById(R.id.show_album_activity_delete_media);

        //add image
        image_add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGalleryIntent(Global.IMAGE_TYPE);
            }
        });

        //add video
        video_add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (album.isPrivate()) {
                    showWarningPrivateAlbumCannotContainVideo();
                } else {
                    launchGalleryIntent(Global.VIDEO_TYPE);
                }
            }
        });

        //put password
        //TODO: put password album
        password_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performEnterPasswordDialog();
            }
        });

        //Delete album
        album_delete_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDeleteAlbum();
            }
        });

        //Delete media out of album
        //TODO: delete media out of album
        media_delete_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDeleteMediaOutOfAlbum();
            }
        });
    }

    private void refreshRecyclerview() {
        recyclerAdapter.notifyDataSetChanged();
    }

    // ------------------------- Add media to album ------------------------------------
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

    private boolean isExistInMediaList(MediaEntity media) {
        for (MediaEntity m : mediaList) {
            if (m.getPath().equals(media.getPath())) {
                return true;
            }
        }

        return false;
    }

    private void add_media_to_database(ArrayList<MediaEntity> mediaEntities, int type) {
        ArrayList<MediaEntity> tempList = new ArrayList<>();

        if (this.mediaList != null) {
            for (MediaEntity m : mediaEntities) {
                if (!isExistInMediaList(m)) {
                    //model.insertMedia(m);
                    tempList.add(m);
                }
            }
        } else {
            tempList.addAll(mediaEntities);
        }

        if (this.album.isPrivate()) {
            encryptMediaListAndSetPassword(tempList, this.album.getPassword());
        }

        this.model.insertMedia(tempList);
        load_media();

        refreshRecyclerviewWithNewData(this.mediaList);

        //TODO: add media to private album
    }

    private void refreshRecyclerviewWithNewData(List<MediaEntity> mediaList) {
        recyclerAdapter.setDataAndNotifyDataSetChange(mediaList);
    }

    private void add_media_to_database (String mediaPath, int type) {
        if (type == Global.IMAGE_TYPE) {

        }

        if (type == Global.VIDEO_TYPE) {

        }
    }

    // ------------------------- Set/ change password ------------------------------------
    public void performEnterPasswordDialog() {
        if (isMediaListContainVideo(this.mediaList)) {
            Toast.makeText(this, R.string.warning_private_album_contain_video, Toast.LENGTH_SHORT).show();
        } else {
            if (this.album.getName().equals(Global.FAVORITE_ALBUM.name)) {
                Toast.makeText(this, R.string.favorite_album_cannot_be_private, Toast.LENGTH_SHORT).show();
            } else {
                if (this.album.isPrivate()) {
                    EnterPasswordDialog dialog = new EnterPasswordDialog();
                    dialog.show(this.getSupportFragmentManager(), "Enter password dialog");
                } else {
                    performChangePassword();
                }
            }
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof EnterPasswordDialog) {
            EnterPasswordDialog dialog = (EnterPasswordDialog) fragment;
            dialog.setOnEnterPasswordDialogListener(this);
        }

        if (fragment instanceof SetPasswordDialog) {
            SetPasswordDialog dialog = (SetPasswordDialog) fragment;
            dialog.setOnSetPasswordDialogListener(this);
        }
    }

    @Override
    public boolean isPasswordCorrect(String password) {
        return this.album.checkPassword(password);
    }

    @Override
    public void callBackAction() {
        performChangePassword();
    }

    private void performChangePassword() {
        SetPasswordDialog dialog = new SetPasswordDialog();
        dialog.show(this.getSupportFragmentManager(), "Set password dialog");
    }

    @Override
    public void setPassword(String password) {
        boolean isAlbumPrivate = this.album.isPrivate();

        if (password != null)  {
            password = password.trim();
        }

        if (password == null || password.isEmpty()){
            if (isAlbumPrivate) { //Make album become public
                //Todo: perform decrypt all file
                decryptMediaListAndSave(this.mediaList);
            }
        } else {
            if (!isAlbumPrivate) { //Make album become private from public
                //TODO: encrypt all media file
                encryptMediaListAndSetPassword(this.mediaList, password);
            }
        }

        this.album.setPassword(password);
        model.updateAlbum(this.album);

        finish();
    }

    private void encryptMediaListAndSetPassword(List<MediaEntity> mediaList, String password) {
        AlbumEncrypt encrypt = new AlbumEncrypt();

        for (MediaEntity m : mediaList) {
            if (isFileExist(m.getPath()) && !m.isPrivate()) {
                m.setPassword(password);
                model.updateMedia(m);
                model.deleteMediaSamePathExceptID(m.getPath(), m.getID());

                Uri tempUri = Uri.parse(m.getUriString());
                encrypt.encryptAndSave(m, getBitmapFromUri(tempUri));
            }
        }
    }

    private void decryptMediaListAndSave(List<MediaEntity> mediaList) {
        AlbumEncrypt encrypt = new AlbumEncrypt();

        for (MediaEntity m : mediaList) {
            if (isFileExist(m.getPath())) {
                m.setPassword(null);
                model.updateMedia(m);

                byte[]tempByteArray = encrypt.decrypt(m);

                if (tempByteArray != null) {
                    encrypt.saveFile(tempByteArray, m.getPath());
                }
            }
        }
    }

    private void encryptMediaList(List<MediaEntity> mediaList) {
        AlbumEncrypt encrypt = new AlbumEncrypt();

        for (MediaEntity m : mediaList) {
            if (isFileExist(m.getPath())) {
                Uri tempUri = Uri.parse(m.getUriString());
                encrypt.encryptAndSave(m, getBitmapFromUri(tempUri));
            }
        }
    }

    // ----------------------------- Delete album ----------------------------------------
    private void performDeleteAlbum() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.delete_album))
                .setMessage(getString(R.string.dialog_confirm_message_delete_album) + " (" + album.getName() + ")")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteThisAlbum();
                    }

                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void deleteThisAlbum() {
        //delete normal if album is public
        if (!this.album.isPrivate()) {
            if (this.mediaList != null) {
                //Remove all media in it
                model.deleteMedia(this.mediaList);
                mediaList.clear();
            }

            if (this.album.getName().equals(Global.FAVORITE_ALBUM.name)) { //Favorite album can not be deleted
                //Remove all media in it
                Toast.makeText(this, R.string.warning_cant_delete_favorite_album, Toast.LENGTH_SHORT).show();

                refreshRecyclerview();
            } else {
                if (this.album != null) {
                    //Remove the album
                    model.deleteAlbum(this.album);
                    //Close the activity
                    finish();
                }
            }
        } else {
            //TODO: delete private album
            if (mediaList != null) {
                decryptMediaListAndSave(this.mediaList);
                model.deleteMedia(this.mediaList);
            }

            if (this.album != null) {
                //Remove the album
                model.deleteAlbum(this.album);
                //Close the activity
                finish();
            }
        }
    }

    // ----------------------- Delete Media out of album ----------------------------------
    private void performDeleteMediaOutOfAlbum() {
        recyclerAdapter.setInSelectedMode(Global.SELECTED_MODE_ON);

        init_cancel_done_toolbar();
    }

    private void askDeleteSelectedMedia() {
        ArrayList<MediaEntity> selectedItems = recyclerAdapter.getSelectedMedia();

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.delete_media_out_of_album))
                .setMessage(getString(R.string.dialog_confirm_delete_media) + " " + selectedItems.size() + " " + getString(R.string.item) + " " + getString(R.string.out_of_album) + "?")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMedia(selectedItems);
                    }

                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void deleteMedia(ArrayList<MediaEntity> mediaEntities) {
        decryptMediaListAndSave(mediaEntities);

        for (MediaEntity m : mediaEntities) {
            this.mediaList.remove(m);
        }

        model.deleteMedia(mediaEntities);

        refreshRecyclerview();
    }

    // --------------------------- On Activity Result ------------------------------------
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

    // -------------------- Other Function --------------------------
    private boolean isMediaListContainVideo(List<MediaEntity> mediaEntities) {
        for (MediaEntity m : mediaEntities) {
            if (!m.isImage()) return true;
        }

        return false;
    }

    private void showWarningPrivateAlbumCannotContainVideo() {
        Toast.makeText(this, R.string.warning_private_album_contain_video, Toast.LENGTH_SHORT).show();
    }

    private boolean isFileExist(String filePath) {
        File f = new File(filePath);

        return f.exists();
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap selectedImage = null;
        try{
            InputStream imageStream = getContentResolver().openInputStream(uri);
            selectedImage = BitmapFactory.decodeStream(imageStream);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return selectedImage;
    }

    private void loadDecryptBitmapOfMedia(List<MediaEntity> mediaEntities) {
        AlbumEncrypt encrypt = new AlbumEncrypt();

        for (MediaEntity m : mediaEntities) {
            if (isFileExist(m.getPath())) {
                byte[] tempBytes = encrypt.decrypt(m);
                Bitmap tempBitmap = BitmapFactory.decodeByteArray(tempBytes, 0, tempBytes.length);

                m.setBitmap(tempBitmap);
            }
        }
    }
}
