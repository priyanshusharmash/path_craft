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
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardColors
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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
    navigateToSectionScreen: (Int, String) -> Unit,
    navigateToChatScreen: (String?) -> Unit,
    navigateToLoginScreen: () -> Unit,
    navigateToCourseScreen: (String) -> Unit,
    viewModel: HomeScreenViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }
    val uiState = viewModel.homeUiState
    Surface(
        modifier = modifier.padding(WindowInsets.systemBars.asPaddingValues()),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HomeScreenHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp),
                onProfileClick = { },
                navigateToChatScreen = { navigateToChatScreen(null) },
                greetingMessage = stringResource(
                    viewModel.getGreeting(),
                    viewModel.auth.currentUser?.displayName.toString()
                        .replaceFirstChar { it.uppercaseChar() }),
                onLogOut = {
                    viewModel.auth.signOut()
                    navigateToLoginScreen()
                }
            )
            HomeScreenBody(
                navigateToSectionScreen = { title, courseList ->
                    navigateToSectionScreen(title, courseList)
                },
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
    navigateToSectionScreen: (title: Int, courseList: String) -> Unit,
    uiState: HomeUiState,
    navigateToChatScreen: (String?) -> Unit,
    navigateToCourseScreen: (String) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.padding(PaddingValues(vertical = 10.dp)),
        verticalArrangement = Arrangement.spacedBy(50.dp),
    ) {
        when (uiState) {
            is HomeUiState.Loading -> {
                ShowLoadingScreen()
            }

            is HomeUiState.Error -> {
                Toast.makeText(context, uiState.error, Toast.LENGTH_SHORT).show()
            }

            is HomeUiState.Success -> {
                SectionBody(
                    modifier = Modifier,
                    sectionName = stringResource(R.string.featured_skills),
                    navigateToSectionScreen = {
                        navigateToSectionScreen(
                            R.string.featured_skills,
                            Uri.encode(Gson().toJson(uiState.featuredTopics))
                        )
                    },
                    courseList = uiState.featuredTopics,
                    navigateToChatScreen = {
                        navigateToChatScreen(it)
                    }
                )
                SectionBody(
                    modifier = Modifier,
                    sectionName = stringResource(R.string.trending_skills),
                    navigateToSectionScreen = {
                        navigateToSectionScreen(
                            R.string.trending_skills,
                            Uri.encode(Gson().toJson(uiState.trendingTopics))
                        )
                    },
                    courseList = uiState.trendingTopics.shuffled(),
                    navigateToChatScreen = navigateToChatScreen
                )

                if (uiState.userTopics.isEmpty()) {
                    ShowEmptySection(
                        modifier = Modifier
                            .heightIn(130.dp)
                            .padding(horizontal = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(color = MaterialTheme.colorScheme.surfaceVariant),
                        navigateToChatScreen = { navigateToChatScreen(null) },
                        sectionName = stringResource(R.string.your_learning)
                    )
                } else if (uiState.userTopics[uiState.userTopics.size - 1].imageLink.isEmpty()) {
                    val courseList = remember { mutableListOf("") }
                    uiState.userTopics.forEachIndexed { index, topic ->
                        courseList.add(topic.topicName)
                    }
                    SectionBody(
                        modifier = Modifier,
                        sectionName = stringResource(R.string.your_learning),
                        navigateToSectionScreen = {},
                        courseList = courseList,
                        navigateToChatScreen = {
                            it?.let {
                                navigateToCourseScreen(it)
                            } ?: navigateToChatScreen(null)
                        }
                    )
                } else {
                    UserCoursesSection(
                        uiState = uiState,
                        sectionName = stringResource(R.string.your_learning),
                        onClick = { navigateToCourseScreen(it) }
                    )
                }
            }
        }

    }
}


@Composable
fun UserCoursesSection(
    modifier: Modifier = Modifier,
    uiState: HomeUiState.Success,
    sectionName: String,
    onClick: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 5.dp))
                .clip(CircleShape)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = sectionName,
                modifier = Modifier.padding(horizontal = 15.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            ActionButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp),
                onClick = { },
                painter = painterResource(R.drawable.outline_arrow_right_alt_24),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                tint = MaterialTheme.colorScheme.primaryContainer,
                iconSize = 25.dp
            )
        }
        Spacer(Modifier.height(15.dp))
        Column (modifier=Modifier.background(color=MaterialTheme.colorScheme.surfaceVariant)){
            uiState.userTopics.forEachIndexed { index, topic ->
                UserTopicCard(
                    topicName = topic.topicName,
                    topicImageUrl = topic.imageLink,
                    onClick = onClick,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    color = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    imageModifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Composable
fun UserTopicCard(
    modifier: Modifier = Modifier,
    topicName: String,
    topicImageUrl: String,
    onClick: (String) -> Unit,
    color: CardColors,
    imageModifier: Modifier = Modifier
) {
    OutlinedCard(
        elevation = CardDefaults.outlinedCardElevation(5.dp),
        modifier = modifier,
        onClick = { onClick(topicName) },
        colors = color
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = "https://cdn.pixabay.com/photo/2025/05/07/19/13/soap-bubbles-9585871_1280.jpg",
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
            Text(
                text = topicName.replaceFirstChar { it.uppercaseChar() },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
            )
        }
    }

}


@Composable
fun SectionBody(
    modifier: Modifier = Modifier,
    sectionName: String,
    navigateToSectionScreen: () -> Unit,
    courseList: List<String>,
    navigateToChatScreen: (String?) -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 5.dp))
                .clip(CircleShape)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = sectionName,
                modifier = Modifier.padding(horizontal = 15.dp),
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
        RowBody(
            contentPaddingValues = PaddingValues(horizontal = 10.dp, vertical = 20.dp),
            courseList = courseList,
            onItemClick = navigateToChatScreen,
            modifier = Modifier
                .height(180.dp)
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
        )
    }
}

@Composable
fun ShowEmptySection(
    modifier: Modifier = Modifier,
    navigateToChatScreen: () -> Unit,
    sectionName: String
) {
    Column {
        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 5.dp))
                .clip(CircleShape)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = sectionName,
                modifier = Modifier.padding(horizontal = 15.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            ActionButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp),
                onClick = { },
                painter = painterResource(R.drawable.outline_arrow_right_alt_24),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                tint = MaterialTheme.colorScheme.primaryContainer,
                iconSize = 25.dp
            )
        }
        Spacer(Modifier.height(15.dp))
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
}


    @Composable
    fun RowBody(
        modifier: Modifier = Modifier,
        contentPaddingValues: PaddingValues,
        courseList: List<String>,
        onItemClick: (String) -> Unit
    ) {
        LazyHorizontalStaggeredGrid(
            rows = StaggeredGridCells.Fixed(2),
            modifier = modifier,
            contentPadding = contentPaddingValues,
            verticalArrangement = Arrangement.spacedBy(
                15.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            items(count = if (courseList.size < 5) courseList.size else 10) {
                SkillCard(
                    modifier = Modifier.sizeIn(minWidth = 180.dp),
                    courseName = courseList[it],
                    shape = RoundedCornerShape(10.dp),
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
        courseName: String = "name",
        shape: Shape,
        onClick: () -> Unit
    ) {
        OutlinedCard(
            onClick = onClick,
            modifier = Modifier
                .padding(end = 10.dp)
                .border(width = 2.dp, shape = shape, color = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = shape,
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        ) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = courseName.replace("\n", "").trim(),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

        }
    }


    @Composable
    fun HomeScreenHeader(
        modifier: Modifier = Modifier,
        onProfileClick: () -> Unit,
        navigateToChatScreen: () -> Unit,
        greetingMessage: String,
        onLogOut: () -> Unit
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
                modifier = Modifier
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

            ShowRobotAnimation(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
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
        icon: ImageVector? = null,
        containerColor: Color,
        tint: Color,
        iconSize: Dp,
        painter: Painter? = null
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
            painter?.let {
                Icon(
                    modifier = Modifier.size(iconSize),
                    painter = painter,
                    contentDescription = null,
                    tint = tint
                )
            }
        }
    }
