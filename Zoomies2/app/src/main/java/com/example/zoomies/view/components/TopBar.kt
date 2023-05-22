package com.example.zoomies.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.zoomies.R
import com.example.zoomies.model.observer.LanguageEventHandler
import com.example.zoomies.view_model.LoginViewModel

@Composable
fun TopBar(
    onLanguageClicked: (Int) -> Unit = {},
    isHomeScreen: Boolean = false,
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
            if (isHomeScreen) {
                var showDropdown by remember { mutableStateOf(false) }
                LanguageDropdown(
                    modifier = Modifier.clickable { showDropdown = true }.padding(start = 8.dp),
                    showDropdown = showDropdown,
                    onHideDropdown = { showDropdown = false },
                    onClick = {
                        onLanguageClicked(it)
                    }
                )
            } else {
                IconButton(onClick = {
                    onScreenClose()
                }) {
                    Icon(imageVector = imageVector, contentDescription = null)
                }
            }
        },
        actions = {
            if (LoginViewModel.activeUser == null) {
                Text(
                    text = stringResource(id = R.string.login),
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
                    text = stringResource(id = R.string.hello) + " " + LoginViewModel.activeUser!!.userName + "!"
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

@Composable
private fun LanguageDropdown(
    modifier: Modifier,
    showDropdown: Boolean,
    onHideDropdown: () -> Unit,
    onClick: (Int) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        val languageOptions = listOf(
            "EN",
            "RO",
            "FR"
        )
        val selectedIndex = remember {
            mutableStateOf(
                when (LanguageEventHandler.activeLanguagePrefix) {
                    "en" -> 0
                    "ro" -> 1
                    else -> 2
                }
            )
        }
        Text(
            text = languageOptions[selectedIndex.value]
        )
        DropdownMenu(
            expanded = showDropdown,
            onDismissRequest = onHideDropdown,
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .padding(8.dp)
        ) {
            languageOptions.forEachIndexed { index, s ->
                Text(text = s,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onHideDropdown()
                            selectedIndex.value = index
                            onClick(selectedIndex.value)
                        })
            }
        }
    }
}
