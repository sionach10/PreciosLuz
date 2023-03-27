package com.precioLuz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.precioLuz.R;
import com.precioLuz.databinding.ActivityHomeBinding;
import com.precioLuz.databinding.ActivityMainBinding;
import com.precioLuz.fragments.ChatsFragment;
import com.precioLuz.fragments.FiltersFragment;
import com.precioLuz.fragments.HomeFragment;
import com.precioLuz.fragments.PricesFragment;
import com.precioLuz.fragments.ProfileFragment;
import com.precioLuz.providers.AuthProvider;
import com.precioLuz.providers.TokenProvider;
import com.precioLuz.providers.UserProvider;
import com.precioLuz.utils.ViewedMessageHelper;

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
                    openFragment(new PricesFragment());
                    return true;
                case R.id.itemFilters:
                    openFragment(new FiltersFragment());
                    return true;
            }

            return true;
        });

        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        openFragment(new PricesFragment());
        createToken();
    }

    @Override
    protected void onStart() { //Metodos del ciclo de vida de la app.
        super.onStart();
        ViewedMessageHelper.updateOnline(true, HomeActivity.this);//Controlador de si el usuario está online o no.
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, HomeActivity.this);
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
