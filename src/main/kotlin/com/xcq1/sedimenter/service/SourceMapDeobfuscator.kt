package com.xcq1.sedimenter.service

import com.google.debugging.sourcemap.SourceMapConsumerFactory
import com.xcq1.sedimenter.sourcemap.SourceMapBackend
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

data class DeobfuscatedItem(val fileName: String, val line: Int, val column: Int) {
    override fun toString() = "$fileName:$line:$column"
}

@Service
class SourceMapDeobfuscator(private val backend: SourceMapBackend) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun deobfuscateItem(fileUrl: String, line: Int, column: Int): DeobfuscatedItem {
        val parsedSourceMap = backend.getSourceMap(fileUrl)?.let {
            SourceMapConsumerFactory.parse(it)
        }
        return if (parsedSourceMap == null)
            DeobfuscatedItem(fileUrl, line, column)
        else {
            try {
                val mapping = parsedSourceMap.getMappingForLine(line, column)
                DeobfuscatedItem(mapping.originalFile, mapping.lineNumber, mapping.columnPosition)
            } catch (e: Exception) {
                logger.error("Could not deobfuscate $fileUrl:$line:$column", e)
                DeobfuscatedItem(fileUrl, line, column)
            }

        }
    }

    companion object {
        private val URL_PATTERN = Regex("(https?://[^:]+):([\\d]+):([\\d]+)\\)?")
    }

    fun deobfuscateStackTrace(rawTrace: String) = rawTrace.replace(URL_PATTERN) {
        deobfuscateItem(it.groupValues[1], it.groupValues[2].toInt(), it.groupValues[3].toInt()).toString()
    }

}