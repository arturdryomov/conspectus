package conspectus.engine

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId

internal fun UniqueId.childId(type: TestDescriptor.Type, name: String): UniqueId {
    val segmentType = when (type) {
        TestDescriptor.Type.CONTAINER -> "c"
        TestDescriptor.Type.TEST -> "t"
        TestDescriptor.Type.CONTAINER_AND_TEST -> throw IllegalArgumentException("[${type.name}] descriptor is unsupported.")
    }

    val segmentValue = name.hashCode().toString()

    return append(segmentType, segmentValue)
}