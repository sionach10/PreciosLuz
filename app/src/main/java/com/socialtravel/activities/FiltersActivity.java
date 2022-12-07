package com.socialtravel.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.socialtravel.R;

public class FiltersActivity extends AppCompatActivity {

    String mExtraCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        mExtraCategory = getIntent().getStringExtra("category");
        Toast.makeText(this, "La categoría seleccionada es " + mExtraCategory, Toast.LENGTH_SHORT).show();
    }
}