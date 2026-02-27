package com.example.hand_man_work;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {

    private static final String COLLECTION_USERS = "users";

    public static void createUserProfile(FirebaseUser user, String name, String phone, String userType, OnCompleteListener<Void> listener) {
        if (user == null) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("uid", user.getUid());
        userProfile.put("email", user.getEmail());
        userProfile.put("name", name);
        userProfile.put("phone", phone);
        userProfile.put("type", userType);
        userProfile.put("createdAt", FieldValue.serverTimestamp());

        db.collection(COLLECTION_USERS)
                .document(user.getUid())
                .set(userProfile)
                .addOnCompleteListener(listener);
    }

    public static void getUserType(FirebaseUser user, OnCompleteListener<DocumentSnapshot> listener) {
        if (user == null) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_USERS)
                .document(user.getUid())
                .get()
                .addOnCompleteListener(listener);
    }
}
