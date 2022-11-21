package com.socialtravel.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.socialtravel.R;
import com.socialtravel.activities.EditProfileActivity;
import com.socialtravel.providers.AuthProvider;
import com.socialtravel.providers.PostProvider;
import com.socialtravel.providers.UserProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    LinearLayout mLinearLayoutEditProfile;
    View mView;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewEmail;
    TextView mTextViewPostNumber;
    ImageView mImageViewCover;
    CircleImageView mCircleImageProfile;
    PostProvider mPostProvider;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        mLinearLayoutEditProfile = mView.findViewById(R.id.linearLayoutEditProfile);
        mTextViewEmail = mView.findViewById(R.id.textViewEmail);
        mTextViewUsername = mView.findViewById(R.id.textViewUsername);
        mTextViewPhone = mView.findViewById(R.id.textViewPhone);
        mTextViewPostNumber = mView.findViewById(R.id.textViewPostNumber);
        mCircleImageProfile = mView.findViewById(R.id.circleImageProfile);
        mImageViewCover = mView.findViewById(R.id.imageViewCover);



        mLinearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });

        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        getUser();
        getPostNumber();
        return mView;
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);

        startActivity(intent);
    }

    private void getPostNumber() {
        mPostProvider.getPostByUser(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberPost = queryDocumentSnapshots.size();
                mTextViewPostNumber.setText(String.valueOf(numberPost));
            }
        });
    }

    private void getUser() {
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("email")) {
                        String email = documentSnapshot.getString("email");
                        mTextViewEmail.setText(email);
                    }
                    if(documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if(documentSnapshot.contains("phone")) {
                        String phone = documentSnapshot.getString("phone");
                        mTextViewPhone.setText(phone);
                    }
                    if(documentSnapshot.contains("image_profile")) {
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if(imageProfile != null && !imageProfile.isEmpty()){
                            Picasso.with(getContext()).load(imageProfile).into(mCircleImageProfile);
                        }
                    }
                    if(documentSnapshot.contains("image_cover")) {
                        String imageCover = documentSnapshot.getString("image_cover");
                        if(imageCover != null && !imageCover.isEmpty()){
                            Picasso.with(getContext()).load(imageCover).into(mImageViewCover);
                        }
                    }
                }
            }
        });
    }
}