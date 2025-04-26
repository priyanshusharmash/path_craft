package com.metaminds.pathcraft.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.metaminds.pathcraft.PathCraftApplication
import com.metaminds.pathcraft.ui.viewModels.ChatScreenViewModel
import com.metaminds.pathcraft.ui.viewModels.LoginScreenViewModel


object AppViewModelProvider {
    val factory = viewModelFactory {
        initializer {
            LoginScreenViewModel(repository = pathCraftApplication().container.repository)
        }
        initializer {
            ChatScreenViewModel(repository = pathCraftApplication().container.repository)
        }
    }
}

fun CreationExtras.pathCraftApplication(): PathCraftApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PathCraftApplication