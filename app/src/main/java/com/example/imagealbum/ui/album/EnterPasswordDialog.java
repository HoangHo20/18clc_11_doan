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
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.imagealbum.R;

public class EnterPasswordDialog extends AppCompatDialogFragment {
    private EditText passwordInput;
    private EnterPasswordDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.password_input, null);

        //init edit texts
        passwordInput = (EditText) view.findViewById(R.id.password_input);
        //

        builder.setView(view)
                .setTitle(R.string.please_enter_password)
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
                        String password = passwordInput.getText().toString();

                        if (listener.isPasswordCorrect(password)) {
                            listener.callBackAction();
                            requireDialog().dismiss();
                        } else {
                            Toast.makeText(requireContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (EnterPasswordDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement EnterPasswordDialogListener");
        }
    }

    public interface EnterPasswordDialogListener {
        public boolean isPasswordCorrect(String password);
        public void callBackAction();
    }
}
