package com.skydoves.kickbackdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by skydoves on 2018. 4. 13.
 * Copyright (c) 2018 battleent All rights reserved.
 */

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Kickback_SecondActivityExtra.freeAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Kickback_SecondActivityExtra.freeAll();
    }

    private void printKickback() {
        Toast.makeText(this, Kickback_SecondActivityExtra.getName() + " :" + Kickback_SecondActivityExtra.getPassword(), Toast.LENGTH_SHORT).show();
    }
}
