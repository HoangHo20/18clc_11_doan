package com.example.imagealbum.ui.homePages;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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

import java.text.SimpleDateFormat;
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

    ImageView addBtn, deleteBtn, slideShowBtn, doneBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.date_group_fragement, container, false);
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

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, Global.REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mainHomeViewModelCombine.isInDeleteMode()){
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_gray);
                    addBtn.setVisibility(View.INVISIBLE);
                    slideShowBtn.setVisibility(View.INVISIBLE);
                    doneBtn.setVisibility(View.VISIBLE);
                    mainHomeViewModelCombine.setInDeleteMode(Global.DELETE_MODE_ON);
                    setInSelectedMode(Global.SELECTED_MODE_ON);
                }
                else{
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_blue);
                    addBtn.setVisibility(View.VISIBLE);
                    slideShowBtn.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.INVISIBLE);
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
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_gray);
                    addBtn.setVisibility(View.INVISIBLE);
                    deleteBtn.setVisibility(View.INVISIBLE);
                    doneBtn.setVisibility(View.VISIBLE);
                    mainHomeViewModelCombine.setInSlideShow(Global.SLIDE_SHOW_MODE_ON);
                    setInSelectedMode(Global.SELECTED_MODE_ON);
                }
                else{
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_blue);
                    addBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.INVISIBLE);
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
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_blue);
                    mainHomeViewModelCombine.setInSlideShow(Global.SLIDE_SHOW_MODE_OFF);
                }
                else if(mainHomeViewModelCombine.isInDeleteMode()){
                    slideShowBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_blue);
                    mainHomeViewModelCombine.setInDeleteMode(Global.DELETE_MODE_OFF);
                }
                addBtn.setVisibility(View.VISIBLE);
                doneBtn.setVisibility(View.INVISIBLE);
                setInSelectedMode(Global.SELECTED_MODE_OFF);
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
                System.out.println("MainHomeFragmentCombine: " + "loaded data ...");
                recyclerViewAdapter = new MainHomeRecyclerViewCombine(getActivity(), date_groups, mainHomeViewModelCombine.isInSelectedMode());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                recyclerView.setAdapter(recyclerViewAdapter);
                KLoadingSpin a = requireView().findViewById(R.id.KLoadingSpin);
                a.stopAnimation();
            } else {
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        loadDataByThread();
    }

    private void loadDataByThread() {
        System.out.println("MainHomeFragmentCombine: " + "loading data..");
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
        if(requestCode == Global.REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
                assert data != null;
                Bundle extras = data.getExtras();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mainHomeViewModelCombine.insertToDevice(requireContext(), imageBitmap, "Image_" + timeStamp, "");
            }
        }
    }

    private void setInSelectedMode(boolean mode) {
        mainHomeViewModelCombine.setInSelectedMode(mode);
        recyclerViewAdapter.setInSelectedMode(mode);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
