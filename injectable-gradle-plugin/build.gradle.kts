plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
    id("formatting-convention")
    alias(libs.plugins.buildConfig)
}

// Gradle plugin version
version = "0.1.0"

dependencies {
    implementation(gradleApi())
    implementation(libs.kotlin.gradlePlugin)
}

java { withSourcesJar() }

gradlePlugin {
    plugins {
        create("injectablePlugin") {
            id = group.toString()
            implementationClass = "com.vladvamos.injectable.gradle.InjectableGradlePlugin"
        }
    }
}

publishing { repositories { mavenLocal() } }

buildConfig {
    packageName(project.group.toString())

    val compilerPluginSubProjectName = "injectable-compiler-plugin"
    val compilerPluginSubProject = project(":$compilerPluginSubProjectName")
    buildConfigField("COMPILER_PLUGIN_GROUP", compilerPluginSubProject.group.toString())
    buildConfigField("COMPILER_PLUGIN_ARTIFACT", compilerPluginSubProjectName)
    buildConfigField("COMPILER_PLUGIN_VERSION", compilerPluginSubProject.version.toString())
    buildConfigField("COMPILER_PLUGIN_ID", compilerPluginSubProject.group.toString())

    val librarySubProjectName = "injectable"
    val librarySubProject = project(":$librarySubProjectName")
    buildConfigField("LIBRARY_GROUP", librarySubProject.group.toString())
    buildConfigField("LIBRARY_ARTIFACT", librarySubProjectName)
    buildConfigField("LIBRARY_VERSION", librarySubProject.version.toString())
}
