package com.example.imagealbum.ui.homePages;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagealbum.Global;
import com.example.imagealbum.R;
import com.example.imagealbum.image;
import com.example.imagealbum.ui.home.HomeImageRecyclerView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainHomeRecyclerViewCombine extends RecyclerView.Adapter<MainHomeRecyclerViewCombine.ViewHolder> {
    private static int SEND_IMAGE = 1;
    private boolean inSelectedMode;
    TreeMap<String, ArrayList<image>> date_groups;
    Context context;

    // Constructor for initialization
    public MainHomeRecyclerViewCombine(Context context, TreeMap<String, ArrayList<image>> date_groups, boolean inSelectedMode) {
        this.context = context;
        this.date_groups = date_groups;
        this.inSelectedMode = inSelectedMode;
    }

    @NonNull
    @Override
    public MainHomeRecyclerViewCombine.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_group_item, parent, false);

        // Passing view to ViewHolder
        return new MainHomeRecyclerViewCombine.ViewHolder(view);
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull MainHomeRecyclerViewCombine.ViewHolder holder, int position) {
        //
        Set<String> date_set = date_groups.keySet();
        ArrayList<String> date_arr = new ArrayList<>(date_set) ;
        String date_key = date_arr.get(position);

        //if the images array of that date is not empty (images array of that date may be remove in realtime
        int x = date_groups.get(date_key).size();
        if (date_groups.get(date_key).size() > 0) {
            //Log.d("onBindViewHolder.MainHomeRecyclerViewCombine", date_key);
            holder.mTextView.setText(date_key);

            HomeImageRecyclerView recyclerViewAdapter = new HomeImageRecyclerView(context, date_groups.get(date_key), this.inSelectedMode);
            holder.setRecyclerViewAdapter(recyclerViewAdapter);

            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                holder.mRecyclerView.setLayoutManager(new GridLayoutManager(context, Global.ITEM_SIZE_GRID_LAYOUT_PORTRAIT));
            } else {
                holder.mRecyclerView.setLayoutManager(new GridLayoutManager(context, Global.ITEM_SIZE_GRID_LAYOUT_LANDSCAPE));
            }
        } else  {
            date_groups.remove(date_key);
        }
    }

    @Override
    public int getItemCount() {
        // Returns number of items currently available in Adapter
        return date_groups.size();
    }

    public void ChangeLayout() {

    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        View itemView;
        RecyclerView mRecyclerView;
        HomeImageRecyclerView mRecyclerViewAdapter;
        private boolean isHaveAdapter;

        public ViewHolder(View view) {
            super(view);
            isHaveAdapter = false;
            itemView = view;
            mTextView = (TextView) view.findViewById(R.id.date_group_item_title);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.date_group_item_recycler);
        }

        public void setRecyclerViewAdapter(HomeImageRecyclerView adapter) {
            this.mRecyclerViewAdapter = adapter;
            this.mRecyclerView.setAdapter(adapter);
            this.isHaveAdapter = true;
        }
    }

    public void setInSelectedMode(boolean mode) {
        this.inSelectedMode = mode;
    }
}
