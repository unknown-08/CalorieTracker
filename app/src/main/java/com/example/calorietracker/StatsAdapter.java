package com.example.calorietracker;

import android.app.Activity;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;


import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import com.example.calorietracker.Model.Stats2;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.Myviewholder> {

    ArrayList<Stats2> arr;
    Activity activity;

    public StatsAdapter(ArrayList<Stats2> arr, Activity activity)
    {

        this.arr=arr;
        this.activity=activity;
    }


    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.statitem,parent,false);
        return new StatsAdapter.Myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myviewholder holder, int position) {
        String name =arr.get(position).getFood_name();
        String calorie= arr.get(position).getCalories();
        holder.txt3.setText(name);
        holder.txt4.setText(calorie);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy ");
        String dateString = formatter.format(new Date(Long.parseLong(arr.get(position).getTime())));
        holder.txt2.setText(dateString);
        SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm:ss a");
        String timestring = formatter2.format(new Date(Long.parseLong(arr.get(position).getTime())));

        holder.txt1.setText(timestring);

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public  class Myviewholder extends RecyclerView.ViewHolder{

        TextView txt1,txt2,txt3,txt4;


        public Myviewholder(@NonNull View itemView) {


            super(itemView);
            txt1 =(TextView)itemView.findViewById(R.id.date);
            txt2  =(TextView)itemView.findViewById(R.id.time);
            txt3=(TextView)itemView.findViewById(R.id.fooditem);
            txt4=(TextView)itemView.findViewById(R.id.calorie);







        }
    }


}
