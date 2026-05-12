package com.example.hand_man_work_new;

import android.app.Activity;
import com.example.hand_man_work_new.R;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleSignInHelper {

    private final Activity activity;
    private final GoogleSignInClient mGoogleSignInClient;
    private final FirebaseAuth mAuth;
    private final GoogleSignInListener listener;

    public interface GoogleSignInListener {
        void onGoogleSignInSuccess(FirebaseUser user);
        void onGoogleSignInFailure(String error);
        void showLoading(boolean isLoading);
    }

    public GoogleSignInHelper(AppCompatActivity activity, GoogleSignInListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void signIn(ActivityResultLauncher<Intent> launcher) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        launcher.launch(signInIntent);
    }

    public void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            }
        } catch (ApiException e) {
            listener.onGoogleSignInFailure("Google sign in failed: " + e.getMessage());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        listener.showLoading(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        listener.onGoogleSignInSuccess(user);
                    } else {
                        listener.onGoogleSignInFailure("Firebase auth with Google failed");
                    }
                });
    }
}
