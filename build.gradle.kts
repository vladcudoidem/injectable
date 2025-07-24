plugins {
    alias(libs.plugins.buildConfig)
}

allprojects {
    group = "com.vladvamos.injectable"

    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        mavenLocal()
    }
}
