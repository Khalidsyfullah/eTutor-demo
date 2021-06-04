package com.akapps.etutor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    int prog = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        new Handler(Looper.getMainLooper()).postDelayed(runnable, 100);

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            prog+= 5;
            if(prog> 100){
                startActivity(new Intent(MainActivity.this, StartPage.class));
                finish();
            }
            else{
                progressBar.setProgress(prog);
                new Handler(Looper.getMainLooper()).postDelayed(runnable, 200);
            }
        }
    };


}