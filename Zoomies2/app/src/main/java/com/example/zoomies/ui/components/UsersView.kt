package com.example.zoomies.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.zoomies.database.entity.User
import com.example.zoomies.ui.view_model.UsersViewModel

@Composable
fun UsersView(
    navigateToAnimals: () -> Unit,
    navigateToUserCreation: () -> Unit,
    navigateToUserDetails: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: UsersViewModel
) {
    Scaffold(
        topBar = {
            TopBar(
                pageTitle = "Users",
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
                    Row(
                        modifier = Modifier
                            .clickable { navigateToUserCreation() }
                            .padding(bottom = 16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.AddCircle, contentDescription = null)
                        Text(
                            text = "New User",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    viewModel.userList.value.forEach{
                        UserCard(it, navigateToUserDetails)
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
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
                    Text(text = "Username:   ", color = MaterialTheme.colors.primary)
                    Text(text = "Email:   ", color = MaterialTheme.colors.primary)
                    Text(text = "Phone Number:   ", color = MaterialTheme.colors.primary)
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