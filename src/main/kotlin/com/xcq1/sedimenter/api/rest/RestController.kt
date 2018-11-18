package com.xcq1.sedimenter.api.rest

import com.xcq1.sedimenter.api.ExceptionData
import com.xcq1.sedimenter.service.ExceptionLogger
import com.xcq1.sedimenter.service.SourceMapDeobfuscator
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController(private val sourceMapDeobfuscator: SourceMapDeobfuscator,
                     private val exceptionLogger: ExceptionLogger) {

    @PostMapping("/deobfuscate")
    fun deobfuscate(@RequestBody input: ExceptionData): ExceptionData {

        val deobfuscatedError = sourceMapDeobfuscator.deobfuscateItem(input.source, input.lineno, input.colno)
        val deobfuscatedStackTrace = sourceMapDeobfuscator.deobfuscateStackTrace(input.error)

        return input.copy(source = deobfuscatedError.fileName,
                lineno = deobfuscatedError.line,
                colno = deobfuscatedError.column,
                error = deobfuscatedStackTrace)
    }

    @PostMapping("/log")
    fun logWithLogstash(@RequestBody input: ExceptionData) {
        exceptionLogger.log(deobfuscate(input))
    }

}