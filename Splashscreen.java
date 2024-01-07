package com.example.MusicX;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.MusicX.R;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        final Handler handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent myIntent = new Intent(Splashscreen.this,MainActivity.class);
                startActivity(myIntent);
                finish();

            }
        },2000);
    }
}