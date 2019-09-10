import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.dsl.ScriptHandler

enum class Library(group: String, artifact: String, internal val version: Version) {
    AssertJCore("org.assertj", "assertj-core", Version.AssertJ),
    JUnitPlatformEngine("org.junit.platform", "junit-platform-engine", Version.JUnitPlatform),
    JUnitPlatformLauncher("org.junit.platform", "junit-platform-launcher", Version.JUnitPlatform),
    JUnitPlatformTestKit("org.junit.platform", "junit-platform-testkit", Version.JUnitPlatform),
    KotlinStd("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", Version.Kotlin),
    MockitoCore("org.mockito", "mockito-core", Version.Mockito),
    ;

    internal val notation = "$group:$artifact:${version.value}"

    internal enum class Version(val value: String) {
        AssertJ("3.13.2"),
        JUnitPlatform("1.5.2"),
        Kotlin("1.3.50"),
        Mockito("3.0.0"),
    }
}

enum class Plugin(val id: String, group: String, artifact: String, version: Version) {
    Kotlin("org.jetbrains.kotlin.jvm", "org.jetbrains.kotlin", "kotlin-gradle-plugin", Version.Kotlin),
    ;

    internal val notation = "$group:$artifact:${version.value}"

    private enum class Version(val value: String) {
        Kotlin(Library.KotlinStd.version.value),
    }
}

fun DependencyHandler.plugin(plugin: Plugin) = add(ScriptHandler.CLASSPATH_CONFIGURATION, plugin.notation)

fun DependencyHandler.implementation(library: Library) = add("implementation", library.notation)
fun DependencyHandler.testImplementation(library: Library) = add("testImplementation", library.notation)