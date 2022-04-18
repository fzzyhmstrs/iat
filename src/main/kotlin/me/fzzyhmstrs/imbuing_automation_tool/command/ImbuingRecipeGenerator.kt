package me.fzzyhmstrs.imbuing_automation_tool.command

import com.google.gson.GsonBuilder
import com.mojang.brigadier.Command
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipeFormat
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.io.File

object ImbuingRecipeGenerator {

    val command: Command<ServerCommandSource> = Command<ServerCommandSource> { generateRecipes(); 1 }
    private val gson = GsonBuilder().setPrettyPrinting().create()

    private fun generateRecipes(){

        Registry.ENCHANTMENT.ids.forEach { id: Identifier ->
            if (id.namespace != "amethyst_imbuement"){
                val enchant = Registry.ENCHANTMENT[id]
                if (enchant != null){
                    val target = enchant.type
                    val itemList: MutableList<Item> = mutableListOf()
                    val gemList: MutableList<Pair<Item,Item>> = mutableListOf()
                    try{
                        itemList.addAll(ItemLists.valueOf(target.name).itemList())
                        gemList.addAll(ItemLists.valueOf(target.name).gemList())
                    } finally {
                        itemList.addAll(ItemLists.GENERIC.itemList())
                        gemList.addAll(ItemLists.GENERIC.gemList())
                    }

                }
            }
        }
    }

    private fun writeToJson(fileName: String, recipe: ImbuingRecipeFormat){
        val dir = File(FabricLoader.getInstance().configDir.toFile(), "amethyst_imbuement")
        if (!dir.exists() && !dir.mkdirs()) {
            println("Couldn't make config directory!")
        }
        val f = File(dir,fileName)
        try{
            if (!f.createNewFile()){
                println("Couldn't generate new imbuing recipe or already exists: $fileName")
            } else {
                f.writeText(gson.toJson(recipe))
            }
        } catch (e: Exception) {
            println("Failed to write file: $fileName")
        }
    }


    private enum class ItemLists{
        ARMOR{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        ARMOR_FEET{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        ARMOR_LEGS{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        ARMOR_CHEST{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        ARMOR_HEAD{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        WEAPON{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        DIGGER{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        FISHING_ROD{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        TRIDENT{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        BREAKABLE{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        BOW{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        WEARABLE{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        CROSSBOW{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        VANISHABLE{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(Pair(Items.AIR,Items.AIR))
            }
        },
        GENERIC{
            override fun itemList(): List<Item> {
                return listOf(Items.AIR)
            }
            override fun gemList(): List<Pair<Item, Item>> {
                return listOf(
                    Pair(RegisterItem.CITRINE, RegisterItem.CITRINE),
                    Pair(RegisterItem.SMOKY_QUARTZ, RegisterItem.SMOKY_QUARTZ),
                    Pair(RegisterItem.DANBURITE, RegisterItem.DANBURITE)
                )
            }
        };


        abstract fun itemList(): List<Item>
        abstract fun gemList(): List<Pair<Item,Item>>

    }

}