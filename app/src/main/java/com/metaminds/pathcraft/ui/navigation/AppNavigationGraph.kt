package com.metaminds.pathcraft.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.metaminds.pathcraft.ui.screens.HomeScreen
import com.metaminds.pathcraft.ui.screens.HomeScreenNavigationDestination
import com.metaminds.pathcraft.ui.screens.LoginScreen
import com.metaminds.pathcraft.ui.screens.LoginScreenNavigationDestination
import com.metaminds.pathcraft.ui.screens.SignUpScreen
import com.metaminds.pathcraft.ui.screens.SignUpScreenNavigationDestination

@Composable
fun AppNavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController= rememberNavController()
) {
    NavHost(
        modifier=modifier,
        navController = navController,
        startDestination ="login_screen"
    ){
        composable(route = LoginScreenNavigationDestination.route){
            LoginScreen(
                navigateToHome = {navController.navigate(HomeScreenNavigationDestination.route)},
                navigateToSignUp = {navController.navigate(SignUpScreenNavigationDestination.route)}
            )
        }
        composable(route= SignUpScreenNavigationDestination.route) {
            SignUpScreen(
                navigateToHome = {navController.navigate(HomeScreenNavigationDestination.route)},
                navigateToLogIn = { navController.popBackStack()}
            )
        }
        composable(route= HomeScreenNavigationDestination.route) {
            HomeScreen()
        }
    }

}