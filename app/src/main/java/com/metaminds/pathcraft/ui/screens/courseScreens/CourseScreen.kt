package com.metaminds.pathcraft.ui.screens.courseScreens

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.metaminds.pathcraft.R
import com.metaminds.pathcraft.YOUR_LEARNING
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.navigation.CourseScreenNavigationGraph
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.screens.DefaultAppBar
import com.metaminds.pathcraft.ui.viewModels.CourseScreenViewModel

object CourseScreenNavigationDestination: NavigationDestination{
    override val titleRes: Int = R.string.your_learning
    override val route: String = YOUR_LEARNING
    const val COURSE :String ="course"
    val routeWithArgs = "$route/{$COURSE}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    viewModel: CourseScreenViewModel= viewModel(factory = AppViewModelProvider.factory),
    onBackPressed: () -> Unit,
) {
    BackHandler {
        onBackPressed()
    }
    val navController = rememberNavController()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val scrollBehavior= TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            CourseScreenTopBar(
                scrollBehavior =scrollBehavior,
                onTabChange = {index,route->
                    selectedTabIndex = index
                    navController.navigate(route){
                        popUpTo(0)
                    }
                },
                selectedTabIndex = selectedTabIndex,
                onBackPressed = {onBackPressed()},
                actionBarTitle = viewModel.title
            )}
    ) { contentPadding ->
        CourseScreenNavigationGraph(
            modifier = Modifier.padding(contentPadding),
            navController = navController,
            courseScreenViewModel = viewModel
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreenTopBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    onTabChange: (Int, String) -> Unit,
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
            onTabChange ={index,route-> onTabChange(index,route)},
            selectedTabIndex = selectedTabIndex,
        )
    }
}

@Composable
fun CourseScreenNavigationBar(
    selectedTabIndex: Int,
    onTabChange:(Int,String)-> Unit
) {
    TabRow(selectedTabIndex = selectedTabIndex) {
        TabItems.entries.forEachIndexed { index, destination ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = {
                    onTabChange(index,destination.route)
                },
                text = { Text(destination.tabName.replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

enum class TabItems(val tabName:String,val route:String){
    Notes("Notes", NotesTabScreenNavigationDestination.route),
    Videos("Videos", CourseVideosResourcesTabNavigationDestination.route),
    Books("Books", CourseBooksResourcesTabNavigationDestination.route)
}
