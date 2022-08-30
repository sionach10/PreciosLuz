package com.socialtravel.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.socialtravel.R;
import com.socialtravel.databinding.ActivityHomeBinding;
import com.socialtravel.databinding.ActivityMainBinding;
import com.socialtravel.fragments.ChatsFragment;
import com.socialtravel.fragments.FiltersFragment;
import com.socialtravel.fragments.HomeFragment;
import com.socialtravel.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater()); //Enlaza un objeto de java con el objeto del .xml de layout.
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemHome:
                    openFragment(new HomeFragment());
                    return true;
                case R.id.itemFilters:
                    openFragment(new FiltersFragment());
                    return true;
                case R.id.itemChat:
                    openFragment(new ChatsFragment());
                    return true;
                case R.id.itemProfile:
                    openFragment(new ProfileFragment());
                    return true;
            }

            return true;
        });

        openFragment(new HomeFragment());
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
