package com.metaminds.pathcraft.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginScreenViewModel: ViewModel() {
    var loginUIState by mutableStateOf(LoginScreenUiState())
        private set
    var signUpUiState by mutableStateOf(SignUpScreenUiState())
        private set
    var authState: AuthState = AuthState.NotStarted
        private set

    fun updateLogInUiState(uiState: LoginScreenUiState){
        loginUIState=uiState
        Log.d("msg",uiState.email)
    }
    fun updateSignUpUiState(uiState: SignUpScreenUiState){
        signUpUiState=uiState
    }
    fun updateAuthState(state:AuthState){
        authState = state
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