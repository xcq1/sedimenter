package com.xcq1.sedimenter.service

import com.xcq1.sedimenter.api.ExceptionData
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service

@Service
class ExceptionLogger {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun log(exception: ExceptionData) {
        exception.additionalFields?.forEach { MDC.put(it.key, it.value) }
        logger.error(exception.toString())
        exception.additionalFields?.forEach { MDC.remove(it.key) }
    }

}