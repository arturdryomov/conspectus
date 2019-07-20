package com.github.konspekt.engine

import com.github.konspekt.Spec
import org.junit.platform.commons.util.ClassFilter
import org.junit.platform.commons.util.ReflectionUtils
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.*
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
        val classFilter = ClassFilter.of { Spec::class.java.isAssignableFrom(it) }

        val classNameFilters = request.getFiltersByType(ClassNameFilter::class.java)
        val packageNameFilters = request.getFiltersByType(PackageNameFilter::class.java)

        val classpathRootSelectorClasses = request.getSelectorsByType(ClasspathRootSelector::class.java)
                .map { ReflectionUtils.findAllClassesInClasspathRoot(it.classpathRoot, classFilter) }
                .flatten()

        val packageSelectorClasses = request.getSelectorsByType(PackageSelector::class.java)
                .map { ReflectionUtils.findAllClassesInPackage(it.packageName, classFilter) }
                .flatten()

        val classSelectorClasses = request.getSelectorsByType(ClassSelector::class.java)
                .map { it.javaClass }
                .filter { classFilter.match(it) }

        val classes = classpathRootSelectorClasses + packageSelectorClasses + classSelectorClasses

        return classes
                .filter { javaClass -> classNameFilters.all { it.apply(javaClass.name).included() } }
                .filter { javaClass -> packageNameFilters.all { it.apply(javaClass.`package`.name).included() } }
                .filter { !ReflectionUtils.isAbstract(it) }
                .map { ReflectionUtils.newInstance(it) as Spec }
    }

    private fun createSpecRoot(rootId: UniqueId, spec: Spec): ExampleGroupNode {
        val specName = spec.javaClass.simpleName
        val specId = rootId.childId(ExampleGroupNode.TYPE, specName)
        val specSource = ClassSource.from(spec.javaClass)

        return ExampleGroupNode(specId, specName, specSource).also {
            spec.action.invoke(it)
        }
    }
}
