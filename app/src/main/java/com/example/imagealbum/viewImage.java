package com.example.imagealbum;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.BitmapCompat;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.facebook.common.file.FileUtils;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.FrescoImageViewFactory;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class viewImage extends AppCompatActivity {
    private static int SEND_INFO = 1;
    private image image;
    private BigImageView bigImageView;
    private Intent intent;
    private int pos;
    private ImageView detailBtn;
    private ImageView setWallpaperBtn;
    private ImageView encrypt_decryptBtn;
    private boolean isEncrypted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.view_image);

        BigImageViewer.initialize(FrescoImageLoader.with(this));

        setContentView(R.layout.view_image);

        innit();

        detailBtn = findViewById(R.id.toolBar_imageView_detailBtn);
        setWallpaperBtn = findViewById(R.id.toolBar_imageView_setWallpaperlBtn);
        encrypt_decryptBtn = findViewById(R.id.toolBar_imageView_encrypt_decryptBtn);

        if(isEncrypted){
            encrypt_decryptBtn.setImageResource(R.drawable.ic_baseline_lock_open_24_blue);
        }



        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(viewImage.this, showImageInfo.class);
                sendIntent.putExtra("IMAGE", image.toJson());
                startActivityForResult(sendIntent, SEND_INFO);
            }
        });

        setWallpaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap selectedImage = getBitMap();
                if (selectedImage == null || isEncrypted){
                    Toast.makeText(viewImage.this, R.string.viewImage_nullBitmap, Toast.LENGTH_SHORT).show();
                }
                else{
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    try{
                        wallpaperManager.setBitmap(selectedImage);
                        Toast.makeText(viewImage.this, R.string.set_wallpaper_success, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(viewImage.this, R.string.set_wallpaper_fail, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        encrypt_decryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEncrypted){
                    Bitmap bitmap = getBitMap();
                    if(bitmap != null){
                        Encryption encryption = new Encryption();
                        encryption.encrypt(image);
                        Toast.makeText(viewImage.this, R.string.Encrypt_success, Toast.LENGTH_SHORT).show();
                        isEncrypted = true;
                        SharedPreferences sharedPref = getSharedPreferences("NOTE_DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(image.getImage_URI().toString(), true);
                        editor.commit();
                        encrypt_decryptBtn.setImageResource(R.drawable.ic_baseline_lock_open_24_blue);
                    }
                }
                else{
                    Encryption encryption = new Encryption();
                    encryption.decrypt(image);
                    Toast.makeText(viewImage.this, R.string.Decrypt_success, Toast.LENGTH_SHORT).show();
                    isEncrypted = false;
                    SharedPreferences sharedPref = getSharedPreferences("NOTE_DATA", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(image.getImage_URI().toString(), false);
                    editor.commit();
                    encrypt_decryptBtn.setImageResource(R.drawable.ic_baseline_lock_24_blue);
                }
                bigImageView.showImage(image.getImage_URI());
            }
        });

    }


    private void innit(){
        intent = this.getIntent();
        String data = intent.getStringExtra("IMAGE");
        pos = Integer.parseInt(intent.getStringExtra("POS"));
        image = new Gson().fromJson(data, image.class);

        loadData();

        bigImageView = (BigImageView) findViewById(R.id.mBigImage);
        bigImageView.setImageViewFactory(new FrescoImageViewFactory());
        bigImageView.showImage(image.getImage_URI());

//        bigImageView.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CUSTOM);
    }

    public void loadData(){
        SharedPreferences sharedPref = this.getSharedPreferences("NOTE_DATA", Context.MODE_PRIVATE);
        this.isEncrypted = sharedPref.getBoolean(image.getImage_URI().toString(), false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_INFO){
            if (resultCode == Activity.RESULT_OK){
                String str = data.getStringExtra("IMAGE");
                if (str != null){
                    this.image = new Gson().fromJson(str, image.class);
                }
            }
        }
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



    }

}