package com.socialtravel.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.Distribution;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.socialtravel.R;
import com.socialtravel.adapters.SliderAdapter;
import com.socialtravel.models.Comment;
import com.socialtravel.models.SliderItem;
import com.socialtravel.providers.AuthProvider;
import com.socialtravel.providers.CommentsProvider;
import com.socialtravel.providers.PostProvider;
import com.socialtravel.providers.UserProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();
    PostProvider mPostProvider;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    CommentsProvider mCommentsProvider;

    String mExtraPostId;
    TextView mTextViewTitle;
    TextView mTextViewDescription;
    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewNameCategory;
    ImageView mImageViewCategory;
    CircleImageView mCircleImageViewProfile;
    Button mButtonShowProfile;
    CircleImageView mCircleImageViewBack;
    String mIdUser = "";
    FloatingActionButton mFabComment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mSliderView = findViewById(R.id.imageSlider);
        mTextViewTitle = findViewById(R.id.textViewTitle);
        mTextViewDescription = findViewById(R.id.textViewDescription);
        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewNameCategory = findViewById(R.id.textViewNameCategory);
        mImageViewCategory = findViewById(R.id.imageViewCategory);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mButtonShowProfile = findViewById(R.id.btnShowProfile);
        mCircleImageViewBack = findViewById(R.id.circleImageBack);
        mFabComment = findViewById(R.id.fabComment);
        mExtraPostId = getIntent().getStringExtra("id");

        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mCommentsProvider = new CommentsProvider();


        getPost();

        mFabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComment();
            }
        });

        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButtonShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowProfile();
            }
        });
    }

    private void showDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);
        alert.setTitle("¡COMENTARIO!");
        alert.setMessage("Ingresa tu comentario");

        EditText editText = new EditText(PostDetailActivity.this);
        editText.setHint("texto");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT

        );
        params.setMargins(36,0,36,36);
        editText.setLayoutParams(params);

        RelativeLayout container = new RelativeLayout(PostDetailActivity.this);

        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        container.setLayoutParams(relativeParams);
        container.addView(editText);

        alert.setView(container);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString();
                if(!value.isEmpty()) {
                    createComment(value);
                }
                else {
                    Toast.makeText(PostDetailActivity.this, "Debe ingresar un comentario.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    private void createComment(String value) {
        Comment comment = new Comment();
        comment.setComment(value);
        comment.setIdPost(mExtraPostId);
        comment.setIdUser(mAuthProvider.getUid());
        comment.setTimestamp(new Date().getTime());
        mCommentsProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(PostDetailActivity.this, "El comentario se creó correctamente.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(PostDetailActivity.this, "Error al guardar el comentario.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToShowProfile() {
        if(!mIdUser.equals("")) {
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser", mIdUser);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "El id del usuario aún no se ha cargado.", Toast.LENGTH_SHORT).show();
        }

    }

    private void instanceSlider() {
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //Forma de pasar los bullets de la animación.
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }
    private void getPost() {
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("image1")){
                        String image1 = documentSnapshot.getString("image1");
                        SliderItem item = new SliderItem();
                        item.setImageURL(image1);
                        mSliderItems.add(item);
                    }
                    if(documentSnapshot.contains("image2")) {
                        String image2 = documentSnapshot.getString("image2");
                        SliderItem item = new SliderItem();
                        item.setImageURL(image2);
                        mSliderItems.add(item);
                    }
                    if(documentSnapshot.contains("title")) {
                        String title = documentSnapshot.getString("title");
                        mTextViewTitle.setText(title.toUpperCase());
                    }
                    if(documentSnapshot.contains("description")) {
                        String description = documentSnapshot.getString("description");
                        mTextViewDescription.setText(description);
                    }
                    if(documentSnapshot.contains("category")) {
                        String category = documentSnapshot.getString("category");
                        mTextViewNameCategory.setText(category);

                        switch (category) {
                            case "PS4":
                                mImageViewCategory.setImageResource(R.drawable.icon_ps4);
                                break;
                            case "PC":
                                mImageViewCategory.setImageResource(R.drawable.icon_pc);
                                break;
                            case "NINTENDO":
                                mImageViewCategory.setImageResource(R.drawable.icon_nintendo);
                                break;
                            case "XBOX":
                                mImageViewCategory.setImageResource(R.drawable.icon_xbox);
                                break;
                        }
                    }
                    if(documentSnapshot.contains("idUser")) {
                        mIdUser = documentSnapshot.getString("idUser");
                        getUserInfo(mIdUser);
                    }
                    instanceSlider();
                }
            }
        });
    }

    private void getUserInfo(String idUser) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                }
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("phone")) {
                        String phone = documentSnapshot.getString("phone");
                        mTextViewPhone.setText(phone);
                    }
                }
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("image_profile")) {
                        String image_profile = documentSnapshot.getString("image_profile");
                        Picasso.with(PostDetailActivity.this).load(image_profile).into(mCircleImageViewProfile);
                    }
                }
            }
        });
    }


}