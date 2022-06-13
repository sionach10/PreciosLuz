package com.socialtravel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    CircleImageView mCircleFlechaAtras;
    TextInputEditText mTextInputUserName;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputPasswordConfirmation;
    Button mButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mCircleFlechaAtras = findViewById(R.id.flecha_atras);
        mTextInputUserName = findViewById(R.id.textInputUserName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputPasswordConfirmation = findViewById(R.id.textInputPasswordConfirmation);
        mButtonRegister = findViewById(R.id.btnRegister);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });


        mCircleFlechaAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void register(){
        String username = mTextInputUserName.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        String passwordConfirmation = mTextInputPasswordConfirmation.getText().toString();

        if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordConfirmation.isEmpty()){
            if(isEmailValid(email))
                Toast.makeText(this, "El email es válido", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Email malo", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Para registrarte, inserta todos los campos", Toast.LENGTH_LONG).show();
        }

    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}