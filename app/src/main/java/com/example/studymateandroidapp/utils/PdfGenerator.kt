package com.example.studymateandroidapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.studymateandroidapp.viewmodel.StatisticsViewmodel
import java.io.OutputStream

object PdfGenerator {
    fun generateStatisticsPdf(
        uiState: StatisticsViewmodel.StatisticsUiState,
        outputStream: OutputStream
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        var yPos = 50f

        // Title
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 24f
        canvas.drawText("StudyMate - Statistics Report", 50f, yPos, paint)
        yPos += 40f

        // Overview
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("Overview", 50f, yPos, paint)
        yPos += 30f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 14f
        canvas.drawText("Tasks Done: ${uiState.completedTasks}/${uiState.totalTasks} (${uiState.taskCompletionRate}%)", 50f, yPos, paint)
        yPos += 20f
        canvas.drawText("Goals Met: ${uiState.completedGoals}", 50f, yPos, paint)
        yPos += 20f
        canvas.drawText("Current Streak: ${uiState.currentStreak} days", 50f, yPos, paint)
        yPos += 20f
        canvas.drawText("Best Streak: ${uiState.bestStreak} days", 50f, yPos, paint)
        yPos += 40f

        // Study Time
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("Study Time", 50f, yPos, paint)
        yPos += 30f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 14f
        canvas.drawText("Today: ${formatDuration(uiState.todayStudySeconds)}", 50f, yPos, paint)
        yPos += 20f
        canvas.drawText("This Week: ${formatDuration(uiState.thisWeekStudySeconds)}", 50f, yPos, paint)
        yPos += 20f
        canvas.drawText(uiState.weeklyAverageSubtitle, 50f, yPos, paint)
        yPos += 40f

        // Weekly Activity
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("Weekly Activity", 50f, yPos, paint)
        yPos += 30f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 12f
        uiState.dailyChartData.forEach { point ->
            canvas.drawText("${point.label}: ${formatDuration(point.seconds)}", 70f, yPos, paint)
            yPos += 15f
        }

        pdfDocument.finishPage(page)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
    }

    private fun formatDuration(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) "${h}h ${m}m ${s}s" else if (m > 0) "${m}m ${s}s" else "${s}s"
    }
}
