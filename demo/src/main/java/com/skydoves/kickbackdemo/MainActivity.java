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

    private Kickback_SecondActivityExtra box = Kickback_SecondActivityExtra.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        box.setName("skydoves");
        box.setPassword(1234);

        startActivity(new Intent(this, SecondActivity.class));

        // init preference persist
        Kickback_SecondActivityExtra.initPersist(this);

        Button button = findViewById(R.id.button0);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printKickback();
            }
        });
    }

    private void printKickback() {
        Toast.makeText(this, box.getName() + " :" + box.getPassword(), Toast.LENGTH_SHORT).show();
    }
}
