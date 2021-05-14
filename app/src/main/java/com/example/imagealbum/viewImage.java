package com.example.imagealbum;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.imagealbum.customutil.FileUtils;
import com.example.imagealbum.ui.album.AlbumModel;
import com.example.imagealbum.ui.album.database.AlbumEntity;
import com.example.imagealbum.ui.album.database.MediaEntity;
import com.example.imagealbum.ui.album.database.database;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.FrescoImageViewFactory;
import com.github.piasy.biv.view.GlideImageViewFactory;
import com.google.gson.Gson;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class viewImage extends AppCompatActivity {
    private static int SEND_INFO = 1;
    private image image;
    private Context context;
    private AlbumModel model;
    private boolean isFavorite;
    private boolean isPrivate;
    private AlbumEntity favoriteAlbum;

    //Show image
    private BigImageView bigImageView;
    //Show video
    private int videoPosition;
    private VideoView videoView;
    private FrameLayout frameLayout;
    private MediaController mediaController;
    private Bitmap imageBitmap;

    private Intent intent;
    private int pos;

    private ImageButton backBtn,
            detailBtn,
            setWallpaperBtn,
            shareBtn,
            addFavoriteBtn,
            editBtn;
    private ImageView imageView;
    private View toolbar_upper, toolbar_bottom;
    //private ImageView encrypt_decryptBtn;
    //private boolean isEncrypted = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int theme_id = Global.loadLastTheme(viewImage.this);
        if(theme_id == 0){
            setTheme(R.style.Theme_ImageAlbum);
        }
        else{
            setTheme(R.style.Theme_ImageAlbumDark);
        }

        //Hide tool bar
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}

        context = this;
        //setContentView(R.layout.activity_main_navigation);
        BigImageViewer.initialize(GlideImageLoader.with(this));

        setContentView(R.layout.view_image);

        model = new AlbumModel(this);

        innit();

        backBtn = findViewById(R.id.toolBar_imageBtn_backBtn);
        detailBtn = findViewById(R.id.toolBar_imageBtn_detailBtn);
        setWallpaperBtn = findViewById(R.id.toolBar_imageBtn_setWallpaperlBtn);

        shareBtn = findViewById(R.id.toolBar_imageBtn_shareBtn);
        addFavoriteBtn = findViewById(R.id.toolBar_imageBtn_addFavoriteBtn);
        editBtn = findViewById(R.id.toolBar_imageBtn_editBtn);

        toolbar_bottom = findViewById(R.id.toolBar_bottom);
        toolbar_upper = findViewById(R.id.toolBar);

        checkFavoriteItem();
        //encrypt_decryptBtn = findViewById(R.id.toolBar_imageView_encrypt_decryptBtn);

//        if(isEncrypted){
//            encrypt_decryptBtn.setImageResource(R.drawable.ic_baseline_lock_open_24);
//        }

        setOnclickListenerBtns();

    }

    private void toolbar_set_visible(boolean setVisible) {
        if (setVisible) {
            toolbar_upper.setVisibility(View.VISIBLE);
            if (!isPrivate) {
                toolbar_bottom.setVisibility(View.VISIBLE);
            }
        } else {
            toolbar_upper.setVisibility(View.INVISIBLE);
            if (!isPrivate) {
                toolbar_bottom.setVisibility(View.GONE  );
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void checkFavoriteItem() {
        List<AlbumEntity> albumEntity = model.getAlbumByName(Global.FAVORITE_ALBUM.name);

        if (albumEntity == null || albumEntity.isEmpty()) {
            favoriteAlbum = new AlbumEntity(Global.FAVORITE_ALBUM.name);
            model.insertAlbum(favoriteAlbum);
        } else {
            favoriteAlbum = albumEntity.get(0);
        }

        MediaEntity media= model.getOneItemByAlbumIDAndPath(image.getPath(), favoriteAlbum.getID());

        if (media != null) {
            this.isFavorite = true;
        } else {
            this.isFavorite = false;
        }

        setFavoriteBtnIcon(this.isFavorite);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setFavoriteBtnIcon(boolean isFavoriteItem) {
        if (isFavoriteItem) {
            addFavoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_star_gold_24));
        } else {
            addFavoriteBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_star_border_24));
        }
    }

    private void setOnclickListenerBtns() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(viewImage.this, showImageInfo.class);
                sendIntent.putExtra("IMAGE", image.toJson());
                startActivity(sendIntent);
            }
        });

        if (!image.isImage()) { //video type
            setWallpaperBtn.setVisibility(View.INVISIBLE);
        }

        setWallpaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AlertDialog.Builder(context, R.style.Theme_AppCompat)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle(R.string.set_wallpaper)
//                        .setMessage(R.string.set_this_image_as_home_wallpaper)
//                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                setSetWallpaper();
//                            }
//
//                        })
//                        .setNegativeButton(R.string.no, null)
//                        .show();

                performSetWallpaper();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                if (image.isImage()) {
                    share.setType("image/jpg");
                } else {
                    share.setType("video/mp4");
                }

                share.putExtra(Intent.EXTRA_STREAM, image.getImage_URI());
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });

        addFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    //Remove out of favorite album
                    MediaEntity mediaEntity = model.getOneItemByAlbumIDAndPath(image.getPath(), favoriteAlbum.getID());
                    model.deleteMedia(mediaEntity);
                } else {
                    //Add to favorite album
                    MediaEntity mediaEntity = new MediaEntity(image.getImage_URI(), image.getPath(), image.getType(), favoriteAlbum.getID());
                    model.insertMedia(mediaEntity);

                }

                isFavorite = !isFavorite;
                setFavoriteBtnIcon(isFavorite);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performEdit();
            }
        });

        if (isPrivate) {
//            shareBtn.setVisibility(View.INVISIBLE);
//            addFavoriteBtn.setVisibility(View.INVISIBLE);
//            editBtn.setVisibility(View.INVISIBLE);
//            setWallpaperBtn.setVisibility(View.INVISIBLE);
            toolbar_bottom.setVisibility(View.INVISIBLE);
        }
    }

    private void performEdit(){
        if (image.isImage()) {
            //Perform edit image
            File outputFile = FileUtils.genEditFile();
            EditImageActivity.start((Activity) context, image.getPath(), outputFile.getAbsolutePath(), Global.INTENT_EDIT_IMAGE);
        } else {
            Toast.makeText(this, R.string.editing_video_is_under_development, Toast.LENGTH_SHORT).show();
        }
    }

    private void performSetWallpaper() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_wallpaper_24)
                .setTitle(getString(R.string.set_wallpaper))
                .setMessage(getString(R.string.set_this_image_as_home_wallpaper))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setSetWallpaper();
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void setSetWallpaper() {
        Bitmap selectedImage = getBitMap();

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try{
            wallpaperManager.setBitmap(selectedImage);
            Toast.makeText(viewImage.this, R.string.set_wallpaper_success, Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Toast.makeText(viewImage.this, R.string.set_wallpaper_fail, Toast.LENGTH_SHORT).show();
        }
    }

    private void innit(){
        intent = this.getIntent();
        String data = intent.getStringExtra("IMAGE");
        pos = Integer.parseInt(intent.getStringExtra("POS"));
        image = new Gson().fromJson(data, image.class);

        byte[] bitmapBytes = intent.getByteArrayExtra("BITMAP");
        if (bitmapBytes != null) {
            this.isPrivate = true;
            this.imageBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        }

        loadData();

        if (image.isImage()) {
            loadImage();
        } else  {
            this.videoPosition = 0;
            loadVideo();
        }

//        bigImageView.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CUSTOM);
    }

    private void loadImage() {
        if (this.isPrivate) {
            imageView = (ImageView) findViewById(R.id.view_image_imageView);
            imageView.setImageBitmap(this.imageBitmap);
            imageView.setVisibility(View.VISIBLE);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toolbar_set_visible(toolbar_upper.getVisibility() != View.VISIBLE);
                }
            });
        } else {
            bigImageView = (BigImageView) findViewById(R.id.mBigImage);
            bigImageView.setImageViewFactory(new GlideImageViewFactory());
            bigImageView.showImage(image.getImage_URI());
            bigImageView.setVisibility(View.VISIBLE);

            bigImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toolbar_set_visible(toolbar_upper.getVisibility() != View.VISIBLE);
                }
            });
        }
    }

    private void loadVideo() {
        videoView = (VideoView) findViewById(R.id.video_view);

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar_set_visible(toolbar_upper.getVisibility() != View.VISIBLE);
            }
        });

        frameLayout = (FrameLayout) findViewById(R.id.view_image_video_frame);
        frameLayout.setVisibility(View.VISIBLE);

        videoView.setVideoURI(image.getImage_URI());

        this.mediaController = new MediaController(this);

        // Set MediaController for VideoView
        this.videoView.setMediaController(mediaController);

        // Set the videoView that acts as the anchor for the MediaController.
        this.mediaController.setAnchorView(videoView);


        // When the video file ready for playback.
        this.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {

                videoView.seekTo(videoPosition);
                if (videoPosition == 0) {
                    videoView.start();
                }

                // When video Screen change size.
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {

                        // Re-Set the videoView that acts as the anchor for the MediaController
                        mediaController.setAnchorView(videoView);
                    }
                });
            }
        });
    }

    //Change phone direction -> save position of current video

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        // Store current position.
        outState.putInt(Global.VIDEO_CURRENT_POSITION_STRING_NAME,
                videoView.getCurrentPosition());

        videoView.pause();
    }

    //Restore the state when rotated phone -> get position of video


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //Load position of video
        videoPosition = savedInstanceState.getInt(Global.VIDEO_CURRENT_POSITION_STRING_NAME);
        //videoView.seekTo(videoPosition);
    }

    public void loadData(){
        SharedPreferences sharedPref = this.getSharedPreferences("NOTE_DATA", Context.MODE_PRIVATE);

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

    class Encryption{

        byte[] keyBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04,
                0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c,
                0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14,
                0x15, 0x16, 0x17 };

        public byte[] toByteArray(Bitmap bitmap){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }

        public byte[] encryptByteArray(byte[] input)
                throws NoSuchPaddingException, NoSuchAlgorithmException,
                NoSuchProviderException, InvalidKeyException,
                ShortBufferException, BadPaddingException, IllegalBlockSizeException {
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");

            cipher.init(Cipher.ENCRYPT_MODE, key);

            return cipher.doFinal(input);
        }

        public void encrypt(image img){
            Bitmap bitmap = getBitMap();
            byte[] bitmap_array = toByteArray(bitmap);

            byte[] encrypted_array = null;

            try{
                encrypted_array = encryptByteArray(bitmap_array);
            }
            catch (Exception e){
                e.printStackTrace();
                return;
            }

            saveFile(encrypted_array, img.getPath());
        }

        public void saveFile(byte[] data, String path){
            FileOutputStream fos=null;

            try {
                fos=new FileOutputStream(path);
                fos.write(data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            finally{

                try {
                    fos.close();
                } catch (NullPointerException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public byte[] decryptByteArray(byte[] encryptedData)
                throws NoSuchPaddingException, NoSuchAlgorithmException,
                NoSuchProviderException, InvalidKeyException,
                BadPaddingException, IllegalBlockSizeException {

            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, key);

            return cipher.doFinal(encryptedData);
        }



        public void decrypt(image img){
            FileInputStream fis = null;
            try{
                File file = new File(img.getPath());
                fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                saveFile(decryptByteArray(data), img.getPath());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try {
                    fis.close();
                } catch (NullPointerException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Global.INTENT_EDIT_IMAGE) {
                finish();
            }
        }
    }
}