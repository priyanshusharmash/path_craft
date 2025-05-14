package com.metaminds.pathcraft.ui.screens

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metaminds.pathcraft.R
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.viewModels.AuthState
import com.metaminds.pathcraft.ui.viewModels.LoginScreenUiState
import com.metaminds.pathcraft.ui.viewModels.LoginScreenViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.metaminds.pathcraft.LOGIN_SCREEN_TITLE
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


object LoginScreenNavigationDestination: NavigationDestination{
    override val titleRes: Int = R.string.login
    override val route: String = LOGIN_SCREEN_TITLE
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginScreenViewModel= viewModel(factory = AppViewModelProvider.factory),
    navigateToHome:()->Unit,
    navigateToSignUp:()-> Unit
    ) {

    val uiState=viewModel.loginUIState
    var focusManager=LocalFocusManager.current
    val context = LocalContext.current
    val coroutineScope= rememberCoroutineScope()
    var forgotPasswordDialogVisible by remember { mutableStateOf(false) }

    if(forgotPasswordDialogVisible){
        val toastMsg=stringResource(R.string.reset_link_send_toast_message)
        ForgotPasswordDialog(
            onDismiss = { forgotPasswordDialogVisible = false },
            value = viewModel.passwordResetEmail,
            onValueChange = {viewModel.updatePasswordResetEmail(it)},
            onClick = {
                viewModel.resetPassword { isSuccess,message->
                    if(isSuccess){
                        Toast.makeText(context,toastMsg,Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() },
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                BackgroundImage(imageRes = R.drawable.login_background)
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val successToastMessage = stringResource(R.string.success_log_in)
                LoginBox(
                    modifier = Modifier.width(350.dp),
                    enteredEmail = uiState.email,
                    onEnteredEmailChange = { viewModel.updateLogInUiState(uiState.copy(email = it)) },
                    enteredPassword = uiState.password,
                    onEnteredPasswordChange = {
                        viewModel.updateLogInUiState(
                            LoginScreenUiState(
                                email = uiState.email,
                                password = it
                            )
                        )
                    },
                    onLogin = {
                        viewModel.updateAuthState(AuthState.Loading)
                        viewModel.signInWithEmailPassword { isSuccess, errorMessage ->
                            if (isSuccess) {
                                Toast.makeText(context, successToastMessage, Toast.LENGTH_SHORT)
                                    .show()
                                coroutineScope.launch {
                                    delay(500)
                                    navigateToHome()
                                }
                                coroutineScope.launch {
                                    delay(1000)
                                    viewModel.updateAuthState(AuthState.Success)
                                }
                            } else {
                                viewModel.updateAuthState(AuthState.Error)
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
                Spacer(Modifier.height(10.dp))
                GoToResetPasswordPage(onClick = { forgotPasswordDialogVisible = true })
                GoToSignUpPage(onClick = navigateToSignUp)
            }

            if (viewModel.authState == AuthState.Loading) {
                ShowLoadingScreen()
            }
        }

}
@Composable
private fun LoginBox(
    modifier: Modifier = Modifier,
    enteredEmail: String,
    onEnteredEmailChange:(String)->Unit,
    enteredPassword:String,
    onEnteredPasswordChange:(String)->Unit,
    onLogin:()-> Unit,
) {
    Card(
        elevation = CardDefaults.cardElevation(50.dp),
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(color = MaterialTheme.colorScheme.primary, width = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(
                text = stringResource(R.string.login),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))
            InputField(
                modifier= Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Email,
                value = enteredEmail,
                onValueChange = onEnteredEmailChange,
                label = stringResource(R.string.email_label),
                isPassword = false,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(10.dp))
            InputField(
                modifier= Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Lock,
                value = enteredPassword,
                onValueChange = onEnteredPasswordChange,
                label = stringResource(R.string.password_label),
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )
            Spacer(Modifier.height(20.dp))
            LoginButton(onClick =onLogin, enabled = enteredEmail.isNotEmpty() && enteredPassword.isNotEmpty())
        }
    }
}



@Composable
fun GoToResetPasswordPage(modifier: Modifier = Modifier,onClick: () -> Unit) {
    TextButton(
        modifier=modifier,
        onClick = onClick
    ) {
        Text(text=stringResource(R.string.forgotten_password))
    }
}

@Composable
fun GoToSignUpPage(modifier: Modifier= Modifier,onClick:()-> Unit) {
    TextButton(
        onClick = onClick,
        modifier=modifier
    ) {
        Text(text = stringResource(R.string.go_to_sign_up_page))
    }
}



@Composable
fun LoginButton(modifier: Modifier = Modifier,onClick: () -> Unit,enabled:Boolean) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text = stringResource(R.string.login))
    }
}

@Composable
fun InputField(
    leadingIcon: ImageVector,
    value: String,
    onValueChange:(String)-> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean,
    keyboardOptions: KeyboardOptions
    ) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange =onValueChange,
        placeholder = { Text(text = label) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },
        trailingIcon = {
            if(isPassword){
                IconButton(onClick = {passwordVisible=!passwordVisible})
                {
                    Icon(
                        painter = painterResource(if (passwordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
                        contentDescription = null
                    )
                }
            }
        },
        visualTransformation = if(isPassword){if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation()} else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        shape = CircleShape,
        maxLines = 1,
        keyboardOptions=keyboardOptions,
        singleLine = true
    )
}

@Composable
fun BackgroundImage(
    modifier: Modifier = Modifier,
    @DrawableRes imageRes: Int) {
    Image(
        painter = painterResource(imageRes),
        contentDescription = null,
        modifier = modifier
            .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
fun ShowLoadingScreen(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition=composition,
        iterations = LottieConstants.IterateForever
    )
    Dialog(onDismissRequest = { }) {
        LottieAnimation(
            modifier=modifier,
            composition = composition,
            progress = progress
        )
    }

}

@Composable
private fun ForgotPasswordDialog(
    modifier: Modifier = Modifier,
    onDismiss:()-> Unit,
    value:String,
    onValueChange:(String)-> Unit,
    onClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        )
    ) {
        Card(
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
        ) {
            Box(
                modifier = modifier.padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column (
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = stringResource(R.string.forgotten_password), style = MaterialTheme.typography.titleLarge)
                    InputField(
                        leadingIcon = Icons.Default.Email,
                        value = value,
                        onValueChange = onValueChange,
                        label = stringResource(R.string.email_label),
                        isPassword =false,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        )
                    )
                    Button(
                        onClick = onClick
                    ) {
                        Text(text = stringResource(R.string.send_password_reset_link))
                    }
                }
            }
        }
    }
}
