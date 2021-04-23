package com.example.imagealbum.ui.home;

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

import java.util.ArrayList;

public class HomeImageRecyclerViewByDate extends RecyclerView.Adapter<HomeImageRecyclerViewByDate.ViewHolder> {
    private static int SEND_IMAGE = 1;
    ArrayList< ArrayList<image> > date_groups;
    Context context;

    // Constructor for initialization
    public HomeImageRecyclerViewByDate(Context context, ArrayList<ArrayList<image> > date_groups) {
        this.context = context;
        this.date_groups = date_groups;
    }

    @NonNull
    @Override
    public HomeImageRecyclerViewByDate.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_group_item, parent, false);

        // Passing view to ViewHolder
        return new HomeImageRecyclerViewByDate.ViewHolder(view);
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull HomeImageRecyclerViewByDate.ViewHolder holder, int position) {
        //
        String date_key = date_groups.get(position).get(0).getDate();
        //Log.d("onBindViewHolder.HomeImageRecyclerViewByDate", date_key);
        holder.mTextView.setText(date_key);

        HomeImageRecyclerView recyclerViewAdapter = new HomeImageRecyclerView(context, date_groups.get(position));
        holder.setRecyclerViewAdapter(recyclerViewAdapter);



        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            holder.mRecyclerView.setLayoutManager(new GridLayoutManager(context, Global.ITEM_SIZE_GRID_LAYOUT_PORTRAIT));
        }
        else{
            holder.mRecyclerView.setLayoutManager(new GridLayoutManager(context, Global.ITEM_SIZE_GRID_LAYOUT_LANDSCAPE));
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
}
