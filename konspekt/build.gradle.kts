dependencies {
    implementation(Library.KotlinStd)
    implementation(Library.JUnitPlatformEngine)
}

dependencies {
    testImplementation(Library.JUnitPlatformTestKit)
    testImplementation(Library.AssertJCore)
    testImplementation(Library.MockitoCore)
}

tasks.withType<Test> {
    filter {
        // Ignore integration subjects which contain explicit failures.
        excludeTestsMatching("com.github.konspekt.engine.integration.*")
    }
}