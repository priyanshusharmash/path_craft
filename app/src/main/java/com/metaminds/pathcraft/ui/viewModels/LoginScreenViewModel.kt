package com.metaminds.pathcraft.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.UserProfileChangeRequest
import com.metaminds.pathcraft.data.FirebaseRepository

class LoginScreenViewModel(private val repository: FirebaseRepository): ViewModel() {
    var loginUIState by mutableStateOf(LoginScreenUiState())
        private set
    var signUpUiState by mutableStateOf(SignUpScreenUiState())
        private set
    var authState by  mutableStateOf<AuthState>(AuthState.NotStarted)
        private set
    var passwordResetEmail by mutableStateOf(loginUIState.email)
        private set

    fun updatePasswordResetEmail(email:String){
        passwordResetEmail=email
    }
    fun updateUserDetails(){
        repository.getAuth().currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(signUpUiState.name)
                .build()
        )
    }


    fun updateLogInUiState(uiState: LoginScreenUiState){
        loginUIState=uiState
    }
    fun updateSignUpUiState(uiState: SignUpScreenUiState){
        signUpUiState=uiState
    }
    fun updateAuthState(state:AuthState){
        authState = state
    }
    fun signUpWithEmailPassword(signUpStatus:(Boolean,String?)-> Unit){
        repository.signUpWithEmailPassword(signUpUiState.email,signUpUiState.confirmPassword){isSuccess,errorMessage->

            return@signUpWithEmailPassword signUpStatus(isSuccess,errorMessage)
        }
    }
    fun signInWithEmailPassword(signInStatus:(Boolean,String?)->Unit){
        repository.logInWithEmailPassword(loginUIState.email,loginUIState.password) { isSuccess,errorMessage->
            return@logInWithEmailPassword signInStatus(isSuccess,errorMessage)
        }
    }
    fun matchPasswords():Boolean{
        return signUpUiState.createPassword == signUpUiState.confirmPassword
    }

    fun resetPassword(callback:(Boolean,String?)-> Unit){
        repository.getAuth().sendPasswordResetEmail(passwordResetEmail)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    return@addOnCompleteListener callback(true,null)
                }else{
                    return@addOnCompleteListener callback(false,task.exception?.message)
                }
            }
    }

}
sealed class AuthState{
    object Success: AuthState()
    object Loading : AuthState()
    object Error:AuthState()
    object NotStarted:AuthState()
}
data class LoginScreenUiState(
    val email: String="",
    val password: String=""
)

data class SignUpScreenUiState(
    val name:String="",
    val email:String="",
    val createPassword:String="",
    val confirmPassword:String=""
)