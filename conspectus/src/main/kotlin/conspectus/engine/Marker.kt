package conspectus.engine

import conspectus.Marker
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.support.hierarchical.Node.SkipResult

internal interface Markable {
    val marker: Marker?
}

internal fun TestDescriptor.markerAvailable(marker: Marker): Boolean = children.any {
    val matchDirect = it is Markable && it.marker == marker
    val matchNested = it.markerAvailable(marker)

    matchDirect || matchNested
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