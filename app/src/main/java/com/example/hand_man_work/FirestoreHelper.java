package com.example.hand_man_work;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simplified helper class to manage all interactions with Firebase Firestore.
 */
public class FirestoreHelper {

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_BOOKINGS = "bookings";

    // Saves or updates a user profile
    public static void saveUser(FirebaseUser user, String name, String phone, String type, OnCompleteListener<Void> listener) {
        if (user == null) return;
        
        Map<String, Object> data = new HashMap<>();
        data.put("uid", user.getUid());
        data.put("email", user.getEmail());
        data.put("name", name);
        data.put("phone", phone);
        data.put("type", type);
        data.put("createdAt", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(user.getUid())
                .set(data)
                .addOnCompleteListener(listener);
    }

    // Fetches user document to check role
    public static void checkUserType(String uid, OnCompleteListener<DocumentSnapshot> listener) {
        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Helper to get user type. Delegates to checkUserType.
     */
    public static void getUserType(FirebaseUser user, OnCompleteListener<DocumentSnapshot> listener) {
        if (user == null) return;
        checkUserType(user.getUid(), listener);
    }

    public static void getUserData(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        checkUserType(userId, listener);
    }

    public static void updateCustomerProfile(String userId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(userId).update(updates).addOnCompleteListener(listener);
    }

    public static void updateWorkerProfile(String userId, String name, String phone, List<String> skills, double hourlyRate, String photoUrl, OnCompleteListener<Void> listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("skills", skills);
        updates.put("hourlyRate", hourlyRate);
        updates.put("photoUrl", photoUrl);

        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(userId).update(updates).addOnCompleteListener(listener);
    }

    public static void getWorkers(OnCompleteListener<QuerySnapshot> listener) {
        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .whereEqualTo("type", "worker")
                .get().addOnCompleteListener(listener);
    }

    public static void createBooking(String customerId, String workerId, String workerName, Date dateTime, String description, String location, OnCompleteListener<Void> listener) {
        Map<String, Object> booking = new HashMap<>();
        booking.put("customerId", customerId);
        booking.put("workerId", workerId);
        booking.put("workerName", workerName);
        booking.put("dateTime", dateTime);
        booking.put("description", description);
        booking.put("location", location);
        booking.put("status", "pending");
        booking.put("createdAt", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance().collection(COLLECTION_BOOKINGS)
                .document().set(booking).addOnCompleteListener(listener);
    }

    // Fetch bookings for a worker
    public static void getWorkerBookings(String workerId, OnCompleteListener<QuerySnapshot> listener) {
        FirebaseFirestore.getInstance().collection(COLLECTION_BOOKINGS)
                .whereEqualTo("workerId", workerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(listener);
    }

    // Fetch bookings for a customer
    public static void getCustomerBookings(String customerId, OnCompleteListener<QuerySnapshot> listener) {
        FirebaseFirestore.getInstance().collection(COLLECTION_BOOKINGS)
                .whereEqualTo("customerId", customerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(listener);
    }

    // Update booking status
    public static void updateBookingStatus(String bookingId, String newStatus, OnCompleteListener<Void> listener) {
        FirebaseFirestore.getInstance().collection(COLLECTION_BOOKINGS)
                .document(bookingId)
                .update("status", newStatus)
                .addOnCompleteListener(listener);
    }
}
