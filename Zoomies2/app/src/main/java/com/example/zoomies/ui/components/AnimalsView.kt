package com.example.zoomies.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoomies.database.entity.Animal
import com.example.zoomies.database.entity.UserRole
import com.example.zoomies.ui.view_model.AnimalsViewModel
import com.example.zoomies.ui.view_model.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun AnimalsView(
    navigateToAnimalCreation: () -> Unit,
    navigateToAnimalDetails: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToUsers: () -> Unit,
    navigateToFilters: () -> Unit,
    viewModel: AnimalsViewModel
) {
    Scaffold(
        topBar = {
            TopBar(
                pageTitle = "Animals",
                navigateToLogin = navigateToLogin,
                imageVector = Icons.Filled.Home
            )
        },
        bottomBar = {
            if (LoginViewModel.activeUser?.role == UserRole.ADMIN) {
                BottomBar(
                    navigateToAnimals = {},
                    navigateToUsers = { navigateToUsers() }
                )
            }
        }
    )
    { paddingValues ->
        Card(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                item {
                    val items = remember { mutableStateOf(viewModel.animalList.value) }
                    val showSortingDropdown = remember { mutableStateOf(false) }
                    SearchBar(viewModel = viewModel, items = items)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                showSortingDropdown.value = true
                            }
                        ) {
                            SortingOptions(
                                showSortingDropdown = showSortingDropdown.value,
                                onHideSortingDropdown = {
                                    showSortingDropdown.value = false
                                },
                                viewModel = viewModel,
                                items = items
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
//                                viewModel.getFilterOptions()
//                                coroutineScope.launch {
//                                    viewModel.filterOptions.collect {
//                                        filterOptions.value = it
//                                        showFilters.value = true
//                                    }
//                                }
                                navigateToFilters()
                            }
                        ) {
                            Text(text = "Filters", modifier = Modifier.padding(end = 4.dp))
                            Icon(
                                imageVector = Icons.Filled.List,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary,
                            )
                        }
                    }

                    if (LoginViewModel.activeUser?.role == UserRole.EMPLOYEE) {
                        AnimalCreationOption(
                            navigateToAnimalCreation = navigateToAnimalCreation
                        )
                    }
//                    if (showFilters.value) {
//                        SearchFilters(
//                            filterOptions = filterOptions.value,
//                            hideFilters = { showFilters.value = false })
//                    }
                    items.value.forEach {
                        AnimalCard(it, navigateToAnimalDetails)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimalCreationOption(
    navigateToAnimalCreation: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { navigateToAnimalCreation() }
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Filled.AddCircle, contentDescription = null)
        Text(
            text = "New Animal",
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun AnimalCard(
    animal: Animal,
    navigateToAnimalDetails: () -> Unit,
) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navigateToAnimalDetails()
                AnimalsViewModel.selectedAnimal = animal
            }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Name:   ", color = MaterialTheme.colors.primary)
                    Text(text = "Species:   ", color = MaterialTheme.colors.primary)
                    Text(text = "Habitat:   ", color = MaterialTheme.colors.primary)
                    Text(text = "Diet:   ", color = MaterialTheme.colors.primary)
                }
                Column {
                    Text(text = animal.name)
                    Text(text = animal.species)
                    Text(text = animal.habitat)
                    Text(text = animal.diet)
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
private fun SearchBar(
    viewModel: AnimalsViewModel,
    items: MutableState<List<Animal>>
) {
    val focusManager = LocalFocusManager.current
    val searchText = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        value = searchText.value,
        onValueChange = {
            searchText.value = it
            viewModel.performSearch(it)
            coroutineScope.launch {
                viewModel.searchResultsProcessed.collect { searchResults ->
                    items.value = searchResults
                }
            }
        },
        placeholder = {
            Text(text = "Find by name")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = searchText.value.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = {
                    searchText.value = ""
                    viewModel.resetList()
                    coroutineScope.launch {
                        viewModel.searchResultsProcessed.collect { searchResults ->
                            items.value = searchResults
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null
                    )
                }

            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
    )
}

@Composable
fun SortingOptions(
    showSortingDropdown: Boolean,
    onHideSortingDropdown: () -> Unit,
    viewModel: AnimalsViewModel,
    items: MutableState<List<Animal>>
) {

    Text(text = "Sort by:", modifier = Modifier.padding(end = 4.dp))

    Box {
        val sortingOptions = listOf("Name", "Species", "Diet")
        val selectedIndex = remember { mutableStateOf(0) }
        Text(
            text = sortingOptions[selectedIndex.value],
            color = MaterialTheme.colors.primary
        )
        DropdownMenu(
            expanded = showSortingDropdown,
            onDismissRequest = onHideSortingDropdown,
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .padding(8.dp)
        ) {
            sortingOptions.forEachIndexed { index, s ->
                Text(
                    text = s,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onHideSortingDropdown()
                            selectedIndex.value = index
                            when (index) {
                                0 -> {
                                    viewModel.sortByName()
                                }
                                1 -> {
                                    viewModel.sortBySpecies()
                                }
                                2 -> {
                                    viewModel.sortByDiet()
                                }
                            }
                            items.value = viewModel.animalList.value
                        }
                )
            }
        }
    }
}