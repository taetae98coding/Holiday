package io.github.taetae98coding.holiday.file

import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

data object FileDataSource {
    val printJson by lazy {
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
    }

    suspend inline fun <reified T> write(value: T, file: File) {
        withContext(Dispatchers.Default) {
            file.parentFile?.mkdirs()
            file.outputStream()
                .buffered()
                .use { stream -> printJson.encodeToStream(value, stream) }
        }
    }

    suspend inline fun <reified T> read(file: File): T {
        return withContext(Dispatchers.Default) {
            file.inputStream()
                .buffered()
                .use { stream -> printJson.decodeFromStream(stream) }
        }
    }
}
