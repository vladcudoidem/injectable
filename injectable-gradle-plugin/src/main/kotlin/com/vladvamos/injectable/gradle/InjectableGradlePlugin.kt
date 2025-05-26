package com.vladvamos.injectable.gradle

import com.vladvamos.injectable.BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.internal.cc.base.logger
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class InjectableGradlePlugin : KotlinCompilerPluginSupportPlugin {

    // Todo: only apply for testing
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun getCompilerPluginId(): String = BuildConfig.COMPILER_PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = BuildConfig.COMPILER_PLUGIN_GROUP,
            artifactId = BuildConfig.COMPILER_PLUGIN_ARTIFACT,
            version = BuildConfig.COMPILER_PLUGIN_VERSION,
        )
    }

    override fun apply(target: Project) {
        val libraryCoordinates =
            with(BuildConfig) { "$LIBRARY_GROUP:$LIBRARY_ARTIFACT:$LIBRARY_VERSION" }
        target.dependencies.add("implementation", libraryCoordinates)
        logger.lifecycle("Dependency '$libraryCoordinates' was added.")

        val compilerPluginCoordinates =
            with(BuildConfig) {
                "$COMPILER_PLUGIN_GROUP:$COMPILER_PLUGIN_ARTIFACT:$COMPILER_PLUGIN_VERSION"
            }
        logger.lifecycle(
            "The current version of the Injectable Gradle Plugin uses '$compilerPluginCoordinates'."
        )
    }

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        return project.provider {
            val options = mutableListOf<SubpluginOption>()
            // Todo: add options

            options
        }
    }
}
