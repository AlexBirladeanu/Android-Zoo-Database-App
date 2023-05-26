package com.example.zoomies.view_model

import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.util.Xml
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zoomies.MainActivity
import com.example.zoomies.model.FileType
import com.example.zoomies.model.Filter
import com.example.zoomies.model.dto.AnimalDTO
import com.example.zoomies.model.dto.factory.AnimalDTOFactory
import com.example.zoomies.model.exportUtils.*
import com.example.zoomies.model.observer.LanguageEventHandler
import com.example.zoomies.model.observer.Observer
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlSerializer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*
import kotlin.collections.HashMap

class AnimalsViewModel(val languageEventHandler: LanguageEventHandler, private val refreshPage: () -> Unit = {}) : ViewModel(),
    Observer {


    private val animalListLock = Any()
    private val _animalList: MutableStateFlow<List<AnimalDTO>> = MutableStateFlow(listOf())
    val animalList = _animalList.asStateFlow()

    val searchResultsProcessed: MutableSharedFlow<List<AnimalDTO>> = MutableSharedFlow(replay = 0)

    private val _filtersList: MutableStateFlow<List<Filter>> = MutableStateFlow(listOf())
    val filterList = _filtersList.asStateFlow()

    private val _chartData: MutableStateFlow<HashMap<String, Float>> = MutableStateFlow(hashMapOf("Animals" to 1f))
    val chartData = _chartData.asStateFlow()

    private val _showDonutChart: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val showDonutChart = _showDonutChart.asStateFlow()

    private val _chartContentType: MutableStateFlow<ChartContent> = MutableStateFlow(ChartContent.SPECIES)
    val chartContentType = _chartContentType.asStateFlow()

    init {
        languageEventHandler.attach(this)

        getFilterOptions()

        chartContentChanged.observeForever {
            getChartData()
        }
        MainActivity.serverRequestFinished.observeForever {
            resetList()
        }
    }

    override fun onLanguageChanged() {
        refreshPage()
    }

    fun insert(id: Int? = null, name: String, species: String, habitat: String, diet: String, context: Context) {
        val animal = AnimalDTOFactory.instance.createDTO(
            animalId = id, name = name, species = species, habitat = habitat, diet = diet
        )
        (context as MainActivity).requestInsertAnimal(animal)
    }

    fun delete(animal: AnimalDTO, context: Context) {
        (context as MainActivity).requestDeleteAnimal(animal)
    }

    fun performSearch(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _animalList.value = listOf()
            MainActivity.animals?.let { list ->
                _animalList.value = list.filter { it.name.contains(name) }
                getChartData()
                searchResultsProcessed.emit(_animalList.value)
            }
        }
    }

    fun resetList() {
        MainActivity.animals?.let {
            _animalList.value = it
            viewModelScope.launch(Dispatchers.IO) {
                searchResultsProcessed.emit(_animalList.value)
            }
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
                MainActivity.animals?.forEach {
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

    fun setAnimalList(newList: List<AnimalDTO>) {
        _animalList.value = newList
    }

    fun exportFile(filename: String, fileType: FileType, context: Context) {
        val fileContent: List<AnimalDTO> = _animalList.value
        when (fileType) {
            FileType.CSV -> {
                val fileExporter = FileExporter(CsvExportStrategy())
                fileExporter.exportFile(filename, FileType.CSV, context, fileContent)
            }
            FileType.JSON -> {
                val fileExporter = FileExporter(JsonExportStrategy())
                fileExporter.exportFile(filename, FileType.JSON, context, fileContent)
            }
            FileType.XML -> {
                val fileExporter = FileExporter(XmlExportStrategy())
                fileExporter.exportFile(filename, FileType.XML, context, fileContent)
            }
            FileType.TXT -> {
                val fileExporter = FileExporter(TxtExportStrategy())
                fileExporter.exportFile(filename, FileType.TXT, context, fileContent)
            }
        }
    }


    private fun getChartData() {
        val newHashMap: HashMap<String, Float> = hashMapOf()
        when (_chartContentType.value) {
            ChartContent.SPECIES -> {
                _animalList.value.forEach {
                    if (newHashMap.keys.contains(it.species)) {
                        val currentCounter = newHashMap[it.species]
                        currentCounter?.let { counter ->
                            newHashMap[it.species] = counter + 1f
                        }
                    } else {
                        newHashMap[it.species] = 1f
                    }
                }
            }
            ChartContent.HABITAT -> {
                _animalList.value.forEach {
                    if (newHashMap.keys.contains(it.habitat)) {
                        val currentCounter = newHashMap[it.habitat]
                        currentCounter?.let { counter ->
                            newHashMap[it.habitat] = counter + 1f
                        }
                    } else {
                        newHashMap[it.habitat] = 1f
                    }
                }
            }
            ChartContent.DIET -> {
                _animalList.value.forEach {
                    if (newHashMap.keys.contains(it.diet)) {
                        val currentCounter = newHashMap[it.diet]
                        currentCounter?.let { counter ->
                            newHashMap[it.diet] = counter + 1f
                        }
                    } else {
                        newHashMap[it.diet] = 1f
                    }
                }
            }
        }
        _chartData.value = newHashMap
    }

    fun generateRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    fun updateShowDonutChart(newValue: Boolean) {
        _showDonutChart.value = newValue
    }

    fun updateChartContentType(index: Int) {
        _chartContentType.value = when (index) {
            0 -> ChartContent.SPECIES
            1 -> ChartContent.HABITAT
            else -> ChartContent.DIET
        }
        chartContentChanged.postValue(Unit)
    }

    enum class ChartContent {
        SPECIES,
        HABITAT,
        DIET
    }

    companion object {
        var selectedAnimal: AnimalDTO? = null

        var previousFilters: List<Filter>? = null

        var chartContentChanged = MutableLiveData<Unit>()

    }
}