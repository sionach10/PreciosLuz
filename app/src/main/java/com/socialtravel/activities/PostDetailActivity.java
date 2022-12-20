package com.socialtravel.activities;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.socialtravel.R;
import com.socialtravel.adapters.CommentAdapter;
import com.socialtravel.adapters.SliderAdapter;
import com.socialtravel.models.Comment;
import com.socialtravel.models.FCMBody;
import com.socialtravel.models.FCMResponse;
import com.socialtravel.models.SliderItem;
import com.socialtravel.providers.AuthProvider;
import com.socialtravel.providers.CommentsProvider;
import com.socialtravel.providers.LikesProvider;
import com.socialtravel.providers.NotificationProvider;
import com.socialtravel.providers.PostProvider;
import com.socialtravel.providers.TokenProvider;
import com.socialtravel.providers.UserProvider;
import com.socialtravel.utils.RelativeTime;
import com.socialtravel.utils.ViewedMessageHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    CommentAdapter mCommentsAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();

    PostProvider mPostProvider;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    CommentsProvider mCommentsProvider;
    LikesProvider mLikesProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    String mExtraPostId;
    String mIdUser = "";
    TextView mTextViewTitle;
    TextView mTextViewDescription;
    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewNameCategory;
    TextView mTextViewRelativeTime;
    TextView mTextViewLikes;
    ImageView mImageViewCategory;
    CircleImageView mCircleImageViewProfile;
    Button mButtonShowProfile;
    FloatingActionButton mFabComment;
    RecyclerView mRecyclerView;
    Toolbar mToolbar;

    ListenerRegistration mListener;

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
        mTextViewRelativeTime = findViewById(R.id.textViewRelativeTime);
        mTextViewLikes = findViewById(R.id.textViewLikes);
        mImageViewCategory = findViewById(R.id.imageViewCategory);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mButtonShowProfile = findViewById(R.id.btnShowProfile);
        mFabComment = findViewById(R.id.fabComment);
        mExtraPostId = getIntent().getStringExtra("id");
        mRecyclerView = findViewById(R.id.recyclerViewComment);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);//para que me ponga las tarjetas una debajo de otra.
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mCommentsProvider = new CommentsProvider();
        mLikesProvider = new LikesProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();


        mFabComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComment();
            }
        });

        mButtonShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowProfile();
            }
        });
        getPost();
        getNumberLikes();
    }

    private void getNumberLikes() {
        mListener = mLikesProvider.getLikesByPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                mLikesProvider.getLikesByPost(mExtraPostId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots!= null) {
                            int numberLikes = queryDocumentSnapshots.size();
                            if(numberLikes ==1) {
                                mTextViewLikes.setText(numberLikes + " like");
                            }
                            else {
                                mTextViewLikes.setText(numberLikes + " likes");
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = mCommentsProvider.getCommentByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class).build();

        mCommentsAdapter = new CommentAdapter(options, PostDetailActivity.this);
        mRecyclerView.setAdapter(mCommentsAdapter);
        mCommentsAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, PostDetailActivity.this);//Controlador de si el usuario está online o no.
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostDetailActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCommentsAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener!= null) {
            mListener.remove();
        }
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

    private void createComment(final String value) {
        Comment comment = new Comment();
        comment.setComment(value);
        comment.setIdPost(mExtraPostId);
        comment.setIdUser(mAuthProvider.getUid());
        comment.setTimestamp(new Date().getTime());
        mCommentsProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    sendNotification(value);
                    Toast.makeText(PostDetailActivity.this, "El comentario se creó correctamente.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(PostDetailActivity.this, "Error al guardar el comentario.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification(String comment) {
        if(mIdUser == null) {
            return;
        }
        mTokenProvider.getToken(mIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "NUEVO COMENTARIO");
                        data.put("body", comment);
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if(response.body()!= null) {
                                    if(response.body().getSuccess()== 1) {
                                        Toast.makeText(PostDetailActivity.this, "Notificación enviada correctamente.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(PostDetailActivity.this, "La notificación no se pudo enviar.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(PostDetailActivity.this, "La notificación no se pudo enviar."+ response.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {
                                Toast.makeText(PostDetailActivity.this, "Error: "+ t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(PostDetailActivity.this, "El token no existe.", Toast.LENGTH_SHORT).show();
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
                    if(documentSnapshot.contains("timestamp")) {
                        long timestamp = documentSnapshot.getLong("timestamp");
                        String relativeTime = RelativeTime.getTimeAgo(timestamp, PostDetailActivity.this);
                        mTextViewRelativeTime.setText(relativeTime);
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
                    if(documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        Picasso.with(PostDetailActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                    }
                }
            }
        });
    }


}