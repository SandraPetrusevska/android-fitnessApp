package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AddTraining extends AppCompatActivity {
    SQLiteDatabase db;
    EditText date;
    EditText time;
    EditText name;
    EditText capacity;
    Spinner type;
    DatePickerDialog datePickerDialog;
    TimePickerDialog picker;
    Button btn;
    private boolean mTimerRunning;
    private long mStartTimeInMillis;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis;
    private long mEndTime;
    String endD, endT, end;
    Date startDate, endDate;
    int id;

   // TextView tl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_training);

        db = openOrCreateDatabase("fitnessApp", MODE_PRIVATE, null);

        date = (EditText) findViewById(R.id.date);
        time = (EditText) findViewById(R.id.time);
        name = (EditText) findViewById(R.id.name);
        capacity = (EditText) findViewById(R.id.capacity);
        type = (Spinner) findViewById(R.id.spinner);
        btn = (Button) findViewById(R.id.submit);
       // tl = (TextView) findViewById(R.id.timel);

        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM trainings", null);
        c1.moveToFirst();
        id = c1.getInt(0) + 1;

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date, month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(AddTraining.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(AddTraining.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                time.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

    }

    public void addTraining(View view) {
            if (name.getText().toString().trim().length() == 0 || capacity.getText().toString().trim().length() == 0 || date.getText().toString().trim().length() == 0 || time.getText().toString().trim().length() == 0) {
                Toast.makeText(this, "Please enter all fields.", Toast.LENGTH_SHORT).show();
            } else {
                String n = name.getText().toString();
                String capac = capacity.getText().toString();
                int finalC = Integer.parseInt(capac);
                String t = type.getSelectedItem().toString();
                if ((finalC > 1) && t.equals("Personal training")) {
                    Toast.makeText(this, "One person is allowed for personal training", Toast.LENGTH_SHORT).show();
                } else {
                    endD = String.valueOf(date.getText());
                    endD = endD.replace("/", "-");
                    endT = String.valueOf(time.getText());

                    if (!endD.isEmpty() && !endT.isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        end = endD + " " + endT + ":00";
                        try {
                            startDate = new Date();
                            endDate = (Date) sdf.parse(end);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mStartTimeInMillis = startDate.getTime();
                        // setTime(startDate.getTime());
                        mEndTime = endDate.getTime();
                        mTimeLeftInMillis = mEndTime - mStartTimeInMillis;
                        if (mStartTimeInMillis <= System.currentTimeMillis()) {
                            startTimer();
                        }
                        if (mTimeLeftInMillis < 0) {
                            mTimeLeftInMillis = 0;
                            Toast.makeText(this, "The time you entered has passed. Please enter the correct time.", Toast.LENGTH_SHORT).show();
                        } else {
                            String status = "active";
                            String d = date.getText().toString();
                            String ti = time.getText().toString();
                            db.execSQL("INSERT INTO trainings (name, type, capacity, status, date, time) VALUES ('" + n + "','" + t + "','" + finalC + "','" + status + "','" + d + "','" + ti + "');");
                            Intent intent = new Intent(this, AdminView.class);
                            startActivity(intent);

                            String notification = "Hello from fitnessApp. New training " + name.getText() + " is now available";
                            Cursor u = db.rawQuery("SELECT * FROM users", null);
                            if (u.moveToFirst()) {
                                do {
                                    String username = u.getString(1);
                                    db.execSQL("INSERT INTO notifications(content, username, status, trainingID) VALUES('" + notification + "','" + username + "','" + 1 + "','" + id + "' );");
                                } while (u.moveToNext());
                                u.close();
                            }
                        }
                    }
                }
            }
    }

    private void startTimer(){
        //mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                String status = "finished";
                Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM trainings", null);
                c1.moveToFirst();
                id = c1.getInt(0) + 1;
                c1.close();
                Cursor c = db.rawQuery("UPDATE trainings SET status ="+ "\""+status+"\""+" WHERE id ="+id, null);
                c.moveToFirst();
                c.close();
            }
        }.start();
        mTimerRunning = true;
    }

    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis"+id, mStartTimeInMillis);
        editor.putLong("millisLeft"+id, mTimeLeftInMillis);
        editor.putBoolean("timerRunning"+id, mTimerRunning);
        editor.putLong("endTime"+id, mEndTime);
        editor.apply();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mStartTimeInMillis = prefs.getLong("startTimeInMillis"+id, 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft"+id, mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning"+id, false);

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime"+id, 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                String status = "finished";
                Cursor c = db.rawQuery("UPDATE trainings SET status ="+ "\""+status+"\""+" WHERE id ="+id, null);
                c.moveToFirst();
                c.close();
                Cursor c3 = db.rawQuery("SELECT * FROM trainings WHERE id = " + id, null);
                String trainingName;
                if (c3.getCount() > 0) {
                    c3.moveToFirst();
                    trainingName = c3.getString(1);
                    String notification2 ="Training: " + trainingName + " started";
                    Cursor c1 = db.rawQuery("SELECT username FROM users",null);
                    int i=0;
                    if(c1.moveToFirst()){
                        do{
                            String username = c1.getString(0);
                            //    db.execSQL("INSERT INTO notifications(content, username, status, pollID) VALUES('" + notification2 + "','" + username + "','" + 0 + "','" + id +"' );");
                            i++;
                            Cursor c2 = db.rawQuery("UPDATE notifications SET status = " + 0 + " , content = "+ "\""+notification2+"\"" +" WHERE username = "+"\""+username+"\"" + " AND trainingID = "+ id , null);
                            //   Cursor c2 = db.rawQuery("DELETE FROM notifications WHERE username = "+"\""+username+"\"", null);
                            c2.moveToFirst();
                            c2.close();
                        } while (c1.moveToNext());
                        c1.close();
                    }
                }
                c3.close();
               /* String notification2 ="Training: " + trainingName + " started";
                Cursor c1 = db.rawQuery("SELECT username FROM users",null);
                int i=0;
                if(c1.moveToFirst()){
                    do{
                        String username = c1.getString(0);
                        //    db.execSQL("INSERT INTO notifications(content, username, status, pollID) VALUES('" + notification2 + "','" + username + "','" + 0 + "','" + id +"' );");
                        i++;
                        Cursor c2 = db.rawQuery("UPDATE notifications SET status = " + 0 + " , content = "+ "\""+notification2+"\"" +" WHERE username = "+"\""+username+"\"" + " AND trainingID = "+ id , null);
                        //   Cursor c2 = db.rawQuery("DELETE FROM notifications WHERE username = "+"\""+username+"\"", null);
                        c2.moveToFirst();
                        c2.close();
                    } while (c1.moveToNext());
                    c1.close();
                } */
            } else {
                startTimer();
            }
        }
    }
}