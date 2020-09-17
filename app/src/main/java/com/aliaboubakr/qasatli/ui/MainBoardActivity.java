package com.aliaboubakr.qasatli.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.aliaboubakr.qasatli.R;

public class MainBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_board);
        FragmentMainBoard  fragmentMainBoard = new FragmentMainBoard();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainboard_container, fragmentMainBoard).commit();
        if (findViewById(R.id.mainboard_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

        }

    }


}
