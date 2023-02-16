package com.example.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {

    private List<String> myList;
    private List<Integer> idList;
    private int rowLayout;
    private Context mContext;
    private String typeT;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView myName;
        public ImageView Pic;
        public TextView type;
        public TextView cap;
        public TextView date;
        public LinearLayout ll;

        public ViewHolder(View itemView) {
            super(itemView);
            myName = (TextView) itemView.findViewById(R.id.nameV);
            Pic = (ImageView) itemView.findViewById(R.id.image);
            type = (TextView) itemView.findViewById(R.id.typeV);
            ll = (LinearLayout) itemView.findViewById(R.id.ll);
        }
    }

    // конструктор
    public myAdapter(List<String> myList, List<Integer> idList, String typeT, int rowLayout, Context context) {
        this.myList = myList;
        this.idList = idList;
        this.typeT = typeT;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String entry = myList.get(i);
        int id = idList.get(i);
        viewHolder.myName.setText(entry);
        viewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TrainingInfo.class);
                intent.putExtra("name", entry);
                intent.putExtra("type", typeT);
                intent.putExtra("id", id);
                intent.putExtra("who", "admin");
                mContext.startActivity(intent);
            }
        });
        if(typeT.equals("Group training")) {
            viewHolder.Pic.setImageResource(R.drawable.group);
        }
        else if(typeT.equals("Personal training")){
            viewHolder.Pic.setImageResource(R.drawable.personal);
        }
        else if(typeT.equals("Cardio training")){
            viewHolder.Pic.setImageResource(R.drawable.cardio);
        }
        else if(typeT.equals("Yoga session")){
            viewHolder.Pic.setImageResource(R.drawable.yoga);
        }

        viewHolder.type.setText(typeT);
    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

}
