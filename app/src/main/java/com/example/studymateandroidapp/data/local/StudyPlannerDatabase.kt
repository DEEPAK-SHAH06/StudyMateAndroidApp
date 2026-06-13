package com.example.studymateandroidapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.studymateandroidapp.data.model.*
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [
        Exam::class,
        Task::class,
        Goal::class,
        StudySession::class,
        Note::class,
        Flashcard::class,
        ReminderSetting::class,
        Achievement::class,
        DailyReflection::class,
        StudyProgress::class,
        FlashcardReview::class
    ],
    version = 16,
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
    abstract fun studyProgressDao(): StudyProgressDao

    companion object {

        private const val DATABASE_NAME = "study_planner.db"

        /**
         * MIGRATIONS
         */

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    DELETE FROM reminder_settings 
                    WHERE type = 'FOCUS_MODE'
                """)
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    ALTER TABLE exams 
                    ADD COLUMN isTimeSet INTEGER NOT NULL DEFAULT 0
                """)
            }
        }

        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {

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
                        FOREIGN KEY(taskId) REFERENCES tasks(id) ON DELETE SET NULL,
                        FOREIGN KEY(examId) REFERENCES exams(id) ON DELETE SET NULL
                    )
                """)

                db.execSQL("""
                    INSERT INTO study_sessions_new (
                        id, taskId, examId, subject,
                        startTime, endTime,
                        durationSeconds, isCompleted,
                        notes, userId, serverId, lastUpdated
                    )
                    SELECT
                        id, taskId, examId, subject,
                        startTime,
                        endTime,
                        durationMinutes * 60,
                        0,
                        notes,
                        userId,
                        serverId,
                        lastUpdated
                    FROM study_sessions
                """)

                db.execSQL("DROP TABLE study_sessions")
                db.execSQL("ALTER TABLE study_sessions_new RENAME TO study_sessions")

                db.execSQL("CREATE INDEX index_study_sessions_taskId ON study_sessions(taskId)")
                db.execSQL("CREATE INDEX index_study_sessions_examId ON study_sessions(examId)")
                db.execSQL("CREATE INDEX index_study_sessions_startTime ON study_sessions(startTime)")
            }
        }

        /**
         * FIXED MIGRATION (THIS WAS YOUR CRASH)
         */
        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {

                // ALWAYS rebuild cleanly (prevents corrupted schema states)
                db.execSQL("DROP TABLE IF EXISTS study_progress")

                db.execSQL("""
                    CREATE TABLE study_progress (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        examId INTEGER NOT NULL,
                        totalStudyTime INTEGER NOT NULL,
                        flashcardMastery REAL NOT NULL,
                        completionPercentage REAL NOT NULL,
                        lastStudiedTimestamp INTEGER NOT NULL,
                        FOREIGN KEY(examId) REFERENCES exams(id) ON DELETE CASCADE
                    )
                """)

                db.execSQL("""
                    CREATE INDEX index_study_progress_examId 
                    ON study_progress(examId)
                """)
            }
        }

        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
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

        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Fix flashcard_reviews (Add missing columns from broken version 15)
                // We check if they exist first to avoid errors if some users had a "semi-working" version 15
                try {
                    db.execSQL("ALTER TABLE flashcard_reviews ADD COLUMN examId INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}
                try {
                    db.execSQL("ALTER TABLE flashcard_reviews ADD COLUMN correctCount INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}

                // 2. Fix study_sessions (Add missing indices)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_study_sessions_userId ON study_sessions(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_study_sessions_serverId ON study_sessions(serverId)")
            }
        }

        /**
         * SINGLETON
         */

        @Volatile
        private var INSTANCE: StudyPlannerDatabase? = null

        init {
            try {
                System.loadLibrary("sqlcipher")
            } catch (_: Exception) {}
        }

        fun getInstance(context: Context): StudyPlannerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): StudyPlannerDatabase {

            val factory = SupportOpenHelperFactory(
                DatabaseKeyHelper.getDatabaseKey(context),
                object : net.zetetic.database.sqlcipher.SQLiteDatabaseHook {
                    override fun preKey(connection: net.zetetic.database.sqlcipher.SQLiteConnection?) {
                        connection?.execute("PRAGMA cipher_memory_security = OFF;", null, null)
                    }
                    override fun postKey(connection: net.zetetic.database.sqlcipher.SQLiteConnection?) {}
                },
                true
            )

            return Room.databaseBuilder(
                context.applicationContext,
                StudyPlannerDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(factory)
                .addMigrations(
                    MIGRATION_10_11,
                    MIGRATION_11_12,
                    MIGRATION_12_13,
                    MIGRATION_13_14,
                    MIGRATION_14_15,
                    MIGRATION_15_16
                )
                .build()
        }
    }
}