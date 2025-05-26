dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

plugins {
    id("com.android.library") version "8.9.3" apply false
    id("org.jetbrains.kotlin.android") version "2.1.21" apply false
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

include(":injectable")
include(":injectable-compiler-plugin")
include(":injectable-gradle-plugin")

rootProject.name = "injectable"
