plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("formatting-convention")
    `maven-publish`
}

// Library version
version = "0.1.0"

kotlin { explicitApi() }

android {
    namespace = "com.vladvamos.injectable"
    compileSdk = 35

    defaultConfig { minSdk = 24 }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_19
        sourceCompatibility = JavaVersion.VERSION_19
    }

    kotlinOptions { jvmTarget = "19" }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications { create<MavenPublication>("release") { from(components["release"]) } }

        repositories { mavenLocal() }
    }
}

dependencies {
    implementation(libs.compose.ui)
    implementation(libs.compose.runtime)

    testImplementation(libs.kotlin.test)
}
