package com.xcq1.sedimenter.api

data class ExceptionData(val message: String,
                         val source: String,
                         val lineno: Int,
                         val colno: Int,
                         val error: String,
                         val additionalFields: Map<String, String>?) {
    override fun toString() = "$message ($source:$lineno:$colno)\n$error"
}