package com.socialtravel.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.socialtravel.R;
import com.socialtravel.models.User;
import com.socialtravel.providers.AuthProvider;
import com.socialtravel.providers.UserProvider;

import java.util.HashMap;
import java.util.Map;

public class CompleteProfileActivity extends AppCompatActivity {

    TextInputEditText mTextInputUserName;
    Button mButtonConfirm;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mTextInputUserName = findViewById(R.id.textInputUserName);
        mButtonConfirm = findViewById(R.id.btnConfirm);

        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmar();
            }
        });



    }

    private void confirmar(){
        String username = mTextInputUserName.getText().toString();

        if(!username.isEmpty()){
            updateUser(username);
        }
        else{
            Toast.makeText(this, "Para completar el registro, inserta todos los campos", Toast.LENGTH_LONG).show();
        }

    }

    private void updateUser(final String username){
        String id = mAuthProvider.getUid();
        User user = new User();
        user.setId(id);
        user.setUsername(username);


        mUserProvider.update(user).addOnCompleteListener((task) ->  {
                if(task.isSuccessful()){
                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText( CompleteProfileActivity.this, "Username no actualizado correctamente.", Toast.LENGTH_SHORT).show();
                }
        });


    }
}