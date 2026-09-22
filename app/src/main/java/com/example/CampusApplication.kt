package com.example

import android.app.Application
import android.util.Log
import com.example.data.local.CampusIqDatabase
import com.example.data.repository.AiRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.CampusRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class CampusApplication : Application() {

    val database by lazy { CampusIqDatabase.getInstance(this) }
    val campusRepository by lazy { CampusRepository(database) }
    val authRepository by lazy { AuthRepository(database) }
    val aiRepository by lazy { AiRepository() }

    override fun onCreate() {
        super.onCreate()
        // Initialize FirebaseApp safely if not already initialized
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:100000000000:android:campusiq")
                    .setApiKey("AIzaSyCampusIqDefaultAppKeyMock001")
                    .setProjectId("campusiq-college")
                    .build()
                FirebaseApp.initializeApp(this, options)
                Log.i("CampusApplication", "FirebaseApp initialized with options")
            }
        } catch (e: Exception) {
            Log.w("CampusApplication", "FirebaseApp initialization: ${e.message}")
        }

        // Trigger database initialization and default pre-population
        database
    }
}
