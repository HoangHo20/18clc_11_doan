package com.example.imagealbum.ui.home;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagealbum.R;
import com.example.imagealbum.image;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeImageFragmentByDate extends Fragment {

    private HomeImageViewModelByDate homeImageViewModelByDate;
    private HomeImageFragmentByDate context;

    private RecyclerView recyclerView;
    private HomeImageRecyclerViewByDate recyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = this;

        View root = inflater.inflate(R.layout.date_group_fragement, container, false);
        recyclerView = root.findViewById(R.id.date_group_fragment_recycler);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerViewAdapter = null;

        homeImageViewModelByDate = new ViewModelProvider(this).get(HomeImageViewModelByDate.class);

        homeImageViewModelByDate.loadImageFromDevice(requireContext());

        homeImageViewModelByDate.getImageMutableLiveData().observe((LifecycleOwner) requireContext(), dateListUpdateObserver);

        // TODO: Use the ViewModel
    }

    Observer< ArrayList<ArrayList<image>> > dateListUpdateObserver = new Observer< ArrayList<ArrayList<image>> >() {
        @Override
        public void onChanged(ArrayList<ArrayList<image> > date_groups) {
            if (recyclerViewAdapter == null) {
                recyclerViewAdapter = new HomeImageRecyclerViewByDate(getActivity(), date_groups);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                recyclerView.setAdapter(recyclerViewAdapter);
            } else {
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        homeImageViewModelByDate.loadImageFromDevice(requireContext());
    }
}
