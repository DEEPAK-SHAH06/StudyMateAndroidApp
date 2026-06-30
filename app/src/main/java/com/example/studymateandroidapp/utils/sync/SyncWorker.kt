package com.example.studymateandroidapp.utils.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.studymateandroidapp.data.local.StudyPlannerDatabase
import com.example.studymateandroidapp.data.model.Achievement
import com.example.studymateandroidapp.data.model.DailyReflection
import com.example.studymateandroidapp.data.model.Goal
import com.example.studymateandroidapp.data.model.StudySession
import com.example.studymateandroidapp.data.model.Task
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.model.Note
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.data.model.FlashcardReview
import com.example.studymateandroidapp.data.model.StudyProgress
import com.example.studymateandroidapp.data.model.UserProgress
import com.example.studymateandroidapp.data.model.ReminderSetting
import com.example.studymateandroidapp.data.local.PreferenceManager
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Worker that synchronizes local Room data with Firebase Firestore.
 */
class SyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val db = StudyPlannerDatabase.getInstance(appContext)
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth
    private val preferenceManager = PreferenceManager(appContext)

    override suspend fun doWork(): Result {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            preferenceManager.setSyncState("ERROR")
            return Result.failure()
        }

        preferenceManager.setSyncState("SYNCING")

        return try {
            syncEntity(
                userId = userId,
                collectionName = "tasks",
                getLocalItems = { db.taskDao().getAllTasksList() },
                insertLocal = { db.taskDao().insert(it) },
                updateLocal = { db.taskDao().update(it) },
                getId = { it.id },
                getServerId = { it.serverId },
                getWithServerId = { item, sId -> item.copy(userId = userId, serverId = sId) },
                copyLocalId = { cloud, localId -> cloud.copy(id = localId) },
                getLastUpdated = { it.lastUpdated },
                clazz = Task::class.java
            )

            syncEntity(
                userId = userId,
                collectionName = "goals",
                getLocalItems = { db.goalDao().getAllGoalsList() },
                insertLocal = { db.goalDao().insert(it) },
                updateLocal = { db.goalDao().update(it) },
                getId = { it.id },
                getServerId = { it.serverId },
                getWithServerId = { item, sId -> item.copy(userId = userId, serverId = sId) },
                copyLocalId = { cloud, localId -> cloud.copy(id = localId) },
                getLastUpdated = { it.lastUpdated },
                clazz = Goal::class.java
            )

            syncEntity(
                userId = userId,
                collectionName = "sessions",
                getLocalItems = { db.sessionDao().getAllSessionsList() },
                insertLocal = { db.sessionDao().insert(it) },
                updateLocal = { db.sessionDao().update(it) },
                getId = { it.id },
                getServerId = { it.serverId },
                getWithServerId = { item, sId -> item.copy(userId = userId, serverId = sId) },
                copyLocalId = { cloud, localId -> cloud.copy(id = localId) },
                getLastUpdated = { it.lastUpdated },
                clazz = StudySession::class.java
            )

            syncEntity(
                userId = userId,
                collectionName = "reflections",
                getLocalItems = { db.motivationDao().getAllReflectionsList() },
                insertLocal = { db.motivationDao().insertReflection(it) },
                updateLocal = { db.motivationDao().updateReflection(it) },
                getId = { it.id },
                getServerId = { it.serverId },
                getWithServerId = { item, sId -> item.copy(userId = userId, serverId = sId) },
                copyLocalId = { cloud, localId -> cloud.copy(id = localId) },
                getLastUpdated = { it.lastUpdated },
                clazz = DailyReflection::class.java
            )

            syncEntity(
                userId = userId,
                collectionName = "achievements",
                getLocalItems = { db.motivationDao().getAllAchievementsList() },
                insertLocal = { db.motivationDao().insertAchievement(it) },
                updateLocal = { db.motivationDao().insertAchievement(it) }, // MotivationDao has no update for achievements, re-insert or ignore
                getId = { it.id },
                getServerId = { it.serverId },
                getWithServerId = { item, sId -> item.copy(userId = userId, serverId = sId) },
                copyLocalId = { cloud, localId -> cloud.copy(id = localId) },
                getLastUpdated = { it.lastUpdated },
                clazz = Achievement::class.java
            )

            syncEntity(
                userId = userId,
                collectionName = "exams",
                getLocalItems = { db.examDao().getAllExamsList() },
                insertLocal = { db.examDao().insert(it) },
                updateLocal = { db.examDao().update(it) },
                getId = { it.id },
                getServerId = { it.serverId },
                getWithServerId = { item, sId -> item.copy(userId = userId, serverId = sId) },
                copyLocalId = { cloud, localId -> cloud.copy(id = localId) },
                getLastUpdated = { it.lastUpdated },
                clazz = Exam::class.java
            )

            syncEntity(
                userId = userId,
                collectionName = "notes",
                getLocalItems = { db.noteDao().getAllNotesList() },
                insertLocal = { db.noteDao().insert(it) },
                updateLocal = { db.noteDao().update(it) },
                getId = { it.id },
                getServerId = { it.serverId },
                getWithServerId = { item, sId -> item.copy(userId = userId, serverId = sId) },
                copyLocalId = { cloud, localId -> cloud.copy(id = localId) },
                getLastUpdated = { it.lastUpdated },
                clazz = Note::class.java
            )

            syncEntity(
                userId = userId,
                collectionName = "flashcards",
                getLocalItems = { db.flashcardDao().getAllFlashcardsList() },
                insertLocal = { db.flashcardDao().insert(it) },
                updateLocal = { db.flashcardDao().update(it) },
                getId = { it.id },
                getServerId = { it.serverId },
                getWithServerId = { item, sId -> item.copy(userId = userId, serverId = sId) },
                copyLocalId = { cloud, localId -> cloud.copy(id = localId) },
                getLastUpdated = { it.lastUpdated },
                clazz = Flashcard::class.java
            )

            syncEntity(
                userId = userId,
                collectionName = "flashcard_reviews",
                getLocalItems = { db.flashcardDao().getAllReviewsList() },
                insertLocal = { db.flashcardDao().insertReview(it) },
                updateLocal = { db.flashcardDao().insertReview(it) },
                getId = { it.id },
                getServerId = { it.serverId },
                getWithServerId = { item, sId -> item.copy(userId = userId, serverId = sId) },
                copyLocalId = { cloud, localId -> cloud.copy(id = localId) },
                getLastUpdated = { it.lastUpdated },
                clazz = FlashcardReview::class.java
            )

            syncEntity(
                userId = userId,
                collectionName = "study_progress",
                getLocalItems = { db.studyProgressDao().getAllProgressList() },
                insertLocal = { db.studyProgressDao().insertOrUpdate(it) },
                updateLocal = { db.studyProgressDao().update(it) },
                getId = { it.id },
                getServerId = { it.serverId },
                getWithServerId = { item, sId -> item.copy(userId = userId, serverId = sId) },
                copyLocalId = { cloud, localId -> cloud.copy(id = localId) },
                getLastUpdated = { it.lastUpdated },
                clazz = StudyProgress::class.java
            )

            syncEntity(
                userId = userId,
                collectionName = "user_progress",
                getLocalItems = { db.userProgressDao().getUserProgressList() },
                insertLocal = { db.userProgressDao().saveUserProgress(it); 1L },
                updateLocal = { db.userProgressDao().update(it) },
                getId = { it.id },
                getServerId = { it.serverId },
                getWithServerId = { item, sId -> item.copy(userId = userId, serverId = sId) },
                copyLocalId = { cloud, localId -> cloud.copy(id = localId) },
                getLastUpdated = { it.lastUpdated },
                clazz = UserProgress::class.java
            )

            // Sync Reminder Settings (Special handling since primary key is Enum)
            val reminderCollectionRef = firestore.collection("users").document(userId).collection("reminder_settings")
            val localReminders = db.reminderDao().getAllSettingsList()
            localReminders.forEach { setting ->
                val docRef = reminderCollectionRef.document(setting.type.name)
                val itemToPush = setting.copy(userId = userId, serverId = setting.type.name)
                docRef.set(itemToPush).await()
            }
            
            val cloudRemindersSnapshot = reminderCollectionRef.get().await()
            for (doc in cloudRemindersSnapshot.documents) {
                val cloudSetting = doc.toObject(ReminderSetting::class.java) ?: continue
                val existingLocal = db.reminderDao().getSettingByType(cloudSetting.type)
                if (existingLocal == null) {
                    db.reminderDao().upsertSetting(cloudSetting.copy(userId = userId, serverId = cloudSetting.type.name))
                } else {
                    if (cloudSetting.lastUpdated > existingLocal.lastUpdated) {
                        db.reminderDao().upsertSetting(cloudSetting.copy(userId = userId, serverId = cloudSetting.type.name))
                    }
                }
            }

            preferenceManager.setSyncState("IDLE", System.currentTimeMillis())
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            preferenceManager.setSyncState("ERROR")
            Result.retry()
        }
    }

    private suspend fun <T : Any> syncEntity(
        userId: String,
        collectionName: String,
        getLocalItems: suspend () -> List<T>,
        insertLocal: suspend (T) -> Long,
        updateLocal: suspend (T) -> Unit,
        getId: (T) -> Long,
        getServerId: (T) -> String?,
        getWithServerId: (T, String) -> T,
        copyLocalId: (T, Long) -> T,
        getLastUpdated: (T) -> Long,
        clazz: Class<T>
    ) {
        val collectionRef = firestore.collection("users").document(userId).collection(collectionName)

        // 1. PUSH to Cloud
        val localItems = getLocalItems()
        localItems.forEach { localItem ->
            val serverId = getServerId(localItem)
            val docRef = if (serverId != null) {
                collectionRef.document(serverId)
            } else {
                collectionRef.document()
            }

            val itemToPush = getWithServerId(localItem, docRef.id)
            docRef.set(itemToPush).await()

            if (serverId == null) {
                updateLocal(itemToPush)
            }
        }

        // 2. PULL from Cloud
        val cloudSnapshot = collectionRef.get().await()
        val currentLocalItems = getLocalItems()
        val localItemsByServerId = currentLocalItems.mapNotNull { 
            val sId = getServerId(it)
            if (sId != null) sId to it else null 
        }.toMap()

        for (doc in cloudSnapshot.documents) {
            val cloudItem = doc.toObject(clazz) ?: continue
            val docId = doc.id
            val fullySyncedCloudItem = getWithServerId(cloudItem, docId)

            val existingLocal = localItemsByServerId[docId]
            if (existingLocal == null) {
                // Must ensure id = 0 to autogenerate
                insertLocal(copyLocalId(fullySyncedCloudItem, 0L))
            } else {
                if (getLastUpdated(fullySyncedCloudItem) > getLastUpdated(existingLocal)) {
                    // Overwrite local keeping its primary key id
                    updateLocal(copyLocalId(fullySyncedCloudItem, getId(existingLocal)))
                }
            }
        }
    }
}
