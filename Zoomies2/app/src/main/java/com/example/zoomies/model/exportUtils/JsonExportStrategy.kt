package com.example.zoomies.model.exportUtils

import com.example.zoomies.model.dto.AnimalDTO
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class JsonExportStrategy : ExportStrategy {
    override fun export(file: File, fileContent: List<AnimalDTO>) {
        val gson = Gson()
        val outputStream = FileOutputStream(file)
        val writer = OutputStreamWriter(outputStream)
        gson.toJson(fileContent, writer)
        writer.flush()
        writer.close()
    }
}