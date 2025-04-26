package com.metaminds.pathcraft.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metaminds.pathcraft.data.MessageModel
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.viewModels.ChatScreenViewModel

object ChatScreenNavigationDestination : NavigationDestination {
    override val title: String = "home_screen"
    override val route: String = title
}

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatScreenViewModel = viewModel(factory = AppViewModelProvider.factory),
    navigateToLogInScreen:()-> Unit
) {
    var prompt by remember { mutableStateOf("") }
    val focusManager= LocalFocusManager.current
    Scaffold(
        modifier= Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }){focusManager.clearFocus()},
        floatingActionButton = {
            IconButton(
                onClick = {
                    viewModel.getAuth().signOut()
                    navigateToLogInScreen()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Start
    ) { contentPadding->
        Column(
            modifier = modifier.fillMaxSize(1F).padding(contentPadding),
            verticalArrangement = Arrangement.Bottom,
        ) {
            ChatColumn(
                modifier = Modifier.weight(1F).padding(10.dp),
                messageList = viewModel.messageList.reversed(),
                viewModel=viewModel
            )
            ChatBotInputBox(
                modifier = Modifier,
                prompt = prompt,
                onPromptChange = { prompt = it },
                onPromptSubmit = {
                    viewModel.sendMessage(prompt)
                    prompt = ""
                }
            )

        }
    }

}

@Composable
fun ChatColumn(
    modifier: Modifier = Modifier,
    messageList: List<MessageModel>,
    viewModel: ChatScreenViewModel
) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true
    ) {

        itemsIndexed(items = messageList) { index, messageModel ->
            if(index == messageList.size -4 &&viewModel.topicList.isNotEmpty()) {
                    RoadMapChart(
                        topicList = viewModel.topicList
                    )
            }
            if(messageModel.isShown) {
                ChatBox(
                    messageModel = messageModel,
                )
            }
        }
    }
}

@Composable
fun ChatBox(
    modifier: Modifier = Modifier,
    messageModel: MessageModel,
) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        top = 20.dp,
                        bottom = 20.dp,
                        start = if (messageModel.role == "user") 70.dp else 10.dp,
                        end = if (messageModel.role == "user") 10.dp else 70.dp
                    ),
                contentAlignment = if (messageModel.role == "user") Alignment.CenterEnd else Alignment.CenterStart,
            ) {

                Box(
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                        .background(color = if (messageModel.role == "user") Color.Blue else Color.Green)

                ) {

                    SelectionContainer {
                        Text(
                            text = messageModel.message, modifier = Modifier
                                .padding(10.dp)
                        )
                    }


                }
            }
        }

}

@Composable
fun ChatBotInputBox(
    modifier: Modifier = Modifier,
    prompt: String,
    onPromptChange: (String) -> Unit,
    onPromptSubmit: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = prompt,
            onValueChange = onPromptChange,
            modifier = Modifier.weight(1F)
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

@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview() {
    ChatScreen(navigateToLogInScreen = {})
}
