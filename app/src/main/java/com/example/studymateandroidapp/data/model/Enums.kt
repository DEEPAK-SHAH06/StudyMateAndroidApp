package com.example.studymateandroidapp.data.model

/**
 * Priority levels for tasks.
 */
enum class Priority { LOW, MEDIUM, HIGH }

/**
 * Status of a task through its lifecycle.
 */
enum class TaskStatus { TODO, IN_PROGRESS, COMPLETED }

/**
 * Status of a goal through its lifecycle.
 */
enum class GoalStatus { NOT_STARTED, IN_PROGRESS, COMPLETED, ABANDONED }
