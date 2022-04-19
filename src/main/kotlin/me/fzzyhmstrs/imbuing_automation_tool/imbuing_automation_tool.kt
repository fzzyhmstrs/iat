package me.fzzyhmstrs.imbuing_automation_tool

import me.fzzyhmstrs.imbuing_automation_tool.registry.RegisterCommand
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import kotlin.random.Random


object IAT: ModInitializer {
    const val MOD_ID = "imbuing_automation_tool"
    val iatRandom = Random(System.currentTimeMillis())

    override fun onInitialize() {
        RegisterCommand.registerAll()
    }
}