package com.example.zoomies.view.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.zoomies.R

@Composable
fun BottomBar(
    navigateToAnimals: () -> Unit,
    navigateToUsers: () -> Unit
) {
    val selectedIndex = remember { mutableStateOf(0) }
    BottomNavigation(
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.paw),
                    contentDescription = null
                )
            },
            label = { Text(stringResource(id = R.string.animals)) },
            selected = (selectedIndex.value == 0),
            onClick = {
                selectedIndex.value = 0
                navigateToAnimals()
            }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null
                )
            },
            label = { Text(stringResource(id = R.string.users)) },
            selected = (selectedIndex.value == 1),
            onClick = {
                selectedIndex.value = 1
                navigateToUsers()
            }
        )
    }
}