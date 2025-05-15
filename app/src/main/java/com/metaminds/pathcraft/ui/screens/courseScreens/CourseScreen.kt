package com.metaminds.pathcraft.ui.screens.courseScreens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.metaminds.pathcraft.R
import com.metaminds.pathcraft.YOUR_LEARNING
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.navigation.CourseScreenNavigationGraph
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.screens.DefaultAppBar
import com.metaminds.pathcraft.ui.viewModels.CourseFetchStatus
import com.metaminds.pathcraft.ui.viewModels.CourseScreenViewModel
import kotlinx.coroutines.delay

object CourseScreenNavigationDestination: NavigationDestination{
    override val titleRes: Int = R.string.your_learning
    override val route: String = YOUR_LEARNING
    const val COURSE :String = com.metaminds.pathcraft.COURSE
    val routeWithArgs = "$route/{$COURSE}"
}

@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    viewModel: CourseScreenViewModel= viewModel(factory = AppViewModelProvider.factory),
    onBackPressed: () -> Unit,
    navigateToNotesContentScreen:(String,String,String)-> Unit
) {
    BackHandler {
        onBackPressed()
    }
    val navController = rememberNavController()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val scrollBehavior= TopAppBarDefaults.pinnedScrollBehavior()
    var previousTab by remember { mutableStateOf("") }
    LaunchedEffect(selectedTabIndex) {
        viewModel.courseUiState= CourseFetchStatus.Waiting
        delay(10)
        viewModel.refreshState()
    }

    Scaffold(
        topBar = {
            CourseScreenTopBar(
                scrollBehavior =scrollBehavior,
                onTabChange = {index,route,previousTabRoute->
                    previousTab=previousTabRoute
                    if(selectedTabIndex!=index){
                        navController.navigate(route){
                            popUpTo(0)
                        }}
                    selectedTabIndex = index

                },
                selectedTabIndex = selectedTabIndex,
                onBackPressed = {onBackPressed()},
                actionBarTitle = viewModel.title
            )}
    ) { contentPadding ->
        CourseScreenNavigationGraph(
            modifier = Modifier.padding(contentPadding),
            navController = navController,
            courseScreenViewModel = viewModel,
            navigateToNotesContentScreen={topic,subtopic->
                navigateToNotesContentScreen(viewModel.title,topic,subtopic)
            },
            previousDestination = previousTab
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreenTopBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    onTabChange: (Int, String, String) -> Unit,
    selectedTabIndex: Int,
    actionBarTitle: String,
    onBackPressed:()-> Unit
) {
    Column (
        modifier=modifier.fillMaxWidth()
    ) {
        DefaultAppBar(
            title = actionBarTitle.replaceFirstChar { it.uppercaseChar() },
            onNavIconClick = onBackPressed,
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        CourseScreenNavigationBar(
            onTabChange ={index,route,previousRoute-> onTabChange(index,route,previousRoute)},
            selectedTabIndex = selectedTabIndex,
        )
    }
}

@Composable
fun CourseScreenNavigationBar(
    selectedTabIndex: Int,
    onTabChange:(Int, String, String)-> Unit
) {
    TabRow(selectedTabIndex = selectedTabIndex) {
        var previousTabIndex by remember { mutableIntStateOf(selectedTabIndex) }
        TabItems.entries.forEachIndexed { index, destination ->

            Tab(
                selected = selectedTabIndex == index,
                onClick = {
                    previousTabIndex=selectedTabIndex
                    onTabChange(index,destination.route, TabItems.entries[previousTabIndex].route)

                },
                text = { Text(text=stringResource(destination.tabNameRes).replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

enum class TabItems(@StringRes val tabNameRes: Int, val route:String){
    Notes(R.string.notes_tab_label, NotesTabScreenNavigationDestination.route),
    Videos(R.string.tutorials_tab_label, CourseVideosResourcesTabNavigationDestination.route),
    Books(R.string.resources_tab_label, CourseBooksResourcesTabNavigationDestination.route)
}
