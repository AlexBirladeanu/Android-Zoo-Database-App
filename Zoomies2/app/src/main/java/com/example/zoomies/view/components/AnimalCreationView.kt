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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.zoomies.R
import com.example.zoomies.view_model.AnimalsViewModel

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
                pageTitle = stringResource(id = R.string.new_animal),
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
                        label = {Text(text = stringResource(id = R.string.name))},
                        value = name.value,
                        onValueChange = {
                            name.value = it
                        }
                    )

                    val species = remember { mutableStateOf("")}
                    TextField(
                        label = {Text(text = stringResource(id = R.string.species))},
                        value = species.value,
                        onValueChange = {
                            species.value = it
                        }
                    )

                    val habitat = remember { mutableStateOf("")}
                    TextField(
                        label = {Text(text = stringResource(id = R.string.habitat))},
                        value = habitat.value,
                        onValueChange = {
                            habitat.value = it
                        }
                    )

                    val diet = remember { mutableStateOf("")}
                    TextField(
                        label = {Text(text = stringResource(id = R.string.diet))},
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
                            text = stringResource(id = R.string.cancel),
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable {
                                onScreenClose()
                            }
                        )
                        Text(
                            text = stringResource(id = R.string.create),
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