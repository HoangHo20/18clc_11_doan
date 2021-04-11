package com.example.imagealbum;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CustomGridView extends BaseAdapter {
    private Context context;
    private int idLayout;
    private List<Album> listAlbum;
    private int positionSelect = -1;
    private boolean inDeleteMode = false;

    public CustomGridView(Context context, int idLayout, List<Album> listAlbum) {
        this.context = context;
        this.idLayout = idLayout;
        this.listAlbum= listAlbum;
    }

    public void changeDeleteMode(){
        this.inDeleteMode = !this.inDeleteMode;
    }

    @Override
    public int getCount() {
        if (listAlbum.size() != 0 && !listAlbum.isEmpty()) {
            return listAlbum.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        GridView grid = (GridView)parent;
        int size = grid.getColumnWidth();

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_gridview_item, null);
        }

        TextView albumName = (TextView) convertView.findViewById(R.id.imageButton_text);
        ImageButton imageBtn = (ImageButton) convertView.findViewById(R.id.imageButton);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inDeleteMode){
                    listAlbum.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
        albumName.setText(listAlbum.get(position).getName());

        return convertView;

    }

}
