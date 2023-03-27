package com.precioLuz.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.precioLuz.R;
import com.precioLuz.models.Post;
import com.precioLuz.providers.AuthProvider;
import com.precioLuz.providers.LikesProvider;
import com.precioLuz.providers.PostProvider;
import com.precioLuz.providers.UserProvider;
import com.precioLuz.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyPostAdapter extends FirestoreRecyclerAdapter <Post, MyPostAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;


    public MyPostAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
        mLikesProvider = new LikesProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        String relativeTime = RelativeTime.getTimeAgo(post.getTimestamp(), context);
        holder.textViewRelativeTime.setText(relativeTime);
        holder.textViewTitle.setText(post.getTitle().toUpperCase(Locale.ROOT));

        if(post.getIdUser().equals(mAuthProvider.getUid())) {
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.imageViewDelete.setVisibility(View.GONE);//Ocultamos la X de borrar publicación para que los otros usuarios que vean un perfil no puedan eliminar una publicación.
        }

        if(post.getImage1()!= null) {
            if(!post.getImage1().isEmpty()){
                Picasso.with(context).load(post.getImage1()).into(holder.circleImagePost); //Para traer una URL necesitamos la libreria Picasso.
            }
        }

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDelete(postId);
            }
        });

    }

    private void showConfirmDelete(String postId) {
        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar Publicación")
                .setMessage("¿Estas seguro de que quieres eliminar el Post?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(postId);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deletePost(String postId) {
        mPostProvider.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(context, "El post se eliminó correctamente", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(context, "No se pudo eliminar el post.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewRelativeTime;
        CircleImageView circleImagePost;
        ImageView imageViewDelete;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitleMyPost);
            textViewRelativeTime = view.findViewById(R.id.textViewRelativeTimeMyPost);
            circleImagePost = view.findViewById(R.id.circleImageMyPost);
            imageViewDelete = view.findViewById(R.id.imageViewDeleteMyPost);
            viewHolder = view;
        }
    }
}
