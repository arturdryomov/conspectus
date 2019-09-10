dependencies {
    implementation(Library.KotlinStd)
}

dependencies {
    testImplementation(project(":konspekt"))

    testImplementation(Library.AssertJCore)
    testImplementation(Library.JUnitPlatformLauncher)
    testImplementation(Library.MockitoCore)
}