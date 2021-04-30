package com.example.imagealbum.ui.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.imagealbum.Global;
import com.example.imagealbum.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment {

    ImageView takePicture, recordVideo;
    private String currentPhotoPath = "none";
    CameraModel cameraModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.camera_fragment, container, false);

        initImageView(root);

        cameraModel = new CameraModel();

        return root;
    }

    private void initImageView(View root) {
        takePicture = root.findViewById(R.id.camera_fragment_takePicture_imageView);
        recordVideo = root.findViewById(R.id.camera_fragment_recordVideo_imageView);

        Glide.with(requireContext())
                .load(Global.getImageFromDrawable(requireContext(), "girl_taking_picture"))
                .placeholder(R.drawable.girl_taking_picture)
                .into(takePicture);

//        Glide.with(requireContext())
//                .asGif()
//                .load(Global.getImageFromDrawable(requireContext(), "rugby"))
//                .placeholder(R.drawable.rugby)
//                .into(recordVideo);

        Glide.with(requireContext())
                .load(Global.getImageFromDrawable(requireContext(), "rugby"))
                .placeholder(R.drawable.rugby)
                .into(recordVideo);

        //request take picture
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        //request record video
        recordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });
    }

    // ------------- Take Picture -------------
    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir =getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileproviderx",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Global.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void galleryAddPic() {
        File f = new File(currentPhotoPath);
        FileInputStream fis = null;
        byte[] data = null;
        try{
            fis = new FileInputStream(f);
            data = new byte[(int) f.length()];
            fis.read(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                fis.close();
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }
        Bitmap bitmap = null;
        if(data != null){
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        if(bitmap != null){
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formatDateTime = now.format(format);
            cameraModel.insertToDevice(getContext(), bitmap, "Image_" + formatDateTime, "");
        }

    }

    // ------------- Record video -------------
    public void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, Global.REQUEST_VIDEO_CAPTURE);
        }
    }

    //on activity camera return result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Receive, HomeImageFragment");
        if(requestCode == Global.REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
//                Bundle extras = data.getExtras();
//                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                homeImageViewModel.insertToDevice(getContext(), imageBitmap, "Image_" + timeStamp, "");
                galleryAddPic();
            }
        } else if(resultCode == Global.REQUEST_VIDEO_CAPTURE){
            if(resultCode == RESULT_OK){
                Toast.makeText(getContext(), R.string.video_capture_success, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
