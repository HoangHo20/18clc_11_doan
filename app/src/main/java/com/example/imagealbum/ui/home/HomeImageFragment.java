package com.example.imagealbum.ui.home;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.imagealbum.R;
import com.example.imagealbum.image;
import com.example.imagealbum.showAlbum;
import com.example.imagealbum.slideShow;
import com.google.gson.Gson;

import java.util.ArrayList;

public class HomeImageFragment extends Fragment {

    private HomeImageViewModel homeImageViewModel;
    private HomeImageFragment context;

    private RecyclerView recyclerView;
    private HomeImageRecyclerView recyclerViewAdapter;

    private static int SEND_IMAGE = 1;
    private static int SLIDE_SHOW = 3;
    private ImageView addBtn;
    private ImageView deleteBtn;
    private ImageView slideShowBtn;
    private ImageView doneBtn;
    private boolean inSlideShow = false;
    private boolean inDeleteMode = false;

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

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inDeleteMode){
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_gray);
                    addBtn.setVisibility(View.INVISIBLE);
                    slideShowBtn.setVisibility(View.INVISIBLE);
                    doneBtn.setVisibility(View.VISIBLE);
                    inDeleteMode = !inDeleteMode;
                    recyclerViewAdapter.setInSelectionMode(true);
                }
                else{
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_blue);
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
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_gray);
                    addBtn.setVisibility(View.INVISIBLE);
                    deleteBtn.setVisibility(View.INVISIBLE);
                    doneBtn.setVisibility(View.VISIBLE);
                    inSlideShow = !inSlideShow;
                    recyclerViewAdapter.setInSelectionMode(true);
                }
                else{
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_blue);
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
                    recyclerViewAdapter.deleteSelectedImages();
                }
                if(inSlideShow){
                    deleteBtn.setVisibility(View.VISIBLE);
                    slideShowBtn.setImageResource(R.drawable.ic_baseline_slideshow_24_blue);
                    inSlideShow = !inSlideShow;
                }
                else if(inDeleteMode){
                    slideShowBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setImageResource(R.drawable.ic_baseline_delete_24_blue);
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
    public void onResume() {
        super.onResume();
        homeImageViewModel.loadImageFromDevice(requireContext());
    }
}