package com.example.zoomies.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zoomies.database.AppDatabase
import com.example.zoomies.database.entity.Animal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnimalsViewModel(appDatabase: AppDatabase) : ViewModel() {

    private val database: AppDatabase

    private val animalListLock = Any()
    private val _animalList: MutableStateFlow<List<Animal>> = MutableStateFlow(listOf())
    val animalList = _animalList.asStateFlow()

    val searchResultsProcessed: MutableSharedFlow<List<Animal>> = MutableSharedFlow(replay = 0)

    private val _filtersList: MutableStateFlow<List<Filter>> = MutableStateFlow(listOf())
    val filterList = _filtersList.asStateFlow()

    init {
        database = appDatabase
        getFilterOptions()
        resetList()
    }

    private suspend fun getAll(): List<Animal>? = withContext(Dispatchers.IO) {
        return@withContext database.animalDao().getAll()
    }

    private suspend fun findByName(name: String): List<Animal>? = withContext(Dispatchers.IO) {
        return@withContext database.animalDao().findByName(name)
    }


    fun insert(id: Int? = null, name: String, species: String, habitat: String, diet: String) {
        val animal = Animal(
            animalId = id, name = name, species = species, habitat = habitat, diet = diet
        )
        addToComposableView(animal)
        viewModelScope.launch(Dispatchers.IO) {
            database.animalDao().insert(animal)
        }
    }

    fun delete(animal: Animal) {
        viewModelScope.launch(Dispatchers.IO) {
            database.animalDao().delete(animal)
            resetList()
        }

    }

    private fun addToComposableView(animal: Animal) {
        synchronized(animalListLock) {
            val species = _filtersList.value.filter { it.isSelected }.filter { it.category == "Species" }.map { it.name }
            val habitats = _filtersList.value.filter { it.isSelected }.filter { it.category == "Habitats" }.map { it.name }
            val diets = _filtersList.value.filter { it.isSelected }.filter { it.category == "Diets" }.map { it.name }
            val isItemValid = species.contains(animal.species) && habitats.contains(animal.habitat) && diets.contains(animal.diet)
            if (isItemValid) {
                _animalList.value += animal
                _animalList.value = _animalList.value.sortedBy { it.name }
            }
        }
    }

    fun performSearch(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _animalList.value = listOf()
            val searchResults: MutableList<Animal> = mutableListOf()
            getAll()?.forEach {
                searchResults.add(it)
            }
            _animalList.value = searchResults.filter { it.name.contains(name) }
            searchResultsProcessed.emit(_animalList.value)
        }
    }

    fun resetList() {
        viewModelScope.launch(Dispatchers.IO) {
            _animalList.value = listOf()
            getAll()?.forEach {
                addToComposableView(it)
            }
            searchResultsProcessed.emit(_animalList.value)
        }
    }

    fun sortByName() {
        _animalList.value = _animalList.value.sortedBy { it.name }
    }

    fun sortBySpecies() {
        _animalList.value = _animalList.value.sortedBy { it.species }
    }

    fun sortByDiet() {
        _animalList.value = _animalList.value.sortedBy { it.diet }
    }

    private fun getFilterOptions() {
        if (previousFilters != null) {
            _filtersList.value = previousFilters!!
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                getAll()?.forEach {
                    _filtersList.value += Filter("Species", it.species, true)
                    _filtersList.value += Filter("Habitats", it.habitat, true)
                    _filtersList.value += Filter("Diets", it.diet, true)
                }
            }
        }
    }

    fun onFilterClicked(filter: Filter) {
        val newFilter = filter.copy(isSelected = !filter.isSelected)
        val index = _filtersList.value.indexOf(filter)
        _filtersList.value = _filtersList.value.take(index) + newFilter + _filtersList.value.drop(
            index + 1
        )
    }

    companion object {
        var selectedAnimal: Animal? = null

        var previousFilters: List<Filter>? = null
    }
}