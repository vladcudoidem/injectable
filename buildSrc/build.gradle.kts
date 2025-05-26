plugins {
    `kotlin-dsl`
}

dependencies {
    // "libs" in parentheses to avoid IDE highlight error
    implementation(files((libs).javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.plugins.spotless.asModuleDependency())
}

/**
 * Converts a [PluginDependency] into a String-based module dependency. If [reversed] is set to
 * `true`, the module name is inferred to be `...-gradle-plugin` instead of `...-plugin-gradle`.
 */
private fun Provider<PluginDependency>.asModuleDependency(reversed: Boolean = false): String {
    val group = get().pluginId
    val simplePluginName = group.split(".").last()
    val artifactSuffix =
        if (reversed) {
            "-gradle-plugin"
        } else {
            "-plugin-gradle"
        }
    val version = get().version.requiredVersion

    return "$group:${simplePluginName + artifactSuffix}:$version"
}
