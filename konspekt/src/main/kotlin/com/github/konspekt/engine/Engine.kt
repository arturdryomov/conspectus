package com.github.konspekt.engine

import com.github.konspekt.Marker
import com.github.konspekt.Spec
import com.github.konspekt.SpecListener
import org.junit.platform.commons.util.ClassFilter
import org.junit.platform.commons.util.ReflectionUtils
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver
import org.junit.platform.engine.support.discovery.SelectorResolver
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine
import java.util.*
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext as JUnitEngineExecutionContext

internal data class EngineExecutionContext(val markersAvailable: Set<Marker>) : JUnitEngineExecutionContext

internal class Engine : HierarchicalTestEngine<EngineExecutionContext>() {

    companion object {
        private const val ID = "konspekt"
        private const val NAME = "Konspekt"
    }

    private lateinit var markersAvailable: Set<Marker>

    override fun getId() = ID

    override fun discover(request: EngineDiscoveryRequest, rootId: UniqueId) = EngineDescriptor(rootId, NAME).apply {
        discover(this, request)

        markersAvailable = Marker.values().filter { this.markerAvailable(marker = it) }.toSet()
    }

    private fun discover(root: EngineDescriptor, request: EngineDiscoveryRequest) {
        val classFilter = ClassFilter.of {
            Spec::class.java.isAssignableFrom(it) && !ReflectionUtils.isAbstract(it)
        }

        EngineDiscoveryRequestResolver.builder<EngineDescriptor>()
                .addClassContainerSelectorResolver(classFilter)
                .addSelectorResolver(SpecClassResolver(classFilter))
                .build()
                .resolve(request, root)
    }

    private class SpecClassResolver(private val classFilter: ClassFilter) : SelectorResolver {

        private val listeners = ServiceLoader.load(SpecListener::class.java)

        override fun resolve(selector: ClassSelector, context: SelectorResolver.Context): SelectorResolver.Resolution {
            return if (classFilter.match(selector.javaClass)) {
                val descriptor = context.addToParent { parent ->
                    val spec = ReflectionUtils.newInstance(selector.javaClass) as Spec

                    Optional.of(createSpecDescriptor(parent.uniqueId, spec))
                }

                SelectorResolver.Resolution.match(SelectorResolver.Match.exact(descriptor.get()))
            } else {
                SelectorResolver.Resolution.unresolved()
            }
        }

        private fun createSpecDescriptor(rootId: UniqueId, spec: Spec): TestDescriptor {
            val name = spec.javaClass.simpleName
            val id = rootId.childId(ExampleGroupNode.TYPE, name)
            val source = ClassSource.from(spec.javaClass)

            return ExampleGroupNode(id, name, source).also { node ->
                listeners.forEach {
                    node.beforeEach { it.onBeforeEach() }
                    node.afterEach { it.onAfterEach() }
                }

                spec.action.invoke(node)
            }
        }
    }

    override fun createExecutionContext(request: ExecutionRequest) = EngineExecutionContext(markersAvailable)
}
