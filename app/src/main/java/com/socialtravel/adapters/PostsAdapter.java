package com.socialtravel.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.socialtravel.R;
import com.socialtravel.activities.PostDetailActivity;
import com.socialtravel.models.Post;
import com.socialtravel.providers.PostProvider;
import com.socialtravel.providers.UserProvider;
import com.squareup.picasso.Picasso;

import java.util.Locale;


public class PostsAdapter extends FirestoreRecyclerAdapter <Post, PostsAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;


    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();


        holder.textViewTitle.setText(post.getTitle().toUpperCase(Locale.ROOT));
        holder.textViewDescription.setText(post.getDescription());
        if(post.getImage1()!= null) {
            if(!post.getImage1().isEmpty()){
                Picasso.with(context).load(post.getImage1()).into(holder.imageViewPost); //Para traer una URL necesitamos la libreria Picasso.
            }
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);//Origen y destino.
                intent.putExtra("id", postId);
                context.startActivity(intent);
            }
        });

        getUserInfo(post.getIdUser(), holder);
    }

    private void getUserInfo(String idUser, ViewHolder holder) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsername.setText("By: " + username.toUpperCase(Locale.ROOT));
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewUsername;
        ImageView imageViewPost;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewDescription = view.findViewById(R.id.textViewDescriptionPostCard);
            textViewUsername = view.findViewById(R.id.textViewUsernamePostCard);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            viewHolder = view;
        }
    }
}
