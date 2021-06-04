package com.akapps.etutor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

public class CenterPage extends AppCompatActivity {
    TextView bar_text, back_btn;
    TabLayout tabLayout;
    public String title, post, details, user_uid;
    boolean isDetails = false;
    String title1 = "All Posts", title2 = "Create new Post", title3 = "Profile Management",
        title4 = "Post Details";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_page);
        bar_text = findViewById(R.id.textView15);
        back_btn = findViewById(R.id.textView17);
        tabLayout = findViewById(R.id.tabLayout);

        back_btn.setOnClickListener(v -> {
            backPressed();
        });

        bar_text.setText(title1);

        //add a fragment
        getSupportFragmentManager().beginTransaction().add(R.id.frame, new ViewPost()).commit();

        //tab layout listner
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    bar_text.setText(title1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ViewPost()).commit();
                }
                else if(tab.getPosition() == 1){
                    bar_text.setText(title2);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new CreatePost()).commit();
                }
                else{
                    bar_text.setText(title3);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MyProfile()).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        backPressed();
    }

    void backPressed()
    {
        if(isDetails){
            isDetails = false;
            bar_text.setText(title1);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ViewPost()).commit();
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(CenterPage.this)
                    .setTitle("Confirm Exit")
                    .setMessage("Action will exit the app, sure to continue?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CenterPage.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();

            alertDialog.show();
        }
    }

    public void logout()
    {
        startActivity(new Intent(CenterPage.this, StartPage.class));
        finish();
    }

    public void showDetails(String s1, String s2, String s3, String s4){
        isDetails = true;
        title = s1;
        post = s2;
        details = s3;
        user_uid = s4;
        bar_text.setText(title4);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new DetailsPost()).commit();
    }
}