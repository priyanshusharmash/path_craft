package com.metaminds.pathcraft.ui.screens

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metaminds.pathcraft.R
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.viewModels.LoginScreenUiState
import com.metaminds.pathcraft.ui.viewModels.LoginScreenViewModel


object LoginScreenNavigationDestination: NavigationDestination{
    override val title: String="login_screen"
    override val route: String = title
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginScreenViewModel= viewModel(),
    navigateToHome:()->Unit,
    navigateToSignUp:()-> Unit
    ) {

    val uiState=viewModel.loginUIState
    var focusManager=LocalFocusManager.current
    Box(
        modifier = modifier.fillMaxSize()
            .clickable(interactionSource = remember { MutableInteractionSource() },indication = null){focusManager.clearFocus()},
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

            LoginBox(
                modifier= Modifier.width(350.dp),
                enteredEmail = uiState.email,
                onEnteredEmailChange = { viewModel.updateLogInUiState(uiState.copy(email=it))},
                enteredPassword = uiState.password,
                onEnteredPasswordChange = {viewModel.updateLogInUiState(LoginScreenUiState(email = uiState.email, password = it))},
                onLogin = navigateToHome
            )
            Spacer(Modifier.height(10.dp))
            GoToResetPasswordPage(onClick = {})
            GoToSignUpPage(onClick = navigateToSignUp)
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
            modifier = Modifier.padding(20.dp)
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
                leadingIcon = Icons.Default.Person,
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
            LoginButton(onClick =onLogin)
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
fun LoginButton(modifier: Modifier = Modifier,onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = onClick
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

@Preview(showBackground = true)
@Composable
private fun LoginScreenPrev() {
    LoginScreen(
        navigateToHome = {},
        navigateToSignUp = {}
    )
}