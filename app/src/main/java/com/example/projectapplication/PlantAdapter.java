package com.example.projectapplication;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.ViewHolder> {
    Context context;
    ArrayList<Plant> arraylist;
    OnItemClickListener onItemClickListener;
    private OnItemClickListener deleteListener;

    public PlantAdapter(Context context, ArrayList<Plant> arraylist){
        this.context=context;
        this.arraylist=arraylist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("XXXXXXXXXXX", "+onActivityResult");
        View view = LayoutInflater.from(context).inflate(R.layout.plant_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("XXXXXXXXXXX", "+onBindViewHolder");
        Plant plant = arraylist.get(position);  // Define the local variable 'plant' here
        holder.name.setText(arraylist.get(position).getName());
        holder.place.setText(arraylist.get(position).getPlace());
        holder.time.setText(arraylist.get(position).getTime());
        holder.water.setText(String.valueOf(arraylist.get(position).getWateramount()));
        if (plant.getImageUrl() != null && !plant.getImageUrl().isEmpty()) {
            Uri imageUri = Uri.parse(plant.getImageUrl());
            holder.imageView.setImageURI(imageUri);
        }
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(position, plant);
            }
        });
        holder.delete.setOnClickListener(v->deleteListener.onClick(position, plant));

    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,place,water;
        TextView time;
        public ImageView imageView, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.list_item_name);
            place=itemView.findViewById(R.id.list_item_place);
            water=itemView.findViewById(R.id.list_item_wateramount);
            time=itemView.findViewById(R.id.list_item_time);
            imageView=itemView.findViewById(R.id.imageofplant);
            delete=itemView.findViewById(R.id.delete);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setDeleteClickListener(OnItemClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public interface OnItemClickListener {
        void onClick(int position, Plant plant);
    }
    public void updateData(ArrayList<Plant> plants) {
        this.arraylist = plants;
        notifyDataSetChanged();
    }

}
