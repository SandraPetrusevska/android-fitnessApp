package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Registration extends AppCompatActivity {
    SQLiteDatabase db;
    EditText usernameR;
    EditText passwordR;
    EditText confPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        db = openOrCreateDatabase("fitnessApp", MODE_PRIVATE, null);
    }

    public Boolean checkusername(String username){
        Cursor cursor2 = db.rawQuery("SELECT * FROM admins WHERE username = ?",new String[] {username});
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[] {username});

        if (cursor.getCount()>0 || cursor2.getCount()>0) {
            cursor.close();
            cursor2.close();
            return true;
        }
        else {
            cursor.close();
            cursor2.close();
            return false;
        }
    }

    public void databaseInput(View view) {
        usernameR = (EditText) findViewById(R.id.usernameR);
        passwordR = (EditText) findViewById(R.id.passwordR);
        confPass = (EditText) findViewById(R.id.passwordConf);
        if (usernameR.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Please enter username!", Toast.LENGTH_SHORT).show();
        }
        else if(passwordR.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_SHORT).show();
        }
        else if(passwordR.getText().toString().compareTo(confPass.getText().toString()) != 0) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }
        else {
            String usr = usernameR.getText().toString();
            Boolean checkuser = checkusername(usr);
            if(checkuser == false) {
                db.execSQL("INSERT INTO users(username, password) VALUES('" + usernameR.getText().toString() + "','" + passwordR.getText().toString() + "' );");
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                usernameR.setText("");
                passwordR.setText("");
                confPass.setText("");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}