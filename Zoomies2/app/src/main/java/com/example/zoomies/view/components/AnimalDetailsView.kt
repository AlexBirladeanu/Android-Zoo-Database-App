package com.example.zoomies.view.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.zoomies.R
import com.example.zoomies.model.dto.UserRole
import com.example.zoomies.view_model.AnimalsViewModel
import com.example.zoomies.view_model.LoginViewModel

@Composable
fun AnimalDetailsView(
    onScreenClose: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: AnimalsViewModel
) {
    val animal = AnimalsViewModel.selectedAnimal!!
    val context = LocalContext.current
    BackHandler(onBack = onScreenClose)
    Scaffold(
        topBar = {
            TopBar(
                pageTitle = animal.name,
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
                val isEmployee = LoginViewModel.activeUser?.role == UserRole.EMPLOYEE

                item {
                    val name = remember { mutableStateOf(animal.name) }
                    TextField(
                        label = {Text(text = stringResource(id = R.string.name))},
                        value = name.value,
                        onValueChange = {
                            name.value = it
                        },
                        enabled = isEmployee
                    )

                    val species = remember { mutableStateOf(animal.species) }
                    TextField(
                        label = {Text(text = stringResource(id = R.string.species))},
                        value = species.value,
                        onValueChange = {
                            species.value = it
                        },
                        enabled = isEmployee
                    )

                    val habitat = remember { mutableStateOf(animal.habitat) }
                    TextField(
                        label = {Text(text = stringResource(id = R.string.habitat))},
                        value = habitat.value,
                        onValueChange = {
                            habitat.value = it
                        },
                        enabled = isEmployee
                    )

                    val diet = remember { mutableStateOf(animal.diet) }
                    TextField(
                        label = {Text(text = stringResource(id = R.string.diet))},
                        value = diet.value,
                        onValueChange = {
                            diet.value = it
                        },
                        enabled = isEmployee
                    )

                    if (isEmployee) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(id = R.string.delete),
                                color = MaterialTheme.colors.onSecondary,
                                modifier = Modifier.clickable {
                                    viewModel.delete(animal, context)
                                    onScreenClose()
                                }
                            )
                            Text(
                                text = stringResource(id = R.string.update),
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier.clickable {
                                    viewModel.insert(
                                        animal.animalId,
                                        name.value,
                                        species.value,
                                        habitat.value,
                                        diet.value,
                                        context
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
}