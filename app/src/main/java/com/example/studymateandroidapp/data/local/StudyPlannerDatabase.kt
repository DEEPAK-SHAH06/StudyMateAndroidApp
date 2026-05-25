package com.example.studymateandroidapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studymateandroidapp.data.model.Achievement
import com.example.studymateandroidapp.data.model.DailyReflection
import com.example.studymateandroidapp.data.model.Exam
import com.example.studymateandroidapp.data.model.Flashcard
import com.example.studymateandroidapp.data.model.Goal
import com.example.studymateandroidapp.data.model.Note
import com.example.studymateandroidapp.data.model.StudySession
import com.example.studymateandroidapp.data.model.Task
import com.example.studymateandroidapp.data.local.TaskDao
import com.example.studymateandroidapp.data.local.SessionDao
import com.example.studymateandroidapp.data.local.ExamDao
import com.example.studymateandroidapp.data.local.GoalDao
import com.example.studymateandroidapp.data.local.NoteDao
import com.example.studymateandroidapp.data.local.FlashcardDao
import com.example.studymateandroidapp.data.local.MotivationDao
import com.example.studymateandroidapp.data.model.ReminderSetting
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.io.File

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
    exportSchema = false
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
