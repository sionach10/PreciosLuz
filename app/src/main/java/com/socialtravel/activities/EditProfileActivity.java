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
import com.google.firebase.storage.UploadTask;
import com.socialtravel.R;
import com.socialtravel.models.Post;
import com.socialtravel.models.User;
import com.socialtravel.providers.AuthProvider;
import com.socialtravel.providers.ImageProvider;
import com.socialtravel.providers.UserProvider;
import com.socialtravel.utils.FileUtil;
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
    SpotsDialog mDialog;
    ImageProvider mImageProvider;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];
    enum fuenteImagen { cover, profile}
    EditProfileActivity.fuenteImagen mFuenteImagen;
    int imageSelected;

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
                imageSelected = 1;
                selectOptionImage();
            }
        });

        mImageViewCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelected = 2;
                selectOptionImage();
            }
        });

        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void clickEditProfile() {
        mUsername = mTextInputUserName.getText().toString();
        mPhone = mTextInputPhone.getText().toString();

        if(!mUsername.isEmpty() && !mPhone.isEmpty())
        {
            //2 GALERIA.
            if(mImageFile!= null && mImageFile2!= null) {
                saveImage(mImageFile, mImageFile2);
            }
            //2 CAMARA.
            else if(mPhotoFile != null && mPhotoFile2!= null){
                saveImage(mPhotoFile, mPhotoFile2);
            }
            //1 CAMARA, 2 GALERIA.
            else if(mPhotoFile != null && mImageFile2!= null){
                saveImage(mPhotoFile, mImageFile2);
            }
            //1 GALERIA, 2 CAMARA.
            else if(mImageFile != null && mPhotoFile2!= null){
                saveImage(mPhotoFile, mPhotoFile2);
            }
            else {
                Toast.makeText(this, "Imagenes vacías.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "Rellene todos los campos para actualizar.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage(File _mImageFile1, File _mImageFile2) {
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

    private void selectOptionImage() {
        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == 0) {
                    mFuenteImagen = fuenteImagen.profile;
                    openGallery();
                }
                else if(i==1) {
                    mFuenteImagen = fuenteImagen.cover;
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

        if(imageSelected == 1) {
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }
        else if(imageSelected == 2) {
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
                            if (requestCode == RESULT_OK && imageSelected == 1 && mFuenteImagen == fuenteImagen.profile) {
                                try {
                                    mPhotoFile= null;
                                    mImageFile = FileUtil.from(EditProfileActivity.this, data.getData());//Nos transforma la URI de la imagen en el archivo (Creo).
                                    mCircleImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));

                                } catch (Exception e) {
                                    Log.w("ERROR", "Gallery 1 Intent failed, error: ", e);
                                    Toast.makeText(EditProfileActivity.this, "Error al cargar la imagen 1", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if (requestCode == RESULT_OK && imageSelected == 2 && mFuenteImagen == fuenteImagen.profile) {
                                try {
                                    mPhotoFile2 = null;
                                    mImageFile2 = FileUtil.from(EditProfileActivity.this, data.getData());//Nos transforma la URI de la imagen en el archivo (Creo).
                                    mImageViewCover.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
                                } catch (Exception e) {
                                    Log.w("ERROR", "Gallery 2 Intent failed, error: ", e);
                                    Toast.makeText(EditProfileActivity.this, "Error al cargar la imagen 2", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if(requestCode == RESULT_OK && mFuenteImagen == fuenteImagen.cover) { //Imagen de la camara.
                                if(imageSelected == 1) {
                                    mImageFile = null;
                                    mPhotoFile = new File(mAbsolutePhotoPath);
                                    Picasso.with(EditProfileActivity.this).load(mPhotoPath).into(mCircleImageViewProfile);
                                }
                                else if(imageSelected == 2) {
                                    mImageFile2 = null;
                                    mPhotoFile2 = new File(mAbsolutePhotoPath2);
                                    Picasso.with(EditProfileActivity.this).load(mPhotoPath2).into(mImageViewCover);
                                }
                                else
                                    Toast.makeText(EditProfileActivity.this, "Error valor imageSelected", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(EditProfileActivity.this, "Error al cargar la imagen de la camara", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

}