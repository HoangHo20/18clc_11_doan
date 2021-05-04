package com.example.imagealbum.ui.album;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.example.imagealbum.R;
import com.example.imagealbum.viewImage;

public class CreateAlbumDialog extends AppCompatDialogFragment {
    private EditText name, password1, password2;
    private CreateAlbumDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.create_album_dialog, null);

        //init edit texts
        name = (EditText) view.findViewById(R.id.album_dialog_name_input);
        password1 = (EditText) view.findViewById(R.id.album_dialog_pass_input1);
        password2 = (EditText) view.findViewById(R.id.album_dialog_pass_input2);
        //

        builder.setView(view)
                .setTitle(R.string.create_album)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.done, null);

        //init positive button click
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String albumName = name.getText().toString();
                        String pass1 = password1.getText().toString();
                        String pass2 = password2.getText().toString();

                        if (checkValid(albumName, pass1, pass2)) {
                            listener.createAlbum(albumName, pass1);
                            requireDialog().dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    private boolean checkValid(String albumName, String pass1, String pass2) {
        boolean isAlbumExist = listener.isAlbumNameExist(albumName);
        if (isAlbumExist) {
            Toast.makeText(requireContext(), R.string.warning_album_name_exist, Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isPasswordMatch = checkPassword(pass1, pass2);
        if (!isPasswordMatch) {
            Toast.makeText(requireContext(), R.string.warning_password_not_match, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean checkPassword(String pass1, String pass2) {
        if (pass1 != null && !pass1.isEmpty()
                && pass2 != null && !pass2.isEmpty()) {
            return pass1.equals(pass2);
        }

        return false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (CreateAlbumDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement CreateAlbumDialogListener");
        }
    }

    public interface CreateAlbumDialogListener {
        boolean isAlbumNameExist(String albumName);
        void createAlbum(String name, String password);
    }
}
