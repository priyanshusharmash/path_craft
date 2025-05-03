package com.metaminds.pathcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.metaminds.pathcraft.ui.navigation.AppNavigationGraph
import com.metaminds.pathcraft.ui.theme.PathCraftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PathCraftTheme {
                Surface {

                AppNavigationGraph(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
