package com.socialtravel.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

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
    ImageView mImageViewPC;
    ImageView mImageViewPS4;
    ImageView mImageViewXbox;
    ImageView mImageViewNintendo;
    CircleImageView mCircleImageBack;
    TextView mTextViewCategory;
    String mCategory = null;
    String mTitle = "";
    String mdescription = "";
    SpotsDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();
        mDialog = new SpotsDialog(this);

        mImageViewPost1 = findViewById(R.id.imageViewPost1);
        mImageViewPost2 = findViewById(R.id.imageViewPost2);
        mButtonPost = findViewById(R.id.btnPost);
        mTextInputTitle = findViewById(R.id.textInputVideoGame);
        mTextInputDescription = findViewById(R.id.textInputDescription);
        mImageViewPC = findViewById(R.id.imageViewPC);
        mImageViewPS4 = findViewById(R.id.imageViewPS4);
        mImageViewXbox = findViewById(R.id.imageViewXbox);
        mImageViewNintendo = findViewById(R.id.imageViewNintendo);
        mTextViewCategory = findViewById(R.id.textViewCategory);
        mCircleImageBack = findViewById(R.id.circle_image_back);

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
                openGallery1();
            }
        });

        mImageViewPost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery2();
            }
        });

        mImageViewPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "PC";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewPS4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "PS4";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewXbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "XBOX";
                mTextViewCategory.setText(mCategory);
            }
        });

        mImageViewNintendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory = "NINTENDO";
                mTextViewCategory.setText(mCategory);
            }
        });
    }

    private void clickPost() {
        mTitle = mTextInputTitle.getText().toString();
        mdescription = mTextInputDescription.getText().toString();

        if(!mTitle.isEmpty() && !mdescription.isEmpty() && !mCategory.isEmpty()) {
            if(mImageFile!= null && mImageFile2!= null) {
                saveImage();
            }
            else {
                Toast.makeText(this, "Debes seleccionar una imagen.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Completa todos los campos para publicar.", Toast.LENGTH_LONG).show();
        }

    }

    private void saveImage() {
        mDialog.show();
        mDialog.setMessage("Guardando");
        mImageProvider.save(PostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();

                            mImageProvider.save(PostActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                                post.setTitle(mTitle);
                                                post.setDescription(mdescription);
                                                post.setCategory(mCategory);
                                                post.setIdUser(mAuthProvider.getUid());
                                                mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                                        mDialog.dismiss();
                                                        if(taskSave.isSuccessful()) {
                                                            Toast.makeText(PostActivity.this, "La información se almacenó correctamente.", Toast.LENGTH_LONG).show();
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
        mImageViewPost1.setImageResource(R.drawable.upload_image);
        mImageViewPost2.setImageResource(R.drawable.upload_image);
        mTitle = "";
        mdescription = "";
        mCategory = "";
        mImageFile = null;
        mImageFile2 = null;
    }

    private void openGallery1() { //De momento creo una función para cada imagen porque no puedo pasar parametro en launch.
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
                            if(requestCode == RESULT_OK) {
                                try {
                                    mImageFile = FileUtil.from(PostActivity.this, data.getData());//Nos transforma la URI de la imagen en el archivo (Creo).
                                    mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));

                                }catch(Exception e) {
                                    Log.w("ERROR","Gallery Intent failed, error: ", e);
                                    Toast.makeText(PostActivity.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                                    // ...
                                }
                            }

                        }
                    }
            );

    private void openGallery2() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        activityResultLauncher2.launch(galleryIntent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher2 =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int requestCode = activityResult.getResultCode();
                            Intent data = activityResult.getData();

                            //Result returned from launching the Intent
                            if(requestCode == RESULT_OK) {
                                try {
                                    mImageFile2 = FileUtil.from(PostActivity.this, data.getData());//Nos transforma la URI de la imagen en el archivo (Creo).
                                    mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));

                                }catch(Exception e) {
                                    Log.w("ERROR","Gallery Intent failed, error: ", e);
                                    Toast.makeText(PostActivity.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                                    // ...
                                }
                            }

                        }
                    }
            );

}