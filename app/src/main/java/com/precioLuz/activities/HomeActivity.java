package com.precioLuz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.precioLuz.R;
import com.precioLuz.databinding.ActivityHomeBinding;
import com.precioLuz.fragments.ChartsFragment;
import com.precioLuz.fragments.PricesFragment;
import com.precioLuz.fragments.SettingsFragment;
import com.precioLuz.providers.AuthProvider;
import com.precioLuz.providers.TokenProvider;
import com.precioLuz.providers.UserProvider;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater()); //Enlaza un objeto de java con el objeto del .xml de layout.
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemHome:
                    openFragment(new ChartsFragment());
                    return true;
                case R.id.itemHistory:
                    openFragment(new PricesFragment());
                    return true;
            }

            return true;
        });

        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        openFragment(new ChartsFragment());
        createToken();
    }

    @Override
    protected void onStart() { //Metodos del ciclo de vida de la app.
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void createToken() {
        mTokenProvider.create(mAuthProvider.getUid());
    }


}
