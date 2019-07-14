package com.github.konspekt.engine

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId

fun UniqueId.childId(type: TestDescriptor.Type, name: String): UniqueId {
    val segmentType = when (type) {
        TestDescriptor.Type.CONTAINER -> "c"
        TestDescriptor.Type.TEST -> "t"
        TestDescriptor.Type.CONTAINER_AND_TEST -> "ct"
    }

    val segmentValue = name.hashCode().toString()

    return this.append(segmentType, segmentValue)
}