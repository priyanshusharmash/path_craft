package com.metaminds.pathcraft.ui.navigation

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.screens.ChatScreen
import com.metaminds.pathcraft.ui.screens.ChatScreenNavigationDestination
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseScreen
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseScreenNavigationDestination
import com.metaminds.pathcraft.ui.screens.HomeScreen
import com.metaminds.pathcraft.ui.screens.HomeScreenNavigationDestination
import com.metaminds.pathcraft.ui.screens.LoginScreen
import com.metaminds.pathcraft.ui.screens.LoginScreenNavigationDestination
import com.metaminds.pathcraft.ui.screens.SectionScreen
import com.metaminds.pathcraft.ui.screens.SectionScreenNavigationDestination
import com.metaminds.pathcraft.ui.screens.SignUpScreen
import com.metaminds.pathcraft.ui.screens.SignUpScreenNavigationDestination
import com.metaminds.pathcraft.ui.screens.NotesContentScreen
import com.metaminds.pathcraft.ui.screens.NotesContentScreenNavigationDestination
import com.metaminds.pathcraft.ui.viewModels.HomeScreenViewModel

@Composable
fun AppNavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController= rememberNavController(),
    viewModel: HomeScreenViewModel = viewModel(factory= AppViewModelProvider.factory),
    auth: FirebaseAuth = viewModel.auth
) {
    NavHost(
        modifier=modifier,
        navController = navController,
        startDestination = if(auth.currentUser!=null) HomeScreenNavigationDestination.route else LoginScreenNavigationDestination.route
    ){
        composable(route = LoginScreenNavigationDestination.route){
            LoginScreen(
                navigateToHome = {
                    navController.navigate(HomeScreenNavigationDestination.route){
                        popUpTo(0){inclusive = true}
                    }
                                 },
                navigateToSignUp = {navController.navigate(SignUpScreenNavigationDestination.route)}
            )
        }
        composable(route= SignUpScreenNavigationDestination.route) {
            SignUpScreen(
                navigateToHome = {
                    navController.navigate(HomeScreenNavigationDestination.route){
                        popUpTo(0){inclusive = true}
                    } },
                navigateToLogIn = { navController.popBackStack()}
            )
        }
        composable(route= ChatScreenNavigationDestination.route) {
            ChatScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = HomeScreenNavigationDestination.route) {
            HomeScreen(
                navigateToSectionScreen = {titleRes,courseList->
                    navController.navigate("${SectionScreenNavigationDestination.route}/$titleRes/$courseList") },
                navigateToChatScreen = {courseName->

                    if(courseName.isNullOrEmpty())
                        navController.navigate(ChatScreenNavigationDestination.route)
                    else
                        navController.navigate("${ChatScreenNavigationDestination.route}/$courseName")
                },
                navigateToLoginScreen = {
                    navController.navigate(LoginScreenNavigationDestination.route){
                        popUpTo(0){inclusive = true}
                    }
                },
                navigateToCourseScreen = {
                    val encodedTitle= Uri.encode(it)
                    navController.navigate("${CourseScreenNavigationDestination.route}/$encodedTitle")

                }
            )
        }
        composable(route = SectionScreenNavigationDestination.routeWithArgs,
            arguments = listOf(
                navArgument("${SectionScreenNavigationDestination.titleRes}") { type= NavType.IntType },
                navArgument (SectionScreenNavigationDestination.course_list){type= NavType.StringType}
                )) {
            SectionScreen(
                onBackPressed = {navController.popBackStack()},
                navigateToChatScreen = {navController.navigate("${ChatScreenNavigationDestination.route}/$it")}
            )
        }
        composable (route= ChatScreenNavigationDestination.route_with_args,
            arguments = listOf(
                navArgument(ChatScreenNavigationDestination.COURSE) { type= NavType.StringType }
            )){
            ChatScreen(
                onBackPressed = {navController.popBackStack()}
            )
        }

        composable(route = CourseScreenNavigationDestination.routeWithArgs,
            arguments = listOf(
                navArgument (CourseScreenNavigationDestination.COURSE){type= NavType.StringType }
            ),
            enterTransition = {fadeIn(animationSpec = tween(300))},
            exitTransition = {fadeOut(animationSpec = tween(300))}
            ){
            CourseScreen(
                onBackPressed = {navController.popBackStack()},
                navigateToNotesContentScreen = {course,topic,subtopic->
                    navController.navigate("${NotesContentScreenNavigationDestination.route}/${Uri.encode(course)}/${Uri.encode(topic)}/${Uri.encode(subtopic)}")
                }
            )
        }

        composable (route = NotesContentScreenNavigationDestination.routeWithArgs,
            arguments = listOf(
                navArgument(NotesContentScreenNavigationDestination.COURSE_NAME) {type = NavType.StringType},
                navArgument(NotesContentScreenNavigationDestination.TOPIC){type= NavType.StringType},
                navArgument (NotesContentScreenNavigationDestination.SUBTOPIC){ type= NavType.StringType }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = {it}, animationSpec = tween(300))},
            exitTransition = { slideOutHorizontally(targetOffsetX = {it}, animationSpec = tween(300))}
            ){
            NotesContentScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }

}