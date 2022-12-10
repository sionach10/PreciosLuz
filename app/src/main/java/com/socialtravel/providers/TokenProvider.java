package com.socialtravel.providers;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.socialtravel.activities.PostDetailActivity;
import com.socialtravel.models.Token;

public class TokenProvider {

    CollectionReference mCollection;

    public TokenProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Tokens");
    }

    public void create(String idUser) {
        if(idUser == null) {
            return;
        }
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Token token = new Token(s);
                mCollection.document(idUser).set(token);
            }
        });
    }

    public Task<DocumentSnapshot> getToken(String idUser) {
        return mCollection.document(idUser).get();
    }
}
