package com.example.imagealbum.ui.homePages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.imagealbum.Global;
import com.example.imagealbum.R;
import com.example.imagealbum.ui.home.HomeImageFragment;
import com.example.imagealbum.ui.home.HomeImageFragmentByDate;
import com.example.imagealbum.ui.home.HomeVideoFragment;
import com.google.android.material.tabs.TabLayout;

public class MainHomeFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context contextWrapper = null;
        int theme_id = Global.loadLastTheme(getContext());
        if(theme_id == 0){
            contextWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_ImageAlbum);
        }
        else{
            contextWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_ImageAlbumDark);
        }
        LayoutInflater localInflater = inflater.cloneInContext(contextWrapper);
        View view = localInflater.inflate(R.layout.tabs_fragment, container, false);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        TabsFragmentAdapter adapter = new TabsFragmentAdapter(getChildFragmentManager());
//        adapter.addFragment(new HomeImageFragment(), "Photos");
//        adapter.addFragment(new HomeImageFragmentByDate(), "Photos");
//        adapter.addFragment(new HomeVideoFragment(), "Videos");
        adapter.addFragment(new MainHomeFragmentCombine(), "All");
        adapter.addFragment(new HomeImageFragment(), "Photos");
        adapter.addFragment(new HomeVideoFragment(), "Videos");
        viewPager.setAdapter(adapter);
    }

}
