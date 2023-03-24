package com.example.zoomies.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.zoomies.ui.view_model.UsersViewModel

@Composable
fun UserDetailsView(
    onScreenClose: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: UsersViewModel
) {
    val user = UsersViewModel.selectedUser!!

    BackHandler(onBack = onScreenClose)
    Scaffold(
        topBar = {
            TopBar(
                pageTitle = user.userName,
                navigateToLogin = navigateToLogin,
                imageVector = Icons.Filled.ArrowBack,
                onScreenClose = onScreenClose
            )
        }
    )
    { paddingValues ->
        Card(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                item {
                    val userName = remember { mutableStateOf(user.userName) }
                    TextField(
                        label = { Text(text = "Username") },
                        value = userName.value,
                        onValueChange = {
                            userName.value = it
                        }
                    )

                    val password = remember { mutableStateOf(user.password) }
                    val passwordVisible = remember {mutableStateOf(false)}
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

                    val email = remember { mutableStateOf(user.email) }
                    TextField(
                        label = { Text(text = "Email") },
                        value = email.value,
                        onValueChange = {
                            email.value = it
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    val phone = remember { mutableStateOf(user.phoneNumber) }
                    TextField(
                        label = { Text(text = "Phone number") },
                        value = phone.value,
                        onValueChange = {
                            phone.value = it
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colors.onSecondary,
                            modifier = Modifier.clickable {
                                viewModel.delete(user)
                                onScreenClose()
                            }
                        )
                        Text(
                            text = "Update",
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                viewModel.insert(
                                    id = user.uid,
                                    username = userName.value,
                                    password = password.value,
                                    email = email.value,
                                    phoneNumber = phone.value
                                )
                                onScreenClose()
                            }
                        )
                    }
                }
            }
        }
    }
}