package com.example.fitnessapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserView extends AppCompatActivity {
    SQLiteDatabase db;
    LinearLayout ll;
    Button btn;
    String username;
    int capacity;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private long mStartTimeInMillis;
    private boolean mTimerRunning;
    ArrayList<String> trainingNames = new ArrayList<String>();
    ArrayList<Integer> trainingIDs = new ArrayList<Integer>();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        db = openOrCreateDatabase("fitnessApp", MODE_PRIVATE, null);

        ll = (LinearLayout) findViewById(R.id.container);
        btn = (Button) findViewById(R.id.myTrainings);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        String status = "active";
        Cursor c = db.rawQuery("SELECT * FROM trainings WHERE status =" + "\"" + status + "\"", null);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    //ll.addView(view);
                    String s = c.getString(3);
                    capacity = Integer.parseInt(s);
                    int id = c.getInt(0);
                    String name = c.getString(1);
                    String date = c.getString(5);
                    String type = c.getString(2);
                    String time = c.getString(6);
                    add(id, name, capacity, date, time,  type);
                } while (c.moveToNext());
            }
        }
        c.close();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserView.this, MyTrainings.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My notification", "My notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void add(Integer id, String name, Integer capacity, String date, String time, String type){
        View view = getLayoutInflater().inflate(R.layout.row, null);

        TextView nameV = view.findViewById(R.id.nameV);
        TextView typeV = view.findViewById(R.id.typeV);
        TextView capacityV = view.findViewById(R.id.capacityV);
        TextView dateV = view.findViewById(R.id.dateV);
        ImageView imgV = view.findViewById(R.id.image);


        String c = String.valueOf(capacity) + " left";
        nameV.setText(name);
        typeV.setText(type);
        capacityV.setText(c);
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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newNotification()){
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(UserView.this, "Clicked " + name, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserView.this, TrainingInfo.class);
                    intent.putExtra("name", name);
                    intent.putExtra("date", date);
                    intent.putExtra("type", type);
                    intent.putExtra("time", time);
                    intent.putExtra("id", id);
                    intent.putExtra("username", username);
                    intent.putExtra("who", "user");
                    intent.putExtra("capacity", String.valueOf(capacity));
                    startActivity(intent);
                }
            }
        });
    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 2404) {
            if(data != null) {
                username = data.getStringExtra("username");
            }
        }
    }

    public boolean newNotification() {
        boolean retr = false;
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        Cursor c = db.rawQuery("SELECT * FROM notifications, trainings WHERE username = " + "\"" + username + "\"" + " AND notifications.trainingID = trainings.id", null);
        if (c.moveToFirst()) {
            do {
                trainingIDs.add(c.getInt(4));
                trainingNames.add(c.getString(6));
            } while (c.moveToNext());
            c.close();

            int i;
            for (i = 0; i < trainingIDs.size(); i++) {
                mEndTime = prefs.getLong("endTime" + trainingIDs.get(i), 0);
                mStartTimeInMillis = prefs.getLong("startTimeInMillis" + trainingIDs.get(i), 600000);
                mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

                if (mTimeLeftInMillis < 0) {
                    mTimeLeftInMillis = 0;
                    mTimerRunning = false;
                    String status = "finished";
                    Cursor c2 = db.rawQuery("UPDATE trainings SET status =" + "\"" + status + "\"" + " WHERE id =" + trainingIDs.get(i), null);
                    c2.moveToFirst();
                    c2.close();
                    c2 = db.rawQuery("SELECT username FROM users WHERE username = " + "\"" + username + "\"", null);
                    c2.moveToFirst();
                    String notification2 = "Training: " + trainingNames.get(i) + " started";
                    // db.execSQL("INSERT INTO notifications(content, username, status, pollID) VALUES('" + notification2 + "','" + username + "','" + 0 + "','" + pollIDs.get(i) +"' );");
                    Cursor c3 = db.rawQuery("UPDATE notifications SET status = "+ 0 + ", content = " + "\"" + notification2 + "\"" + " WHERE username = " + "\"" + username + "\"" + " AND trainingID = " + trainingIDs.get(i) , null);
                    c3.moveToFirst();
                    c3.close();

                    retr = true;
                }
                else {
                    retr = false;
                }

            }
        }
        c = db.rawQuery("SELECT * FROM notifications WHERE username = " + "\"" + username + "\"" + " AND status= " + 0, null);
        if (c.moveToFirst()) {
            do {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(UserView.this, "My notification");
                builder.setContentTitle("TRAINING");
                builder.setContentText(c.getString(1));
                builder.setSmallIcon(R.drawable.cardio);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(UserView.this);
                managerCompat.notify(i, builder.build());
                i++;

            } while (c.moveToNext());
            c.close();

            Cursor c4 = db.rawQuery("DELETE FROM notifications WHERE username = " + "\"" + username + "\"" + " AND status = " + 0, null);
            c4.moveToFirst();
            c4.close();
        }
        return retr;
    }
}