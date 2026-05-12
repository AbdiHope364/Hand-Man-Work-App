package com.example.hand_man_work_new;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String USERS_COLLECTION = "users";
    private static final String BOOKINGS_COLLECTION = "bookings";

    public interface FirestoreCallback<T> {
        void onComplete(Task<T> task);
    }

    // ============ SAMPLE DATA METHODS ============

    public static void addSampleWorkers() {
        Log.d(TAG, "Adding sample workers...");
        
        addWorker("John Plumbing", Arrays.asList("plumbing", "pipe repair"), 30.0, "+1234567890");
        addWorker("Mike Electric", Arrays.asList("electrical", "wiring"), 35.0, "+1234567891");
        addWorker("Sarah Carpenter", Arrays.asList("carpentry", "furniture"), 28.0, "+1234567892");
        addWorker("Alex Handyman", Arrays.asList("painting", "mounting"), 25.0, "+1234567893");
    }

    private static void addWorker(String name, List<String> skills, double rate, String phone) {
        Map<String, Object> worker = new HashMap<>();
        worker.put("name", name);
        worker.put("type", "worker");
        worker.put("skills", skills);
        worker.put("hourlyRate", rate);
        worker.put("phone", phone);
        worker.put("createdAt", new Date());
        worker.put("email", name.toLowerCase().replace(" ", ".") + "@example.com");

        db.collection(USERS_COLLECTION)
                .add(worker)
                .addOnSuccessListener(docRef -> 
                    Log.d(TAG, "✅ Added worker: " + name))
                .addOnFailureListener(e -> 
                    Log.e(TAG, "❌ Error adding worker: " + name, e));
    }

    // ============ USER METHODS ============

    public static void getUserType(FirebaseUser user, FirestoreCallback<DocumentSnapshot> callback) {
        String userId = user.getUid();
        db.collection(USERS_COLLECTION).document(userId)
            .get()
            .addOnCompleteListener(task -> {
                if (callback != null) callback.onComplete(task);
            });
    }

    public static void saveUser(FirebaseUser user, String name, String phone, String type, 
                                FirestoreCallback<Void> callback) {
        String userId = user.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("email", user.getEmail());
        userData.put("type", type);
        userData.put("createdAt", new Date());

        db.collection(USERS_COLLECTION).document(userId)
            .set(userData, SetOptions.merge())
            .addOnCompleteListener(task -> {
                if (callback != null) callback.onComplete(task);
            });
    }

    public static void getUserData(String userId, FirestoreCallback<DocumentSnapshot> callback) {
        db.collection(USERS_COLLECTION).document(userId)
            .get()
            .addOnCompleteListener(task -> {
                if (callback != null) callback.onComplete(task);
            });
    }

    public static void updateUser(String userId, Map<String, Object> updates, 
                                 FirestoreCallback<Void> callback) {
        db.collection(USERS_COLLECTION).document(userId)
            .update(updates)
            .addOnCompleteListener(task -> {
                if (callback != null) callback.onComplete(task);
            });
    }

    public static void updateCustomerProfile(String userId, Map<String, Object> updates, 
                                            FirestoreCallback<Void> callback) {
        updateUser(userId, updates, callback);
    }

    public static void updateWorkerProfile(String userId, String name, String phone, 
                                           List<String> skills, double rate, 
                                           String photoUrl, FirestoreCallback<Void> callback) {
        Map<String, Object> updates = new HashMap<>();
        if (name != null) updates.put("name", name);
        if (phone != null) updates.put("phone", phone);
        if (skills != null) updates.put("skills", skills);
        if (rate > 0) updates.put("hourlyRate", rate);
        if (photoUrl != null) updates.put("photoUrl", photoUrl);
        updates.put("type", "worker");

        db.collection(USERS_COLLECTION).document(userId)
            .update(updates)
            .addOnCompleteListener(task -> {
                if (callback != null) callback.onComplete(task);
            });
    }

    public static void getAllWorkers(FirestoreCallback<QuerySnapshot> callback) {
        db.collection(USERS_COLLECTION)
            .whereEqualTo("type", "worker")
            .get()
            .addOnCompleteListener(task -> {
                if (callback != null) callback.onComplete(task);
            });
    }

    // ============ BOOKING METHODS ============

    public static void createBooking(String customerId, String workerId, String workerName,
                                     Date dateTime, String description, 
                                     String location, FirestoreCallback<Void> callback) {
        String bookingId = db.collection(BOOKINGS_COLLECTION).document().getId();
        
        Map<String, Object> booking = new HashMap<>();
        booking.put("customerId", customerId);
        booking.put("workerId", workerId);
        booking.put("workerName", workerName);
        booking.put("dateTime", dateTime);
        booking.put("description", description);
        booking.put("location", location);
        booking.put("status", "pending");
        booking.put("createdAt", new Date());

        db.collection(BOOKINGS_COLLECTION).document(bookingId)
            .set(booking)
            .addOnCompleteListener(task -> {
                if (callback != null) callback.onComplete(task);
            });
    }

    public static void getCustomerBookings(String customerId, FirestoreCallback<QuerySnapshot> callback) {
        db.collection(BOOKINGS_COLLECTION)
            .whereEqualTo("customerId", customerId)
            .get()
            .addOnCompleteListener(task -> {
                if (callback != null) callback.onComplete(task);
            });
    }

    public static void getWorkerBookings(String workerId, FirestoreCallback<QuerySnapshot> callback) {
        db.collection(BOOKINGS_COLLECTION)
            .whereEqualTo("workerId", workerId)
            .get()
            .addOnCompleteListener(task -> {
                if (callback != null) callback.onComplete(task);
            });
    }

    public static void updateBookingStatus(String bookingId, String status, 
                                           FirestoreCallback<Void> callback) {
        db.collection(BOOKINGS_COLLECTION)
            .document(bookingId)
            .update("status", status)
            .addOnCompleteListener(task -> {
                if (callback != null) callback.onComplete(task);
            });
    }
}
