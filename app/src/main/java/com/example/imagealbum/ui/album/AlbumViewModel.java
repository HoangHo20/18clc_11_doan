package com.example.imagealbum.ui.album;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.imagealbum.Album;
import com.example.imagealbum.Global;
import com.example.imagealbum.ui.album.database.AlbumEntity;
import com.example.imagealbum.ui.album.database.MediaEntity;

import java.util.ArrayList;
import java.util.List;

public class AlbumViewModel extends AndroidViewModel {
    private MutableLiveData<List<AlbumEntity>> LiveData;
    private List<AlbumEntity> albums;
    private AlbumModel model;

    public AlbumViewModel(Application application) {
        super(application);
        LiveData = new MutableLiveData<>();
        albums = new ArrayList<>();

        init_model();
    }

    private void init_model() {
        model = new AlbumModel(getApplication());

        //if Favorite album does not exist, create it
        List<AlbumEntity> res =  model.getAlbumByName(Global.FAVORITE_ALBUM.name);
        if (res == null || res.isEmpty()) {
            AlbumEntity favorite = new AlbumEntity(Global.FAVORITE_ALBUM.name);
            model.insertAlbum(favorite);
        }
    }

    public MutableLiveData<List<AlbumEntity>> getAlbumsLiveData() {
        return this.LiveData;
    }

    public void loadData() {
        albums = model.getAllAlbums();

        getAlbumAvatar();

        LiveData.postValue(albums);
    }

    private void getAlbumAvatar() {
        for(AlbumEntity a : albums) {
            MediaEntity avatar = model.getOneMediaByAlbumID(a.getID());
            if (avatar != null) {
                a.setAvatar(avatar);
            }
        }
    }

    public boolean isAlbumNameExist(String albumName) {
        List<AlbumEntity> res = model.getAlbumByName(albumName);

        return res != null && !res.isEmpty();
    }

    public void createAlbum(String albumName, String password) {
        if (!isAlbumNameExist(albumName)) {
            AlbumEntity album = new AlbumEntity(albumName, password);

            model.insertAlbum(album);

            //refresh data
            loadData();
        }
    }
}
