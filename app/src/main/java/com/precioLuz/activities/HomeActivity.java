package com.precioLuz.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewPager = binding.viewPager;
        bottomNavigationView = binding.bottomNavigation;

        // Configura el adaptador del ViewPager2
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Listener para el ViewPager2
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.itemHome);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.itemHourDetail);
                        break;
                }
            }
        });

        // Listener para el BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemHome:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.itemHourDetail:
                    viewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        });

        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        createToken();
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new ChartsFragment();
                case 1:
                    return new PricesFragment();
                default:
                    return new ChartsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Número de fragmentos
        }
    }

    private void createToken() {
        mTokenProvider.create(mAuthProvider.getUid());
    }
}
