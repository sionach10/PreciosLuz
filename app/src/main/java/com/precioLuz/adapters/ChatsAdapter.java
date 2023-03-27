package com.precioLuz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.precioLuz.R;
import com.precioLuz.models.Chat;
import com.precioLuz.providers.AuthProvider;
import com.precioLuz.providers.ChatsProvider;
import com.precioLuz.providers.MessagesProvider;
import com.precioLuz.providers.UserProvider;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsAdapter extends FirestoreRecyclerAdapter <Chat, ChatsAdapter.ViewHolder> {

    Context context;
    UserProvider mUserProvidier;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;
    MessagesProvider mMessagesProvider;
    ListenerRegistration mListener;
    ListenerRegistration mListenerLastMessage;

    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;
        mUserProvidier = new UserProvider();
        mAuthProvider = new AuthProvider();
        mChatsProvider = new ChatsProvider();
        mMessagesProvider = new MessagesProvider();
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


        getLastMessage(chatId, holder.textViewLastMessageChat);

        String idSender = "";
        if(mAuthProvider.getUid().equals(chat.getIdUser1())) {
            idSender = chat.getIdUser2(); //Esto no llego a pillarlo, por qué es el usuario contrario. Debería repasarlo.
        }
        else {
            idSender = chat.getIdUser1();
        }

        getMessageNotRead(chatId,idSender, holder.textViewMessaegNotRead, holder.frameLayoutMessageNotRead);
    }

    private void getMessageNotRead(String chatId, String idSender, TextView textViewMessaegNotRead, final FrameLayout frameLayoutMessageNotRead) {

        mListener = mMessagesProvider.getMessagesByChatAndSender(chatId, idSender).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    int size = value.size();
                    if(size > 0) {
                        frameLayoutMessageNotRead.setVisibility(View.VISIBLE);
                        textViewMessaegNotRead.setText(String.valueOf(size));
                    }
                    else {
                        frameLayoutMessageNotRead.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    public ListenerRegistration getListener() { //Creo este método para que desde la clase donde llamo al ChatAdapter (desde ChatFragment), pueda matar el escuchador.
        return mListener;
    }

    public ListenerRegistration getListenerLastMessage() { //Para matar al listener desde el activity ChatFragment.
        return mListenerLastMessage;
    }


    private void getLastMessage(String chatId, TextView textViewLastMessageChat) {
        mListenerLastMessage = mMessagesProvider.getLastMessage(chatId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!= null) {
                    int size = value.size();
                    if(size > 0) {
                        String lastMessage = value.getDocuments().get(0).getString("message");
                        textViewLastMessageChat.setText(lastMessage);
                    }
                }

            };
        });
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
                    if(documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
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
        TextView textViewMessaegNotRead;
        CircleImageView circleImageChat;
        FrameLayout frameLayoutMessageNotRead;

        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.textViewUsernameChat);
            textViewLastMessageChat = view.findViewById(R.id.textViewLastMessageChat);
            textViewMessaegNotRead = view.findViewById(R.id.textViewMessageNotRead);
            circleImageChat = view.findViewById(R.id.circleImageChat);
            frameLayoutMessageNotRead = view.findViewById(R.id.frameLayoutMessageNotRead);
            viewHolder = view;
        }
    }
}
