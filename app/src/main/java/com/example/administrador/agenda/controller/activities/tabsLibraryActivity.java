package com.example.administrador.agenda.controller.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.administrador.agenda.R;

/**
 * Created by c1284518 on 26/10/2015.
 */
public class tabsLibraryActivity extends AppCompatActivity{

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarytabs);
        bindToolbar();
    }

    private void bindToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
