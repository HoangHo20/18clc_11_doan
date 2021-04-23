package com.example.imagealbum;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivityNavigation extends AppCompatActivity {
    public static final int STORAGE_PERMISSION = 100;
    public static final int WALLPAPER_PERMISSION = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getPermission();

    }

    private void innit(){
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}

        setContentView(R.layout.activity_main_navigation);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int SET_WALLPAPER = ContextCompat.checkSelfPermission(this, Manifest.permission.SET_WALLPAPER);
        if (requestCode == STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    innit();
            }
            else{
                finish();
            }

            if(SET_WALLPAPER != 0){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SET_WALLPAPER}, WALLPAPER_PERMISSION);
            }
        }
        else if(requestCode == WALLPAPER_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
            else{
                finish();
            }
        }
    }

    public boolean isStoragePermissionGranted() {
        int ACCESS_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int SET_WALLPAPER = ContextCompat.checkSelfPermission(this, Manifest.permission.SET_WALLPAPER);
        if (ACCESS_EXTERNAL_STORAGE == 0 && SET_WALLPAPER == 0){
            return true;
        }
        return false;
    }

    public void getPermission(){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SET_WALLPAPER}, WALLPAPER_PERMISSION);
    }
}
