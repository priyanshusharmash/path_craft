package com.metaminds.pathcraft.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseResourcesTab
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseBooksResourcesTabNavigationDestination
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseVideosResourcesTab
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseVideosResourcesTabNavigationDestination
import com.metaminds.pathcraft.ui.screens.courseScreens.NotesTabScreen
import com.metaminds.pathcraft.ui.screens.courseScreens.NotesTabScreenNavigationDestination
import com.metaminds.pathcraft.ui.viewModels.CourseScreenViewModel

@Composable
fun CourseScreenNavigationGraph(
    modifier: Modifier = Modifier,
    courseScreenViewModel: CourseScreenViewModel,
    navController: NavHostController=rememberNavController(),
    previousDestination: String,
    navigateToNotesContentScreen:(String,String)-> Unit
) {
    NavHost(
        modifier=modifier,
        navController=navController,
        startDestination= NotesTabScreenNavigationDestination.route
    ){
        val transitionTime=100
        composable(route= NotesTabScreenNavigationDestination.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = {-it}
                   , animationSpec = tween(transitionTime))},
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = {
                        -it
                    },
                    animationSpec = tween(transitionTime)
                )
            }
        ) {
            NotesTabScreen(
                viewModel=courseScreenViewModel,
                navigateToNotesContentScreen = {topic,subtopic->
                    navigateToNotesContentScreen(topic,subtopic)
                }
            )
        }


        composable(route = CourseVideosResourcesTabNavigationDestination.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = {it->
                        when(previousDestination){
                            NotesTabScreenNavigationDestination.route ->{it}
                            CourseBooksResourcesTabNavigationDestination.route ->{-it}
                            else -> 0
                        }
                    }
                    , animationSpec = tween(transitionTime))},
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = {
                        if(navController.currentDestination?.route== CourseBooksResourcesTabNavigationDestination.route){
                            -it
                        }else if(navController.currentDestination?.route== NotesTabScreenNavigationDestination.route){
                            it
                        }
                        else if (previousDestination == NotesTabScreenNavigationDestination.route){
                            it
                        } else{
                            0
                        }
                    },
                    animationSpec = tween(transitionTime)
                )
            }
            ) {
            CourseVideosResourcesTab(viewModel=courseScreenViewModel)
        }


        composable(route = CourseBooksResourcesTabNavigationDestination.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = {it}
                    , animationSpec = tween(transitionTime))},
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = {
                        it
                    },
                    animationSpec = tween(transitionTime)
                )
            }
        ) {
            CourseResourcesTab(viewModel=courseScreenViewModel)
        }
    }
}