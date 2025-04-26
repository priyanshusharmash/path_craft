package com.metaminds.pathcraft.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metaminds.pathcraft.R
import com.metaminds.pathcraft.SIGN_UP_SCREEN
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.theme.PathCraftTheme
import com.metaminds.pathcraft.ui.viewModels.AuthState
import com.metaminds.pathcraft.ui.viewModels.LoginScreenViewModel
import com.metaminds.pathcraft.ui.viewModels.SignUpScreenUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SignUpScreenNavigationDestination : NavigationDestination {
    override val title: String = SIGN_UP_SCREEN
    override val route: String = title
}

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginScreenViewModel = viewModel(factory = AppViewModelProvider.factory),
    navigateToHome: () -> Unit,
    navigateToLogIn: () -> Unit
) {
    val uiState = viewModel.signUpUiState
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() },
        contentAlignment = Alignment.Center
    ) {

        Background(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier.offset(y = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val userCreatedToastMessage = stringResource(R.string.user_created_toast_message)
            val passwordDoesNotMatchesMessage =
                stringResource(R.string.passwords_do_not_matches_toast_message)
            SignUpBox(
                modifier = Modifier.width(350.dp),
                onSubmit = {
                    if (viewModel.matchPasswords()) {
                        viewModel.updateAuthState(AuthState.Loading)
                        viewModel.signUpWithEmailPassword { isSuccess, errorMessage ->
                            if (isSuccess) {
                                viewModel.updateUserDetails()
                                Toast.makeText(
                                    context,
                                    userCreatedToastMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
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
                    } else {
                        Toast.makeText(context, passwordDoesNotMatchesMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                viewModel = viewModel,
                uiState = uiState
            )
            Spacer(Modifier.height(10.dp))
            GoToLoginPage(onClick = navigateToLogIn)
        }
        if (viewModel.authState == AuthState.Loading) ShowLoadingScreen()


    }
}

@Composable
fun GoToLoginPage(modifier: Modifier = Modifier, onClick: () -> Unit) {
    TextButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(text = stringResource(R.string.login_instead))
    }
}


@Composable
fun SignUpBox(
    modifier: Modifier = Modifier,
    onSubmit: () -> Unit,
    uiState: SignUpScreenUiState,
    viewModel: LoginScreenViewModel
) {
    Card(
        elevation = CardDefaults.cardElevation(50.dp),
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(color = MaterialTheme.colorScheme.primary, width = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.sign_up),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))
            InputField(
                leadingIcon = Icons.Default.Person,
                value = uiState.name,
                onValueChange = { viewModel.updateSignUpUiState(uiState.copy(name = it)) },
                label = stringResource(R.string.full_name_label),
                isPassword = false,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(10.dp))
            InputField(
                leadingIcon = Icons.Default.Email,
                value = uiState.email,
                onValueChange = { viewModel.updateSignUpUiState(uiState.copy(email = it.trim())) },
                label = stringResource(R.string.email_label),
                isPassword = false,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(10.dp))
            InputField(
                leadingIcon = Icons.Default.Lock,
                value = uiState.createPassword,
                onValueChange = { viewModel.updateSignUpUiState(uiState.copy(createPassword = it.trim())) },
                label = stringResource(R.string.create_password_label),
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            Spacer(Modifier.height(10.dp))
            InputField(
                leadingIcon = Icons.Default.Lock,
                value = uiState.confirmPassword,
                onValueChange = { viewModel.updateSignUpUiState(uiState.copy(confirmPassword = it.trim())) },
                label = stringResource(R.string.confirm_password_label),
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onSubmit
            ) {
                Text(text = stringResource(R.string.submit))
            }
        }
    }
}

@Composable
fun Background(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
    ) {
        BackgroundImage(
            imageRes = R.drawable.sign_up_page_background
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    PathCraftTheme {

        SignUpScreen(navigateToLogIn = {}, navigateToHome = {})
    }
}