package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.model.PartyRoomEntity
import com.example.data.model.UserEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

/**
 * Service to manage Firebase Firestore synchronization for:
 * - User Profiles & Levels (1 to 100 max, Exclusive IDs)
 * - Party Rooms & Room Levels/EXP
 */
object FirestoreService {
    private const val TAG = "FirestoreService"
    private const val COLLECTION_USERS = "users"
    private const val COLLECTION_ROOMS = "party_rooms"

    private var firestoreInstance: FirebaseFirestore? = null
    var isInitialized: Boolean = false
        private set

    /**
     * Initializes the Firebase Firestore instance safely.
     * Gracefully handles cases where google-services.json is pending.
     */
    fun initialize(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            firestoreInstance = FirebaseFirestore.getInstance()
            isInitialized = true
            Log.i(TAG, "Firebase Firestore initialized successfully.")
        } catch (e: Exception) {
            Log.w(TAG, "Firestore initialization skipped: ${e.message}")
            isInitialized = false
        }
    }

    fun getDb(): FirebaseFirestore? = firestoreInstance

    /**
     * Saves or updates a User Profile in Firestore (Level, VIP, Exp, Coins, Exclusive ID).
     */
    fun saveUserProfile(
        user: UserEntity,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        val db = getDb() ?: run {
            Log.d(TAG, "Firestore not available, skipping cloud save for user: ${user.username}")
            return
        }

        val userData = hashMapOf<String, Any>(
            "id" to user.id,
            "username" to user.username,
            "standardId" to user.standardId,
            "exclusiveId" to (user.exclusiveId ?: ""),
            "level" to user.level,
            "currentExp" to user.currentExp,
            "coins" to user.coins,
            "diamonds" to user.diamonds,
            "vipTier" to user.vipTier,
            "nobleTitle" to user.nobleTitle,
            "isAdmin" to user.isAdmin,
            "bio" to user.bio,
            "songsSung" to user.songsSung,
            "totalScore" to user.totalScore,
            "followers" to user.followers,
            "following" to user.following,
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection(COLLECTION_USERS)
            .document(user.id.toString())
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "User ${user.username} (Lv.${user.level}) saved to Firestore.")
                onSuccess?.invoke()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Failed to save user to Firestore: ${exception.message}")
                onFailure?.invoke(exception)
            }
    }

    /**
     * Updates user level and experience in Firestore.
     */
    fun updateUserLevel(
        userId: Long,
        level: Int,
        currentExp: Long,
        exclusiveId: String? = null
    ) {
        val db = getDb() ?: return

        val updates = mutableMapOf<String, Any>(
            "level" to level,
            "currentExp" to currentExp,
            "updatedAt" to System.currentTimeMillis()
        )
        if (exclusiveId != null) {
            updates["exclusiveId"] = exclusiveId
        }

        db.collection(COLLECTION_USERS)
            .document(userId.toString())
            .set(updates, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "User $userId level updated to $level (Exp: $currentExp) in Firestore.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Failed to update user level in Firestore: ${e.message}")
            }
    }

    /**
     * Saves or updates Party Room data in Firestore (Level, Room EXP, Host, Category).
     */
    fun savePartyRoom(
        room: PartyRoomEntity,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        val db = getDb() ?: run {
            Log.d(TAG, "Firestore not available, skipping cloud save for room: ${room.title}")
            return
        }

        val roomData = hashMapOf<String, Any>(
            "id" to room.id,
            "title" to room.title,
            "hostUserId" to room.hostUserId,
            "hostUsername" to room.hostUsername,
            "category" to room.category,
            "roomLevel" to room.roomLevel,
            "roomExp" to room.roomExp,
            "onlineCount" to room.onlineCount,
            "announcement" to room.announcement,
            "isLive" to room.isLive,
            "roomTheme" to room.roomTheme,
            "totalGiftsValue" to room.totalGiftsValue,
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection(COLLECTION_ROOMS)
            .document(room.id.toString())
            .set(roomData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Party Room ${room.title} (Lv.${room.roomLevel}) saved to Firestore.")
                onSuccess?.invoke()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Failed to save room to Firestore: ${exception.message}")
                onFailure?.invoke(exception)
            }
    }

    /**
     * Updates Party Room experience and level in Firestore.
     */
    fun updateRoomExp(roomId: Long, roomLevel: Int, roomExp: Long) {
        val db = getDb() ?: return

        val updates = hashMapOf<String, Any>(
            "roomLevel" to roomLevel,
            "roomExp" to roomExp,
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection(COLLECTION_ROOMS)
            .document(roomId.toString())
            .set(updates, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Room $roomId updated to Lv.$roomLevel (Exp: $roomExp) in Firestore.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Failed to update room EXP in Firestore: ${e.message}")
            }
    }

    /**
     * Real-time snapshot listener for a Party Room.
     */
    fun listenToPartyRoom(
        roomId: Long,
        onUpdate: (roomLevel: Int, roomExp: Long, onlineCount: Int) -> Unit
    ): ListenerRegistration? {
        val db = getDb() ?: return null

        return db.collection(COLLECTION_ROOMS)
            .document(roomId.toString())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error listening to room $roomId: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val level = snapshot.getLong("roomLevel")?.toInt() ?: 1
                    val exp = snapshot.getLong("roomExp") ?: 0L
                    val online = snapshot.getLong("onlineCount")?.toInt() ?: 1
                    onUpdate(level, exp, online)
                }
            }
    }

    /**
     * Real-time snapshot listener for a User Profile.
     */
    fun listenToUserProfile(
        userId: Long,
        onUpdate: (level: Int, currentExp: Long, coins: Long, exclusiveId: String?) -> Unit
    ): ListenerRegistration? {
        val db = getDb() ?: return null

        return db.collection(COLLECTION_USERS)
            .document(userId.toString())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error listening to user $userId: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val level = snapshot.getLong("level")?.toInt() ?: 1
                    val currentExp = snapshot.getLong("currentExp") ?: 0L
                    val coins = snapshot.getLong("coins") ?: 0L
                    val exclusiveId = snapshot.getString("exclusiveId")
                    onUpdate(level, currentExp, coins, exclusiveId)
                }
            }
    }
}
