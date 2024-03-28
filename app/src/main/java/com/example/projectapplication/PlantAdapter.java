package com.example.projectapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.ViewHolder> {
    Context context;
    ArrayList<Plant> arraylist;
    OnItemClickListener onItemClickListener;
    public PlantAdapter(Context context, ArrayList<Plant> arraylist){
        this.context=context;
        this.arraylist=arraylist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.plant_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(arraylist.get(position).getName());
        holder.place.setText(arraylist.get(position).getPlace());
        holder.time.setText(arraylist.get(position).getTime());
        holder.water.setText(String.valueOf(arraylist.get(position).getWateramount()));
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arraylist.get(position)));


    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,place,water;
        TextView time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.list_item_name);
            place=itemView.findViewById(R.id.list_item_place);
            water=itemView.findViewById(R.id.list_item_wateramount);
            time=itemView.findViewById(R.id.list_item_time);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(Plant plant);
    }
}
