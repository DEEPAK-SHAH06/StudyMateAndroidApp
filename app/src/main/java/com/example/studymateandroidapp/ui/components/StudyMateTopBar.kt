package com.example.studymateandroidapp.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.studymateandroidapp.ui.theme.BackgroundWhite
import com.example.studymateandroidapp.ui.theme.PureBlack

/**
 * Shared top app bar used throughout the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyMateTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = PureBlack
            )
        },

        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = PureBlack
                    )
                }
            }
        },

        actions = actions,

        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = BackgroundWhite
        ),

        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun StudyMateTopBarPreview() {
    StudyMateTopBar(
        title = "Tasks",
        onBack = {}
    )
}
