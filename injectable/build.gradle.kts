plugins {
    id("formatting-convention")
    id("android-convention")
}

// Library version
version = "0.1.0"

kotlin { explicitApi() }

android {
    sourceSets {
        getByName("test") {
            java.srcDirs("src/integrationTest/kotlin")
            res.srcDirs("src/integrationTest/res")
            manifest.srcFile("src/integrationTest/AndroidManifest.xml")
        }
    }
}

dependencies {
    implementation(libs.compose.ui)
    implementation(libs.compose.runtime)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.junit5)
}
