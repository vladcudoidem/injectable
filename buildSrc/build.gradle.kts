plugins {
    `kotlin-dsl`
}

dependencies {
    // "libs" in parentheses to avoid IDE highlight error
    implementation(files((libs).javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.plugins.spotless.asModuleDependency("-plugin-gradle", true))
    implementation(libs.plugins.androidGradle.asModuleDependency(".gradle.plugin"))
    implementation(libs.plugins.kotlinAndroid.asModuleDependency(".gradle.plugin"))
}

// Converts a plugin dependency to a module dependency.
private fun Provider<PluginDependency>.asModuleDependency(
    artifactIdSuffix: String,
    trimArtifactId: Boolean = false,
): String {
    val group = get().pluginId
    val version = get().version.requiredVersion
    val artifactIdPrefix =
        if (!trimArtifactId) {
            group
        } else {
            group.substringAfterLast(".")
        }

    return "$group:${artifactIdPrefix + artifactIdSuffix}:$version"
}
