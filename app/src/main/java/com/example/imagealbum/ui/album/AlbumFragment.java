package com.example.imagealbum.ui.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagealbum.Global;
import com.example.imagealbum.R;
import com.example.imagealbum.ui.album.database.AlbumEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AlbumFragment extends Fragment
        implements CreateAlbumDialog.CreateAlbumDialogListener,
        EnterPasswordDialog.EnterPasswordDialogListener,
        AlbumRecyclerAdapter.RecyclerViewAdapterListener {
    private AlbumViewModel albumViewModel;

    private RecyclerView recyclerView;
    private AlbumRecyclerAdapter recyclerAdapter = null;

    private FloatingActionButton fab;

    Observer<List<AlbumEntity>> albumsUpdateObserver;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Context contextWrapper = null;
        int theme_id = Global.loadLastTheme(requireContext());
        System.out.println(theme_id);
        if(theme_id == 0){
            contextWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_ImageAlbum);
        }
        else{
            contextWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_ImageAlbumDark);
        }
        LayoutInflater localInflater = inflater.cloneInContext(contextWrapper);

        View root = localInflater.inflate(R.layout.album_fragment, container, false);

        init_floatingButton(root);

        init_recyclerview(root);

        init_viewmodel();

        return root;
    }

    private void init_floatingButton(View view) {
        fab = view.findViewById(R.id.album_fragment_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateAlbumDialog();
            }
        });
    }

    private void init_recyclerview(View view) {
        recyclerView = view.findViewById(R.id.albumRecyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                }
            }
        });
    }

    private void init_viewmodel() {
        albumViewModel = new ViewModelProvider(this).get(AlbumViewModel.class);

        albumsUpdateObserver = new Observer<List<AlbumEntity>>() {
            @Override
            public void onChanged(List<AlbumEntity> albumEntities) {
                if (recyclerAdapter == null) {
                    recyclerAdapter = new AlbumRecyclerAdapter(requireContext(), albumEntities);
                    recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), Global.ITEM_SIZE_GRID_LAYOUT_ALBUM));
                    recyclerView.setAdapter(recyclerAdapter);
                } else {
                    recyclerAdapter.setDataAndUpdate(albumEntities);
                }
            }
        };

        albumViewModel.getAlbumsLiveData().observe((LifecycleOwner) requireContext(), albumsUpdateObserver);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuInflater inflater1 = requireActivity().getMenuInflater();
        inflater1.inflate(R.menu.actionbar_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: LOAD albums
        albumViewModel.loadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    // --------------------- Create Album dialog ----------------------
    private void openCreateAlbumDialog() {
        CreateAlbumDialog dialog = new CreateAlbumDialog();
        dialog.setTargetFragment(AlbumFragment.this, Global.REQUEST_CREATE_ALBUM);
        dialog.show(getParentFragmentManager() , "create album dialog");
    }

    @Override
    public boolean isAlbumNameExist(String albumName) {
        return albumViewModel.isAlbumNameExist(albumName);
    }

    @Override
    public void createAlbum(String name, String password) {
        albumViewModel.createAlbum(name, password);
    }

    // ----------------- Enter Album password dialog ------------------
    @Override
    public boolean isPasswordCorrect(String password) {
        return false;
    }

    @Override
    public void openAlbum() {

    }

    // ----------------- Recycler item click ------------------
    @Override
    public void onRecyclerAdapterClick(AlbumEntity album) {
        // TODO: show album
        Toast.makeText(requireContext(), album.getName(), Toast.LENGTH_SHORT).show();
    }
}
