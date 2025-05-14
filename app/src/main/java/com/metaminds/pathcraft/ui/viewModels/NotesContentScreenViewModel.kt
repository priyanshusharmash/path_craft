package com.metaminds.pathcraft.ui.viewModels

import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metaminds.pathcraft.data.AppRepository
import com.metaminds.pathcraft.data.GeminiRepository
import com.metaminds.pathcraft.ui.screens.NotesContentScreenNavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesContentScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: AppRepository
): ViewModel() {
    private val courseName:String = Uri.decode(checkNotNull(savedStateHandle[NotesContentScreenNavigationDestination.COURSE_NAME]))
    val title: String= Uri.decode(checkNotNull(savedStateHandle[NotesContentScreenNavigationDestination.TOPIC]))
    val subtopic:String =Uri.decode(checkNotNull(savedStateHandle[NotesContentScreenNavigationDestination.SUBTOPIC]))
    var notesUiState: NotesContentScreenUiState by mutableStateOf(NotesContentScreenUiState.Loading)
        private set

    init {
        viewModelScope.launch{
            notesUiState=try {
                NotesContentScreenUiState.Success(
                    content = repository.getContentNotes(courseName,title,subtopic)?:repository.generateTopicContent(title,subtopic)
                )
            }catch (e:Exception){
                NotesContentScreenUiState.Error(error=e.message.toString())
            }
            if(notesUiState is NotesContentScreenUiState.Success){
                repository.saveContentNotes(courseName =courseName ,topic = title, subTopic = subtopic, content = (notesUiState as NotesContentScreenUiState.Success).content)
            }
        }
    }
}

fun formatAnnotatedText(response: String): AnnotatedString {
    val builder = AnnotatedString.Builder()

    // Patterns for headings, bullets, and inline bold markers (for both ** and *)
    val boldPattern = Regex("(\\*\\*(.*?)\\*\\*)|(\\*(.*?)\\*)")
    val headingPattern = Regex("^(#+)\\s*(.*)")
    val bulletPattern = Regex("^\\*\\s+(.*)")

    // Local function to process inline text and apply bold styles.
    fun appendProcessedInline(text: String) {
        var currentIndex = 0
        boldPattern.findAll(text).forEach { match ->
            val start = match.range.first
            // Append text before the bold marker
            if (start > currentIndex) {
                builder.append(text.substring(currentIndex, start))
            }
            // Check which capturing group matched (group 2 for **bold**; group 4 for *bold*)
            val boldText = if (match.groupValues[2].isNotEmpty()) {
                match.groupValues[2]
            } else {
                match.groupValues[4]
            }
            // Append bold text without the asterisks
            builder.pushStyle(SpanStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
            builder.append(boldText)
            builder.pop()
            currentIndex = match.range.last + 1
        }
        // Append any remaining text after the last bold segment.
        if (currentIndex < text.length) {
            builder.append(text.substring(currentIndex))
        }
    }

    // Process each line from the response
    response.lineSequence().forEach { line ->
        // Check if the line is a heading (starts with one or more '#' characters)
        val headingMatch = headingPattern.matchEntire(line)
        if (headingMatch != null) {
            val (hashes, headingText) = headingMatch.destructured
            val headingLevel = hashes.length
            // Choose heading style based on the number of '#' characters
            val headingStyle = when (headingLevel) {
                1 -> SpanStyle(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 28.sp
                )

                2 -> SpanStyle(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 24.sp
                )

                else -> SpanStyle(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            builder.pushStyle(headingStyle)
            appendProcessedInline(headingText)
            builder.pop()
            builder.append("\n")
        } else {
            // Check if the line is a bullet list item (starts with "* ")
            val bulletMatch = bulletPattern.matchEntire(line)
            if (bulletMatch != null) {
                val content = bulletMatch.groupValues[1]
                builder.append("• ")
                appendProcessedInline(content)
                builder.append("\n")
            } else {
                // Process a normal line with inline bold formatting
                appendProcessedInline(line)
                builder.append("\n")
            }
        }
    }
    return builder.toAnnotatedString()
}
sealed class NotesContentScreenUiState{
    data class Success(val content:String): NotesContentScreenUiState()
    object Loading: NotesContentScreenUiState()
    data class Error(val error:String): NotesContentScreenUiState()
}