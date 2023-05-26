package com.example.zoomies.view.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.zoomies.MainActivity
import com.example.zoomies.R
import com.example.zoomies.view.components.charts.BarChart
import com.example.zoomies.view.components.charts.DonutChart
import com.example.zoomies.view_model.AnimalsViewModel
import com.example.zoomies.model.FileType
import com.example.zoomies.model.dto.AnimalDTO
import com.example.zoomies.model.dto.UserRole
import com.example.zoomies.view_model.LoginViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun AnimalsView(
    navigateToAnimalCreation: () -> Unit,
    navigateToAnimalDetails: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToUsers: () -> Unit,
    navigateToFilters: () -> Unit,
    viewModel: AnimalsViewModel
) {
    val animals by viewModel.animalList.collectAsState()
    val activity = LocalContext.current as MainActivity
    Scaffold(topBar = {
        TopBar(
            isHomeScreen = true,
            onLanguageClicked = { languageIndex ->
                viewModel.languageEventHandler.setLocale(activity, languageIndex)
            },
            pageTitle = stringResource(id = R.string.animals),
            navigateToLogin = navigateToLogin,
            imageVector = Icons.Filled.Home
        )
    }, bottomBar = {
        if (LoginViewModel.activeUser?.role == UserRole.ADMIN) {
            BottomBar(navigateToAnimals = {}, navigateToUsers = { navigateToUsers() })
        }
    }) { paddingValues ->
        Card(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            val showSortingDropdown = remember { mutableStateOf(false) }
            val showExportDialog = remember { mutableStateOf(false) }

            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                item {
                    var showChartShapeDropdown by remember { mutableStateOf(false) }
                    var showChartContentDropdown by remember { mutableStateOf(false) }
                    val showDonutChart by viewModel.showDonutChart.collectAsState()
                    val chartContentType by viewModel.chartContentType.collectAsState()

                    if (LoginViewModel.activeUser?.role == UserRole.EMPLOYEE) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        showChartShapeDropdown = true
                                    }
                                    .padding(bottom = 8.dp)) {
                                ChartShapeDropdown(currentItemIndex = if (showDonutChart) 0 else 1,
                                    show = showChartShapeDropdown,
                                    onHide = {
                                        showChartShapeDropdown = false
                                    },
                                    onClick = {
                                        viewModel.updateShowDonutChart(it)
                                    })
                            }

                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    showChartContentDropdown = true
                                }) {
                                ChartContentDropdown(currentItemIndex = chartContentType.ordinal,
                                    show = showChartContentDropdown,
                                    onHide = {
                                        showChartContentDropdown = false
                                    },
                                    onClick = {
                                        viewModel.updateChartContentType(it)
                                    })
                            }

                        }
                        val chartData by viewModel.chartData.collectAsState()
                        val colorsList: MutableList<Color> = mutableListOf()
                        chartData.keys.forEach {
                            colorsList.add(Color(viewModel.generateRandomColor()))
                        }
                        if (chartData.keys.size > 0) {
                            if (showDonutChart) {
                                DonutChart(
                                    colors = colorsList,
                                    inputValues = chartData.values.toList(),
                                    inputValuesNames = chartData.keys.toList()
                                )
                            } else {
                                BarChart(
                                    values = chartData.values.map { it * 30 }.toList(),
                                    valuesNames = chartData.keys.toList()
                                )
                            }
                        }
                    }
                    SearchBar(viewModel = viewModel, items = animals, onUpdate = {
                        viewModel.setAnimalList(it)
                    })
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                showSortingDropdown.value = true
                            }) {
                            SortingOptions(
                                showSortingDropdown = showSortingDropdown.value,
                                onHideSortingDropdown = {
                                    showSortingDropdown.value = false
                                },
                                viewModel = viewModel,
                                items = animals
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    navigateToFilters()
                                }) {
                                Text(
                                    text = stringResource(R.string.filters),
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Icon(
                                    imageVector = Icons.Filled.List,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.primary,
                                )
                            }

                            val context = LocalContext.current
                            val launcher =
                                rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                                    if (isGranted) {
                                        showExportDialog.value = true
                                    }
                                }
                            if (LoginViewModel.activeUser?.role == UserRole.EMPLOYEE) {

                                Row(
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .clickable {
                                            when (PackageManager.PERMISSION_GRANTED) {
                                                ContextCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                ) -> {
                                                    showExportDialog.value = true
                                                }
                                                else -> {
                                                    launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                }
                                            }
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.export),
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Send,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.primary,
                                    )
                                }
                            }
                        }
                    }

                    if (showExportDialog.value) {
                        ExportFileDialog(viewModel = viewModel, hideDialog = {
                            showExportDialog.value = false
                        })
                    }
                    if (LoginViewModel.activeUser?.role == UserRole.EMPLOYEE) {
                        AnimalCreationOption(
                            navigateToAnimalCreation = navigateToAnimalCreation
                        )
                    }
                }
                items(animals) { animal ->
                    AnimalCard(animal, navigateToAnimalDetails)
                }

            }
        }
    }
}

@Composable
private fun ExportFileDialog(
    viewModel: AnimalsViewModel, hideDialog: () -> Unit
) {
    val filename = remember { mutableStateOf("") }
    val fileTypes = listOf(
        FileType.CSV, FileType.JSON, FileType.XML, FileType.TXT
    )
    val selectedIndex = remember { mutableStateOf(0) }
    val context = LocalContext.current

    AlertDialog(onDismissRequest = hideDialog, title = {
        Text(
            text = stringResource(id = R.string.export_list),
            fontSize = 24.sp,
        )
    }, confirmButton = {
        Button(onClick = {
            viewModel.exportFile(
                filename.value, fileTypes[selectedIndex.value], context
            )
            hideDialog()
        }) {
            Text(
                text = stringResource(id = R.string.ok)
            )
        }
    }, dismissButton = {
        Button(onClick = hideDialog) {
            Text(text = stringResource(id = R.string.cancel))
        }
    }, text = {
        Column(
            modifier = Modifier.padding(
                top = 16.dp, end = 16.dp, bottom = 16.dp
            )
        ) {
            val focusManager = LocalFocusManager.current
            TextField(value = filename.value,
                onValueChange = {
                    filename.value = it
                },
                label = { Text(text = stringResource(id = R.string.filename)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )
            val showFileTypeDropdown = remember { mutableStateOf(false) }
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.filetype),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Box(modifier = Modifier.clickable {
                    showFileTypeDropdown.value = true
                }) {
                    Text(
                        text = fileTypes[selectedIndex.value].toString(),
                        color = MaterialTheme.colors.primary
                    )
                    DropdownMenu(
                        expanded = showFileTypeDropdown.value,
                        onDismissRequest = {
                            showFileTypeDropdown.value = false
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colors.background)
                            .padding(8.dp)
                    ) {
                        fileTypes.forEachIndexed { i, fileType ->
                            Text(text = fileType.toString(),
                                color = MaterialTheme.colors.primary,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        showFileTypeDropdown.value = false
                                        selectedIndex.value = i
                                    })
                        }
                    }
                }
            }
        }
    }, shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun AnimalCreationOption(
    navigateToAnimalCreation: () -> Unit,
) {
    Row(modifier = Modifier
        .clickable { navigateToAnimalCreation() }
        .padding(vertical = 16.dp)
        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Filled.AddCircle, contentDescription = null)
        Text(
            text = stringResource(id = R.string.new_animal),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun AnimalCard(
    animal: AnimalDTO,
    navigateToAnimalDetails: () -> Unit,
) {
    Card(elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navigateToAnimalDetails()
                AnimalsViewModel.selectedAnimal = animal
            }) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.name) + ":   ",
                        color = MaterialTheme.colors.primary
                    )
                    Text(
                        text = stringResource(id = R.string.species) + ":   ",
                        color = MaterialTheme.colors.primary
                    )
                    Text(
                        text = stringResource(id = R.string.habitat) + ":   ",
                        color = MaterialTheme.colors.primary
                    )
                    Text(
                        text = stringResource(id = R.string.diet) + ":   ",
                        color = MaterialTheme.colors.primary
                    )
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
    viewModel: AnimalsViewModel, items: List<AnimalDTO>, onUpdate: (List<AnimalDTO>) -> Unit
) {
    val newItems = items
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
                    onUpdate(searchResults)
                }
            }
        },
        placeholder = {
            Text(text = stringResource(id = R.string.find_by_name))
        },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = searchText.value.isNotEmpty(), enter = fadeIn(), exit = fadeOut()
            ) {
                IconButton(onClick = {
                    searchText.value = ""
                    viewModel.resetList()
                    coroutineScope.launch {
                        viewModel.searchResultsProcessed.collect { searchResults ->
                            onUpdate(searchResults)
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close, contentDescription = null
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
    items: List<AnimalDTO>
) {

    Text(
        text = stringResource(id = R.string.sort_by) + ":", modifier = Modifier.padding(end = 4.dp)
    )

    Box {
        val sortingOptions = listOf(
            stringResource(id = R.string.name),
            stringResource(id = R.string.species),
            stringResource(id = R.string.diet)
        )
        val selectedIndex = remember { mutableStateOf(0) }
        Text(
            text = sortingOptions[selectedIndex.value], color = MaterialTheme.colors.primary
        )
        DropdownMenu(
            expanded = showSortingDropdown,
            onDismissRequest = onHideSortingDropdown,
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .padding(8.dp)
        ) {
            sortingOptions.forEachIndexed { index, s ->
                Text(text = s,
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
                            //items.value = viewModel.animalList.value
                        })
            }
        }
    }
}


@Composable
private fun ChartShapeDropdown(
    currentItemIndex: Int, show: Boolean, onHide: () -> Unit, onClick: (Boolean) -> Unit
) {
    Text(
        text = stringResource(id = R.string.chart_shape) + ": ",
        modifier = Modifier.padding(end = 4.dp)
    )

    Box {
        val options = listOf(
            stringResource(id = R.string.donut), stringResource(id = R.string.bars)
        )
        val selectedIndex = currentItemIndex
        Text(
            text = options[selectedIndex], color = MaterialTheme.colors.primary
        )
        DropdownMenu(
            expanded = show,
            onDismissRequest = onHide,
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .padding(8.dp)
        ) {
            options.forEachIndexed { index, s ->
                Text(text = s,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onHide()
                            when (index) {
                                0 -> {
                                    onClick(true)
                                }
                                1 -> {
                                    onClick(false)
                                }
                            }
                        })
            }
        }
    }
}

@Composable
private fun ChartContentDropdown(
    currentItemIndex: Int, show: Boolean, onHide: () -> Unit, onClick: (Int) -> Unit
) {
    Text(
        text = stringResource(id = R.string.chart_for) + ": ",
        modifier = Modifier.padding(end = 4.dp)
    )
    Box {
        val options = listOf(
            stringResource(id = R.string.species),
            stringResource(id = R.string.habitat),
            stringResource(id = R.string.diet)
        )
        val selectedIndex = currentItemIndex
        Text(
            text = options[selectedIndex], color = MaterialTheme.colors.primary
        )
        DropdownMenu(
            expanded = show,
            onDismissRequest = onHide,
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .padding(8.dp)
        ) {
            options.forEachIndexed { index, s ->
                Text(text = s,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onHide()
                            onClick(index)
                        })
            }
        }
    }
}