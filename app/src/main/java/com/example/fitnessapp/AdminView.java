package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.security.acl.Group;

public class AdminView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);
    }

    public void newTraining(View view) {
        Intent intent = new Intent(this, AddTraining.class);
        startActivity(intent);
    }

    public void groupTraining(View view) {
        Intent intent = new Intent(this, Training.class);
        intent.putExtra("type", "Group training");
        startActivity(intent);
    }

    public void personalTraining(View view) {
        Intent intent = new Intent(this, Training.class);
        intent.putExtra("type", "Personal training");
        startActivity(intent);
    }

    public void cardioTraining(View view) {
        Intent intent = new Intent(this, Training.class);
        intent.putExtra("type", "Cardio training");
        startActivity(intent);
    }

    public void yogaSession(View view) {
        Intent intent = new Intent(this, Training.class);
        intent.putExtra("type", "Yoga session");
        startActivity(intent);
    }

    public void viewStatistic(View view) {
        Intent intent = new Intent(this, Statistic.class);
        startActivity(intent);
    }
}