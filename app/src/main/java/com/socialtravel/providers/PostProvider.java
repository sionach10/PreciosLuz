package com.socialtravel.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.socialtravel.models.Post;

public class PostProvider {

    CollectionReference mCollection;

    public PostProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Post");
    }

    public Task<Void> save(Post post) {
        return mCollection.document().set(post);
    }

    public Query getAll(){
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING);
    }
    public Query getPostByTitle(String title){
        return mCollection.orderBy("title").startAt(title).endAt(title+'\uf8ff');//Codigo para que sea como una SQL. El uf8ff es como el % en el like de SQL.
    }

    public Query getPostByCategoryAndTimestap(String category){
        return mCollection.whereEqualTo("category", category).orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Query getPostByUser(String id) {
        return mCollection.whereEqualTo("idUser", id); //Busca todos los post donde el idUser = id.
    }
    public Task<DocumentSnapshot> getPostById(String id) {
        return mCollection.document(id).get();
    }

    public Task<Void> delete(String id) {
        return mCollection.document(id).delete();
    }


}
