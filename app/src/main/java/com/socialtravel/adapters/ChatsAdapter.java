package com.socialtravel.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.socialtravel.R;
import com.socialtravel.activities.ChatActivity;
import com.socialtravel.models.Chat;
import com.socialtravel.providers.AuthProvider;
import com.socialtravel.providers.UserProvider;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsAdapter extends FirestoreRecyclerAdapter <Chat, ChatsAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvidier;
    AuthProvider mAuthProvider;

    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;
        mUserProvidier = new UserProvider();
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String chatId = document.getId();
        if(mAuthProvider.getUid().equals(chat.getIdUser1())) {
            getUserInfo(chat.getIdUser2(), holder);
        }
        else {
            getUserInfo(chat.getIdUser1(), holder);
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(chatId, chat.getIdUser1(), chat.getIdUser2());
            }
        });
    }

    private void goToChatActivity(String chatId, String idUser1, String idUser2) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat", chatId);
        intent.putExtra("idUser1", idUser1);
        intent.putExtra("idUser2", idUser2);
        context.startActivity(intent);

    }

    private void getUserInfo(String idUser, ViewHolder holder) {
        mUserProvidier.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsername.setText(username.toUpperCase(Locale.ROOT));
                    }
                    if(documentSnapshot.contains("image_profile")) {
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if(imageProfile != null) {
                            if(!imageProfile.isEmpty()) {
                               Picasso.with(context).load(imageProfile).into(holder.circleImageChat); //Para traer una URL necesitamos la libreria Picasso.
                            }
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewLastMessageChat;
        CircleImageView circleImageChat;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.textViewUsernameChat);
            textViewLastMessageChat = view.findViewById(R.id.textViewLastMessageChat);
            circleImageChat = view.findViewById(R.id.circleImageChat);
            viewHolder = view;
        }
    }
}
