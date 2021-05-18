package com.example.onetimechat;

import android.os.Bundle;

import com.example.onetimechat.ui.main.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.example.onetimechat.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private Session sess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        // GET USER SESSION
        sess = Session.getInstance();
        if(sess.getToken().equals("")){
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1); // DON'T ALLOW UNREGISTERED USERS
        }

        // TODO BUTTON FUNCTIONALITY
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Thank You for rating One Time Chat!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}