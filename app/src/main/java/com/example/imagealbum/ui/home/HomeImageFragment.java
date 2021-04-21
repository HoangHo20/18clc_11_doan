package com.example.imagealbum.ui.home;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imagealbum.R;
import com.example.imagealbum.image;

import java.util.ArrayList;

public class HomeImageFragment extends Fragment {

    private HomeImageViewModel homeImageViewModel;
    private HomeImageFragment context;

    private RecyclerView recyclerView;
    private HomeImageRecyclerView recyclerViewAdapter;

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
        homeImageViewModel = new ViewModelProvider(this, factory).get(HomeImageViewModel.class);

        homeImageViewModel.getImageMutableLiveData().observe((LifecycleOwner) requireContext(), imageListUpdateObserver);

        homeImageViewModel.loadImageFromDevice(requireContext());
        // TODO: Use the ViewModel
    }

    Observer<ArrayList<image>> imageListUpdateObserver = new Observer<ArrayList<image>>() {
        @Override
        public void onChanged(ArrayList<image> images) {
            recyclerViewAdapter = new HomeImageRecyclerView(getActivity(), images);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    };
}