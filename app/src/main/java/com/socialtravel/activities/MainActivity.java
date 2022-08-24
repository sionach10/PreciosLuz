package com.socialtravel.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.socialtravel.R;
import com.socialtravel.models.User;
import com.socialtravel.providers.AuthProvider;
import com.socialtravel.providers.UserProvider;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView mTextViewRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLoginEmail;
    AuthProvider mAuthProvider;
    SignInButton mButtonGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    UserProvider mUserProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewRegister = findViewById(R.id.textViewRegister);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonLoginEmail = findViewById(R.id.btnLoginEmail);
        mButtonGoogle = findViewById(R.id.btnLoginGoogle);
        mAuthProvider = new AuthProvider();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mUserProvider = new UserProvider();


        mButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

        mButtonLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmail();
            }
        });

        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class); //Esto me lleva a la pantalla de registro cuando pulso el botón desde la pantalla de Login.
                startActivity(intent);
            }
        });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE);
        activityResultLauncher.launch(signInIntent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int requestCode = activityResult.getResultCode();
                            Intent data = activityResult.getData();

                            //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                            if(requestCode == RESULT_OK) {
                                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                                try {
                                    //Google Sign in was successful, authenticate with firebase.
                                    GoogleSignInAccount account = task.getResult(ApiException.class);
                                    firebaseAuthWithGoogle(account);
                                }catch(ApiException e) {
                                    Log.w("Error","Google Sign in failed.", e);
                                    // ...
                                }
                            }

                        }
                    }
            );

/* Funcion que va con startActivityForResult que está deprecada.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(requestCode == REQUEST_CODE_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign in was successful, authenticate with firebase.
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch(ApiException e) {
                Log.w("Error","Google Sign in failed.", e);
                // ...
            }
        }
    }
*/
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        mAuthProvider.googleLogin(acct).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String id = mAuthProvider.getUid();
                            checkUserExist(id);
                        }
                        else {
                            //if signIn fails.
                            Log.w("Error", "signInWithCredential: failure", task.getException());
                            Toast.makeText(MainActivity.this, "No se pudo iniciar sesión con Google", Toast.LENGTH_LONG);
                        }
                    }
                });
    }
    //Cuando inicia sesión con Google, chequeamos si es la primera vez, para crear el usuario en BBDD.
    private void checkUserExist(final String id) {
        mUserProvider.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else { //Si el usuario no existe.
                    String email = mAuthProvider.getEmail();
                    User user = new User();
                    user.setEmail(email);
                    user.setId(id);
                    mUserProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(MainActivity.this, CompleteProfileActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "No se pudo almacenar el usuario en BBDD", Toast.LENGTH_LONG);
                            }
                        }
                    });
                }
            }
        });
    }

    private void loginEmail()
    {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        mAuthProvider.login(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);//Se le pasa pantalla origen y pantalla destino.
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, "El email o la contraseña no son correctos.", Toast.LENGTH_LONG).show();
                }
            }
        });
        Log.d("Campo", "email: "+ email);
        Log.d("Campo", "Password: "+ password);
    }
}