package com.metaminds.pathcraft.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.metaminds.pathcraft.ui.screens.ChatScreen
import com.metaminds.pathcraft.ui.screens.ChatScreenNavigationDestination
import com.metaminds.pathcraft.ui.screens.LoginScreen
import com.metaminds.pathcraft.ui.screens.LoginScreenNavigationDestination
import com.metaminds.pathcraft.ui.screens.SignUpScreen
import com.metaminds.pathcraft.ui.screens.SignUpScreenNavigationDestination

@Composable
fun AppNavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController= rememberNavController(),
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    NavHost(
        modifier=modifier,
        navController = navController,
        startDestination = if(auth.currentUser!=null) ChatScreenNavigationDestination.route else LoginScreenNavigationDestination.route
    ){
        composable(route = LoginScreenNavigationDestination.route){
            LoginScreen(
                navigateToHome = {
                    navController.navigate(ChatScreenNavigationDestination.route){
                        popUpTo(0){inclusive = true}
                    }
                                 },
                navigateToSignUp = {navController.navigate(SignUpScreenNavigationDestination.route)}
            )
        }
        composable(route= SignUpScreenNavigationDestination.route) {
            SignUpScreen(
                navigateToHome = {
                    navController.navigate(ChatScreenNavigationDestination.route){
                        popUpTo(0){inclusive = true}
                    } },
                navigateToLogIn = { navController.popBackStack()}
            )
        }
        composable(route= ChatScreenNavigationDestination.route) {
            ChatScreen(
                navigateToLogInScreen = {
                    navController.navigate(LoginScreenNavigationDestination.route){
                        popUpTo(0){inclusive = true}
                    }

                }
            )
        }
    }

}