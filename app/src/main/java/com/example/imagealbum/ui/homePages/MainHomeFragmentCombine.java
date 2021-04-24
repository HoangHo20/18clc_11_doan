package com.example.imagealbum.ui.homePages;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagealbum.R;
import com.example.imagealbum.image;
import com.example.kloadingspin.KLoadingSpin;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MainHomeFragmentCombine extends Fragment {

    private MainHomeViewModelCombine mainHomeViewModelCombine;
    private MainHomeFragmentCombine context;

    private RecyclerView recyclerView;
    private MainHomeRecyclerViewCombine recyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = this;

        View root = inflater.inflate(R.layout.date_group_fragement, container, false);
        recyclerView = root.findViewById(R.id.date_group_fragment_recycler);

        //if there is no data loaded yet
        if (recyclerViewAdapter == null) {
            KLoadingSpin a = root.findViewById(R.id.KLoadingSpin);
            a.startAnimation();
            a.setIsVisible(true);
        }

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
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
                recyclerViewAdapter = new MainHomeRecyclerViewCombine(getActivity(), date_groups);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                recyclerView.setAdapter(recyclerViewAdapter);
                KLoadingSpin a = requireView().findViewById(R.id.KLoadingSpin);
                a.stopAnimation();
            } else {
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("MainHomeFragmentCombine: " + "loading data..");
        LoadDataThread loadDataThread = new LoadDataThread();
        loadDataThread.start();
    }

    public class LoadDataThread extends Thread {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        public void run(){
            mainHomeViewModelCombine.loadImageFromDevice(requireContext());
        }
    }
}
