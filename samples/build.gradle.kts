dependencies {
    implementation(Library.KotlinStd)
}

dependencies {
    testImplementation(project(":conspectus"))

    testImplementation(Library.AssertJCore)
    testImplementation(Library.JUnitPlatformLauncher)
    testImplementation(Library.MockitoCore)
}