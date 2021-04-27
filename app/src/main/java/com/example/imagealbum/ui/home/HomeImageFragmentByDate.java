package com.example.imagealbum.ui.home;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagealbum.R;
import com.example.imagealbum.image;
import com.example.imagealbum.slideShow;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class HomeImageFragmentByDate extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private ImageView addBtn;
    private ImageView deleteBtn;
    private ImageView slideShowBtn;
    private ImageView doneBtn;
    private boolean inSlideShow = false;
    private boolean inDeleteMode = false;

    private HomeImageViewModelByDate homeImageViewModelByDate;
    private HomeImageFragmentByDate context;

    private RecyclerView recyclerView;
    private HomeImageRecyclerViewByDate recyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = this;

        View root = inflater.inflate(R.layout.date_group_fragement, container, false);
        recyclerView = root.findViewById(R.id.date_group_fragment_recycler);
        addBtn = root.findViewById(R.id.actionBar_showAlbum_addBtn);
        deleteBtn = root.findViewById(R.id.actionBar_showAlbum_deleteBtn);
        slideShowBtn = root.findViewById(R.id.actionBar_showAlbum_slideShowBtn);
        doneBtn = root.findViewById(R.id.actionBar_showAlbum_doneBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
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
                        homeImageViewModelByDate.deleteImageInDevice(img, getContext());
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

        homeImageViewModelByDate = new ViewModelProvider(this).get(HomeImageViewModelByDate.class);

        homeImageViewModelByDate.getImageMutableLiveData().observe((LifecycleOwner) requireContext(), dateListUpdateObserver);

        homeImageViewModelByDate.loadImageFromDevice(requireContext());
        // TODO: Use the ViewModel
    }

    Observer< ArrayList<ArrayList<image>> > dateListUpdateObserver = new Observer< ArrayList<ArrayList<image>> >() {
        @Override
        public void onChanged(ArrayList<ArrayList<image> > date_groups) {
            recyclerViewAdapter = new HomeImageRecyclerViewByDate(getActivity(), date_groups);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                homeImageViewModelByDate.insertToDevice(getContext(), imageBitmap, "Image_" + timeStamp, "");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        homeImageViewModelByDate.loadImageFromDevice(requireContext());
    }
}
