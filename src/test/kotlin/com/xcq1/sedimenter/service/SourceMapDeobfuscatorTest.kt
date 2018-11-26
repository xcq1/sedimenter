package com.xcq1.sedimenter.service

import com.xcq1.sedimenter.sourcemap.SourceMapBackend
import io.kotlintest.shouldBe
import io.kotlintest.shouldHave
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk

class SourceMapDeobfuscatorTest : StringSpec({
    "it deobfuscates one element correctly" {
        val backend = mockk<SourceMapBackend>()
        every { backend.getSourceMap("http://example.com/main.js") } returns ""
        SourceMapDeobfuscator(backend).deobfuscateItem("http://example.com/main.js", 1, 1).let {
            it.fileName shouldBe "http://example.com/main.js"
            it.line shouldBe 1
            it.column shouldBe 1
        }
    }

    "it deobfuscates one stack trace correctly" {
    }
})