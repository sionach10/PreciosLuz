package com.socialtravel.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.socialtravel.R;
import com.socialtravel.models.Post;
import com.socialtravel.providers.AuthProvider;
import com.socialtravel.providers.ImageProvider;
import com.socialtravel.providers.PostProvider;
import com.socialtravel.utils.FileUtil;
import com.socialtravel.utils.ViewedMessageHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    private static final int IMAGE_1 = 1;
    private static final int IMAGE_2 = 2;
    private static final int CAMERA_REQUEST_CODE = 3;

    ImageView mImageViewPost1;
    ImageView mImageViewPost2;
    File mImageFile;
    File mImageFile2;
    Button mButtonPost;
    ImageProvider mImageProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;
    TextInputEditText mTextInputTitle;
    TextInputEditText mTextInputDescription;
    CircleImageView mCircleImageBack;
    TextView mTextViewCategory;
    String mCategory = null;
    String mTitle = "";
    String mdescription = "";
    SpotsDialog mDialog;
    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];
    enum fuenteImagen { camara, galeria}
    fuenteImagen mFuenteImagen;
    int imageSelected;

    //FiltroCategorias.
    String[] itemsFiltro = {"Salir de Fiesta", "Conocer Gente", "Quedadas Deportes", "Clases Online", "Planear Viajes", "Otros"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;


    //Foto 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;

    //Foto 2
    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();
        mDialog = new SpotsDialog(this);
        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[] {"Galería", "Tomar foto"};

        mImageViewPost1 = findViewById(R.id.imageViewPost1);
        mImageViewPost2 = findViewById(R.id.imageViewPost2);
        mButtonPost = findViewById(R.id.btnPost);
        mTextInputTitle = findViewById(R.id.textInputVideoGame);
        mTextInputDescription = findViewById(R.id.textInputDescription);
        mTextViewCategory = findViewById(R.id.textViewCategory);
        mCircleImageBack = findViewById(R.id.circleImageBack);
        autoCompleteTextView = findViewById(R.id.autoCompleteFilter);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, itemsFiltro);
        autoCompleteTextView.setAdapter(adapterItems);

        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPost();
            }
        });

        mImageViewPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelected = 1;
                selectOptionImage();

            }
        });

        mImageViewPost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelected = 2;
                selectOptionImage();
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item: "+item, Toast.LENGTH_LONG).show();
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
            Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.socialtravel", photoFile);
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

    private void clickPost() {
        mTitle = mTextInputTitle.getText().toString().toUpperCase(Locale.ROOT);
        mdescription = mTextInputDescription.getText().toString();

        if(!mTitle.isEmpty() && !mdescription.isEmpty() && !mCategory.isEmpty()) {

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
        else {
            Toast.makeText(this, "Completa todos los campos para publicar.", Toast.LENGTH_LONG).show();
        }

    }

    private void saveImage(File _mImageFile1, File _mImageFile2) {
        mDialog.show();
        mDialog.setMessage("Guardando");
        mImageProvider.save(PostActivity.this, _mImageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();

                            mImageProvider.save(PostActivity.this, _mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful()) {
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                final String url2 = uri2.toString();
                                                Post post = new Post();
                                                post.setImage1(url);
                                                post.setImage2(url2);
                                                post.setTitle(mTitle.toLowerCase(Locale.ROOT));
                                                post.setDescription(mdescription);
                                                post.setCategory(mCategory);
                                                post.setIdUser(mAuthProvider.getUid());
                                                post.setTimestamp(new Date().getTime());
                                                mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                                        mDialog.dismiss();
                                                        if(taskSave.isSuccessful()) {
                                                            //Toast.makeText(PostActivity.this, "La información se almacenó correctamente.", Toast.LENGTH_LONG).show();
                                                            clearForm();
                                                        }
                                                        else {
                                                            Toast.makeText(PostActivity.this, "No se pudo almacenar la configuración.", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else {
                                        mDialog.dismiss();
                                        Toast.makeText(PostActivity.this, "Hubo un error al almacenar la imagen 2."+ taskImage2.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Hubo un error al almacenar la imagen 1."+ task.getException(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void clearForm() {
        mTextInputTitle.setText("");
        mTextInputDescription.setText("");
        mTextViewCategory.setText("");
        mImageViewPost1.setImageResource(R.mipmap.upload_image);
        mImageViewPost2.setImageResource(R.mipmap.upload_image);
        mTitle = "";
        mdescription = "";
        mCategory = "";
        mImageFile = null;
        mImageFile2 = null;
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
                            if (requestCode == RESULT_OK && imageSelected == 1 && mFuenteImagen == fuenteImagen.galeria) {
                                try {
                                    mPhotoFile= null;
                                    mImageFile = FileUtil.from(PostActivity.this, data.getData());//Nos transforma la URI de la imagen en el archivo (Creo).
                                    mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));

                                } catch (Exception e) {
                                    Log.w("ERROR", "Gallery 1 Intent failed, error: ", e);
                                    Toast.makeText(PostActivity.this, "Error al cargar la imagen 1", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if (requestCode == RESULT_OK && imageSelected == 2 && mFuenteImagen == fuenteImagen.galeria) {
                                try {
                                    mPhotoFile2 = null;
                                    mImageFile2 = FileUtil.from(PostActivity.this, data.getData());//Nos transforma la URI de la imagen en el archivo (Creo).
                                    mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
                                } catch (Exception e) {
                                    Log.w("ERROR", "Gallery 2 Intent failed, error: ", e);
                                    Toast.makeText(PostActivity.this, "Error al cargar la imagen 2", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if(requestCode == RESULT_OK && mFuenteImagen == fuenteImagen.camara) { //Imagen de la camara.
                                if(imageSelected == 1) {
                                    mImageFile = null;
                                    mPhotoFile = new File(mAbsolutePhotoPath);
                                    Picasso.with(PostActivity.this).load(mPhotoPath).into(mImageViewPost1);
                                }
                                else if(imageSelected == 2) {
                                    mImageFile2 = null;
                                    mPhotoFile2 = new File(mAbsolutePhotoPath2);
                                    Picasso.with(PostActivity.this).load(mPhotoPath2).into(mImageViewPost2);
                                }
                                else
                                    Toast.makeText(PostActivity.this, "Error valor imageSelected", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(PostActivity.this, "Error al cargar la imagen de la camara", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

    @Override
    protected void onStart() { //Metodos del ciclo de vida de la app.
        super.onStart();
        ViewedMessageHelper.updateOnline(true, PostActivity.this);//Controlador de si el usuario está online o no.
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostActivity.this);
    }
}