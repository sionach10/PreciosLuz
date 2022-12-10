package com.socialtravel.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socialtravel.R;
import com.socialtravel.activities.FiltersActivity;

public class FiltersFragment extends Fragment {

    View mView;
    CardView mCardViewPS4;
    CardView mCardViewXbox;
    CardView mCardViewNintendo;
    CardView mCardViewPC;


    public FiltersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_filters, container, false);

        mCardViewPS4 = mView.findViewById(R.id.cardViewPs4);
        mCardViewXbox = mView.findViewById(R.id.cardViewXbox);
        mCardViewNintendo = mView.findViewById(R.id.cardViewNintendo);
        mCardViewPC = mView.findViewById(R.id.cardViewPC);

        mCardViewPS4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("PS4");
            }
        });
        mCardViewXbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("XBOX");
            }
        });
        mCardViewNintendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("NINTENDO");
            }
        });
        mCardViewPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("PC");
            }
        });

        return mView;
    }

    private void goToFilterActivity(String category) {
        Intent intent = new Intent(getContext(), FiltersActivity.class); //Aqui pasamos el contexto ya que nos encontramos en un fragment.
        intent.putExtra("category", category);
        startActivity(intent);
    }
}