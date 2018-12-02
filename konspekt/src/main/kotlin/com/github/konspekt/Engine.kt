package com.github.konspekt

import org.junit.platform.commons.util.ClassFilter
import org.junit.platform.commons.util.ReflectionUtils
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.PackageSelector
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext as JUnitEngineExecutionContext

class EngineExecutionContext : JUnitEngineExecutionContext

class Engine : HierarchicalTestEngine<EngineExecutionContext>() {

    companion object {
        private const val ID = "konspekt"
        private const val NAME = "Konspekt"
    }

    override fun getId() = ID

    override fun createExecutionContext(request: ExecutionRequest) = EngineExecutionContext()

    override fun discover(request: EngineDiscoveryRequest, rootId: UniqueId): TestDescriptor {
        val root = EngineDescriptor(rootId, NAME)

        discoverSpecs(request)
                .map { createSpecRoot(rootId, it) }
                .forEach { root.addChild(it) }

        return root
    }

    private fun discoverSpecs(request: EngineDiscoveryRequest): List<Spec> {
        val classSelectorClasses = request.getSelectorsByType(ClassSelector::class.java)
                .map { it.javaClass }

        val packageSelectorClasses = request.getSelectorsByType(PackageSelector::class.java)
                .map { selector ->
                    ReflectionUtils.findAllClassesInPackage(selector.packageName, ClassFilter.of {
                        Spec::class.java.isAssignableFrom(it)
                    })
                }
                .flatten()

        val classes = classSelectorClasses + packageSelectorClasses

        return classes
                .filter { !ReflectionUtils.isAbstract(it) }
                .map { ReflectionUtils.newInstance(it) as Spec }
    }

    private fun createSpecRoot(rootId: UniqueId, spec: Spec): ExampleGroupNode {
        val specName = spec.javaClass.simpleName
        val specId = rootId.childId(ExampleGroupNode.TYPE, specName)
        val specSource = ClassSource.from(spec.javaClass)

        return ExampleGroupNode(specId, specName, { }, specSource).also {
            spec.action.invoke(it)
        }
    }
}
