package com.example.studymateandroidapp.ui.widget

import android.content.Context
import androidx.glance.appwidget.updateAll

object WidgetUpdateHelper {
    suspend fun updateAllWidgets(context: Context) {
        StudyPlannerWidget().updateAll(context)
        FlashcardWidget().updateAll(context)
        ExamWidget().updateAll(context)
        StatisticsWidget().updateAll(context)
        GoalWidget().updateAll(context)
        HabitWidget().updateAll(context)
    }
}