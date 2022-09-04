package com.socialtravel.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.socialtravel.R;
import com.socialtravel.activities.HomeActivity;
import com.socialtravel.activities.MainActivity;
import com.socialtravel.activities.PostActivity;

public class HomeFragment extends Fragment {

    View mView;
    FloatingActionButton mFab;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mFab = mView.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPost();
            }
        });
        return mView;
    }

    private void goToPost() {
        // Vamos de un fragment a un activity, por eso el metodo intent cambia respecto al que
        // teníamos en los activities.
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);

    }
}