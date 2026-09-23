package com.example.organshare.repositories;

import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.UserModel;
import com.example.organshare.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public interface AuthCallback<T> {
        void onSuccess(T result);
        void onFailure(String errorMessage);
    }

    public AuthRepository() {
        this.auth = FirestoreHelper.getAuth();
        this.db = FirestoreHelper.getFirestore();
    }

    public void login(String email, String password, AuthCallback<UserModel> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        getUserProfile(firebaseUser.getUid(), callback);
                    } else {
                        callback.onFailure("Failed to retrieve authentication details.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void registerUser(String email, String password, UserModel userModel, AuthCallback<UserModel> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        userModel.setUid(firebaseUser.getUid());
                        db.collection(FirestoreCollections.USERS)
                                .document(userModel.getUid())
                                .set(userModel)
                                .addOnSuccessListener(aVoid -> {
                                    FirestoreHelper.logAction(userModel.getUid(), userModel.getDisplayName(), userModel.getRole(),
                                            "REGISTER_USER", "USER", userModel.getUid(), "User registered with role: " + userModel.getRole());
                                    callback.onSuccess(userModel);
                                })
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getUserProfile(String uid, AuthCallback<UserModel> callback) {
        db.collection(FirestoreCollections.USERS).document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        if (user != null) {
                            if (user.isSuspended()) {
                                callback.onFailure("Account has been suspended by administration.");
                            } else {
                                callback.onSuccess(user);
                            }
                        } else {
                            callback.onFailure("User profile data could not be parsed.");
                        }
                    } else {
                        callback.onFailure("User profile not found.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void resetPassword(String email, AuthCallback<Void> callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void logout() {
        auth.signOut();
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return auth.getCurrentUser();
    }
}
