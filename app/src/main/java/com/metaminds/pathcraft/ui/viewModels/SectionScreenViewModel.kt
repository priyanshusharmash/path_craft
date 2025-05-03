package com.metaminds.pathcraft.ui.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.metaminds.pathcraft.ui.screens.SectionScreenNavigationDestination

class SectionScreenViewModel(savedStateHandle: SavedStateHandle): ViewModel() {
    val title: Int = checkNotNull(savedStateHandle[SectionScreenNavigationDestination.titleRes.toString()])
    private val courseListJson: String = checkNotNull(savedStateHandle[SectionScreenNavigationDestination.course_list.toString()] as? String)
    val listType = object : TypeToken<List<String>>() {}.type
    val decodedCourseList: List<String> = Gson().fromJson(courseListJson, listType)


}