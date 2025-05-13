package com.metaminds.pathcraft.ui.screens.courseScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.metaminds.pathcraft.ui.navigation.NavigationDestination

object CourseBooksResourcesTabNavigationDestination: NavigationDestination{
    override val titleRes: Int? = null
    override val route: String = "course_books"
}

@Composable
fun CourseBooksResourcesTab(modifier: Modifier = Modifier) {
    Box(
        modifier=modifier.fillMaxSize()
    ){
        Text(text="Books Tab")
    }
}