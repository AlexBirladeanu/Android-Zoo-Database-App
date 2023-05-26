package com.example.zoomies.model.exportUtils

import com.example.zoomies.model.dto.AnimalDTO
import java.io.File

interface ExportStrategy {
    fun export(file: File, fileContent: List<AnimalDTO>)
}