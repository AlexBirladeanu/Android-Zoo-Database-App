package com.example.zoomies.model.exportUtils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.example.zoomies.model.FileType
import com.example.zoomies.model.dto.AnimalDTO
import java.io.File
import java.io.IOException

class FileExporter(private val strategy: ExportStrategy) {
    fun exportFile(filename: String, fileType: FileType, context: Context, fileContent: List<AnimalDTO>) {
        val docsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = when (fileType) {
            FileType.CSV -> File(docsDir, "$filename.csv")
            FileType.JSON -> File(docsDir, "$filename.json")
            FileType.XML -> File(docsDir, "$filename.xml")
            FileType.TXT -> File(docsDir, "$filename.txt")
        }

        try {
            strategy.export(file, fileContent)
            Toast.makeText(context, "File saved", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(context, "Cannot save file", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}