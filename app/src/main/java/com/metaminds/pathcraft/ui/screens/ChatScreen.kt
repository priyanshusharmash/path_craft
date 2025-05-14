package com.metaminds.pathcraft.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.metaminds.pathcraft.CHAT_SCREEN
import com.metaminds.pathcraft.R
import com.metaminds.pathcraft.data.MessageModel
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.viewModels.ChatScreenViewModel
import com.metaminds.pathcraft.ui.viewModels.ChatStatus
import com.metaminds.pathcraft.ui.viewModels.DataFetchingState

object ChatScreenNavigationDestination : NavigationDestination {
    override val titleRes: Int = R.string.chat_screen
    override val route: String = CHAT_SCREEN
    const val COURSE:String = "course"
    val route_with_args : String = "$route/{$COURSE}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatScreenViewModel = viewModel(factory = AppViewModelProvider.factory),
    onBackPressed: () -> Unit
) {
    BackHandler {
        viewModel.onBackPressed()
       onBackPressed()
    }
    val scrollBehavior= TopAppBarDefaults.enterAlwaysScrollBehavior()
    var prompt by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() },
        topBar = {
            DefaultAppBar(
                scrollBehavior =scrollBehavior,
                title= stringResource(ChatScreenNavigationDestination.titleRes),
                onNavIconClick = {onBackPressed()}
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                verticalArrangement = Arrangement.spacedBy(space = 10.dp, alignment = Alignment.Bottom)
            ) {
                ChatColumn(
                    modifier = Modifier
                        .weight(1f),
                    messageList = viewModel.messageList.reversed(),
                    viewModel = viewModel,
                )
                if(viewModel.chatState== ChatStatus.End)
                    ConfirmDialog(
                        onStart = {
                            viewModel.onBackPressed()
                            onBackPressed()
                        }
                    )
                else {
                    ChatBotInputBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingValues(horizontal = 10.dp)),
                        prompt = prompt,
                        onPromptChange = {prompt=it},
                        onPromptSubmit = {
                            viewModel.sendMessage(prompt)
                            prompt=""
                        },
                    )
                }

            }
        }
    }

}


@Composable
fun ChatColumn(
    modifier: Modifier = Modifier,
    messageList: List<MessageModel>,
    viewModel: ChatScreenViewModel
) {
    val context=LocalContext.current
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Bottom),
        reverseLayout = true,
        contentPadding = PaddingValues(10.dp),
    ) {
        when(viewModel.dataFetchingState){
            is DataFetchingState.Loading -> {
                item {
                    ChatBox(
                        modifier = Modifier.size(width = 150.dp, height = 50.dp),
                        messageModel = MessageModel(isShown = false, message = "", role = ""),
                        isLoading = true,
                        viewModel = viewModel
                    )
                }
            }
            is DataFetchingState.Error -> {
                Toast.makeText(context,(viewModel.dataFetchingState as DataFetchingState.Error).error,Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
        itemsIndexed(items = messageList) { index, messageModel ->
            if (index == messageList.size - 4 && viewModel.topicList.isNotEmpty()) {
                RoadMapChart()
            }
            if (messageModel.isShown) {
                ChatBox(
                    messageModel = messageModel,
                    isLoading = false,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun ChatBox(
    modifier: Modifier = Modifier,
    messageModel: MessageModel,
    isLoading: Boolean,
    viewModel: ChatScreenViewModel
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (messageModel.role == "user") 70.dp else 10.dp,
                    end = if (messageModel.role == "user") 10.dp else 70.dp
                ),
            contentAlignment = if (messageModel.role == "user") Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        color =
                            if (messageModel.role == "user") MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primaryContainer
                    )
            ) {
               if(isLoading)
                   ChatLoading()
               else
                    SelectionContainer {
                    Text(
                        text = viewModel.formatText(messageModel.message), modifier = Modifier
                            .padding(10.dp),
                        color =if(messageModel.role== "user") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                    ) }
            }
        }
    }

}

@Composable
fun ChatBotInputBox(
    modifier: Modifier = Modifier,
    prompt: String,
    onPromptChange: (String) -> Unit,
    onPromptSubmit: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = prompt,
            onValueChange = onPromptChange,
            modifier = Modifier.weight(1F),
            shape = CircleShape,
            singleLine = true,
        )
        IconButton(
            onClick = onPromptSubmit
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null
            )
        }
    }
}

@Composable
fun ConfirmDialog(
    modifier: Modifier = Modifier,
    onStart:()-> Unit,
    onChangeCourse:()-> Unit = { }
) {
    Column(
        modifier=modifier.fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color=MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp))
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text="Ready to embark a new journey?",
            style = MaterialTheme.typography.bodyLarge,
            color= MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row (
            modifier=Modifier.fillMaxWidth()
                .padding(PaddingValues(horizontal = 20.dp)),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            TextButton(
                modifier=Modifier.widthIn(100.dp),
                onClick = onChangeCourse,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Text(
                    text = "Change Course",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            TextButton(
                modifier=Modifier.widthIn(100.dp),
                onClick = onStart,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Text(
                    text="Start",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


@Composable
fun ChatLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.chat_loading))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
        LottieAnimation(
            modifier = modifier,
            composition = composition,
            progress = progress
        )
    }
}

