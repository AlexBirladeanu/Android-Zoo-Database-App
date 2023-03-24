package com.example.zoomies.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoomies.ui.view_model.AnimalsViewModel

@Composable
fun FiltersView(
    onScreenClose: () -> Unit,
    viewModel: AnimalsViewModel
) {
    val items = viewModel.filterList.collectAsState()

    BackHandler(onBack = {
        onScreenClose()
        AnimalsViewModel.previousFilters = items.value
    })
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Filters",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
                navigationIcon = {
                    IconButton(onClick = {
                        onScreenClose()
                        AnimalsViewModel.previousFilters = items.value
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        })
    { paddingValues ->
        Card(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Species",
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items.value.filter { it.category == "Species" }) { filter ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp),
                            shape = RoundedCornerShape(8.dp),
                            backgroundColor = if (filter.isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                        ) {
                            Text(
                                text = filter.name,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        viewModel.onFilterClicked(filter)
                                    }
                            )
                        }
                    }
                }
                Text(
                    text = "Habitats",
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items.value.filter { it.category == "Habitats" }) { filter ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp),
                            shape = RoundedCornerShape(8.dp),
                            backgroundColor = if (filter.isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                        ) {
                            Text(
                                text = filter.name,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        viewModel.onFilterClicked(filter)
                                    }
                            )
                        }
                    }
                }
                Text(
                    text = "Diets",
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items.value.filter { it.category == "Diets" }) { filter ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp),
                            shape = RoundedCornerShape(8.dp),
                            backgroundColor = if (filter.isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                        ) {
                            Text(
                                text = filter.name,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        viewModel.onFilterClicked(filter)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}