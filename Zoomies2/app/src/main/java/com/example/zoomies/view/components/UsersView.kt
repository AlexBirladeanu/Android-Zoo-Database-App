package com.example.zoomies.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.zoomies.MainActivity
import com.example.zoomies.R
import com.example.zoomies.model.dto.UserDTO
import com.example.zoomies.view_model.UsersViewModel

@Composable
fun UsersView(
    navigateToAnimals: () -> Unit,
    navigateToUserCreation: () -> Unit,
    navigateToUserDetails: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: UsersViewModel
) {
    val activity = LocalContext.current as MainActivity
    val users by viewModel.userList.collectAsState()
    Scaffold(
        topBar = {
            TopBar(
                isHomeScreen = true,
                onLanguageClicked = { languageIndex ->
                    viewModel.languageEventHandler.setLocale(activity, languageIndex)
                },
                pageTitle = stringResource(id = R.string.users),
                navigateToLogin = navigateToLogin,
                imageVector = Icons.Filled.Home
            )
        },
        bottomBar = {
            BottomBar(
                navigateToAnimals = { navigateToAnimals() },
                navigateToUsers = {}
            )
        }
    )
    { paddingValues ->
        Card(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth()
        )
        {
            LazyColumn(
                modifier =Modifier.padding(16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(id = R.string.notify),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    val isEmailNotification by viewModel.isEmailNotification.collectAsState()
                    val notificationModes = listOf("Email", "SMS")
                    ToggleButtons(
                        items = notificationModes,
                        currentSelection = if (isEmailNotification) "Email" else "SMS",
                        onChange = { index ->
                            when (notificationModes[index]) {
                                "Email" -> {
                                    viewModel.setIsEmailNotification(true)
                                }
                                "SMS" -> {
                                    viewModel.setIsEmailNotification(false)
                                }
                            }
                        }
                    )
                }
                item {
                    Row(
                        modifier = Modifier
                            .clickable { navigateToUserCreation() }
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.AddCircle, contentDescription = null)
                        Text(
                            text = stringResource(id = R.string.new_user),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    users.forEach{
                        UserCard(it, navigateToUserDetails)
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: UserDTO,
    navigateToUserDetails: () -> Unit
) {

    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navigateToUserDetails()
                UsersViewModel.selectedUser = user
            }
    )
    {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "ID:   ", color = MaterialTheme.colors.primary)
                    Text(text = stringResource(id = R.string.username) + ":   ", color = MaterialTheme.colors.primary)
                    Text(text = stringResource(id = R.string.email) + ":   ", color = MaterialTheme.colors.primary)
                    Text(text = stringResource(id = R.string.phone_number) + ":   ", color = MaterialTheme.colors.primary)
                }
                Column {
                    Text(text = user.uid.toString())
                    Text(text = user.userName)
                    Text(text = user.email)
                    Text(text = user.phoneNumber)
                }
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.align(Alignment.End)
            )
        }

    }
}


@Composable
private fun ToggleButtons(
    items: List<String>,
    currentSelection: String,
    onChange: (Int) -> Unit
) {
    val dividerColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    var currentSelectionState by remember { mutableStateOf(currentSelection) }
    val selectedIndex = items.indexOf(currentSelectionState)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .border(BorderStroke(0.2.dp, dividerColor), shape = RoundedCornerShape(4.dp))
    ) {
        items.forEachIndexed { index, s ->
            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .height(IntrinsicSize.Max),
                onClick = {
                    currentSelectionState = items[index]
                    onChange(index)
                },
                shape = RectangleShape,
                colors = if (selectedIndex == index) {
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = dividerColor,
                        backgroundColor = MaterialTheme.colors.primary
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = dividerColor,
                        backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                    )
                },
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 8.dp,
                )
            ) {
                Text(
                    text = s,
                    modifier = Modifier.padding(vertical = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}