package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class TrainingInfo extends AppCompatActivity {
    SQLiteDatabase db;
    TextView dt;
    TextView n;
    TextView t;
    TextView c;
    TextView s;
    TextView b;
    TextView daysTV;
    TextView hoursTV;
    TextView minutesTV;
    TextView secondsTV;
    String capacity;
    Button btn;
    Button btn2;
    int id;
    int uid;
    int ca;
    String name, date, status, timeC;
    String username;
    Double longitude, latitude;
    Long time;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    private boolean mTimerRunning;
    private long mStartTimeInMillis;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis;
    private long mEndTime;
    String who;
    ArrayList<String> trainingNames = new ArrayList<String>();
    ArrayList<Integer> trainingIDs = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_info);

        db = openOrCreateDatabase("fitnessApp", MODE_PRIVATE, null);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        dt = (TextView) findViewById(R.id.datetime);
        n = (TextView) findViewById(R.id.name);
        t = (TextView) findViewById(R.id.type);
        c = (TextView) findViewById(R.id.capacity);
        b = (TextView) findViewById(R.id.booked);
        s = (TextView) findViewById(R.id.status);
        btn = (Button) findViewById(R.id.book);
        btn2 = (Button) findViewById(R.id.map);
        daysTV = (TextView) findViewById(R.id.txtTimerDay);
        hoursTV = (TextView) findViewById(R.id.txtTimerHour);
        minutesTV = (TextView) findViewById(R.id.txtTimerMinute);
        secondsTV = (TextView) findViewById(R.id.txtTimerSecond);

        Intent intent = getIntent();
       // String name = intent.getStringExtra("name");
        String type = intent.getStringExtra("type");
       // String date = intent.getStringExtra("date");
       // String time = intent.getStringExtra("time");
       // capacity = intent.getStringExtra("capacity");
        id = intent.getIntExtra("id", 0);
        username = intent.getStringExtra("username");
        who = intent.getStringExtra("who");

    /*    dt.setText(date + " at " + time);
        n.setText("Name: " + name);
        t.setText("Type: " + type);
        if(Integer.parseInt(capacity) == 1) {
            c.setText(capacity + " available spot");
        }
        else {
            c.setText(capacity + " available spots");
        } */

        Cursor c2 = db.rawQuery("SELECT * FROM users WHERE username =" + "\"" + username + "\"", null);
        c2.moveToFirst();
        if(c2.getCount()>0){
        uid = c2.getInt(0);}
        c2.close();
        Cursor c1 = db.rawQuery("SELECT * FROM trainings WHERE id = " + id, null);
        if(c1.getCount()>0){
            if (c1.moveToFirst() ){
                do {
                    name = c1.getString(1);
                    date = c1.getString(5);
                    timeC = c1.getString(6);
                    capacity = c1.getString(3);
                    status = c1.getString(4);
                } while (c1.moveToNext());
            }
        }
        c1.close();

        dt.setText(date + " at " + timeC);
        n.setText("Name: " + name);
        t.setText("Type: " + type);
        s.setText("Status: " + status);

        if(Integer.parseInt(capacity) == 1) {
            c.setText(capacity + " available spot");
        }
        else {
            c.setText(capacity + " available spots");
        }

        int count = 0;
        Cursor c3 = db.rawQuery("SELECT * FROM userTrainings WHERE id = " + id, null);
        if(c3.getCount()>0){
            if (c3.moveToFirst() ){
                do {
                    count = count + 1;
                } while (c3.moveToNext());
            }
        }
        c3.close();

        if(count == 1){
            b.setText(count + " booking");
        } else {
            b.setText("Bookings: " + count);
        }

        if(who.equals("user")) {
            btn.setVisibility(View.VISIBLE);
        } else if(who.equals("admin")) {
            btn2.setVisibility(View.VISIBLE);
            s.setVisibility(View.VISIBLE);
            b.setVisibility(View.VISIBLE);
        }

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrainingInfo.this, MapsActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My notification", "My notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Button b = (Button) findViewById(R.id.book);
        String txt = b.getText().toString();
        outState.putString("txt", txt);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String txt = savedInstanceState.getString("txt");
        Button b = (Button) findViewById(R.id.book);
        b.setText(txt);
        updateBtn();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Cursor c = db.rawQuery("SELECT * FROM users WHERE username =" + "\"" + username + "\"", null);
        c.moveToFirst();
        if(c.getCount()>0){
            uid = c.getInt(0);}
        c.close();
        SharedPreferences sharedPreferences = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        Button b = (Button) findViewById(R.id.book);
        String txt = b.getText().toString();
        edit.putString(uid + "txt" + id , txt);
        edit.commit();

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

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Cursor c = db.rawQuery("SELECT * FROM users WHERE username =" + "\"" + username + "\"", null);
        c.moveToFirst();
        if(c.getCount()>0){
            uid = c.getInt(0);}
        c.close();
        SharedPreferences sharedPreferences = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        Button b = (Button) findViewById(R.id.book);
        String txt = sharedPreferences.getString(uid + "txt" + id, "Book a spot");
        b.setText(txt);
        updateBtn();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mStartTimeInMillis = prefs.getLong("startTimeInMillis"+id, 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft"+id, mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning"+id, false);

        updateCountDownText();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime"+id, 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                String status = "finished";
                Cursor c1 = db.rawQuery("UPDATE trainings SET status ="+ "\""+status+"\""+" WHERE id ="+id, null);
                c1.moveToFirst();
                c1.close();
              /*  String notification2 ="Poll: "+pollName+" has ended";
                Cursor c1 = db.rawQuery("SELECT username FROM users",null);
                int i=0;
                if(c1.moveToFirst()){
                    do{
                        String username = c1.getString(0);
                        //    db.execSQL("INSERT INTO notifications(content, username, status, pollID) VALUES('" + notification2 + "','" + username + "','" + 0 + "','" + id +"' );");
                        i++;
                        Cursor c2 = db.rawQuery("UPDATE notifications SET status = " + 0 + " , content = "+ "\""+notification2+"\"" +" WHERE username = "+"\""+username+"\"" + " AND pollID = "+ id , null);
                        //   Cursor c2 = db.rawQuery("DELETE FROM notifications WHERE username = "+"\""+username+"\"", null);
                        c2.moveToFirst();
                        c2.close();
                    } while (c1.moveToNext());
                    c1.close();
                } */
                updateCountDownText();
            } else {
                startTimer();
            }
        }
    }
    private void startTimer(){
        //mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
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

                Cursor c3 = db.rawQuery("SELECT username FROM users",null);
                if(c3.moveToFirst()){
                    do{
                        String notification2 ="Training: " + name + " started";
                        // i++;
                        Cursor c2 = db.rawQuery("UPDATE notifications SET status = " + 0 + " , content = "+ "\""+notification2+"\"" +" WHERE username = "+"\""+username+"\"" + " AND trainingID = "+ id , null);
                        c2.moveToFirst();
                        c2.close();
                    } while(c3.moveToNext());
                    c3.close();
                }
                updateBtn2();
                if(who.equals("user")) {
                    newNotification();
                    Intent intent1 = new Intent(TrainingInfo.this, UserView.class);
                    intent1.putExtra("username", username);
                    startActivity(intent1);
                }
            }
        }.start();
        mTimerRunning = true;
    }
    private void updateCountDownText(){
        long days = TimeUnit.MILLISECONDS.toDays(mTimeLeftInMillis);
        mTimeLeftInMillis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(mTimeLeftInMillis);
        mTimeLeftInMillis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mTimeLeftInMillis);
        mTimeLeftInMillis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mTimeLeftInMillis);

        daysTV.setText(String.valueOf(days));
        hoursTV.setText(String.valueOf(hours));
        minutesTV.setText(String.valueOf(minutes));
        secondsTV.setText(String.valueOf(seconds));
    }

    public void bookSpot(View view) {
        int cap = Integer.parseInt(capacity);

        if(cap == 0) {
            Toast.makeText(this, "No available spots", Toast.LENGTH_SHORT).show();
          //  Intent intent = new Intent(this, UserView.class);
        }
        else {
           // if(buttonState) {
                SharedPreferences sharedPreferences = getSharedPreferences("prefs",MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString(uid + "txt" + id, "Booked" );
                edit.commit();

                ca = cap - 1;
                Cursor c1 = db.rawQuery("UPDATE trainings SET capacity =" + ca + " WHERE id =" + id, null);
                c1.moveToFirst();
                c1.close();
                btn.setText("Booked");
                btn.setBackgroundColor(getResources().getColor(R.color.light_gray));
                btn.setClickable(false);
                c = (TextView) findViewById(R.id.capacity);
                if(ca == 1) {
                    String str = String.valueOf(ca) + " available spot";
                    c.setText(str);
                } else {
                    String str = String.valueOf(ca) + " available spots";
                    c.setText(str);
                }

                getLastLocation();

              //  Toast.makeText(this, "Latitude: " + latitude + " longitude: " + longitude + " time " + time, Toast.LENGTH_SHORT).show();


                db.execSQL("INSERT INTO userTrainings VALUES('" + id + "','" + username + "','" + latitude + "','" + longitude + "','" + time + "' );");

                Intent intent = new Intent (this, UserView.class);
                intent.putExtra("username", username);
                startActivity(intent);
            // }
        }
    }

    public void updateBtn(){
        Button b = (Button) findViewById(R.id.book);
        if(b.getText().toString().equals("Booked")) {
            b.setBackgroundColor(getResources().getColor(R.color.light_gray));
            b.setClickable(false);
        }
    }

    public void updateBtn2(){
        Button b = (Button) findViewById(R.id.book);
        b.setText("Book a spot");
        b.setBackgroundColor(getResources().getColor(R.color.dark_blue));
        b.setClickable(true);
    }

    public void newNotification() {
        //  username = (EditText) findViewById(R.id.username);
        //  usr = username.getText().toString();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        Cursor c = db.rawQuery("SELECT * FROM notifications, trainings WHERE username = " + "\""+username+"\"" + " AND notifications.trainingID = trainings.id", null);
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
                    c2 = db.rawQuery("SELECT username FROM users WHERE username = "+ "\""+username+"\"", null);
                    c2.moveToFirst();
                    String notification2 ="Training: "+trainingNames.get(i)+" started";
                    Cursor c3 = db.rawQuery("UPDATE notifications SET status = " + 0 + " , content = "+ "\""+notification2+"\"" +" WHERE username = "+"\""+username+"\"" + " AND trainingID = "+ trainingIDs.get(i) , null);
                    c3.moveToFirst();
                    c3.close();
                }
            }
        }
        c = db.rawQuery("SELECT * FROM notifications WHERE username = " + "\""+username+"\"" +" AND (status = "+ 1 + " OR status= " + 0 +")", null);
        int j=0;
        if(c.moveToFirst()){
            do{
                NotificationCompat.Builder builder = new NotificationCompat.Builder(TrainingInfo.this, "My notification");
                builder.setContentTitle("POLL");
                builder.setContentText(c.getString(1));
                builder.setSmallIcon(R.drawable.cardio);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(TrainingInfo.this);
                managerCompat.notify(j, builder.build());
                j++;
            } while(c.moveToNext());
            c.close();
            Cursor c3 = db.rawQuery("UPDATE notifications SET status = "  + 2 + " WHERE username = "+"\""+username+"\"" +  " AND status = " + 1, null);
            c3.moveToFirst();
            c3.close();

            Cursor c4 = db.rawQuery("DELETE FROM notifications WHERE username = "+"\""+username+"\"" + " AND status = " + 0, null);
            c4.moveToFirst();
            c4.close();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {

            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            time = System.currentTimeMillis();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {

            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            time= System.currentTimeMillis();
        }
    };
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}