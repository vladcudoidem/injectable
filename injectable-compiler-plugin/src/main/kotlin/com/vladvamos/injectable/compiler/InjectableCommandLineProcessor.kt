package com.vladvamos.injectable.compiler

import com.vladvamos.injectable.BuildConfig
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class InjectableCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = BuildConfig.COMPILER_PLUGIN_ID

    override val pluginOptions: Collection<AbstractCliOption> = listOf()
}
