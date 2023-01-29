package com.precioLuz.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.precioLuz.R;
import com.precioLuz.activities.MainActivity;
import com.precioLuz.activities.PostActivity;
import com.precioLuz.adapters.PostsAdapter;
import com.precioLuz.models.Post;
import com.precioLuz.providers.AuthProvider;
import com.precioLuz.providers.PostProvider;

import java.util.Locale;

public class HomeFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener{

    View mView;
    FloatingActionButton mFab;
    MaterialSearchBar mSearchBar;

    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostAdapter;
    PostsAdapter mPostAdapterSearch;

    public HomeFragment() {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mFab = mView.findViewById(R.id.fab);
        mRecyclerView = mView.findViewById(R.id.recyclerViewHome);
        mSearchBar = mView.findViewById(R.id.searchBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());//para que me ponga las tarjetas una debajo de otra.
        mRecyclerView.setLayoutManager(linearLayoutManager);

        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        mSearchBar.setOnSearchActionListener(this);
        mSearchBar.inflateMenu(R.menu.main_menu);
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { //Menú opciones de cerrar sesión.
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.itemLogout) {
                    logout();
                }
                return true;
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPost();
            }
        });
        return mView;
    }

    private void searchByTitle (String title) {
        Query query = mPostProvider.getPostByTitle(title);//Consulta No-SQL
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class).build();

        mPostAdapterSearch = new PostsAdapter(options, getContext());
        mPostAdapterSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPostAdapterSearch);
        mPostAdapterSearch.startListening();
    }

    private void getAllPost() {
        Query query = mPostProvider.getAll();
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class).build();

        mPostAdapter = new PostsAdapter(options, getContext());
        mPostAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getAll();
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class).build();

        mPostAdapter = new PostsAdapter(options, getContext());
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
        if(mPostAdapterSearch!= null) {
            mPostAdapterSearch.stopListening();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPostAdapter.getListener()!= null) {
            mPostAdapter.getListener().remove();
        }
    }

    private void goToPost() {
        // Vamos de un fragment a un activity, por eso el metodo intent cambia respecto al que
        // teníamos en los activities.
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);

    }

    private void logout() {
        mAuthProvider.logout();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //Limpiamos historial del botón atras.
        startActivity(intent);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(!enabled){
            getAllPost();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        searchByTitle(text.toString().toLowerCase(Locale.ROOT));
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}