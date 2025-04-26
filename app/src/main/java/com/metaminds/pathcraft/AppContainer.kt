package com.metaminds.pathcraft

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.metaminds.pathcraft.data.AppRepository

interface AppContainer{
    val repository: AppRepository
    val auth : FirebaseAuth
}

class DefaultApplication(context: Context): AppContainer{
    override val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override val repository: AppRepository = AppRepository(auth= auth,context=context)
}