package com.example.zoomies.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.zoomies.ui.view_model.AnimalsViewModel

@Composable
fun AnimalCreationView(
    onScreenClose: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: AnimalsViewModel
) {
    BackHandler(onBack = onScreenClose)
    Scaffold(
        topBar = {
            TopBar(
                pageTitle = "Create Animal",
                navigateToLogin = navigateToLogin,
                imageVector = Icons.Filled.ArrowBack,
                onScreenClose = onScreenClose
            )
        })
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
                    val name = remember { mutableStateOf("")}
                    TextField(
                        label = {Text(text = "Name")},
                        value = name.value,
                        onValueChange = {
                            name.value = it
                        }
                    )

                    val species = remember { mutableStateOf("")}
                    TextField(
                        label = {Text(text = "Species")},
                        value = species.value,
                        onValueChange = {
                            species.value = it
                        }
                    )

                    val habitat = remember { mutableStateOf("")}
                    TextField(
                        label = {Text(text = "Habitat")},
                        value = habitat.value,
                        onValueChange = {
                            habitat.value = it
                        }
                    )

                    val diet = remember { mutableStateOf("")}
                    TextField(
                        label = {Text(text = "diet")},
                        value = diet.value,
                        onValueChange = {
                            diet.value = it
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                        Text(
                            text = "Create",
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                viewModel.insert(
                                    name = name.value,
                                    species = species.value,
                                    habitat = habitat.value,
                                    diet = diet.value
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