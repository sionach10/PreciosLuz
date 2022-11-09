package com.socialtravel.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.DataCollectionDefaultChange;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.socialtravel.R;
import com.socialtravel.models.User;
import com.socialtravel.providers.AuthProvider;
import com.socialtravel.providers.UserProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {

    TextInputEditText mTextInputUserName;
    TextInputEditText mTextInputPhone;
    Button mButtonConfirm;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    SpotsDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mTextInputUserName = findViewById(R.id.textInputUserName);
        mTextInputPhone = findViewById(R.id.textInputPhone);
        mButtonConfirm = findViewById(R.id.btnConfirm);
        mDialog = new SpotsDialog(this);


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
        String phone = mTextInputPhone.getText().toString();
        if(!username.isEmpty() && !phone.isEmpty()){
            updateUser(username, phone);
        }
        else{
            Toast.makeText(this, "Para completar el registro, inserta todos los campos", Toast.LENGTH_LONG).show();
        }

    }

    private void updateUser(final String username, final String phone){
        String id = mAuthProvider.getUid();
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPhone(phone);
        user.setTimestamp(new Date().getTime());
        mDialog.show();
        mDialog.setMessage("Cargando");
        mUserProvider.update(user).addOnCompleteListener((task) ->  {
            mDialog.dismiss();
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