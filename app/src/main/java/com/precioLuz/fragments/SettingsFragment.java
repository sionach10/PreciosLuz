package com.precioLuz.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentSnapshot;
import com.precioLuz.R;
import com.precioLuz.activities.MainActivity;
import com.precioLuz.models.User;
import com.precioLuz.providers.AuthProvider;
import com.precioLuz.providers.PostProvider;
import com.precioLuz.providers.UserProvider;

import dmax.dialog.SpotsDialog;

public class SettingsFragment extends Fragment {

    View mView;
    SpotsDialog mDialog;
    SwitchMaterial switchNotificacion;
    boolean mNotification = false;
    TextView textViewNotification;

    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.itemCalendar).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemLogout:
                mAuthProvider.logout();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //Limpiamos historial del botón atras.
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_settings, container, false);
        mDialog = new SpotsDialog(getContext());
        switchNotificacion = mView.findViewById(R.id.switchNotifications);
        textViewNotification = mView.findViewById(R.id.textViewNotification);


        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        textViewNotification.setVisibility(View.GONE);
        switchNotificacion.setVisibility(View.GONE); //Para esperar a que cargue el valor de BBDD
        getUser();

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchNotificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
                mDialog.setMessage("Guardando");
                User user = new User();
                user.setId(mAuthProvider.getUid()); //Obtengo el usuario que voy a modificar.
                user.setNotifications(switchNotificacion.isChecked());
                updateInfo(user);
            }
        });
    }

    private void getUser() {
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("notifications")) {
                        mNotification = Boolean.TRUE.equals(documentSnapshot.getBoolean("notifications"));
                        switchNotificacion.setChecked(mNotification);
                        textViewNotification.setVisibility(View.VISIBLE);
                        switchNotificacion.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void updateInfo(User user) {
        mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
            }
        });
    }
}