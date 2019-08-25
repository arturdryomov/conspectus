package com.github.konspekt

interface SpecListener {
    fun onBeforeEach() = Unit
    fun onAfterEach() = Unit
}