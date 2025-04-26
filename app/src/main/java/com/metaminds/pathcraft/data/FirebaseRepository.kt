package com.metaminds.pathcraft.data

import com.google.firebase.auth.FirebaseAuth


interface FirebaseRepository {
    fun getAuth(): FirebaseAuth
    fun signUpWithEmailPassword(email:String,password: String,callback:(Boolean,String?)-> Unit)
    fun logInWithEmailPassword(email:String,password:String,callback: (Boolean, String?) -> Unit)
}