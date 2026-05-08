package com.example.studymateandroidapp.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.io.File
import com.studyplanner.core.model.Achievement
import com.studyplanner.core.model.DailyReflection
import com.studyplanner.core.model.Exam
import com.studyplanner.core.model.Goal
import com.studyplanner.core.model.ReminderSetting
import com.studyplanner.core.model.StudySession
import com.studyplanner.core.model.Task
import com.studyplanner.core.model.Note
import com.studyplanner.core.model.Flashcard
import com.studyplanner.feature.exams.data.ExamDao
import com.studyplanner.feature.goals.data.GoalDao
import com.studyplanner.feature.sessions.data.SessionDao
import com.studyplanner.feature.tasks.data.TaskDao
import com.studyplanner.feature.notes.data.NoteDao
import com.studyplanner.feature.flashcards.data.FlashcardDao
import com.studyplanner.feature.motivation.data.MotivationDao

/**
 * Central Room database for the Study Planner app.
 *
 * - Lists all [entities] and their corresponding DAO accessors.
 * - Registers [Converters] for custom types (dates, enums).
 * - `exportSchema = true` writes JSON schemas to `/schemas` for migration testing.
 * - Uses a thread-safe singleton pattern.
 */
@Database(
    entities = [
        Exam::class, Task::class, Goal::class, StudySession::class, Note::class, 
        Flashcard::class, ReminderSetting::class, Achievement::class, DailyReflection::class
    ],
    version = 10,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class StudyPlannerDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun sessionDao(): SessionDao
    abstract fun examDao(): ExamDao
    abstract fun goalDao(): GoalDao
    abstract fun reminderDao(): ReminderDao
    abstract fun noteDao(): NoteDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun motivationDao(): MotivationDao

    companion object {
        private const val DATABASE_NAME = "study_planner.db"

        @Volatile
        private var INSTANCE: StudyPlannerDatabase? = null

        init {
            try {
                System.loadLibrary("sqlcipher")
            } catch (e: Exception) {
                // Log and ignore, the Application class should have loaded it already
            }
        }

        /**
         * Returns the singleton database instance, creating it on first call.
         *
         * Thread-safe via double-checked locking.
         */
        fun getInstance(context: Context): StudyPlannerDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    // Force delete if not migrated to SQLCipher (Legacy Cleanup)
                    // This ensures a clean slate for the new 16KB-aligned version
                    try {
                        val prefs = context.getSharedPreferences("study_planner_secure_prefs", Context.MODE_PRIVATE)
                        val currentVersion = prefs.getInt("database_version", 0)
                        
                        // Force manual destructive migration for version 10 if not already done
                        if (currentVersion < 10) {
                            val dbFile = context.getDatabasePath(DATABASE_NAME)
                            if (dbFile.exists()) {
                                dbFile.delete()
                                File(dbFile.path + "-journal").delete()
                                File(dbFile.path + "-shm").delete()
                                File(dbFile.path + "-wal").delete()
                            }
                            prefs.edit().putInt("database_version", 10).apply()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val factory = SupportOpenHelperFactory(DatabaseKeyHelper.getDatabaseKey(context), object : net.zetetic.database.sqlcipher.SQLiteDatabaseHook {
                        override fun preKey(connection: net.zetetic.database.sqlcipher.SQLiteConnection?) {}
                        override fun postKey(connection: net.zetetic.database.sqlcipher.SQLiteConnection?) {
                            connection?.execute("PRAGMA cipher_memory_security = OFF;", null, null)
                        }
                    }, true)

                    Room.databaseBuilder(
                        context.applicationContext,
                        StudyPlannerDatabase::class.java,
                        DATABASE_NAME
                    )
                        .openHelperFactory(factory)
                        .fallbackToDestructiveMigration()
                        .fallbackToDestructiveMigrationOnDowngrade()
                        .build()
                        .also { INSTANCE = it }
                }
            }
    }
}
