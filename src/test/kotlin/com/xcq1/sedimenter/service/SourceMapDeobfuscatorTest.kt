package com.xcq1.sedimenter.service

import com.xcq1.sedimenter.sourcemap.SourceMapBackend
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk

class SourceMapDeobfuscatorTest : StringSpec({
    "" {
        val backend = mockk<SourceMapBackend>()
        every { backend.getSourceMap("http://example.com/main.js") } returns ""
        SourceMapDeobfuscator(mockk())
    }
    "" {

    }
})