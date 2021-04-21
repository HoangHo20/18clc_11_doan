package com.example.imagealbum.ui.home;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imagealbum.R;
import com.example.imagealbum.RecyclerAdapter;
import com.example.imagealbum.image;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private HomeFragment context;

    private RecyclerView recyclerView;
    private HomeRecyclerView recyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = this;

        View root = inflater.inflate(R.layout.home_fragment, container, false);
        recyclerView = root.findViewById(R.id.imagegallery);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication());
        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        homeViewModel.getImageMutableLiveData().observe((LifecycleOwner) requireContext(), imageListUpdateObserver);

        homeViewModel.loadImageFromDevice(requireContext());
        // TODO: Use the ViewModel
    }

    Observer<ArrayList<image>> imageListUpdateObserver = new Observer<ArrayList<image>>() {
        @Override
        public void onChanged(ArrayList<image> images) {
            recyclerViewAdapter = new HomeRecyclerView(requireContext(), images);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    };
}