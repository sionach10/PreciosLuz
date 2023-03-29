package com.precioLuz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.precioLuz.R;
import com.precioLuz.adapters.PostsAdapter;
import com.precioLuz.models.Post;
import com.precioLuz.providers.AuthProvider;
import com.precioLuz.providers.PostProvider;

public class FiltersActivity extends AppCompatActivity {

    String mExtraCategory;
    TextView mTextViewNumberFilter;

    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostAdapter;
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        mRecyclerView = findViewById(R.id.recyclerViewFilter);

        mToolbar = findViewById(R.id.toolbar);
        mTextViewNumberFilter = findViewById(R.id.textViewNumberFilter);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//Botón de ir hacia atrás. Necesario añadir en el AndroidManifest el parent al que dirigirse al clicar el botón atrás.

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FiltersActivity.this);//para que me ponga las tarjetas una debajo de otra. (1 sola columna).
        mRecyclerView.setLayoutManager(new GridLayoutManager(FiltersActivity.this, 2)); //Para mostrar 2 columnas.

        mExtraCategory = getIntent().getStringExtra("category");

        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

    }

    @Override
    public void onStart() { //Aquí se instancia el adapter.
        super.onStart();
        Query query = mPostProvider.getPostByCategoryAndTimestap(mExtraCategory);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class).build();

        mPostAdapter = new PostsAdapter(options, FiltersActivity.this, mTextViewNumberFilter);
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //Fix para que el botón atrás vuelva al último fragment.
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}