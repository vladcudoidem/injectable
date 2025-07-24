plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.vladvamos.injectable"
    compileSdk = 35

    defaultConfig { minSdk = 24 }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_19
        sourceCompatibility = JavaVersion.VERSION_19
    }

    testOptions { unitTests.all { it.useJUnitPlatform() } }

    kotlinOptions { jvmTarget = "19" }

//    publishing {
//        singleVariant("release") {
//            withSourcesJar()
//            withJavadocJar()
//        }
//    }
}
