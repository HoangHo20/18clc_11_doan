package com.example.imagealbum.ui.homePages;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagealbum.Global;
import com.example.imagealbum.R;
import com.example.imagealbum.image;
import com.example.imagealbum.slideShow;
import com.example.kloadingspin.KLoadingSpin;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static android.app.Activity.RESULT_OK;

// TODO: load delete, slideshow selected mode when reload fragment
public class MainHomeFragmentCombine extends Fragment {
    private MainHomeViewModelCombine mainHomeViewModelCombine;

    private RecyclerView recyclerView;
    private MainHomeRecyclerViewCombine recyclerViewAdapter;

    ImageView addBtn, deleteBtn, slideShowBtn, doneBtn, setThemeBtn;

    private String currentPhotoPath = "none";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Context contextWrapper = null;
        int theme_id = Global.loadLastTheme(getContext());
        System.out.println(theme_id);
        if(theme_id == 0){
            contextWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_ImageAlbum);
        }
        else{
            contextWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_ImageAlbumDark);
        }
        LayoutInflater localInflater = inflater.cloneInContext(contextWrapper);

        View root = localInflater.inflate(R.layout.date_group_fragement, container, false);
        recyclerView = root.findViewById(R.id.date_group_fragment_recycler);

        //if there is no data loaded yet
        if (recyclerViewAdapter == null) {
            KLoadingSpin a = root.findViewById(R.id.KLoadingSpin);
            a.startAnimation();
            a.setIsVisible(true);
        }

        init_button(root);

        return root;
    }

    private void init_button(View root) {
        addBtn = (ImageView) root.findViewById(R.id.actionBar_showAlbum_addBtn);
        deleteBtn = (ImageView) root.findViewById(R.id.actionBar_showAlbum_deleteBtn);
        slideShowBtn = (ImageView) root.findViewById(R.id.actionBar_showAlbum_slideShowBtn);
        doneBtn = (ImageView) root.findViewById(R.id.actionBar_showAlbum_doneBtn);
        setThemeBtn = (ImageView) root.findViewById(R.id.actionBar_showAlbum_setThemeBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                try {
//                    startActivityForResult(takePictureIntent, Global.REQUEST_IMAGE_CAPTURE);
//                } catch (ActivityNotFoundException e) {
//                    e.printStackTrace();
//                }
                dispatchTakePictureIntent();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mainHomeViewModelCombine.isInDeleteMode()){
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_selected);
                    addBtn.setVisibility(View.INVISIBLE);
                    slideShowBtn.setVisibility(View.INVISIBLE);
                    doneBtn.setVisibility(View.VISIBLE);
                    mainHomeViewModelCombine.setInDeleteMode(Global.DELETE_MODE_ON);
                    setThemeBtn.setVisibility(View.INVISIBLE);
                    setInSelectedMode(Global.SELECTED_MODE_ON);
                }
                else{
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_unselect);
                    addBtn.setVisibility(View.VISIBLE);
                    slideShowBtn.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.INVISIBLE);
                    setThemeBtn.setVisibility(View.VISIBLE);
                    mainHomeViewModelCombine.setInDeleteMode(Global.DELETE_MODE_OFF);
                    setInSelectedMode(Global.SELECTED_MODE_OFF);
                    mainHomeViewModelCombine.deSelectedAll();
                }
            }
        });

        slideShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mainHomeViewModelCombine.isInSlideShow()){
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_selected);
                    addBtn.setVisibility(View.INVISIBLE);
                    deleteBtn.setVisibility(View.INVISIBLE);
                    doneBtn.setVisibility(View.VISIBLE);
                    mainHomeViewModelCombine.setInSlideShow(Global.SLIDE_SHOW_MODE_ON);
                    setThemeBtn.setVisibility(View.INVISIBLE);
                    setInSelectedMode(Global.SELECTED_MODE_ON);
                }
                else{
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_unselect);
                    addBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.INVISIBLE);
                    setThemeBtn.setVisibility(View.VISIBLE);
                    mainHomeViewModelCombine.setInSlideShow(Global.SLIDE_SHOW_MODE_OFF);
                    setInSelectedMode(Global.SELECTED_MODE_OFF);
                    mainHomeViewModelCombine.deSelectedAll();
                }

            }
        });



        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainHomeViewModelCombine.isInSlideShow()){
                    ArrayList<image> selectedImgs = mainHomeViewModelCombine.getSelectedImages();
                    if (selectedImgs.size() > 1){
                        String json = new Gson().toJson(selectedImgs);
                        Intent intent = new Intent(requireContext(), slideShow.class);
                        intent.putExtra("IMAGE", json);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getContext(), R.string.no_photo_selected_slideShow, Toast.LENGTH_SHORT).show();
                    }
                    mainHomeViewModelCombine.deSelectedAll();
                }
                else if(mainHomeViewModelCombine.isInDeleteMode()){
                    ArrayList<image> deleteImages = mainHomeViewModelCombine.getSelectedImages();

                    for(image img: deleteImages) {
                        mainHomeViewModelCombine.deleteImageInDevice(img, requireContext());
                    }

                    loadDataByThread();
                }
                if(mainHomeViewModelCombine.isInSlideShow()){
                    deleteBtn.setVisibility(View.VISIBLE);
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_unselect);
                    mainHomeViewModelCombine.setInSlideShow(Global.SLIDE_SHOW_MODE_OFF);
                }
                else if(mainHomeViewModelCombine.isInDeleteMode()){
                    slideShowBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_unselect);
                    mainHomeViewModelCombine.setInDeleteMode(Global.DELETE_MODE_OFF);
                }
                addBtn.setVisibility(View.VISIBLE);
                doneBtn.setVisibility(View.INVISIBLE);
                setThemeBtn.setVisibility(View.VISIBLE);
                setInSelectedMode(Global.SELECTED_MODE_OFF);
            }
        });


        setThemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("THEME", Context.MODE_PRIVATE);
                int temp = sharedPreferences.getInt("ID", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("ID", (temp + 1) % 2);
                editor.apply();

                // Refresh fragment to apply new theme

            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewAdapter = null;

        mainHomeViewModelCombine = new ViewModelProvider(this).get(MainHomeViewModelCombine.class);

        mainHomeViewModelCombine.getImageMutableLiveData().observe((LifecycleOwner) requireContext(), dateListUpdateObserver);

        // TODO: Use the ViewModel
    }

    Observer<TreeMap<String, ArrayList<image>>> dateListUpdateObserver = new Observer< TreeMap<String, ArrayList<image>> >() {
        @Override
        public void onChanged(TreeMap<String, ArrayList<image>> date_groups) {
            if (recyclerViewAdapter == null) {
//                System.out.println("MainHomeFragmentCombine: " + "loaded data ...");
                recyclerViewAdapter = new MainHomeRecyclerViewCombine(getActivity(), date_groups, mainHomeViewModelCombine.isInSelectedMode());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                recyclerView.setAdapter(recyclerViewAdapter);
                KLoadingSpin a = requireView().findViewById(R.id.KLoadingSpin);
                a.stopAnimation();
            } else {
                recyclerViewNotifyDataSetChanged();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        loadDataByThread();
    }

    private void loadDataByThread() {
//        System.out.println("MainHomeFragmentCombine: " + "loading data..");
        LoadDataThread loadDataThread = new LoadDataThread();
        loadDataThread.start();
    }

    public class LoadDataThread extends Thread {

        public void run(){
            mainHomeViewModelCombine.loadImageFromDevice(requireContext());
        }
    }

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
        }
    }

    private void setInSelectedMode(boolean mode) {
        mainHomeViewModelCombine.setInSelectedMode(mode);
        recyclerViewAdapter.setInSelectedMode(mode);
        recyclerViewNotifyDataSetChanged();
    }

    private void recyclerViewNotifyDataSetChanged() {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                while (mainHomeViewModelCombine.isLoadingData());
//                recyclerViewAdapter.notifyDataSetChanged();
//            }
//        };
//        Thread thread = new Thread(runnable);
//        thread.start();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                while (mainHomeViewModelCombine.isLoadingData());
                recyclerView.getRecycledViewPool().clear();
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
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
            mainHomeViewModelCombine.insertToDevice(getContext(), bitmap, "Image_" + formatDateTime, "");
        }

    }
}
