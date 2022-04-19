package me.fzzyhmstrs.imbuing_automation_tool.command

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.mojang.brigadier.Command
import me.fzzyhmstrs.amethyst_imbuement.augment.base_augments.BaseAugment
import me.fzzyhmstrs.amethyst_imbuement.registry.RegisterItem
import me.fzzyhmstrs.amethyst_imbuement.scepter.base_augments.ScepterAugment
import me.fzzyhmstrs.amethyst_imbuement.util.ImbuingRecipeFormat
import me.fzzyhmstrs.amethyst_imbuement.util.ScepterObject
import me.fzzyhmstrs.imbuing_automation_tool.IAT
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.io.File
import java.util.*
import kotlin.math.max

object ImbuingRecipeGenerator {

    val command: Command<ServerCommandSource> = Command<ServerCommandSource> { generateRecipes(); 1 }
    private val gson = GsonBuilder().setPrettyPrinting().create()

    private fun generateRecipes(){

        var generatedRecipes = 0

        Registry.ENCHANTMENT.ids.forEach { id: Identifier ->
            val enchant = Registry.ENCHANTMENT[id]
            if (enchant != null){
                if (enchant !is BaseAugment && enchant !is ScepterAugment) {
                    val target = enchant.type
                    val itemList: MutableList<Item> = mutableListOf()
                    val gemList: Pair<Pair<Item, Item>,Item> = try {
                        itemList.addAll(ItemLists.valueOf(target.name).itemList())
                        ItemLists.valueOf(target.name).gemList()
                    } catch(e: Exception) {
                        println(e.message)
                        itemList.addAll(ItemLists.GENERIC.itemList())
                        ItemLists.GENERIC.gemList()
                    }

                    val weight = enchant.rarity.weight
                    val maxLevel = enchant.maxLevel
                    val maxModLevel = enchant.getMaxPower(maxLevel)
                    val decision = decider(weight,maxModLevel,maxLevel)

                    val rnd = IAT.iatRandom.nextInt(2)
                    val rnd2 = IAT.iatRandom.nextInt(4)
                    val gemPair = GemPair(rnd,gemList.first)
                    val gem1 = gemPair.gem
                    val gem2 =
                        if (decision.second == 2){
                            gemList.second
                        } else {
                            if (rnd2 == 0){
                                gemPair.other
                            } else {
                                null
                            }
                        }

                    val cost = decision.first * decision.second

                    val rnd3 = IAT.iatRandom.nextInt(itemList.size)
                    val component1 = itemList[rnd3]

                    val recipe = if (decision.first == 8){
                        val rnd4 = IAT.iatRandom.nextInt(2)
                        val component2 = if (rnd4 == 0){
                            null
                        } else {
                            val rnd5 = IAT.iatRandom.nextInt(itemList.size)
                            itemList[rnd5]
                        }
                        println(component2)
                        writeEightIngredientRecipe(id,cost,component1,component2,gem1,gem2)
                    } else {
                        writeFourIngredientRecipe(id,cost,component1,gem1,gem2)
                    }

                    if (writeToJson(id.path+".json",recipe)){
                        generatedRecipes++
                    }

                }
            }
        }
        MinecraftClient.getInstance().player?.sendChatMessage("Generated $generatedRecipes imbuing recipes into the config folder.")
    }

    private fun writeToJson(fileName: String, recipe: ImbuingRecipeFormat): Boolean{
        val dir = File(File(FabricLoader.getInstance().configDir.toFile(), "amethyst_imbuement"),"imbuing_recipes")
        if (!dir.exists() && !dir.mkdirs()) {
            println("Couldn't make config directory!")
        }
        val f = File(dir,fileName)
        return try{
            if (!f.createNewFile()){
                println("Couldn't generate new imbuing recipe or already exists: $fileName")
                false
            } else {
                f.writeText(gson.toJson(recipe))
                true
            }
        } catch (e: Exception) {
            println("Failed to write file: $fileName")
            false
        }
    }

    private fun writeFourIngredientRecipe(id: Identifier,cost: Int, component1: Item, gem1: Item, gem2: Item?): ImbuingRecipeFormat{
        val recipeFormat = ImbuingRecipeFormat()
        if(gem2 != null){
            recipeFormat.imbueA = Ingredient.ofItems(gem2).toJson().asJsonObject
        } else {
            recipeFormat.imbueA = Ingredient.ofItems(gem1).toJson().asJsonObject
        }
        recipeFormat.imbueB = Ingredient.ofItems(gem1).toJson().asJsonObject
        recipeFormat.imbueC = Ingredient.ofItems(gem1).toJson().asJsonObject
        recipeFormat.imbueD = Ingredient.ofItems(gem1).toJson().asJsonObject

        recipeFormat.craftB = Ingredient.ofItems(component1).toJson().asJsonObject
        recipeFormat.craftD = Ingredient.ofItems(component1).toJson().asJsonObject
        recipeFormat.craftF = Ingredient.ofItems(component1).toJson().asJsonObject
        recipeFormat.craftH = Ingredient.ofItems(component1).toJson().asJsonObject

        recipeFormat.cost = cost

        recipeFormat.augment = id.toString()

        return recipeFormat
    }

    private fun writeEightIngredientRecipe(id: Identifier,cost: Int, component1: Item,component2: Item?, gem1: Item, gem2: Item?): ImbuingRecipeFormat{
        val recipeFormat = ImbuingRecipeFormat()
        if(gem2 != null){
            recipeFormat.imbueA = Ingredient.ofItems(gem2).toJson().asJsonObject
        } else {
            recipeFormat.imbueA = Ingredient.ofItems(gem1).toJson().asJsonObject
        }
        recipeFormat.imbueB = Ingredient.ofItems(gem1).toJson().asJsonObject
        recipeFormat.imbueC = Ingredient.ofItems(gem1).toJson().asJsonObject
        recipeFormat.imbueD = Ingredient.ofItems(gem1).toJson().asJsonObject

        recipeFormat.craftB = Ingredient.ofItems(component1).toJson().asJsonObject
        recipeFormat.craftD = Ingredient.ofItems(component1).toJson().asJsonObject
        recipeFormat.craftF = Ingredient.ofItems(component1).toJson().asJsonObject
        recipeFormat.craftH = Ingredient.ofItems(component1).toJson().asJsonObject

        if (component2 != null){
            recipeFormat.craftA = Ingredient.ofItems(component2).toJson().asJsonObject
            recipeFormat.craftC = Ingredient.ofItems(component2).toJson().asJsonObject
            recipeFormat.craftG = Ingredient.ofItems(component2).toJson().asJsonObject
            recipeFormat.craftI = Ingredient.ofItems(component2).toJson().asJsonObject
        } else {
            recipeFormat.craftA = Ingredient.ofItems(component1).toJson().asJsonObject
            recipeFormat.craftC = Ingredient.ofItems(component1).toJson().asJsonObject
            recipeFormat.craftG = Ingredient.ofItems(component1).toJson().asJsonObject
            recipeFormat.craftI = Ingredient.ofItems(component1).toJson().asJsonObject
        }

        recipeFormat.cost = cost

        recipeFormat.augment = id.toString()

        return recipeFormat
    }


    private fun decider(weight: Int, maxModifiedLevels: Int, maxLevels: Int): Pair<Int,Int>{
        val weightFactor = 1/weight.toFloat()
        val levelsPerLevelFactor = max(1.0F,(maxModifiedLevels.toFloat()/maxLevels.toFloat()) / 75F)
        val maxLevelFactor = 1/maxLevels.toFloat()

        val ingredientDecider = ((weightFactor * 60) + (levelsPerLevelFactor * 30) + (maxLevelFactor * 10)) / 100F
        val tierDecider = ((weightFactor * 20) + (levelsPerLevelFactor * 30) + (maxLevelFactor * 50)) / 100F

        val first = if (ingredientDecider > 0.5F){
            8
        } else {
            4
        }

        val second = if (tierDecider > 0.5F){
            2
        } else {
            1
        }

        return Pair(first,second)
    }


    private val furyGemList: Pair<Pair<Item,Item>,Item> = Pair(Pair(RegisterItem.CITRINE,Items.AMETHYST_SHARD),RegisterItem.GARNET)
    private val witGemList: Pair<Pair<Item,Item>,Item> = Pair(Pair(RegisterItem.DANBURITE,RegisterItem.IMBUED_LAPIS),RegisterItem.PYRITE)
    private val graceGemList: Pair<Pair<Item,Item>,Item> = Pair(Pair(RegisterItem.IMBUED_QUARTZ,RegisterItem.SMOKY_QUARTZ),RegisterItem.MOONSTONE)

    private enum class ItemLists{
        ARMOR{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.IRON_INGOT,
                    RegisterItem.STEEL_INGOT,
                    Items.OBSIDIAN,
                    Items.COBBLESTONE,
                    Items.GRANITE,
                    Items.SHIELD,
                    Items.GOLDEN_APPLE,
                    Items.GHAST_TEAR,
                    Items.BRICK,
                    Items.BONE_BLOCK
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return graceGemList
            }
        },
        ARMOR_FEET{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.IRON_INGOT,
                    RegisterItem.BERYL_COPPER_INGOT,
                    Items.SLIME_BALL,
                    Items.REDSTONE_BLOCK,
                    Items.BAMBOO,
                    Items.WHITE_WOOL,
                    Items.GLISTERING_MELON_SLICE,
                    Items.RABBIT_HIDE
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return witGemList
            }
        },
        ARMOR_LEGS{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.IRON_INGOT,
                    RegisterItem.STEEL_INGOT,
                    RegisterItem.BERYL_COPPER_INGOT,
                    Items.REDSTONE_BLOCK,
                    Items.OBSIDIAN,
                    Items.GHAST_TEAR,
                    Items.BRICK,
                    Items.BONE_BLOCK
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return graceGemList
            }
        },
        ARMOR_CHEST{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.IRON_INGOT,
                    RegisterItem.STEEL_INGOT,
                    Items.OBSIDIAN,
                    Items.COBBLESTONE,
                    Items.GRANITE,
                    Items.ACACIA_LOG,
                    Items.BEEF,
                    Items.GOLDEN_APPLE,
                    Items.BONE_BLOCK
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return graceGemList
            }
        },
        ARMOR_HEAD{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.IRON_INGOT,
                    Items.GLOWSTONE,
                    Items.CRYING_OBSIDIAN,
                    Items.BAMBOO,
                    Items.LEATHER,
                    Items.ENDER_EYE,
                    Items.GLISTERING_MELON_SLICE,
                    Items.GLOW_INK_SAC
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return witGemList
            }
        },
        WEAPON{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.QUARTZ,
                    Items.IRON_INGOT,
                    RegisterItem.BERYL_COPPER_INGOT,
                    Items.FIRE_CHARGE,
                    Items.BLAZE_POWDER,
                    Items.IRON_SWORD,
                    Items.FLINT,
                    Items.MAGMA_BLOCK,
                    RegisterItem.STEEL_INGOT,
                    Items.GOLDEN_CARROT
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return furyGemList
            }
        },
        DIGGER{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.AMETHYST_SHARD,
                    Items.IRON_INGOT,
                    Items.REDSTONE_BLOCK,
                    Items.EXPERIENCE_BOTTLE,
                    Items.DIAMOND,
                    Items.BOOK,
                    Items.GLISTERING_MELON_SLICE
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return witGemList
            }
        },
        FISHING_ROD{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.PUFFERFISH,
                    Items.PRISMARINE_SHARD,
                    Items.RABBIT_FOOT,
                    Items.BAMBOO,
                    Items.SEA_PICKLE,
                    Items.GLISTERING_MELON_SLICE
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return witGemList
            }
        },
        TRIDENT{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.EMERALD,
                    Items.PRISMARINE_SHARD,
                    Items.NAUTILUS_SHELL,
                    RegisterItem.BERYL_COPPER_INGOT,
                    Items.FLINT,
                    Items.SEA_PICKLE,
                    Items.PACKED_ICE
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return furyGemList
            }
        },
        BREAKABLE{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.OBSIDIAN,
                    Items.IRON_INGOT,
                    RegisterItem.STEEL_INGOT,
                    Items.DIAMOND,
                    Items.SOUL_SAND,
                    Items.CHAIN
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return graceGemList
            }
        },
        BOW{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.FLINT,
                    RegisterItem.CITRINE,
                    Items.QUARTZ,
                    Items.DIAMOND,
                    Items.ARROW,
                    Items.SPECTRAL_ARROW,
                    Items.FIRE_CHARGE,
                    Items.BLAZE_POWDER,
                    Items.ENDER_PEARL,
                    Items.GOLDEN_CARROT,
                    Items.TARGET
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return furyGemList
            }
        },
        WEARABLE{
            override fun itemList(): List<Item> {
                return listOf(Items.IRON_INGOT,
                    RegisterItem.STEEL_INGOT,
                    Items.OBSIDIAN,
                    Items.COBBLESTONE,
                    Items.GRANITE,
                    Items.SHIELD,
                    Items.GOLDEN_APPLE,
                    Items.GHAST_TEAR,
                    Items.BRICK,
                    Items.BONE_BLOCK,
                    Items.RABBIT_FOOT,
                    Items.GOLDEN_CARROT,
                    Items.GLISTERING_MELON_SLICE,
                    Items.GOLD_INGOT,
                    Items.DIAMOND,
                    Items.EMERALD,
                    RegisterItem.BERYL_COPPER_INGOT,
                    RegisterItem.IMBUED_LAPIS,
                    RegisterItem.SMOKY_QUARTZ
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                val list = when(Random(124).nextInt(3)){
                    0 -> {
                        furyGemList}
                    1 -> {
                        witGemList}
                    2 -> {
                        graceGemList}
                    else -> {
                        furyGemList}
                }
                return list
            }
        },
        CROSSBOW{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.FLINT,
                    RegisterItem.CITRINE,
                    Items.QUARTZ,
                    RegisterItem.BERYL_COPPER_INGOT,
                    Items.ARROW,
                    Items.MAGMA_CREAM,
                    Items.BLAZE_ROD,
                    Items.ENDER_PEARL,
                    Items.GOLDEN_CARROT,
                    Items.TARGET
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return furyGemList
            }
        },
        VANISHABLE{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.OBSIDIAN,
                    Items.IRON_INGOT,
                    RegisterItem.STEEL_INGOT,
                    Items.DIAMOND,
                    Items.GHAST_TEAR
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return graceGemList
            }
        },
        GENERIC{
            override fun itemList(): List<Item> {
                return listOf(
                    Items.STICK
                )
            }
            override fun gemList(): Pair<Pair<Item,Item>, Item> {
                return witGemList
            }
        };


        abstract fun itemList(): List<Item>
        abstract fun gemList(): Pair<Pair<Item,Item>,Item>
    }

    private class GemPair(rnd: Int, gemList: Pair<Item,Item>){
        val gem: Item
        val other: Item

        init {
            if (rnd == 0){
                gem = gemList.first
                other = gemList.second
            } else {
                gem = gemList.second
                other = gemList.first
            }
        }
    }

}