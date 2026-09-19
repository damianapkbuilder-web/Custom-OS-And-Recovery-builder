package com.example.data.engine.modules

import java.io.File

/**
 * Interface defining a modular firmware patcher step
 */
interface IFirmwarePatcherModule {
    val moduleName: String
    val category: String
    val priority: Int
    suspend fun execute(
        extractedDir: File,
        logger: (String) -> Unit
    ): Boolean
}

/**
 * Module registry managing all pipeline execution units
 */
object ModulePipelineRegistry {
    private val registeredModules = mutableListOf<IFirmwarePatcherModule>()

    fun register(module: IFirmwarePatcherModule) {
        registeredModules.add(module)
    }

    fun getAllModules(): List<IFirmwarePatcherModule> {
        return registeredModules.sortedBy { it.priority }
    }

    fun getModulesByCategory(category: String): List<IFirmwarePatcherModule> {
        return registeredModules.filter { it.category.equals(category, ignoreCase = true) }
    }
}
