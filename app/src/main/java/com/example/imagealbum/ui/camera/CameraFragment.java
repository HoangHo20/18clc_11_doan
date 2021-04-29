package com.example.imagealbum.ui.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.imagealbum.Global;
import com.example.imagealbum.R;

public class CameraFragment extends Fragment {

    ImageView takePicture, recordVideo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.camera_fragment, container, false);

        initImageView(root);

        return root;
    }

    private void initImageView(View root) {
        takePicture = root.findViewById(R.id.camera_fragment_takePicture_imageView);
        recordVideo = root.findViewById(R.id.camera_fragment_recordVideo_imageView);

        Glide.with(requireContext())
                .load(Global.getImageFromDrawable(requireContext(), "girl_taking_picture"))
                .placeholder(R.drawable.girl_taking_picture)
                .into(takePicture);

//        Glide.with(requireContext())
//                .asGif()
//                .load(Global.getImageFromDrawable(requireContext(), "rugby"))
//                .placeholder(R.drawable.rugby)
//                .into(recordVideo);

        Glide.with(requireContext())
                .load(Global.getImageFromDrawable(requireContext(), "rugby"))
                .placeholder(R.drawable.rugby)
                .into(recordVideo);

        //request take picture
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //request record video
        recordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}
