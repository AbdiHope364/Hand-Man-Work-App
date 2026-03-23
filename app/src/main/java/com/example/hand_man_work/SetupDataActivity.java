package com.example.hand_man_work;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SetupDataActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Add sample workers to Firestore
        FirestoreHelper.addSampleWorkers();
        
        Toast.makeText(this, "Sample data added to Firestore!", Toast.LENGTH_LONG).show();
        
        // Close this activity
        finish();
    }
}
