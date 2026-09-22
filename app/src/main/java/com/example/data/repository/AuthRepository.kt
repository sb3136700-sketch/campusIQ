package com.example.data.repository

import android.util.Log
import com.example.data.local.CampusIqDatabase
import com.example.data.local.entity.UserEntity
import com.example.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository(private val db: CampusIqDatabase) {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val firebaseAuth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w("AuthRepository", "FirebaseAuth not initialized: ${e.message}")
            null
        }
    }

    val isFirebaseReady: Boolean
        get() = firebaseAuth != null

    suspend fun checkExistingSession() = withContext(Dispatchers.IO) {
        // If a Firebase user is already signed in and currentUser is not set, restore session
        val fbUser = try {
            firebaseAuth?.currentUser
        } catch (e: Exception) {
            null
        }

        if (fbUser != null && fbUser.email != null) {
            val user = db.userDao().getUserByEmail(fbUser.email!!.lowercase())
            if (user != null) {
                _currentUser.value = user
                return@withContext
            }
        }
        // If no active session, leave _currentUser as null so the sign-in screen is shown
    }

    suspend fun login(
        email: String,
        password: String,
        expectedRole: UserRole? = null
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Email and password cannot be empty."))
        }
        if (trimmedPassword.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        var firebaseUser: FirebaseUser? = null
        var firebaseError: String? = null

        // Attempt Firebase Authentication
        if (firebaseAuth != null) {
            try {
                firebaseUser = suspendCancellableCoroutine { continuation ->
                    firebaseAuth?.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
                        ?.addOnSuccessListener { authResult ->
                            if (continuation.isActive) continuation.resume(authResult.user)
                        }
                        ?.addOnFailureListener { exception ->
                            if (continuation.isActive) continuation.resumeWithException(exception)
                        }
                }
            } catch (e: Exception) {
                firebaseError = e.message
                Log.w("AuthRepository", "Firebase signIn error: ${e.message}")
            }
        }

        // Check local database for account record
        val user = db.userDao().getUserByEmail(trimmedEmail)
        if (user != null) {
            // Verify expected portal role if specified (e.g. Student vs Faculty)
            if (expectedRole != null && user.role != expectedRole && user.role != UserRole.ADMIN) {
                val roleName = if (expectedRole == UserRole.STUDENT) "Student" else "Faculty"
                return@withContext Result.failure(
                    IllegalArgumentException("This account is registered as ${user.role.name}. Please select the ${user.role.name.lowercase().replaceFirstChar { it.uppercase() }} tab.")
                )
            }
            _currentUser.value = user
            return@withContext Result.success(user)
        }

        // If Firebase authenticated a user who doesn't have a local DB record yet, create one
        if (firebaseUser != null) {
            val role = expectedRole ?: if (trimmedEmail.contains("faculty") || trimmedEmail.contains("prof")) {
                UserRole.FACULTY
            } else {
                UserRole.STUDENT
            }
            val newUser = UserEntity(
                id = firebaseUser.uid,
                name = firebaseUser.displayName ?: trimmedEmail.substringBefore("@")
                    .replace(".", " ")
                    .replaceFirstChar { it.uppercase() },
                email = trimmedEmail,
                role = role,
                department = "AI & ML",
                refId = if (role == UserRole.FACULTY) "FAC_${System.currentTimeMillis() % 1000}" else "1MS21AI042"
            )
            db.userDao().insertUser(newUser)
            _currentUser.value = newUser
            return@withContext Result.success(newUser)
        }

        // If credentials did not match local DB or Firebase
        val errorMsg = when {
            firebaseError != null && !firebaseError.contains("API_KEY_NOT_VALID", ignoreCase = true) ->
                firebaseError
            else ->
                "No registered ${expectedRole?.name?.lowercase() ?: "user"} found with email $email. Please check your credentials or register a new account."
        }
        Result.failure(IllegalArgumentException(errorMsg))
    }

    suspend fun quickLoginRole(role: UserRole): Result<UserEntity> = withContext(Dispatchers.IO) {
        val email = when (role) {
            UserRole.STUDENT -> "student@campusiq.edu"
            UserRole.FACULTY -> "faculty@campusiq.edu"
            UserRole.ADMIN -> "admin@campusiq.edu"
        }
        val user = db.userDao().getUserByEmail(email)
        if (user != null) {
            _currentUser.value = user
            Result.success(user)
        } else {
            // Fallback user if DB not yet seeded
            val fallback = UserEntity(
                id = "usr_${role.name.lowercase()}",
                name = when (role) {
                    UserRole.STUDENT -> "Rohan Sharma"
                    UserRole.FACULTY -> "Dr. Priya Nair"
                    UserRole.ADMIN -> "Dr. S. K. Murthy"
                },
                email = email,
                role = role,
                department = "AI & ML",
                refId = when (role) {
                    UserRole.STUDENT -> "1MS21AI042"
                    UserRole.FACULTY -> "FAC_AIML_01"
                    UserRole.ADMIN -> "ADM_001"
                }
            )
            db.userDao().insertUser(fallback)
            _currentUser.value = fallback
            Result.success(fallback)
        }
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        department: String,
        refId: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = name.trim()
        val trimmedRefId = refId.trim()
        val trimmedPassword = password.trim()

        if (trimmedName.isBlank() || trimmedEmail.isBlank() || trimmedRefId.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please complete all required fields."))
        }
        if (trimmedPassword.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        val existing = db.userDao().getUserByEmail(trimmedEmail)
        if (existing != null) {
            return@withContext Result.failure(IllegalArgumentException("Account with email $trimmedEmail already exists."))
        }

        var firebaseUid: String? = null
        // Create user in Firebase Auth if available
        if (firebaseAuth != null) {
            try {
                val fbResult = suspendCancellableCoroutine<FirebaseUser?> { continuation ->
                    firebaseAuth?.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
                        ?.addOnSuccessListener { result ->
                            if (continuation.isActive) continuation.resume(result.user)
                        }
                        ?.addOnFailureListener { ex ->
                            if (continuation.isActive) continuation.resumeWithException(ex)
                        }
                }
                firebaseUid = fbResult?.uid
            } catch (e: Exception) {
                Log.w("AuthRepository", "Firebase createUser notice: ${e.message}")
            }
        }

        val newUser = UserEntity(
            id = firebaseUid ?: "usr_${System.currentTimeMillis()}",
            name = trimmedName,
            email = trimmedEmail,
            role = role,
            department = department.trim(),
            refId = trimmedRefId
        )
        db.userDao().insertUser(newUser)
        _currentUser.value = newUser
        Result.success(newUser)
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please provide a valid college email."))
        }
        if (firebaseAuth != null) {
            try {
                suspendCancellableCoroutine { continuation ->
                    firebaseAuth?.sendPasswordResetEmail(trimmedEmail)
                        ?.addOnSuccessListener {
                            if (continuation.isActive) continuation.resume(Unit)
                        }
                        ?.addOnFailureListener { ex ->
                            if (continuation.isActive) continuation.resumeWithException(ex)
                        }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                // If Firebase reset failed, simulate safe response
                Result.success(Unit)
            }
        } else {
            Result.success(Unit)
        }
    }

    fun logout() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.w("AuthRepository", "Firebase signOut error: ${e.message}")
        }
        _currentUser.value = null
    }
}
