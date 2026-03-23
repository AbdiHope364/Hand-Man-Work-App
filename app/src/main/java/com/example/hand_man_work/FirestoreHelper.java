package com.example.hand_man_work;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    // ============ GENERIC CALLBACK INTERFACE ============
    public interface FirestoreCallback<T> {
        void onComplete(Task<T> task);
    }

    // ============ SAMPLE DATA METHODS ============

    // Add sample workers to Firestore
    public static void addSampleWorkers() {
        Log.d(TAG, "Adding sample workers...");
        
        // Sample workers data
        addWorker("John Plumbing", Arrays.asList("plumbing", "pipe repair", "water heater"), 30.0, "+1234567890");
        addWorker("Mike Electric", Arrays.asList("electrical", "wiring", "lighting"), 35.0, "+1234567891");
        addWorker("Sarah Carpenter", Arrays.asList("carpentry", "furniture", "cabinet making"), 28.0, "+1234567892");
        addWorker("Alex Handyman", Arrays.asList("general repair", "painting", "mounting"), 25.0, "+1234567893");
        addWorker("Lisa Plumbing", Arrays.asList("plumbing", "drain cleaning", "fixture repair"), 32.0, "+1234567894");
        addWorker("Tom Electric", Arrays.asList("electrical", "security systems", "wiring"), 38.0, "+1234567895");
    }

    private static void addWorker(String name, List<String> skills, double rate, String phone) {
        Map<String, Object> worker = new HashMap<>();
        worker.put("name", name);
        worker.put("type", "worker");
        worker.put("skills", skills);
        worker.put("rate", rate);
        worker.put("phone", phone);
        worker.put("createdAt", new Date());
        worker.put("email", name.toLowerCase().replace(" ", ".") + "@example.com");

        db.collection(USERS_COLLECTION)
                .add(worker)
                .addOnSuccessListener(docRef -> 
                    Log.d(TAG, "✅ Added worker: " + name + " with ID: " + docRef.getId()))
                .addOnFailureListener(e -> 
                    Log.e(TAG, "❌ Error adding worker: " + name, e));
    }

    // ============ USER METHODS ============

    // Get user type by FirebaseUser (NEW METHOD for LoginActivity)
    public static void getUserType(FirebaseUser user, FirestoreCallback<DocumentSnapshot> callback) {
        String userId = user.getUid();
        db.collection(USERS_COLLECTION).document(userId)
            .get()
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // Save user after registration
    public static void saveUser(FirebaseUser user, String name, String phone, String type, 
                                FirestoreCallback<Void> callback) {
        String userId = user.getUid();
        String email = user.getEmail();
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("email", email);
        userData.put("type", type);
        userData.put("createdAt", new Date());

        db.collection(USERS_COLLECTION).document(userId)
            .set(userData)
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // Get user data by ID
    public static void getUserData(String userId, FirestoreCallback<DocumentSnapshot> callback) {
        db.collection(USERS_COLLECTION).document(userId)
            .get()
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // Update customer profile
    public static void updateCustomerProfile(String userId, Map<String, Object> updates, 
                                            FirestoreCallback<Void> callback) {
        db.collection(USERS_COLLECTION).document(userId)
            .update(updates)
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // Update worker profile
    public static void updateWorkerProfile(String userId, String name, String phone, 
                                           List<String> skills, double rate, 
                                           String photoUrl, FirestoreCallback<Void> callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("skills", skills);
        updates.put("rate", rate);
        updates.put("photoUrl", photoUrl);
        updates.put("type", "worker");

        db.collection(USERS_COLLECTION).document(userId)
            .update(updates)
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // Save customer profile
    public static void saveCustomerProfile(String userId, String name, String phone, 
                                           FirestoreCallback<Void> callback) {
        Map<String, Object> customer = new HashMap<>();
        customer.put("name", name);
        customer.put("phone", phone);
        customer.put("type", "customer");
        
        db.collection(USERS_COLLECTION).document(userId)
            .set(customer)
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // Save worker profile
    public static void saveWorkerProfile(String userId, String name, String phone, 
                                         List<String> skills, double rate, 
                                         String photoUrl, FirestoreCallback<Void> callback) {
        Map<String, Object> worker = new HashMap<>();
        worker.put("name", name);
        worker.put("phone", phone);
        worker.put("skills", skills);
        worker.put("rate", rate);
        worker.put("photoUrl", photoUrl);
        worker.put("type", "worker");
        
        db.collection(USERS_COLLECTION).document(userId)
            .set(worker)
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // Get all workers
    public static void getAllWorkers(FirestoreCallback<QuerySnapshot> callback) {
        db.collection(USERS_COLLECTION)
            .whereEqualTo("type", "worker")
            .get()
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // ============ BOOKING METHODS ============

    // Create a new booking with workerName
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
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // Get bookings for a customer
    public static void getCustomerBookings(String customerId, FirestoreCallback<QuerySnapshot> callback) {
        db.collection(BOOKINGS_COLLECTION)
            .whereEqualTo("customerId", customerId)
            .get()
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // Get bookings for a worker
    public static void getWorkerBookings(String workerId, FirestoreCallback<QuerySnapshot> callback) {
        db.collection(BOOKINGS_COLLECTION)
            .whereEqualTo("workerId", workerId)
            .get()
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // Update booking status
    public static void updateBookingStatus(String bookingId, String status, 
                                           FirestoreCallback<Void> callback) {
        db.collection(BOOKINGS_COLLECTION)
            .document(bookingId)
            .update("status", status)
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }

    // ============ HELPER METHODS ============

    // Check if user exists
    public static void checkUserExists(String userId, FirestoreCallback<DocumentSnapshot> callback) {
        db.collection(USERS_COLLECTION).document(userId)
            .get()
            .addOnCompleteListener(task -> {
                if (callback != null) {
                    callback.onComplete(task);
                }
            });
    }
}
