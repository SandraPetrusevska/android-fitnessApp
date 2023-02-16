package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    EditText username;
    EditText password;
    String usr;
    String pw;
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
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("fitnessApp", MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS admins;");
      //  db.execSQL("DROP TABLE IF EXISTS trainings;");
        db.execSQL("CREATE TABLE IF NOT EXISTS admins(username VARCHAR, password VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR, password VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS trainings(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, type VARCHAR, capacity INTEGER, status VARCHAR, date VARCHAR, time VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS trainings(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, type VARCHAR, capacity INTEGER, status VARCHAR, date VARCHAR, time VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS userTrainings(id INTEGER, username VARCHAR, latitude DOUBLE, longitude DOUBLE, time LONG);");
        db.execSQL("CREATE TABLE IF NOT EXISTS notifications(id INTEGER PRIMARY KEY AUTOINCREMENT, content VARCHAR, username VARCHAR, status INTEGER, trainingID INTEGER);");

        db.execSQL("INSERT INTO admins VALUES('sandra','sandra123');");
      //  db.execSQL("INSERT INTO users VALUES('sandrap','123');");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel= new NotificationChannel("My notification", "My notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void onClickSignUp(View view) {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    public void onClickLoginAdmin(View view) {
        Intent intent = new Intent(this, AdminLogin.class);
        startActivity(intent);
    }

    public Boolean checkusernamepassword(String username, String password){
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? and password = ?", new String[] {username, password});
        if (cursor.getCount()>0) {
            cursor.close();
            return true;
        }
        else {
            cursor.close();
            return false;
        }
    }

    public void loginInput(View view) {
        username = (EditText) findViewById(R.id.username);
        usr = username.getText().toString();
        password =  (EditText) findViewById(R.id.password);
        pw = password.getText().toString();
        if (username.getText().toString().trim().length() == 0 || password.getText().toString().trim().length() == 0 ) {
            Toast.makeText(this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
        }
        else {
            Boolean checkuserpw = checkusernamepassword(usr, pw);
            if (checkuserpw==true) {
                int i=0;
                Toast.makeText(this, "Log in successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (getApplicationContext(), UserView.class);
                intent.putExtra("username", usr);
                intent.putExtra("password", pw);
                newNotification();
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void newNotification() {
        //  username = (EditText) findViewById(R.id.username);
        //  usr = username.getText().toString();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        Cursor c = db.rawQuery("SELECT * FROM notifications, trainings WHERE username = " + "\""+usr+"\"" + " AND notifications.trainingID = trainings.id", null);
        if(c.moveToFirst()){
            do {
                trainingIDs.add(c.getInt(4));
                trainingNames.add(c.getString(6));
            } while (c.moveToNext());
            c.close();

            int i;
            for( i=0; i<trainingIDs.size(); i++){
                mEndTime = prefs.getLong("endTime" + trainingIDs.get(i), 0);
                mStartTimeInMillis = prefs.getLong("startTimeInMillis" + trainingIDs.get(i), 600000);
                mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

                if( mTimeLeftInMillis < 0) {
                    mTimeLeftInMillis = 0;
                    mTimerRunning = false;
                    String status = "finished";
                    Cursor c2 = db.rawQuery("UPDATE trainings SET status ="+ "\""+status+"\""+" WHERE id ="+trainingIDs.get(i), null);
                    c2.moveToFirst();
                    c2.close();
                    c2 = db.rawQuery("SELECT username FROM users WHERE username = "+ "\""+usr+"\"", null);
                    c2.moveToFirst();
                    String notification2 ="Training: "+trainingNames.get(i)+" started";
                    Cursor c3 = db.rawQuery("UPDATE notifications SET status = " + 0 + " , content = "+ "\""+notification2+"\"" +" WHERE username = "+"\""+usr+"\"" + " AND trainingID = "+ trainingIDs.get(i) , null);
                    c3.moveToFirst();
                    c3.close();
                }
            }
        }
        c = db.rawQuery("SELECT * FROM notifications WHERE username = " + "\""+usr+"\"" +" AND (status = "+ 1 + " OR status= " + 0 +")", null);
        int j=0;
        if(c.moveToFirst()){
            do{
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My notification");
                builder.setContentTitle("POLL");
                builder.setContentText(c.getString(1));
                builder.setSmallIcon(R.drawable.cardio);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                managerCompat.notify(j, builder.build());
                j++;
            } while(c.moveToNext());
            c.close();
            Cursor c3 = db.rawQuery("UPDATE notifications SET status = "  + 2 + " WHERE username = "+"\""+usr+"\"" +  " AND status = " + 1, null);
            c3.moveToFirst();
            c3.close();

            Cursor c4 = db.rawQuery("DELETE FROM notifications WHERE username = "+"\""+usr+"\"" + " AND status = " + 0, null);
            c4.moveToFirst();
            c4.close();
        }
    }
}