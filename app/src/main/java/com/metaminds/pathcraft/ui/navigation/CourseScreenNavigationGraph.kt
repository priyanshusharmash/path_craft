package com.metaminds.pathcraft.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseBooksResourcesTab
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseBooksResourcesTabNavigationDestination
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseVideosResourcesTab
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseVideosResourcesTabNavigationDestination
import com.metaminds.pathcraft.ui.screens.courseScreens.NotesTabScreen
import com.metaminds.pathcraft.ui.screens.courseScreens.NotesTabScreenNavigationDestination
import com.metaminds.pathcraft.ui.viewModels.CourseScreenViewModel

@Composable
fun CourseScreenNavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController= rememberNavController(),
    courseScreenViewModel: CourseScreenViewModel
) {
    NavHost(
        modifier=modifier,
        navController=navController,
        startDestination= NotesTabScreenNavigationDestination.route
    ){
        composable(route= NotesTabScreenNavigationDestination.route) {
            NotesTabScreen(
                viewModel=courseScreenViewModel
            )
        }
        composable(route = CourseVideosResourcesTabNavigationDestination.route) {
            CourseVideosResourcesTab()
        }
        composable(route = CourseBooksResourcesTabNavigationDestination.route) {
            CourseBooksResourcesTab()
        }
    }
}