package com.m2brcorp.geostatus.Util;

import android.content.Context;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by vinic on 20/01/2017.
 */

public class ReferenceFB {

    static String referencedSon;

    Firebase firebase;

    FirebaseUser user;

    static FirebaseDatabase mDatabase;

    public DatabaseReference getFirebaseContextReference(){
        return FirebaseDatabase.getInstance().getReference(referencedSon);
    }

    public FirebaseAuth getFirebaseAuthReference(){
        return FirebaseAuth.getInstance();
    }

    public Firebase getFirebaseInstance(Context context){
        firebase.setAndroidContext(context);
        firebase = new Firebase("https://geostatus-f6e94.firebaseio.com/");
        return firebase;
    }

    public String getReferencedSon() { return referencedSon; }

    public void setReferencedSon(String referencedSon) {
        this.referencedSon = referencedSon;
    }

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

    public String getUsuarioLogado(){
        user = getFirebaseAuthReference().getCurrentUser();
        return user.getEmail();
    }
}
