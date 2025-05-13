package com.metaminds.pathcraft.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.gson.Gson
import com.metaminds.pathcraft.HOME_SCREEN
import com.metaminds.pathcraft.R
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.viewModels.HomeScreenViewModel
import com.metaminds.pathcraft.ui.viewModels.HomeUiState

object HomeScreenNavigationDestination : NavigationDestination {
    override val titleRes: Int = R.string.home_screen
    override val route: String = HOME_SCREEN
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToSectionScreen:(Int,String)-> Unit,
    navigateToChatScreen:(String?)-> Unit,
    navigateToLoginScreen:()-> Unit,
    navigateToCourseScreen: (String) -> Unit,
    viewModel: HomeScreenViewModel = viewModel(factory= AppViewModelProvider.factory)
) {
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }
    val uiState=viewModel.homeUiState
    Surface(
        modifier = modifier.padding(WindowInsets.systemBars.asPaddingValues()),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            HomeScreenHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp),
                onProfileClick = { },
                navigateToChatScreen={navigateToChatScreen(null)},
                greetingMessage = stringResource(viewModel.getGreeting(),viewModel.auth.currentUser?.displayName.toString().replaceFirstChar { it.uppercaseChar() }),
                onLogOut = {
                    viewModel.auth.signOut()
                    navigateToLoginScreen()
                }
            )
            HomeScreenBody(
                navigateToSectionScreen={title,courseList->
                    navigateToSectionScreen(title,courseList) },
                uiState = uiState,
                navigateToChatScreen = {
                    navigateToChatScreen(it)
                },
                navigateToCourseScreen = {
                    navigateToCourseScreen(it)
                }
            )

        }
    }
}

@Composable
fun HomeScreenBody(
    modifier: Modifier = Modifier,
    navigateToSectionScreen: (title:Int, courseList:String) -> Unit,
    uiState: HomeUiState,
    navigateToChatScreen: (String?) -> Unit,
    navigateToCourseScreen:(String)-> Unit
) {
    val context = LocalContext.current
    LazyColumn(
        contentPadding = PaddingValues(vertical = 10.dp),
        modifier=modifier,
        verticalArrangement = Arrangement.spacedBy(50.dp)
    ) {
        when(uiState){
            is HomeUiState.Loading -> {
                item {
                    ShowLoadingScreen()
                }
            }
            is HomeUiState.Error -> {
                Toast.makeText(context,uiState.error,Toast.LENGTH_SHORT).show()
            }
            is HomeUiState.Success -> {
                item {
                    SectionBody(
                        sectionName = "Featured for you",
                        navigateToSectionScreen = { navigateToSectionScreen(R.string.featured_skills,Uri.encode(Gson().toJson(uiState.featuredTopics))) },
                        courseList = uiState.featuredTopics,
                        navigateToChatScreen = {
                            navigateToChatScreen(it)
                        }
                    )
                }
                item{
                    SectionBody(
                        sectionName = "Trending Skills",
                        navigateToSectionScreen = {navigateToSectionScreen(R.string.trending_skills,
                            Uri.encode(Gson().toJson(uiState.trendingTopics)))},
                        courseList = uiState.trendingTopics,
                        navigateToChatScreen = navigateToChatScreen
                    )
                }
                item{
                    SectionBody(
                        sectionName="Your paths",
                        navigateToSectionScreen = {},
                        courseList = uiState.userTopics,
                        navigateToChatScreen = {
                            it?.let {
                                navigateToCourseScreen(it)
                            }?: navigateToChatScreen(null)
                        }
                    )
                }
            }
        }

    }
}

@Composable
fun SectionBody(
    modifier: Modifier = Modifier,
    sectionName: String,
    navigateToSectionScreen: () -> Unit,
    courseList: List<String>,
    navigateToChatScreen: (String?) -> Unit={}
) {
    Column(
        modifier=modifier
    ){
        Box (
            modifier=Modifier
                .padding(PaddingValues(horizontal = 5.dp))
                .clip(CircleShape)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.CenterStart
        ){
            Text(
                text=sectionName,
                modifier=Modifier.padding(horizontal = 15.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            ActionButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp),
                onClick = navigateToSectionScreen,
                painter = painterResource(R.drawable.outline_arrow_right_alt_24),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                iconSize = 25.dp
            )
        }
        Spacer(Modifier.height(15.dp))
        if(courseList.isEmpty())
            ShowEmptySection(
                modifier=Modifier.heightIn(130.dp)
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(width = 2.dp, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(10.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    ,
                navigateToChatScreen= {navigateToChatScreen(null)}
            )
        else
            RowBody(
                contentPaddingValues = PaddingValues(horizontal = 10.dp),
                courseList = courseList,
                onItemClick = navigateToChatScreen,
            )
    }
}

@Composable
fun ShowEmptySection(
    modifier: Modifier = Modifier,
    navigateToChatScreen: () -> Unit
) {
    Column (
        modifier=modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = "Looks like you haven't started learning yet.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        TextButton(
            onClick = navigateToChatScreen
        ) {
            Text(
                text = "Click here to get a personalized learning path...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun RowBody(
    modifier: Modifier = Modifier,
    contentPaddingValues: PaddingValues,
    courseList:List<String>,
    onItemClick:(String)-> Unit
) {
        LazyRow(
            modifier = modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = contentPaddingValues
        ) {
            items(count = if(courseList.size<5) courseList.size else 5) {
                SkillCard(
                    modifier = Modifier.sizeIn(minWidth = 180.dp, minHeight = 80.dp),
                    courseName = courseList[it],
                    shape = RoundedCornerShape(
                        topStart = 15.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 15.dp
                    ),
                    onClick = {
                        onItemClick(courseList[it])
                    }
                )
            }
    }
}

@Composable
fun SkillCard(
    modifier: Modifier = Modifier,
    courseName: String="name",
    shape: Shape,
    onClick: () -> Unit
    ) {
    OutlinedCard(
        onClick =onClick,
        modifier=Modifier.border(width = 2.dp,shape= shape, color = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = shape,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ){
        Box(
            modifier=modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier=Modifier.padding(10.dp),
                text = courseName,
                style = MaterialTheme.typography.bodyLarge,
            )
        }

    }
}


@Composable
fun HomeScreenHeader(
    modifier: Modifier = Modifier,
    onProfileClick:()-> Unit,
    navigateToChatScreen: () -> Unit,
    greetingMessage: String,
    onLogOut:()-> Unit
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(10.dp)
        ) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(R.drawable.brain),
                contentDescription = null
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = greetingMessage,
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }
        Row(
            modifier=Modifier
                .align(Alignment.TopEnd)
                .padding(PaddingValues(vertical = 5.dp, horizontal = 10.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionButton(
                modifier = Modifier
                    .size(50.dp),
                onClick = onProfileClick,
                icon = Icons.Filled.Person,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                iconSize = 30.dp
            )
            ActionButton(
                modifier = Modifier
                    .size(50.dp),
                onClick = onLogOut,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                iconSize = 30.dp,
                painter = painterResource(R.drawable.baseline_exit_to_app_24)
            )
        }

        ShowRobotAnimation(modifier = Modifier
            .clickable(
                interactionSource = remember {MutableInteractionSource()},
                indication = null
            ) { navigateToChatScreen() }
            .size(250.dp)
            .align(Alignment.BottomEnd))
    }
}

@Composable
fun ShowRobotAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.robot_animation))
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


@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector?=null,
    containerColor: Color,
    tint: Color,
    iconSize: Dp,
    painter: Painter?= null
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
        )
    ) {
        icon?.let {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = icon,
                contentDescription = null,
                tint = tint
            )
        }
        painter?.let{
            Icon(
                modifier = Modifier.size(iconSize),
                painter = painter,
                contentDescription = null,
                tint = tint
            )
        }
    }
}
