package com.metaminds.pathcraft.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.metaminds.pathcraft.ui.navigation.NavigationDestination

object HomeScreenNavigationDestination: NavigationDestination{
    override val title: String="home_screen"
    override val route: String = title
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier=modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(text="Home Screen")
    }
}