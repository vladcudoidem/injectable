plugins {
    kotlin("jvm")
    `maven-publish`
    id("formatting-convention")
    id("com.github.gmazzo.buildconfig")
}

// Kotlin compiler plugin version
version = "0.1.0"

dependencies {
    compileOnly(libs.kotlin.compiler)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.compilerEmbeddable)
    testImplementation(libs.tschuchortdev.kotlinCompileTesting)
}

publishing {
    publications { create<MavenPublication>("maven") { from(components["java"]) } }

    repositories { mavenLocal() }
}

buildConfig {
    packageName(project.group.toString())

    buildConfigField("COMPILER_PLUGIN_ID", group.toString())
}
