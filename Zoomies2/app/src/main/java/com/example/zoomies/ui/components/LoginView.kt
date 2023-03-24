package com.example.zoomies.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoomies.ui.view_model.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginView(
    onScreenClose: () -> Unit,
    viewModel: LoginViewModel
) {
    BackHandler(onBack = onScreenClose)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sign In",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
                navigationIcon = {
                    IconButton(onClick = onScreenClose) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        })
    { paddingValues ->
        Card(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sign In",
                    color = MaterialTheme.colors.primary,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(16.dp)
                )
                val userName = remember { mutableStateOf("") }
                TextField(
                    label = { Text(text = "Name") },
                    value = userName.value,
                    onValueChange = {
                        userName.value = it
                    }
                )

                val password = remember { mutableStateOf("") }
                val passwordVisible = remember { mutableStateOf(false) }
                TextField(
                    label = { Text(text = "Password") },
                    value = password.value,
                    onValueChange = {
                        password.value = it
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(imageVector = Icons.Filled.Lock, contentDescription = null)
                        }
                    },
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                val showDialog = remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.clickable {
                            onScreenClose()
                        }
                    )
                    val coroutineScope = rememberCoroutineScope()
                    Text(
                        text = "Submit",
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.clickable {
                            viewModel.loginUser(userName.value, password.value)
                            coroutineScope.launch {
                                viewModel.loggedInStatus.collect { loggedInSuccessful ->
                                    if (loggedInSuccessful) {
                                        onScreenClose()
                                    } else {
                                        showDialog.value = true
                                    }
                                }
                            }
                        }
                    )
                }

                if (showDialog.value) {
                    AlertDialog(
                        title = {
                            Text(
                                text = "Invalid input!",
                                modifier = Modifier
                                    .padding(16.dp),
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        },
                        onDismissRequest = { showDialog.value = false },
                        buttons = {
                            Button(
                                onClick = { showDialog.value = false },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(16.dp)
                            ) {
                                Text(text = "OK")
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.width(500.dp)
                    )
                }
            }
        }
    }
}