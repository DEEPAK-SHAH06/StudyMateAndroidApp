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
import com.example.studymateandroidapp.data.model.FlashcardReview
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
        Flashcard::class, ReminderSetting::class, Achievement::class, DailyReflection::class,
        FlashcardReview::class
    ],
    version = 14,
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

        val MIGRATION_10_11 = object : androidx.room.migration.Migration(10, 11) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Delete the obsolete FOCUS_MODE setting if it exists
                db.execSQL("DELETE FROM reminder_settings WHERE type = 'FOCUS_MODE'")
            }
        }

        val MIGRATION_11_12 = object : androidx.room.migration.Migration(11, 12) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Add isTimeSet column to exams table
                db.execSQL("ALTER TABLE exams ADD COLUMN isTimeSet INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_12_13 = object : androidx.room.migration.Migration(12, 13) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Refactor study_sessions table: 
                // 1. Rename durationMinutes to durationSeconds
                // 2. Add isCompleted column
                // 3. Ensure timestamps are INTEGER (per Converters)
                
                // Create new table with EXACT Room-generated schema
                db.execSQL("""
                    CREATE TABLE study_sessions_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        taskId INTEGER,
                        examId INTEGER,
                        subject TEXT NOT NULL,
                        startTime INTEGER NOT NULL,
                        endTime INTEGER,
                        durationSeconds INTEGER NOT NULL DEFAULT 0,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        notes TEXT NOT NULL DEFAULT '',
                        userId TEXT,
                        serverId TEXT,
                        lastUpdated INTEGER NOT NULL,
                        FOREIGN KEY(taskId) REFERENCES tasks(id) ON UPDATE NO ACTION ON DELETE SET NULL,
                        FOREIGN KEY(examId) REFERENCES exams(id) ON UPDATE NO ACTION ON DELETE SET NULL
                    )
                """)
                
                // Copy data, converting minutes to seconds
                // Use strftime('%s', ...) to correctly convert ISO strings to epoch seconds.
                // If it's already a numeric string, CAST handles it.
                db.execSQL("""
                    INSERT INTO study_sessions_new (
                        id, taskId, examId, subject, startTime, endTime, 
                        durationSeconds, isCompleted, notes, userId, serverId, lastUpdated
                    )
                    SELECT 
                        id, taskId, examId, subject, 
                        CASE 
                            WHEN startTime GLOB '[0-9]*' THEN CAST(startTime AS INTEGER)
                            ELSE CAST(strftime('%s', startTime) AS INTEGER)
                        END,
                        CASE 
                            WHEN endTime GLOB '[0-9]*' THEN CAST(endTime AS INTEGER)
                            ELSE CAST(strftime('%s', endTime) AS INTEGER)
                        END,
                        durationMinutes * 60, 0, notes, userId, serverId, lastUpdated
                    FROM study_sessions
                """)
                
                // Drop old table and rename new one
                db.execSQL("DROP TABLE study_sessions")
                db.execSQL("ALTER TABLE study_sessions_new RENAME TO study_sessions")
                
                // Re-create indices exactly as Room expects
                db.execSQL("CREATE INDEX index_study_sessions_taskId ON study_sessions(taskId)")
                db.execSQL("CREATE INDEX index_study_sessions_examId ON study_sessions(examId)")
                db.execSQL("CREATE INDEX index_study_sessions_startTime ON study_sessions(startTime)")
                db.execSQL("CREATE INDEX index_study_sessions_userId ON study_sessions(userId)")
                db.execSQL("CREATE INDEX index_study_sessions_serverId ON study_sessions(serverId)")
            }
        }

        val MIGRATION_13_14 = object : androidx.room.migration.Migration(13, 14) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Add flashcard_reviews table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS flashcard_reviews (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        date INTEGER NOT NULL,
                        cardsReviewed INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                """)
            }
        }

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
                    // Clean up legacy non-SQLCipher files if needed
                    val prefs = context.getSharedPreferences("study_planner_secure_prefs", Context.MODE_PRIVATE)
                    val currentStoredVersion = prefs.getInt("database_version", 0)
                    if (currentStoredVersion < 10) {
                        val dbFile = context.getDatabasePath(DATABASE_NAME)
                        if (dbFile.exists()) {
                            dbFile.delete()
                            File(dbFile.path + "-journal").delete()
                            File(dbFile.path + "-shm").delete()
                            File(dbFile.path + "-wal").delete()
                        }
                        prefs.edit().putInt("database_version", 12).apply()
                    } else if (currentStoredVersion < 14) {
                        prefs.edit().putInt("database_version", 14).apply()
                    }

                    val factory = SupportOpenHelperFactory(DatabaseKeyHelper.getDatabaseKey(context), object : net.zetetic.database.sqlcipher.SQLiteDatabaseHook {
                        override fun preKey(connection: net.zetetic.database.sqlcipher.SQLiteConnection?) {
                            // Fix for emulator mlock() ENOMEM (errno 12) issues
                            connection?.execute("PRAGMA cipher_memory_security = OFF;", null, null)
                        }
                        override fun postKey(connection: net.zetetic.database.sqlcipher.SQLiteConnection?) {}
                    }, true)

                    Room.databaseBuilder(
                        context.applicationContext,
                        StudyPlannerDatabase::class.java,
                        DATABASE_NAME
                    )
                        .openHelperFactory(factory)
                        .addMigrations(MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14)
                        .fallbackToDestructiveMigration()
                        .fallbackToDestructiveMigrationOnDowngrade()
                        .build()
                        .also { INSTANCE = it }
                }
            }
    }
}
