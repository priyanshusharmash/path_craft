package com.metaminds.pathcraft.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metaminds.pathcraft.R
import com.metaminds.pathcraft.SECTION_SCREEN
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.viewModels.SectionScreenViewModel

object SectionScreenNavigationDestination: NavigationDestination{
    override val titleRes: Int = R.string.section_screen
    override val route: String = SECTION_SCREEN
    const val course_list:String = com.metaminds.pathcraft.COURSE_LIST
    val routeWithArgs ="$route/{$titleRes}/{$course_list}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionScreen(
    modifier: Modifier = Modifier,
    viewModel: SectionScreenViewModel= viewModel(factory= AppViewModelProvider.factory)
) {
    val scrollBehavior=TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold (
        modifier=modifier.fillMaxSize(),
        topBar = {
            DefaultAppBar(scrollBehavior=scrollBehavior, title = stringResource(viewModel.title))
        }
    ){ contentPadding->
        SectionScreenBody(
            modifier=Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = contentPadding,
            viewModel = viewModel
        )
    }
}

@Composable
fun SectionScreenBody(
    contentPadding:PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: SectionScreenViewModel
) {
    LazyVerticalGrid(
        modifier=modifier,
        contentPadding = PaddingValues(vertical = contentPadding.calculateTopPadding(), horizontal = 20.dp),
        columns = GridCells.Adaptive(150.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        itemsIndexed(viewModel.decodedCourseList) {index,course->
            SkillCard(
                courseName =course,
                shape = CircleShape
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(
    title:String="Title",
    scrollBehavior: TopAppBarScrollBehavior
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title={Text(text=title)},
        navigationIcon = {
            IconButton(
                onClick = {}
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SectionScreenPreview() {
    SectionScreen()
}