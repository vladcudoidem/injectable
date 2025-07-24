plugins {
    kotlin("jvm")
    `maven-publish`
    id("formatting-convention")
    id("com.github.gmazzo.buildconfig")
    id("publishing-convention")
}

// Kotlin compiler plugin version
version = "0.1.0"

dependencies {
    compileOnly(libs.kotlin.compiler)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.compilerEmbeddable)
    testImplementation(libs.tschuchortdev.kotlinCompileTesting)
}

mavenPublishing {
    coordinates(group.toString(), "injectable-compiler-plugin", version.toString())

    pom {
        name = "Injectable Kotlin compiler plugin"
        description =
            "Automatically injects semantic information into Jetpack Compose composables."
        inceptionYear = "2025"
    }
}

buildConfig {
    packageName(project.group.toString())

    buildConfigField("COMPILER_PLUGIN_ID", group.toString())
}
