package com.example.studymateandroidapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studymateandroidapp.data.model.*
import com.example.studymateandroidapp.data.local.TaskDao
import com.example.studymateandroidapp.data.local.SessionDao
import com.example.studymateandroidapp.data.local.ExamDao
import com.example.studymateandroidapp.data.local.GoalDao
import com.example.studymateandroidapp.data.local.NoteDao
import com.example.studymateandroidapp.data.local.FlashcardDao
import com.example.studymateandroidapp.data.local.MotivationDao
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
        StudyProgress::class
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
    abstract fun studyProgressDao(): StudyProgressDao

    companion object {
        private const val DATABASE_NAME = "study_planner.db"

        val MIGRATION_10_11 = object : androidx.room.migration.Migration(10, 11) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("DELETE FROM reminder_settings WHERE type = 'FOCUS_MODE'")
            }
        }

        val MIGRATION_11_12 = object : androidx.room.migration.Migration(11, 12) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE exams ADD COLUMN isTimeSet INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_12_13 = object : androidx.room.migration.Migration(12, 13) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
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
                db.execSQL("DROP TABLE study_sessions")
                db.execSQL("ALTER TABLE study_sessions_new RENAME TO study_sessions")
                db.execSQL("CREATE INDEX index_study_sessions_taskId ON study_sessions(taskId)")
                db.execSQL("CREATE INDEX index_study_sessions_examId ON study_sessions(examId)")
                db.execSQL("CREATE INDEX index_study_sessions_startTime ON study_sessions(startTime)")
                db.execSQL("CREATE INDEX index_study_sessions_userId ON study_sessions(userId)")
                db.execSQL("CREATE INDEX index_study_sessions_serverId ON study_sessions(serverId)")
            }
        }

        val MIGRATION_13_14 = object : androidx.room.migration.Migration(13, 14) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `study_progress` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `examId` INTEGER NOT NULL, 
                        `totalStudyTime` INTEGER NOT NULL, 
                        `flashcardMastery` REAL NOT NULL DEFAULT 0.0,
                        `completionPercentage` REAL NOT NULL, 
                        `lastStudiedTimestamp` INTEGER NOT NULL, 
                        FOREIGN KEY(`examId`) REFERENCES `exams`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_study_progress_examId` ON `study_progress` (`examId`)")
            }
        }

        @Volatile
        private var INSTANCE: StudyPlannerDatabase? = null

        init {
            try {
                System.loadLibrary("sqlcipher")
            } catch (e: Exception) {
            }
        }

        fun getInstance(context: Context): StudyPlannerDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val prefs = context.getSharedPreferences("study_planner_secure_prefs", Context.MODE_PRIVATE)
                    val currentStoredVersion = prefs.getInt("database_version", 0)
                    if (currentStoredVersion < 14) {
                        prefs.edit().putInt("database_version", 14).apply()
                    }

                    val factory = SupportOpenHelperFactory(DatabaseKeyHelper.getDatabaseKey(context), object : net.zetetic.database.sqlcipher.SQLiteDatabaseHook {
                        override fun preKey(connection: net.zetetic.database.sqlcipher.SQLiteConnection?) {
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
                        .build()
                        .also { INSTANCE = it }
                }
            }
    }
}
