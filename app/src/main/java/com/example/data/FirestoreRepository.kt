package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = com.google.firebase.Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ).apply {
            // Enable persistent offline cache for seamless offline access & low-signal resilience
            firestoreSettings = firestoreSettings {
                setLocalCacheSettings(persistentCacheSettings {})
            }
        }
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeProfile(userId: String): Flow<UserProfile?> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val docRef = db.collection("users").document(userId)
        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    snapshot.toObject(
                        UserProfile::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.GET, docRef.path)
                    }
                    throw error
                }
        )
    }

    suspend fun ensureProfileInitialized(user: FirebaseUser): Result<Unit> {
        val docPath = "users/${user.uid}"
        return try {
            val uid = requireUserId()
            check(uid == user.uid) { "Authenticated user mismatch" }
            val docRef = db.collection("users").document(uid)
            val existing = docRef.get().await()
            if (!existing.exists()) {
                val initial = UserProfile(
                    userId = uid,
                    displayName = (user.displayName ?: "SmartLabs Student").take(80).ifBlank { "SmartLabs Student" },
                    email = (user.email ?: "student@smartlabs.lk").take(120).ifBlank { "student@smartlabs.lk" },
                    photoUrl = user.photoUrl?.toString()?.take(500),
                    examGoal = "PTE",
                    targetScore = 79.0,
                    examDateLabel = "In 23 days",
                    weeklyGoalSessions = 12L,
                    sessionsThisWeek = 4L,
                    streakDays = 5L,
                    role = "student",
                    lecturerViewEnabled = false,
                    biometricEnabled = false,
                    creditsSpeaking = 8L,
                    creditsSst = 5L,
                    creditsSwt = 5L,
                    creditsEssay = 4L,
                    creditsIeltsEssay = 3L,
                    creditsMock = 2L,
                    creditsIeltsMock = 2L,
                    creditsUniversal = 10L
                )
                docRef.set(initial.toCreateMap()).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateProfileGoal(
        displayName: String,
        examGoal: String,
        targetScore: Double,
        examDateLabel: String,
        weeklyGoalSessions: Long
    ): Result<Unit> {
        var docPath = "users/unknown"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            docPath = docRef.path
            val updates = mutableMapOf<String, Any>(
                "displayName" to displayName.trim().take(80).ifBlank { "SmartLabs Student" },
                "examGoal" to if (examGoal == "IELTS") "IELTS" else "PTE",
                "targetScore" to targetScore.coerceIn(4.0, 90.0),
                "weeklyGoalSessions" to weeklyGoalSessions.coerceIn(1L, 50L),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (examDateLabel.isNotBlank()) {
                updates["examDateLabel"] = examDateLabel.trim().take(40)
            }
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean): Result<Unit> {
        var docPath = "users/unknown"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            docPath = docRef.path
            docRef.update(
                mapOf(
                    "biometricEnabled" to enabled,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun setLecturerViewEnabled(enabled: Boolean): Result<Unit> {
        var docPath = "users/unknown"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            docPath = docRef.path
            docRef.update(
                mapOf(
                    "lecturerViewEnabled" to enabled,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun addCredits(pool: CreditPoolType, amount: Long): Result<Unit> {
        var docPath = "users/unknown"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            docPath = docRef.path
            docRef.update(
                mapOf(
                    pool.fieldName to FieldValue.increment(amount.coerceIn(1L, 500L)),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun consumeCreditAndRecordAttempt(
        pool: CreditPoolType?,
        cost: Int,
        attempt: PracticeAttempt
    ): Result<Unit> {
        var docPath = "users/unknown"
        return try {
            val uid = requireUserId()
            val userRef = db.collection("users").document(uid)
            val attemptRef = userRef.collection("attempts").document()
            docPath = attemptRef.path

            val batch = db.batch()
            batch.set(attemptRef, attempt.toCreateMap(uid))

            val userUpdates = mutableMapOf<String, Any>(
                "sessionsThisWeek" to FieldValue.increment(1L),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (pool != null && cost > 0) {
                userUpdates[pool.fieldName] = FieldValue.increment(-cost.toLong())
            }
            batch.update(userRef, userUpdates)
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    fun observeAttempts(userId: String): Flow<List<PracticeAttempt>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val colRef = db.collection("users").document(userId).collection("attempts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
        emitAll(
            colRef.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        PracticeAttempt::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.LIST, "users/$userId/attempts")
                    }
                    throw error
                }
        )
    }

    fun observeBookings(userId: String): Flow<List<BookingRecord>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val colRef = db.collection("users").document(userId).collection("bookings")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(40)
        emitAll(
            colRef.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BookingRecord::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.LIST, "users/$userId/bookings")
                    }
                    throw error
                }
        )
    }

    suspend fun createBooking(booking: BookingRecord): Result<Unit> {
        var docPath = "users/unknown/bookings"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("bookings").document()
            docPath = docRef.path
            docRef.set(booking.toCreateMap(uid)).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun cancelBooking(bookingId: String): Result<Unit> {
        var docPath = "users/unknown/bookings/$bookingId"
        return try {
            val uid = requireUserId()
            require(bookingId.isNotBlank()) { "Booking ID cannot be blank" }
            val docRef = db.collection("users").document(uid).collection("bookings").document(bookingId)
            docPath = docRef.path
            docRef.update(
                mapOf(
                    "status" to "cancelled",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    fun observeTutorMessages(userId: String): Flow<List<TutorMessage>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val colRef = db.collection("users").document(userId).collection("tutor_messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .limit(60)
        emitAll(
            colRef.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        TutorMessage::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.LIST, "users/$userId/tutor_messages")
                    }
                    throw error
                }
        )
    }

    suspend fun saveTutorMessage(message: TutorMessage): Result<Unit> {
        var docPath = "users/unknown/tutor_messages"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("tutor_messages").document()
            docPath = docRef.path
            docRef.set(message.toCreateMap(uid)).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    fun observeSupportTickets(userId: String): Flow<List<SupportTicket>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val colRef = db.collection("users").document(userId).collection("support_tickets")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(30)
        emitAll(
            colRef.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        SupportTicket::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(toLog, OperationType.LIST, "users/$userId/support_tickets")
                    }
                    throw error
                }
        )
    }

    suspend fun createSupportTicket(subject: String, category: String, message: String): Result<Unit> {
        var docPath = "users/unknown/support_tickets"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("support_tickets").document()
            docPath = docRef.path
            val ticket = SupportTicket(
                userId = uid,
                subject = subject,
                category = category,
                status = "open",
                latestMessage = message
            )
            docRef.set(ticket.toCreateMap(uid)).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteAccountData(): Result<Unit> {
        var docPath = "users/unknown"
        return try {
            val uid = requireUserId()
            val userRef = db.collection("users").document(uid)
            docPath = userRef.path

            val subCollections = listOf("attempts", "bookings", "tutor_messages", "support_tickets")
            val batch = db.batch()
            for (sub in subCollections) {
                val docs = userRef.collection(sub).limit(100).get().await()
                docs.documents.forEach { batch.delete(it.reference) }
            }
            batch.delete(userRef)
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }
}
