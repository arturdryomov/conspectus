package com.github.konspekt

import org.junit.platform.commons.annotation.Testable

@Testable
abstract class Spec(internal val action: ExampleGroup.() -> Unit)