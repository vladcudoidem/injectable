plugins {
    id("formatting-convention")
    id("android-convention")
}

// Testing library version
version = "0.1.0"

kotlin { explicitApi() }

dependencies {
    api(project(":injectable"))
    implementation(libs.compose.testJunit4)
    implementation(libs.compose.testManifest)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.junit5)
    testImplementation(libs.mockk)
}
