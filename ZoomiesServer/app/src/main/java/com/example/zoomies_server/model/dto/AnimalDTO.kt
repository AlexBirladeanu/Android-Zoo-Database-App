package com.example.zoomies_server.database.dto

import com.example.zoomies_server.model.database.entity.Animal

class AnimalDTO(
    val animalId: Int?,
    val name: String,
    val species: String,
    val habitat: String,
    val diet: String,
) : GeneralDTO() {
    init {
        super.animalIdS = animalId
        super.nameS = name
        super.speciesS = species
        super.habitatS = habitat
        super.dietS = diet
    }

    override fun toString(): String {
        return "AnimalDTO(animalId=$animalId, name='$name', species='$species', habitat='$habitat', diet='$diet')"
    }
}