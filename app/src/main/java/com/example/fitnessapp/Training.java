package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import java.security.acl.Group;
import java.util.ArrayList;

public class Training extends AppCompatActivity {
    SQLiteDatabase db;
    myAdapter adapter;
    ArrayList<String> list;
    ArrayList<Integer> listID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        db = openOrCreateDatabase("fitnessApp", MODE_PRIVATE, null);

        list = new ArrayList<>();
        listID = new ArrayList<>();

        Intent intent = getIntent();
        String training = intent.getStringExtra("type");

        Cursor c = db.rawQuery("SELECT * FROM trainings WHERE type =" + "\""+training+"\"", null);

        if(c.getCount()>0){
            if (c.moveToFirst() ){
                do {
                    list.add(c.getString(1));
                    listID.add(c.getInt(0));
                } while (c.moveToNext());
            }
        }

        RecyclerView recyclerView = findViewById(R.id.recyView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //adapter = new myAdapter(this, list);
        adapter = new myAdapter(list, listID, training,  R.layout.adapter_row, this);

        //  adapter.setClickListener((myAdapter.ItemClickListener) this);
        recyclerView.setAdapter(adapter);
    }
}