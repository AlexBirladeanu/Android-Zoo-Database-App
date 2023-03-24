package com.example.zoomies.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.example.zoomies.ui.view_model.LoginViewModel

@Composable
fun TopBar(
    pageTitle: String,
    navigateToLogin: () -> Unit,
    imageVector: ImageVector,
    onScreenClose: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = pageTitle, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
        navigationIcon = {
            IconButton(onClick = onScreenClose) {
                Icon(imageVector = imageVector, contentDescription = null)
            }
        },
        actions = {
            if(LoginViewModel.activeUser == null) {
                Text(
                    text = "Log In",
                    modifier = Modifier.clickable { navigateToLogin() }
                )
                IconButton(onClick = {
                    navigateToLogin()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null
                    )
                }
            } else {
                Text(
                    text = "Hello " +LoginViewModel.activeUser!!.userName + "!"
                )
                IconButton(onClick = {
                    navigateToLogin()
                    LoginViewModel.activeUser = null
                }) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = null
                    )
                }
            }
        }
    )
}
