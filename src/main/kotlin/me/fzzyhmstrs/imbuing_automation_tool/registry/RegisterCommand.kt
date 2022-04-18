package me.fzzyhmstrs.imbuing_automation_tool.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import me.fzzyhmstrs.imbuing_automation_tool.command.ImbuingRecipeGenerator
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object RegisterCommand {

    private fun execute(context: CommandContext<Any>): Int{
        return 1
    }

    fun registerAll(){
        CommandRegistrationCallback.EVENT.register { commandDispatcher: CommandDispatcher<ServerCommandSource>, b: Boolean ->

            commandDispatcher.register(CommandManager.literal("genImbuingRecipes").executes(ImbuingRecipeGenerator.command))

        }
    }

}