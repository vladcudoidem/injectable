plugins {
    id("formatting-convention")
    id("android-convention")
    id("publishing-convention")
}

// Testing library version
version = "0.1.0"

kotlin { explicitApi() }

mavenPublishing {
    coordinates(group.toString(), "injectable-testing", version.toString())

    pom {
        name = "Injectable testing library"
        description =
            "Provides utils for reading the semantic information injected by the Injectable Kotlin compiler plugin."
        inceptionYear = "2025"
    }
}

dependencies {
    api(project(":injectable"))
    implementation(libs.compose.testJunit4)
    implementation(libs.compose.testManifest)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.junit5)
    testImplementation(libs.mockk)
}
