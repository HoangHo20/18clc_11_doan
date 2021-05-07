package com.example.imagealbum.ui.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.imagealbum.Global;
import com.example.imagealbum.LocaleHelper;
import com.example.imagealbum.R;
import com.google.android.material.tabs.TabLayout;

public class SettingFragment extends Fragment {
    int theme_id;
    SwitchCompat switchBtn;
    int language_id;
    ImageButton languageBtn;
    TextView notifyText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context contextWrapper = null;
        theme_id = Global.loadLastTheme(requireContext());
        if(theme_id == 0){
            contextWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_ImageAlbum);
        }
        else{
            contextWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_ImageAlbumDark);
        }

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LANG", Context.MODE_PRIVATE);
        language_id = sharedPreferences.getInt("ID", 0);

        LayoutInflater localInflater = inflater.cloneInContext(contextWrapper);

        View view = localInflater.inflate(R.layout.setting_fragment, container, false);

        notifyText = view.findViewById(R.id.setting_fragment_text_notify);
        init_button(view);

        return view;
    }

    private void init_button(View view) {
        switchBtn = view.findViewById(R.id.setting_fragment_switchBtn);
        languageBtn = view.findViewById(R.id.setting_fragment_languageBtn);

        if (theme_id == 1) {//DarkTheme
            switchBtn.setChecked(true);
        }

        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                performChangeTheme(isChecked);

                show_warning_reset_app();
            }
        });

        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performChangeLanguage();
            }
        });
    }

    private void performChangeTheme(boolean isDarkMode) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("THEME", Context.MODE_PRIVATE);
        int temp = sharedPreferences.getInt("ID", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isDarkMode) { //Dark mode selected
            editor.putInt("ID", 1);
        } else { //Light mode
            editor.putInt("ID", 0);
        }
        editor.apply();
    }


    private void performChangeLanguage() {
        // setup the alert builder
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        builder.setTitle("Choose Language");
        String[] languages = {getString(R.string.english_language), getString(R.string.vietnamese_language)};
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.choose_language));
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeToLanguage(which);
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeToLanguage(int langID) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LANG", Context.MODE_PRIVATE);
        //int temp = sharedPreferences.getInt("ID", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ID", langID);
        editor.apply();

        String lang = "";
        if(langID == 0){
            lang = "en";
        }
        else{
            lang = "vi";
        }

        // updating the language for devices above android nougat
        Context context = LocaleHelper.setLocale(getContext(), lang);

        if(langID == 0){
            Toast.makeText(getContext(), "Switch to English", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "Chuyển thành Tiếng Việt", Toast.LENGTH_SHORT).show();
        }

        show_warning_reset_app();
    }

    private void show_warning_reset_app() {
        notifyText.setText(getString(R.string.notify_reset_app_apply_change));
        notifyText.setVisibility(View.VISIBLE);
    }
}
