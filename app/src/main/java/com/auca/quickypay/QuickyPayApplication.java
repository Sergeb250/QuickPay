package com.auca.quickypay;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class QuickyPayApplication extends Application {
    private static final String TAG = "QuickyPayApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseApp.initializeApp(this);
            android.util.Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            android.util.Log.e(TAG, "Firebase initialization failed: " + e.getMessage());
        }
    }
}