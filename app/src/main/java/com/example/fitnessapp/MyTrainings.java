package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyTrainings extends AppCompatActivity {
    SQLiteDatabase db;
    LinearLayout ll;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trainings);

        db = openOrCreateDatabase("fitnessApp", MODE_PRIVATE, null);

        ll = (LinearLayout) findViewById(R.id.container);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        String st = "finished";
        Cursor c = db.rawQuery("SELECT * FROM userTrainings WHERE username =" + "\"" + username + "\"", null);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    //ll.addView(view);
                    int id = c.getInt(0);
                    Cursor c1 = db.rawQuery("SELECT * FROM trainings WHERE id =" + id + " and status =" +  "\"" + st + "\"" , null);
                    if (c1.getCount() > 0) {
                        if (c1.moveToFirst()) {
                            do {
                                //ll.addView(view);
                                String name = c1.getString(1);
                                String status = c1.getString(4);
                                String date = c1.getString(5);
                                String type = c1.getString(2);
                                String time = c1.getString(6);
                                add(id, name, status, date, time,  type);
                            } while (c1.moveToNext());
                        }
                    }
                    c1.close();
                } while (c.moveToNext());
            }
        }
        c.close();
    }

    private void add(int id, String name, String status, String date, String time, String type){
        View view = getLayoutInflater().inflate(R.layout.row, null);

        TextView nameV = view.findViewById(R.id.nameV);
        TextView typeV = view.findViewById(R.id.typeV);
        TextView capacityV = view.findViewById(R.id.capacityV);
        TextView dateV = view.findViewById(R.id.dateV);
        ImageView imgV = view.findViewById(R.id.image);

        nameV.setText(name);
        typeV.setText(type);
        capacityV.setText(status);
        dateV.setText(date);
        if(type.equals("Group training")) {
            imgV.setImageResource(getImageId(this, "group"));
        } else if(type.equals("Personal training")) {
            imgV.setImageResource(getImageId(this, "personal"));
        } else if(type.equals("Yoga session")){
            imgV.setImageResource(getImageId(this, "yoga"));
        } else if(type.equals("Cardio training")) {
            imgV.setImageResource(getImageId(this, "cardio"));
        }
        ll.addView(view);

    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent= new Intent();
                intent.putExtra("username", username);
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}