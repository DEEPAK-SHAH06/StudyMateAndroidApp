package com.example.studymateandroidapp.utils.sync

import com.example.studymateandroidapp.data.model.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun Task.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "priority" to priority.name,
        "status" to status.name,
        "dueDate" to dueDate?.toEpochDay(),
        "dueTime" to dueTime?.toNanoOfDay(),
        "createdAt" to createdAt.toEpochDay(),
        "examId" to examId,
        "tagColor" to tagColor,
        "subjectTag" to subjectTag,
        "isCompleted" to isCompleted,
        "completedAt" to completedAt?.toEpochDay(),
        "isXpAwarded" to isXpAwarded,
        "isPinned" to isPinned,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toTask(docId: String): Task {
    return Task(
        id = (this["id"] as? Number)?.toLong() ?: 0L,
        title = this["title"] as? String ?: "",
        description = this["description"] as? String ?: "",
        priority = Priority.valueOf(this["priority"] as? String ?: Priority.MEDIUM.name),
        status = TaskStatus.valueOf(this["status"] as? String ?: TaskStatus.TODO.name),
        dueDate = (this["dueDate"] as? Number)?.toLong()?.let { LocalDate.ofEpochDay(it) },
        dueTime = (this["dueTime"] as? Number)?.toLong()?.let { java.time.LocalTime.ofNanoOfDay(it) },
        createdAt = (this["createdAt"] as? Number)?.toLong()?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.now(),
        examId = (this["examId"] as? Number)?.toLong(),
        tagColor = (this["tagColor"] as? Number)?.toLong() ?: 0L,
        subjectTag = this["subjectTag"] as? String,
        isCompleted = this["isCompleted"] as? Boolean ?: false,
        completedAt = (this["completedAt"] as? Number)?.toLong()?.let { LocalDate.ofEpochDay(it) },
        isXpAwarded = this["isXpAwarded"] as? Boolean ?: false,
        isPinned = this["isPinned"] as? Boolean ?: false,
        userId = this["userId"] as? String,
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

fun Goal.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "status" to status.name,
        "targetValue" to targetValue,
        "currentValue" to currentValue,
        "examId" to examId,
        "deadline" to deadline?.toEpochDay(),
        "subtasks" to subtasks.map { mapOf("title" to it.title, "isCompleted" to it.isCompleted) },
        "createdAt" to createdAt.toEpochDay(),
        "isXpAwarded" to isXpAwarded,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toGoal(docId: String): Goal {
    val subtasksList = (this["subtasks"] as? List<*>)?.mapNotNull { item ->
        val map = item as? Map<*, *> ?: return@mapNotNull null
        val title = map["title"] as? String ?: ""
        val isCompleted = map["isCompleted"] as? Boolean ?: false
        GoalSubtask(title, isCompleted)
    } ?: emptyList()

    return Goal(
        id = (this["id"] as? Number)?.toLong() ?: 0L,
        title = this["title"] as? String ?: "",
        description = this["description"] as? String ?: "",
        status = GoalStatus.valueOf(this["status"] as? String ?: GoalStatus.NOT_STARTED.name),
        targetValue = (this["targetValue"] as? Number)?.toInt() ?: 100,
        currentValue = (this["currentValue"] as? Number)?.toInt() ?: 0,
        examId = (this["examId"] as? Number)?.toLong(),
        deadline = (this["deadline"] as? Number)?.toLong()?.let { LocalDate.ofEpochDay(it) },
        subtasks = subtasksList,
        createdAt = (this["createdAt"] as? Number)?.toLong()?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.now(),
        isXpAwarded = this["isXpAwarded"] as? Boolean ?: false,
        userId = this["userId"] as? String,
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

fun StudySession.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "taskId" to taskId,
        "examId" to examId,
        "subject" to subject,
        "startTime" to startTime.atZone(ZoneId.systemDefault())?.toEpochSecond(),
        "endTime" to endTime?.atZone(ZoneId.systemDefault())?.toEpochSecond(),
        "durationSeconds" to durationSeconds,
        "isCompleted" to isCompleted,
        "notes" to notes,
        "isXpAwarded" to isXpAwarded,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toStudySession(docId: String): StudySession {
    val startSec = (this["startTime"] as? Number)?.toLong() ?: Instant.now().epochSecond
    val endSec = (this["endTime"] as? Number)?.toLong()
    return StudySession(
        id = (this["id"] as? Number)?.toLong() ?: 0L,
        taskId = (this["taskId"] as? Number)?.toLong(),
        examId = (this["examId"] as? Number)?.toLong(),
        subject = this["subject"] as? String ?: "",
        startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(startSec), ZoneId.systemDefault()),
        endTime = endSec?.let { LocalDateTime.ofInstant(Instant.ofEpochSecond(it), ZoneId.systemDefault()) },
        durationSeconds = (this["durationSeconds"] as? Number)?.toInt() ?: 0,
        isCompleted = this["isCompleted"] as? Boolean ?: false,
        notes = this["notes"] as? String ?: "",
        isXpAwarded = this["isXpAwarded"] as? Boolean ?: false,
        userId = this["userId"] as? String,
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

fun DailyReflection.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "date" to date,
        "content" to content,
        "mood" to mood,
        "studyHighlight" to studyHighlight,
        "createdAt" to createdAt,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toDailyReflection(docId: String): DailyReflection {
    return DailyReflection(
        id = (this["id"] as? Number)?.toLong() ?: 0L,
        date = (this["date"] as? Number)?.toLong() ?: 0L,
        content = this["content"] as? String ?: "",
        mood = this["mood"] as? String ?: "😊",
        studyHighlight = this["studyHighlight"] as? String ?: "",
        createdAt = (this["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        userId = this["userId"] as? String ?: "",
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

fun Achievement.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "type" to type.name,
        "unlockedAt" to unlockedAt,
        "title" to title,
        "description" to description,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toAchievement(docId: String): Achievement {
    return Achievement(
        id = (this["id"] as? Number)?.toLong() ?: 0L,
        type = AchievementType.valueOf(this["type"] as? String ?: AchievementType.FIRST_TASK.name),
        unlockedAt = (this["unlockedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        title = this["title"] as? String ?: "",
        description = this["description"] as? String ?: "",
        userId = this["userId"] as? String ?: "",
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

fun Exam.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "title" to title,
        "subject" to subject,
        "examDate" to examDate,
        "isTimeSet" to isTimeSet,
        "createdAt" to createdAt,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toExam(docId: String): Exam {
    return Exam(
        id = (this["id"] as? Number)?.toLong() ?: 0L,
        title = this["title"] as? String ?: "",
        subject = this["subject"] as? String ?: "",
        examDate = (this["examDate"] as? Number)?.toLong() ?: 0L,
        isTimeSet = this["isTimeSet"] as? Boolean ?: false,
        createdAt = (this["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        userId = this["userId"] as? String,
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

fun Note.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "examId" to examId,
        "title" to title,
        "content" to content,
        "imagePath" to imagePath,
        "createdAt" to createdAt,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toNote(docId: String): Note {
    return Note(
        id = (this["id"] as? Number)?.toLong() ?: 0L,
        examId = (this["examId"] as? Number)?.toLong() ?: 0L,
        title = this["title"] as? String ?: "",
        content = this["content"] as? String ?: "",
        imagePath = this["imagePath"] as? String,
        createdAt = (this["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        userId = this["userId"] as? String,
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

fun Flashcard.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "examId" to examId,
        "question" to question,
        "answer" to answer,
        "isLearned" to isLearned,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toFlashcard(docId: String): Flashcard {
    return Flashcard(
        id = (this["id"] as? Number)?.toLong() ?: 0L,
        examId = (this["examId"] as? Number)?.toLong() ?: 0L,
        question = this["question"] as? String ?: "",
        answer = this["answer"] as? String ?: "",
        isLearned = this["isLearned"] as? Boolean ?: false,
        userId = this["userId"] as? String,
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

fun FlashcardReview.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "examId" to examId,
        "date" to date.toEpochDay(),
        "cardsReviewed" to cardsReviewed,
        "correctCount" to correctCount,
        "timestamp" to timestamp,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toFlashcardReview(docId: String): FlashcardReview {
    return FlashcardReview(
        id = (this["id"] as? Number)?.toLong() ?: 0L,
        examId = (this["examId"] as? Number)?.toLong() ?: 0L,
        date = (this["date"] as? Number)?.toLong()?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.now(),
        cardsReviewed = (this["cardsReviewed"] as? Number)?.toInt() ?: 0,
        correctCount = (this["correctCount"] as? Number)?.toInt() ?: 0,
        timestamp = (this["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        userId = this["userId"] as? String,
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

fun StudyProgress.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "examId" to examId,
        "totalStudyTime" to totalStudyTime,
        "flashcardMastery" to flashcardMastery,
        "completionPercentage" to completionPercentage,
        "lastStudiedTimestamp" to lastStudiedTimestamp,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toStudyProgress(docId: String): StudyProgress {
    return StudyProgress(
        id = (this["id"] as? Number)?.toLong() ?: 0L,
        examId = (this["examId"] as? Number)?.toLong() ?: 0L,
        totalStudyTime = (this["totalStudyTime"] as? Number)?.toLong() ?: 0L,
        flashcardMastery = (this["flashcardMastery"] as? Number)?.toFloat() ?: 0f,
        completionPercentage = (this["completionPercentage"] as? Number)?.toFloat() ?: 0f,
        lastStudiedTimestamp = (this["lastStudiedTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        userId = this["userId"] as? String,
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

fun UserProgress.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "id" to id,
        "totalXp" to totalXp,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toUserProgress(docId: String): UserProgress {
    return UserProgress(
        id = (this["id"] as? Number)?.toLong() ?: 1L,
        totalXp = (this["totalXp"] as? Number)?.toInt() ?: 0,
        userId = this["userId"] as? String,
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}

fun ReminderSetting.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "type" to type.name,
        "isEnabled" to isEnabled,
        "scheduledTime" to scheduledTime?.toNanoOfDay(),
        "daysBefore" to daysBefore,
        "userId" to userId,
        "serverId" to serverId,
        "lastUpdated" to lastUpdated
    )
}

fun Map<String, Any?>.toReminderSetting(docId: String): ReminderSetting {
    return ReminderSetting(
        type = ReminderType.valueOf(this["type"] as? String ?: ReminderType.TASK.name),
        isEnabled = this["isEnabled"] as? Boolean ?: true,
        scheduledTime = (this["scheduledTime"] as? Number)?.toLong()?.let { java.time.LocalTime.ofNanoOfDay(it) },
        daysBefore = (this["daysBefore"] as? Number)?.toInt(),
        userId = this["userId"] as? String,
        serverId = docId,
        lastUpdated = (this["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
    )
}
