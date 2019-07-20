package com.github.konspekt

import org.junit.platform.commons.annotation.Testable

@Testable
abstract class Spec(val action: ExampleGroup.() -> Unit)