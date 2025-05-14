package com.metaminds.pathcraft.ui.screens

import android.widget.Toast
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.viewModels.NotesContentScreenUiState
import com.metaminds.pathcraft.ui.viewModels.NotesContentScreenViewModel
import com.metaminds.pathcraft.ui.viewModels.formatAnnotatedText
import okhttp3.internal.concurrent.formatDuration
import java.nio.file.Files.size

object NotesContentScreenNavigationDestination : NavigationDestination {
    override val titleRes: Int? = null
    override val route: String = "notes_content"
    const val COURSE_NAME:String ="course_name"
    const val TOPIC = "topic_title"
    const val SUBTOPIC = "sub_topic"
    val routeWithArgs = "$route/{$COURSE_NAME}/{$TOPIC}/{$SUBTOPIC}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesContentScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    viewModel: NotesContentScreenViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState = viewModel.notesUiState
    val context = LocalContext.current
    Scaffold(
        topBar = {
            DefaultAppBar(
                scrollBehavior = scrollBehavior,
                title = viewModel.title,
                onNavIconClick = onBackPressed,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { contentPadding ->
        Box(
            modifier = modifier
                .padding(contentPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is NotesContentScreenUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is NotesContentScreenUiState.Success -> {
                    ContentScreenBody(
                        content = uiState.content,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }

                is NotesContentScreenUiState.Error -> {
                    Toast.makeText(context, uiState.error, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}

@Composable
fun ContentScreenBody(
    modifier: Modifier = Modifier,
    content: String
) {
    val scrollState = rememberScrollState()
        Text(
            text = formatAnnotatedText(content),
            textAlign = TextAlign.Justify,
            modifier = modifier
                .verticalScroll(scrollState)
        )
}




