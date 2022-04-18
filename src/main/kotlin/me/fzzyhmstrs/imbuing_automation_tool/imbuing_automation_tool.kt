package me.fzzyhmstrs.imbuing_automation_tool

import me.fzzyhmstrs.imbuing_automation_tool.registry.RegisterCommand
import net.fabricmc.api.ModInitializer


object VL: ModInitializer {
    const val MOD_ID = "imbuing_automation_tool"

    override fun onInitialize() {
        RegisterCommand.registerAll()
    }
}