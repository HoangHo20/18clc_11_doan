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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HomeImageRecyclerViewByDate extends RecyclerView.Adapter<HomeImageRecyclerViewByDate.ViewHolder> {
    private static int SEND_IMAGE = 1;
    ArrayList< ArrayList<image> > date_groups;
    Context context;
    ArrayList<HomeImageRecyclerView> adapterList;

    // Constructor for initialization
    public HomeImageRecyclerViewByDate(Context context, ArrayList<ArrayList<image> > date_groups) {
        this.context = context;
        this.date_groups = date_groups;
        adapterList = new ArrayList<>();
        for(ArrayList<image> images: date_groups){
            adapterList.add(new HomeImageRecyclerView(context, images));
        }
    }
    public void setInSelectionMode(boolean mode){
        for(HomeImageRecyclerView adapter: adapterList){
            adapter.setInSelectionMode(mode);
        }
    }

    public void deSelectedAll(){
        for(HomeImageRecyclerView adapter: adapterList){
            adapter.deSelectedAll();
        }
    }

    private ArrayList<image> mergeArrayList(ArrayList<image> listOne, ArrayList<image> listTwo){

        Set<image> set = new LinkedHashSet<>(listOne);
        set.addAll(listTwo);
        List<image> combinedList = new ArrayList<>(set);
        return new ArrayList<>(combinedList);
    }

    public ArrayList<image> getSelectedImages(){
        ArrayList<image> res = new ArrayList<>();

        for(HomeImageRecyclerView adapter: adapterList){
            res = mergeArrayList(res, adapter.getSelectedImages());
        }
        return res;
    }

    public ArrayList<image> deleteSelectedImages(){
        ArrayList<image> res = new ArrayList<>();

        for(HomeImageRecyclerView adapter: adapterList){
            res = mergeArrayList(res, adapter.deleteSelectedImages());
        }

        return res;
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
        // HomeImageRecyclerView recyclerViewAdapter = adapterList.get(position);
        // holder.mRecyclerView.setLayoutManager(new GridLayoutManager(context, 5));
        // holder.mRecyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    public int getItemCount() {
        // Returns number of items currently available in Adapter
        return date_groups != null ? date_groups.size() : 0;
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
