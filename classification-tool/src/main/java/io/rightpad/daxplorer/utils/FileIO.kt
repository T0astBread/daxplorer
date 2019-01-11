package io.rightpad.daxplorer.utils

import io.rightpad.daxplorer.data.IndexDataPoint
import java.io.File
import java.io.FileOutputStream
import java.util.stream.Collector

typealias Data = List<IndexDataPoint>
typealias OutputCollector = Collector<IndexDataPoint, *, String>

class FileIO(
        var chooseFile: () -> String?,
        var shouldSaveUnsavedChanges: () -> Boolean?,
        var collector: OutputCollector
) {
    var openFile: File? = null
        private set
    private var fileHasBeenChanged: Boolean = false

    fun open(data: Data): Boolean {
        if(!this.canClose(data))
            return false

        val chosenFilePath = chooseFile()
        if(chosenFilePath != null) {
            open(chosenFilePath)
            return true
        }
        return false
    }

    fun open(filePath: String) {
        this.openFile = File(filePath)
    }

    fun markAsChanged() {
        this.fileHasBeenChanged = true
    }

    fun markAsUnchanged() {
        this.fileHasBeenChanged = false
    }

    fun save(data: Data): Boolean {
        if(this.openFile == null) {
            val chosenFile = chooseFile() ?: return false
            open(chosenFile)
        }
        write(data)
        markAsUnchanged()
        return true
    }

    fun saveAs(data: Data): Boolean {
        this.openFile = null
        return save(data)
    }

    fun write(data: Data) {
        write(data
                .stream()
                .collect(this.collector)
                .toByteArray()
        )
    }

    private fun write(data: ByteArray) {
        FileOutputStream(this.openFile).use {
            it.write(data)
        }
    }

    fun canClose(data: Data) =
            !this.fileHasBeenChanged || when(shouldSaveUnsavedChanges()) {
                true  -> save(data)
                false -> true
                null  -> false
            }
}