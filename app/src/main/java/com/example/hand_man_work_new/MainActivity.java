package com.example.hand_man_work_new;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hand_man_work_new.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ActivityMainBinding binding; // Proper: Using ViewBinding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Proper: Initialize ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        // Replace with your URL if you are getting a "Database Error"
        mDatabase = FirebaseDatabase.getInstance("https://hand-man-work-41c9f-default-rtdb.firebaseio.com/").getReference();

        binding.signOutButton.setOnClickListener(v -> signOut());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Not signed in, go to Login
            sendToLogin();
        } else {
            // Proper: User is signed in, check if they are Customer or Worker
            checkUserRole(currentUser.getUid());
        }
    }

    private void checkUserRole(String uid) {
        mDatabase.child("users").child(uid).child("userType")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String userType = snapshot.getValue(String.class);
                        if (userType != null) {
                            if (userType.equals("Worker")) {
                                startActivity(new Intent(MainActivity.this, WorkerDashboardActivity.class));
                            } else {
                                startActivity(new Intent(MainActivity.this, CustomerDashboardActivity.class));
                            }
                            finish();
                        } else {
                            // User exists in Auth but not in Database, send to Selection
                            startActivity(new Intent(MainActivity.this, UserTypeSelectionActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void signOut() {
        mAuth.signOut();
        sendToLogin();
    }
}
