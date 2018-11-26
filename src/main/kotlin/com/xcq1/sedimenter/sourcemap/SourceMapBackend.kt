package com.xcq1.sedimenter.sourcemap

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import com.netflix.hystrix.HystrixCommand
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class SourceMapBackend {

    @Value("\${sedimenter.timeout}")
    val timeout: String = ""

    @Cacheable("sourcemaps")
    fun getSourceMap(sourceUrl: String) = GetSourceFileRequest(sourceUrl, timeout.toIntOrNull() ?: 40000).execute()

}

private val hostFromUrl = { url: String -> { UriComponentsBuilder.fromUriString(url).build().host } }

private val retryTemplate = RetryTemplate().apply {
    setBackOffPolicy(ExponentialBackOffPolicy().apply {
        initialInterval = 500L
        multiplier = 2.0
    })
    setRetryPolicy(SimpleRetryPolicy().apply {
        maxAttempts = 5
    })
}

private fun <X> Request.responseStringWithRetryOrThrow(success: (String) -> X): X {
    return retryTemplate.execute<X, Throwable> { _ ->
        val (_, _, result) = responseString()
        result.fold(success, { throw it })
    }
}

class GetSourceFileRequest(private val sourceUrl: String,
                           private val timeout: Int)
    : HystrixCommand<String?>(hostFromUrl(sourceUrl), timeout) {
    companion object {
        private const val SOURCE_MAP_PREFIX = "//# sourceMappingURL="
    }

    override fun run(): String? {
        return sourceUrl.httpGet().responseStringWithRetryOrThrow { wholeFile ->
            wholeFile.lines().findLast { line ->
                line.startsWith(SOURCE_MAP_PREFIX)
            }?.let {
                GetSourceMapFileRequest(sourceUrl, it.removePrefix(SOURCE_MAP_PREFIX), timeout / 2).execute()
            }
        }
    }
}

class GetSourceMapFileRequest(private val sourceUrl: String,
                              private val sourceMapUrl: String,
                              timeout: Int)
    : HystrixCommand<String>(hostFromUrl(getUriToRequest(sourceUrl, sourceMapUrl)), timeout) {

    companion object {
        private fun getUriToRequest(originalUrl: String, overwriteUrl: String) = when {
            overwriteUrl.contains("//") -> UriComponentsBuilder.fromHttpUrl(overwriteUrl)
            overwriteUrl.startsWith("/") -> UriComponentsBuilder.fromHttpUrl(originalUrl).replacePath(overwriteUrl)
            else -> UriComponentsBuilder.fromHttpUrl(originalUrl).pathSegment("../$overwriteUrl")
        }.build().toUriString()
    }

    override fun run() = getUriToRequest(sourceUrl, sourceMapUrl).httpGet().responseStringWithRetryOrThrow { it }

}