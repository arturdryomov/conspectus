package conspectus.engine

import conspectus.Marker
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.support.hierarchical.Node.SkipResult

internal interface Markable {
    fun marked(marker: Marker): Boolean
}

internal fun TestDescriptor.markerAvailable(descriptor: TestDescriptor = this, marker: Marker): Boolean {
    if (descriptor.children.isEmpty()) {
        return false
    }

    return descriptor.children.any {
        val matchDirect = if (it is Markable) it.marked(marker) else false
        val matchNested = it.markerAvailable(marker = marker)

        matchDirect || matchNested
    }
}

internal fun Marker?.nested(parentMarker: Marker?): Marker? = if (this == Marker.Exclude || parentMarker == null) {
    this
} else {
    parentMarker
}

internal fun Marker?.toSkipResult(context: EngineExecutionContext): SkipResult {
    if (this == Marker.Exclude) {
        return SkipResult.skip("Excluded explicitly via the [x] marker.")
    }

    return if (context.markersAvailable.contains(Marker.Include)) {
        if (this == Marker.Include) {
            SkipResult.doNotSkip()
        } else {
            SkipResult.skip("Excluded implicitly via the [f] marker.")
        }
    } else {
        SkipResult.doNotSkip()
    }
}