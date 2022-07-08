package com.socialtravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
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
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
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
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

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
            if(isEmailValid(email)){
                if(password.equals(passwordConfirmation)){
                    if(password.length()>=8)
                        createUser(username, email, password);
                    else
                        Toast.makeText(this, "La contraseña debe ser al menos de 8 caracteres.", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "Email no válido.", Toast.LENGTH_SHORT).show();
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

    private void createUser(final String username, final String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    String id = mAuth.getCurrentUser().getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("email", email);
                    map.put("username", username);
                    mFirestore.collection("Users").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText( RegisterActivity.this, "Usuario guardado en BBDD.", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText( RegisterActivity.this, "Usuario NO guardado en BBDD.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText( RegisterActivity.this, "Usuario registrado correctamente.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText( RegisterActivity.this, "No se pudo registrar el usuario. El email ya está en uso.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}