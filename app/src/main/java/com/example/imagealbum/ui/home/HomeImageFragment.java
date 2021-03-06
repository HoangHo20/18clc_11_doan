package com.example.imagealbum.ui.home;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.imagealbum.R;
import com.example.imagealbum.image;
import com.example.imagealbum.slideShow;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class HomeImageFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_VIDEO_CAPTURE = 3;
    private HomeImageViewModel homeImageViewModel;
    private HomeImageFragment context;

    private RecyclerView recyclerView;
    private HomeImageRecyclerView recyclerViewAdapter;

    private ImageView addBtn;
    private ImageView deleteBtn;
    private ImageView slideShowBtn;
    private ImageView doneBtn;
    private boolean inSlideShow = false;
    private boolean inDeleteMode = false;
    private static int SEND_IMAGE = 1;
    private String currentPhotoPath = "none";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = this;

        View root = inflater.inflate(R.layout.home_fragment, container, false);
        recyclerView = root.findViewById(R.id.imagegallery);

        recyclerView = root.findViewById(R.id.imagegallery);
        addBtn = root.findViewById(R.id.actionBar_showAlbum_addBtn);
        deleteBtn = root.findViewById(R.id.actionBar_showAlbum_deleteBtn);
        slideShowBtn = root.findViewById(R.id.actionBar_showAlbum_slideShowBtn);
        doneBtn = root.findViewById(R.id.actionBar_showAlbum_doneBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // take picture
//                dispatchTakePictureIntent();

                // record video

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inDeleteMode){
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_selected);
                    addBtn.setVisibility(View.INVISIBLE);
                    slideShowBtn.setVisibility(View.INVISIBLE);
                    doneBtn.setVisibility(View.VISIBLE);
                    inDeleteMode = !inDeleteMode;
                    recyclerViewAdapter.setInSelectionMode(true);
                }
                else{
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_unselect);
                    addBtn.setVisibility(View.VISIBLE);
                    slideShowBtn.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.INVISIBLE);
                    inDeleteMode = !inDeleteMode;
                    recyclerViewAdapter.setInSelectionMode(false);
                    recyclerViewAdapter.deSelectedAll();
                }
            }
        });

        slideShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inSlideShow){
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_selected);
                    addBtn.setVisibility(View.INVISIBLE);
                    deleteBtn.setVisibility(View.INVISIBLE);
                    doneBtn.setVisibility(View.VISIBLE);
                    inSlideShow = !inSlideShow;
                    recyclerViewAdapter.setInSelectionMode(true);
                }
                else{
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_unselect);
                    addBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.INVISIBLE);
                    inSlideShow = !inSlideShow;
                    recyclerViewAdapter.setInSelectionMode(false);
                    recyclerViewAdapter.deSelectedAll();
                }

            }
        });



        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inSlideShow){
                    ArrayList<image> selectedImgs = recyclerViewAdapter.getSelectedImages();
                    if (selectedImgs.size() > 1){
                        String json = new Gson().toJson(selectedImgs);
                        Intent intent = new Intent(getContext(), slideShow.class);
                        intent.putExtra("IMAGE", json);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getContext(), R.string.no_photo_selected_slideShow, Toast.LENGTH_SHORT).show();
                    }
                }
                else if(inDeleteMode){
                    ArrayList<image> deleteImages = recyclerViewAdapter.deleteSelectedImages();

                    for(image img: deleteImages) {
                        homeImageViewModel.deleteImageInDevice(img, getContext());
                    }
                }
                if(inSlideShow){
                    deleteBtn.setVisibility(View.VISIBLE);
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_unselect);
                    inSlideShow = !inSlideShow;
                }
                else if(inDeleteMode){
                    slideShowBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_unselect);
                    inDeleteMode = !inDeleteMode;
                }
                addBtn.setVisibility(View.VISIBLE);
                doneBtn.setVisibility(View.INVISIBLE);
                recyclerViewAdapter.setInSelectionMode(false);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        homeImageViewModel = new ViewModelProvider(this).get(HomeImageViewModel.class);

        homeImageViewModel.getImageMutableLiveData().observe((LifecycleOwner) requireContext(), imageListUpdateObserver);

        homeImageViewModel.loadImageFromDevice(requireContext());
        // TODO: Use the ViewModel
    }

    Observer<ArrayList<image>> imageListUpdateObserver = new Observer<ArrayList<image>>() {
        @Override
        public void onChanged(ArrayList<image> images) {
            recyclerViewAdapter = new HomeImageRecyclerView(getActivity(), images);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Receive, HomeImageFragment");
        if (requestCode == SEND_IMAGE){
            if (resultCode == RESULT_OK){
                String img = data.getStringExtra("IMAGE");
                image new_img = new Gson().fromJson(img, image.class);
                int pos = Integer.parseInt(data.getStringExtra("POS"));
//                imageList.set(pos, new_img);
//                adapter.notifyDataSetChanged();
            }
            else if(requestCode == Activity.RESULT_CANCELED){
                String img = data.getStringExtra("IMAGE");
                image new_img = new Gson().fromJson(img, image.class);
                int pos = Integer.parseInt(data.getStringExtra("POS"));
                homeImageViewModel.deleteImageInDevice(new_img, getContext());
            }
        }
        else if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
                galleryAddPic();
            }
        }
        else if(resultCode == REQUEST_VIDEO_CAPTURE){
            if(resultCode == RESULT_OK){
                Toast.makeText(getContext(), R.string.video_capture_success, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        homeImageViewModel.loadImageFromDevice(requireContext());
    }

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

    public void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
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
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
            homeImageViewModel.insertToDevice(getContext(), bitmap, "Image_" + formatDateTime, "");
        }

    }

}