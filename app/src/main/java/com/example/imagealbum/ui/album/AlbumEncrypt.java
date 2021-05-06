package com.example.imagealbum.ui.album;

import android.graphics.Bitmap;

import com.example.imagealbum.ui.album.database.MediaEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class AlbumEncrypt{

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

    public void encryptAndSave(MediaEntity img, Bitmap bitmap){
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



    public byte[] decrypt(MediaEntity img){
        FileInputStream fis = null;
        byte[] dataDecrypted = null;
        try{
            File file = new File(img.getPath());
            fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            dataDecrypted = decryptByteArray(data);
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

        return dataDecrypted;
    }
}
