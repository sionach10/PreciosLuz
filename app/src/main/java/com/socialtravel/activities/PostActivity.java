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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
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
import java.io.IOException;
import java.net.URI;

public class PostActivity extends AppCompatActivity {

    ImageView mImageViewPost1;
    ImageView mImageViewPost2;
    File mImageFile;
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
    TextView mTextViewCategory;
    String mCategory = null;
    String mtitle = "";
    String mdescription = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();

        mImageViewPost1 = findViewById(R.id.imageViewPost1);
        mButtonPost = findViewById(R.id.btnPost);
        mTextInputTitle = findViewById(R.id.textInputVideoGame);
        mTextInputDescription = findViewById(R.id.textInputDescription);
        mImageViewPC = findViewById(R.id.imageViewPC);
        mImageViewPS4 = findViewById(R.id.imageViewPS4);
        mImageViewXbox = findViewById(R.id.imageViewXbox);
        mImageViewNintendo = findViewById(R.id.imageViewNintendo);
        mTextViewCategory = findViewById(R.id.textViewCategory);

        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPost();
            }
        });

        mImageViewPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
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
        mtitle = mTextInputTitle.getText().toString();
        mdescription = mTextInputDescription.getText().toString();

        if(!mtitle.isEmpty() && !mdescription.isEmpty() && !mCategory.isEmpty()) {
            if(mImageFile!= null) {
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
        mImageProvider.save(PostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Post post = new Post();
                            post.setImage1(url);
                            post.setTitle(mtitle);
                            post.setDescription(mdescription);
                            post.setCategory(mCategory);
                            post.setIdUser(mAuthProvider.getUid());
                            mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> taskSave) {
                                    if(taskSave.isSuccessful()) {
                                        Toast.makeText(PostActivity.this, "La información se almacenó correctamente.", Toast.LENGTH_LONG);
                                    }
                                    else {
                                        Toast.makeText(PostActivity.this, "No se pudo almacenar la configuración.", Toast.LENGTH_LONG);
                                    }
                                }
                            });
                        }
                    });
                }
                else {
                    Toast.makeText(PostActivity.this, "Hubo un error al almacenar la imagen."+ task.getException(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        activityResultLauncher.launch(galleryIntent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher =
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

}