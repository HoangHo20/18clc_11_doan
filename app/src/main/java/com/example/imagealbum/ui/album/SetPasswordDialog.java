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

import com.example.imagealbum.R;

public class SetPasswordDialog extends AppCompatDialogFragment {
    private EditText name, password1, password2;
    private SetPasswordDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.password_and_confirm_password_form, null);

        //init textview

        //init edit texts
        password1 = (EditText) view.findViewById(R.id.album_dialog_pass_input1);
        password2 = (EditText) view.findViewById(R.id.album_dialog_pass_input2);
        //

        builder.setView(view)
                .setTitle(R.string.set_password)
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
                        String pass1 = password1.getText().toString();
                        String pass2 = password2.getText().toString();

                        if (checkValid(pass1, pass2)) {
                            listener.setPassword(pass1);
                            requireDialog().dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    private boolean checkValid(String pass1, String pass2) {
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

        //both pass is empty
        return pass1 != null && pass1.isEmpty() && pass2 != null && pass2.isEmpty();
    }

    public void setOnSetPasswordDialogListener(SetPasswordDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (SetPasswordDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement SetPasswordDialogListener");
        }
    }

    public interface SetPasswordDialogListener {
        void setPassword(String password);
    }
}
