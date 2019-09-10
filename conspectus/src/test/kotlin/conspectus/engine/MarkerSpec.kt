package conspectus.engine

import conspectus.Marker
import conspectus.Spec
import org.assertj.core.api.Assertions.assertThat

class MarkerSpec : Spec({

    describe("nesting") {

        val markers = listOf(*Marker.values(), null)

        markers.forEach { marker ->

            markers.forEach { parentMarker ->

                context("marker is [${marker?.name}], parent marker is [${parentMarker?.name}]") {

                    val result = marker.nested(parentMarker)

                    if (marker == Marker.Exclude || parentMarker == null) {

                        it("returns marker itself") {
                            assertThat(result).isEqualTo(marker)
                        }

                    } else {

                        it("returns parent marker") {
                            assertThat(result).isEqualTo(parentMarker)
                        }

                    }
                }
            }
        }
    }

    describe("skip result") {

        data class State(val markersAvailable: Set<Marker>, val marker: Marker?, val skip: Boolean)

        listOf(
                State(markersAvailable = setOf(Marker.Include), marker = null, skip = true),
                State(markersAvailable = setOf(Marker.Include), marker = Marker.Exclude, skip = true),
                State(markersAvailable = setOf(Marker.Include), marker = Marker.Include, skip = false),

                State(markersAvailable = emptySet(), marker = null, skip = false),
                State(markersAvailable = emptySet(), marker = Marker.Exclude, skip = true),
                State(markersAvailable = emptySet(), marker = Marker.Include, skip = false)
        ).forEach { state ->

            context("available markers [${state.markersAvailable}], marker is [${state.marker?.name}]") {

                it("converts to [${if (state.skip) "skippable" else "not skippable"}] result") {
                    assertThat(state.marker.toSkipResult(EngineExecutionContext(state.markersAvailable)).isSkipped).isEqualTo(state.skip)
                }
            }
        }
    }

})
