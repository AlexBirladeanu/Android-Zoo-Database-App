package com.example.zoomies.view_model

import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.util.Xml
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zoomies.model.FileType
import com.example.zoomies.model.Filter
import com.example.zoomies.model.database.AppDatabase
import com.example.zoomies.model.database.entity.Animal
import com.example.zoomies.model.observer.LanguageEventHandler
import com.example.zoomies.model.observer.Observer
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlSerializer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*
import kotlin.collections.HashMap

class AnimalsViewModel(val languageEventHandler: LanguageEventHandler, appDatabase: AppDatabase, private val refreshPage: () -> Unit = {}) : ViewModel(),
    Observer {

    private val database: AppDatabase

    private val animalListLock = Any()
    private val _animalList: MutableStateFlow<List<Animal>> = MutableStateFlow(listOf())
    val animalList = _animalList.asStateFlow()

    val searchResultsProcessed: MutableSharedFlow<List<Animal>> = MutableSharedFlow(replay = 0)

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

        database = appDatabase
        getFilterOptions()

        chartContentChanged.observeForever {
            getChartData()
        }

        resetList()
    }

    override fun onLanguageChanged() {
        refreshPage()
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
            previousFilters = null
            getFilterOptions()
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
            if (isItemValid || true) {
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
            getChartData()
            searchResultsProcessed.emit(_animalList.value)
        }
    }

    fun resetList() {
        viewModelScope.launch(Dispatchers.IO) {
            _animalList.value = listOf()
            getAll()?.forEach {
                addToComposableView(it)
            }
            getChartData()
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

    fun setAnimalList(newList: List<Animal>) {
        _animalList.value = newList
    }

    fun exportFile(filename: String, fileType: FileType, context: Context) {
        val docsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val fileContent: List<Animal> = _animalList.value

        val file: File
        when (fileType) {
            FileType.CSV -> {
                file = File(docsDir, "$filename.csv")
                try {
                    val outputStream = FileOutputStream(file)
                    val writer = outputStream.bufferedWriter()
                    writer.write(""""ID", "Name", "Species", "Habitat", "Diet"""")
                    writer.newLine()
                    fileContent.forEach {
                        writer.write("${it.animalId}, ${it.name}, ${it.species}, ${it.habitat},\"${it.diet}\"")
                        writer.newLine()
                    }
                    writer.flush()
                    Toast.makeText(context, "File saved", Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    Toast.makeText(context, "Cannot save file", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
            FileType.JSON -> {
                file = File(docsDir, "$filename.json")
                try {
                    val gson = Gson()
                    val outputStream = FileOutputStream(file)
                    val writer = OutputStreamWriter(outputStream)
                    gson.toJson(fileContent, writer)
                    writer.flush()
                    writer.close()
                    Toast.makeText(context, "File saved", Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    Toast.makeText(context, "Cannot save file", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
            FileType.XML -> {
                file = File(docsDir, "$filename.xml")
                try {
                    val serializer: XmlSerializer = Xml.newSerializer()
                    val outputStream = FileOutputStream(file)
                    serializer.setOutput(outputStream, "UTF-8")
                    serializer.startDocument(null, true)
                    serializer.startTag(null, "Animals")

                    fileContent.forEach { animal ->
                        serializer.startTag(null, "Animal")
                        serializer.attribute(null, "id", animal.animalId.toString())
                        serializer.attribute(null, "name", animal.name)
                        serializer.attribute(null, "species", animal.species)
                        serializer.attribute(null, "habitat", animal.habitat)
                        serializer.attribute(null, "diet", animal.diet)
                        serializer.endTag(null, "Animal")
                    }

                    serializer.endTag(null, "Animals")
                    serializer.endDocument()
                    outputStream.flush()
                    outputStream.close()

                    Toast.makeText(context, "File saved", Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    Toast.makeText(context, "Cannot save file", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
            FileType.TXT -> {
                file = File(docsDir, "$filename.txt")
                try {
                    val outputStream = FileOutputStream(file)
                    outputStream.write(fileContent.toString().encodeToByteArray())
                    outputStream.close()
                    Toast.makeText(context, "File saved", Toast.LENGTH_LONG).show()
                }catch (e: IOException) {
                    Toast.makeText(context, "Cannot save file", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
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
        var selectedAnimal: Animal? = null

        var previousFilters: List<Filter>? = null

        var chartContentChanged = MutableLiveData<Unit>()
    }
}