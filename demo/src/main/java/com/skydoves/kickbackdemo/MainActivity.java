package com.skydoves.kickbackdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by skydoves on 2018. 4. 13.
 * Copyright (c) 2018 battleent All rights reserved.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Kickback_SecondActivityExtra.setName("skydoves");
        Kickback_SecondActivityExtra.setPassword(1234);

        startActivity(new Intent(this, SecondActivity.class));

        Button button = findViewById(R.id.button0);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printKickback();
            }
        });
    }

    private void printKickback() {
        Toast.makeText(this, Kickback_SecondActivityExtra.getName() + " :" + Kickback_SecondActivityExtra.getPassword(), Toast.LENGTH_SHORT).show();
    }
}
