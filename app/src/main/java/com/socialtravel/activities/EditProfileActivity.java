package com.socialtravel.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.socialtravel.R;
import com.socialtravel.models.User;
import com.socialtravel.providers.AuthProvider;
import com.socialtravel.providers.ImageProvider;
import com.socialtravel.providers.UserProvider;
import com.socialtravel.utils.FileUtil;
import com.socialtravel.utils.ViewedMessageHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewBack;
    CircleImageView mCircleImageViewProfile;
    ImageView mImageViewCover;
    TextInputEditText mTextInputUserName;
    TextInputEditText mTextInputPhone;
    Button mButtonEditProfile;
    String mUsername = "";
    String mPhone = "";
    String mImageProfile = "";
    String mImageCover = "";
    SpotsDialog mDialog;
    ImageProvider mImageProvider;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];
    enum fuenteImagen { camara, galeria}
    EditProfileActivity.fuenteImagen mFuenteImagen;
    enum imageSelected {cover, profile};
    EditProfileActivity.imageSelected mImageSelected;

    //Foto 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;
    File mImageFile;

    //Foto 2
    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;
    File mImageFile2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mDialog = new SpotsDialog(this);
        mCircleImageViewBack = findViewById(R.id.circleImageBack);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mImageViewCover = findViewById(R.id.imageViewCover);
        mTextInputUserName = findViewById(R.id.textInputUserName);
        mTextInputPhone = findViewById(R.id.textInputPhone);
        mButtonEditProfile = findViewById(R.id.btnEditProfile);

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[] {"Galería", "Tomar foto"};
        mImageProvider = new ImageProvider();
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();

        mButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEditProfile();
            }
        });

        mCircleImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageSelected = imageSelected.profile;
                selectOptionImage();
            }
        });

        mImageViewCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageSelected = imageSelected.cover;
                selectOptionImage();
            }
        });

        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUser();
    }

    private void getUser() {
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("username")) {
                        mUsername = documentSnapshot.getString("username");
                        mTextInputUserName.setText(mUsername);
                    }
                    if(documentSnapshot.contains("phone")) {
                        mPhone = documentSnapshot.getString("phone");
                        mTextInputPhone.setText(mPhone);
                    }
                    if(documentSnapshot.contains("image_profile")) {
                        mImageProfile = documentSnapshot.getString("image_profile");
                        if(mImageProfile != null && !mImageProfile.isEmpty()) {
                            Picasso.with(EditProfileActivity.this).load(mImageProfile).into(mCircleImageViewProfile);
                        }
                    }
                    if(documentSnapshot.contains("image_cover")) {
                        mImageCover = documentSnapshot.getString("image_cover");
                        if(mImageCover != null && !mImageCover.isEmpty()) {
                            Picasso.with(EditProfileActivity.this).load(mImageCover).into(mImageViewCover);
                        }
                    }
                }
            }
        });
    }


    private void clickEditProfile() {
        mUsername = mTextInputUserName.getText().toString();
        mPhone = mTextInputPhone.getText().toString();

        if(!mUsername.isEmpty() && !mPhone.isEmpty())
        {
            //2 GALERIA.
            if(mImageFile!= null && mImageFile2!= null) {//mImageFile: galeria. mPhotoFile: camara.
                saveImageCoverAndProfile(mImageFile, mImageFile2);
            }
            //2 CAMARA.
            else if(mPhotoFile != null && mPhotoFile2!= null){
                saveImageCoverAndProfile(mPhotoFile, mPhotoFile2);
            }
            //1 CAMARA, 2 GALERIA.
            else if(mPhotoFile != null && mImageFile2!= null){
                saveImageCoverAndProfile(mPhotoFile, mImageFile2);
            }
            //1 GALERIA, 2 CAMARA.
            else if(mImageFile != null && mPhotoFile2!= null){
                saveImageCoverAndProfile(mImageFile, mPhotoFile2);
            }
            else if(mPhotoFile != null) {
                saveImage(mPhotoFile, true);
            }
            else if(mPhotoFile2 != null) {
                saveImage(mPhotoFile2, false);
            }
            else if(mImageFile != null) {
                saveImage(mImageFile, true);
            }
            else if (mImageFile2 != null) {
                saveImage(mImageFile2, false);
            }
            else {//userName o Phone.
                User user = new User();
                user.setUsername(mUsername);
                user.setPhone(mPhone);
                user.setImageProfile(mImageProfile);
                user.setImageCover(mImageCover);
                user.setId(mAuthProvider.getUid());
                updateInfo(user);
            }
        }
        else{
            Toast.makeText(this, "Rellene todos los campos para actualizar.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageCoverAndProfile(File _mImageFile1, File _mImageFile2) {
        mDialog.show();
        mDialog.setMessage("Guardando");
        mImageProvider.save(EditProfileActivity.this, _mImageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlProfile = uri.toString();

                            mImageProvider.save(EditProfileActivity.this, _mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful()) {
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                final String urlCover = uri2.toString();
                                                User user = new User();
                                                user.setUsername(mUsername);
                                                user.setPhone(mPhone);
                                                user.setImageProfile(urlProfile);
                                                user.setImageCover(urlCover);
                                                user.setId(mAuthProvider.getUid());
                                                updateInfo(user);

                                            }
                                        });
                                    }
                                    else {
                                        mDialog.dismiss();
                                        Toast.makeText(EditProfileActivity.this, "Hubo un error al almacenar la imagen cover."+ taskImage2.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo un error al almacenar la imagen de perfil."+ task.getException(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void saveImage(File image, boolean isProfileImage) {
        mDialog.show();
        mDialog.setMessage("Guardando");
        mImageProvider.save(EditProfileActivity.this, image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();
                            User user = new User();
                            user.setUsername(mUsername);
                            user.setPhone(mPhone);
                            if(isProfileImage){
                                user.setImageProfile(url);
                                user.setImageCover(mImageCover);
                            }
                            else{
                                user.setImageCover(url);
                                user.setImageProfile(mImageProfile);
                            }
                            user.setId(mAuthProvider.getUid());
                            updateInfo(user);
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo un error al almacenar la imagen de perfil."+ task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateInfo(User user) {
        if(!mDialog.isShowing()) {
            mDialog.show();
            mDialog.setMessage("Guardando");
        }
        mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "La información se actualizó correctamente", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(EditProfileActivity.this, "La información no se pudo actualizar.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectOptionImage() {
        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == 0) {
                    mFuenteImagen = fuenteImagen.galeria;
                    openGallery();
                }
                else if(i==1) {
                    mFuenteImagen = fuenteImagen.camara;
                    takePhoto();
                }
            }
        });

        mBuilderSelector.show();

    }


    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try{
            photoFile = createPhotoFile();
        }catch (Exception e) {
            Toast.makeText(this, "Hubo un error con el archivo de la camara" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if(photoFile != null) {
            Uri photoUri = FileProvider.getUriForFile(EditProfileActivity.this, "com.socialtravel", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            activityResultLauncher1.launch(takePictureIntent);
        }


    }

    private File createPhotoFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(new Date() + "_photo", ".jpg", storageDir);

        if(mImageSelected == imageSelected.profile) {
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }
        else if(mImageSelected == imageSelected.cover) {
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
        }
        return photoFile;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        activityResultLauncher1.launch(galleryIntent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher1 =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int requestCode = activityResult.getResultCode();
                            Intent data = activityResult.getData();

                            //Result returned from launching the Intent
                            if(requestCode == RESULT_OK)
                            {
                                switch (mFuenteImagen)
                                {
                                    case galeria:
                                        if (mImageSelected == imageSelected.profile) {//profile
                                            try {
                                                mPhotoFile= null;
                                                mImageFile = FileUtil.from(EditProfileActivity.this, data.getData());//Nos transforma la URI de la imagen en el archivo (Creo).
                                                mCircleImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                                            } catch (Exception e) {
                                                Log.w("ERROR", "Gallery profile Intent failed, error: ", e);
                                                Toast.makeText(EditProfileActivity.this, "Error al cargar la imagen de perfil", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else { //cover
                                            try {
                                                mPhotoFile2 = null;
                                                mImageFile2 = FileUtil.from(EditProfileActivity.this, data.getData());
                                                mImageViewCover.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
                                            } catch (Exception e) {
                                                Log.w("ERROR", "Gallery 2 Intent failed, error: ", e);
                                                Toast.makeText(EditProfileActivity.this, "Error al cargar la imagen 2", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        break;
                                    case camara:
                                        if (mImageSelected == imageSelected.profile) {//profile
                                            mImageFile = null;
                                            mPhotoFile = new File(mAbsolutePhotoPath);
                                            Picasso.with(EditProfileActivity.this).load(mPhotoPath).into(mCircleImageViewProfile);
                                        }
                                        else { //cover
                                            mImageFile2 = null;
                                            mPhotoFile2 = new File(mAbsolutePhotoPath2);
                                            Picasso.with(EditProfileActivity.this).load(mPhotoPath2).into(mImageViewCover);
                                        }
                                        break;
                                    default:
                                        Toast.makeText(EditProfileActivity.this, "Error valor imageSelected", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(EditProfileActivity.this, "Error al cargar la imagen de la camara", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

    @Override
    protected void onStart() { //Metodos del ciclo de vida de la app.
        super.onStart();
        ViewedMessageHelper.updateOnline(true, EditProfileActivity.this);//Controlador de si el usuario está online o no.
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, EditProfileActivity.this);
    }

}