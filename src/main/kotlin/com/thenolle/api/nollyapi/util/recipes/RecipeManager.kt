package com.thenolle.api.nollyapi.util.recipes

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

/**
 * The main manager for registering and handling recipes within the plugin.
 *
 * This class provides utility methods for adding, editing, and removing recipes of different types, such as:
 * - Shaped Recipes
 * - Shapeless Recipes
 * - Smelting Recipes (Furnace, Blast Furnace, Campfire, etc.)
 *
 * The RecipeManager allows for seamless interaction with Minecraft's recipe system, providing an intuitive
 * and easy-to-use API for managing recipes directly through code. It supports a DSL-like builder approach for
 * crafting new recipes, editing them, or removing them.
 *
 * @constructor Creates a new RecipeManager instance, registering the plugin with the Bukkit server.
 */
object RecipeManager {
    /**
     * Creates a shaped recipe.
     *
     * This method allows you to create a shaped crafting recipe with a specified 3x3 grid and output.
     *
     * @param key The unique key for this recipe.
     * @param output The output item.
     * @param grid A list of 3 strings representing the crafting grid. E.g., ["XXX", "XYX", "XXX"].
     * @param ingredients A map of ingredient positions (A1, A2, ..., F3) to `ItemStack` ingredients.
     * @param outputCount The number of items produced from the crafting process.
     */
    fun createShapedRecipe(
        key: NamespacedKey,
        output: ItemStack,
        grid: List<String>,
        ingredients: Map<String, ItemStack>,
        outputCount: Int? = null
    ): ShapedRecipe {
        val outputWithCount = output.clone().apply { amount = outputCount ?: 1 }
        val recipe = ShapedRecipe(key, outputWithCount)
        recipe.shape(*grid.toTypedArray())
        ingredients.forEach { (position, item) -> recipe.setIngredient(position[0], item.type) }
        return recipe
    }

    /**
     * Creates a shapeless recipe.
     *
     * This method allows you to create a shapeless crafting recipe where items can be placed in any order.
     *
     * @param key The unique key for this recipe.
     * @param output The output item.
     * @param ingredients A list of ingredients (items) used for crafting the output item.
     * @param outputCount The number of items produced from the crafting process.
     */
    fun createShapelessRecipe(
        key: NamespacedKey, output: ItemStack, ingredients: List<ItemStack>, outputCount: Int? = null
    ): ShapelessRecipe {
        val outputWithCount = output.clone().apply { amount = outputCount ?: 1 }
        val recipe = ShapelessRecipe(key, outputWithCount)
        ingredients.forEach { recipe.addIngredient(it.type) }
        return recipe
    }

    /**
     * Creates a furnace smelting recipe.
     *
     * This method allows you to create a furnace smelting recipe.
     *
     * @param key The unique key for this recipe.
     * @param input The item to be smelted.
     * @param output The resulting smelted item.
     * @param experience The experience awarded for this recipe.
     * @param cookingTime The time in ticks it takes for the smelting process.
     * @param outputCount The number of items produced from the smelting process.
     */
    fun createFurnaceRecipe(
        key: NamespacedKey,
        input: ItemStack,
        output: ItemStack,
        experience: Float,
        cookingTime: Int,
        outputCount: Int? = null
    ): FurnaceRecipe {
        val outputWithCount = output.clone().apply { amount = outputCount ?: 1 }
        return FurnaceRecipe(key, outputWithCount, input.type, experience, cookingTime)
    }

    /**
     * Creates a blast furnace smelting recipe.
     *
     * This method allows you to create a blast furnace smelting recipe.
     *
     * @param key The unique key for this recipe.
     * @param input The item to be smelted.
     * @param output The resulting smelted item.
     * @param experience The experience awarded for this recipe.
     * @param cookingTime The time in ticks it takes for the smelting process.
     * @param outputCount The number of items produced from the smelting process.
     */
    fun createBlastingRecipe(
        key: NamespacedKey,
        input: ItemStack,
        output: ItemStack,
        experience: Float,
        cookingTime: Int,
        outputCount: Int? = null
    ): BlastingRecipe {
        val outputWithCount = output.clone().apply { amount = outputCount ?: 1 }
        return BlastingRecipe(key, outputWithCount, input.type, experience, cookingTime)
    }

    /**
     * Creates a smoker cooking recipe.
     *
     * This method allows you to create a smoker cooking recipe.
     *
     * @param key The unique key for this recipe.
     * @param input The item to be cooked.
     * @param output The resulting cooked item.
     * @param experience The experience awarded for this recipe.
     * @param cookingTime The time in ticks it takes for the cooking process.
     * @param outputCount The number of items produced from the cooking process.
     */
    fun createSmokerRecipe(
        key: NamespacedKey,
        input: ItemStack,
        output: ItemStack,
        experience: Float,
        cookingTime: Int,
        outputCount: Int? = null
    ): SmokingRecipe {
        val outputWithCount = output.clone().apply { amount = outputCount ?: 1 }
        return SmokingRecipe(key, outputWithCount, input.type, experience, cookingTime)
    }

    /**
     * Creates a campfire cooking recipe.
     *
     * This method allows you to create a campfire cooking recipe.
     *
     * @param key The unique key for this recipe.
     * @param input The item to be cooked.
     * @param output The resulting cooked item.
     * @param experience The experience awarded for this recipe.
     * @param cookingTime The time in ticks it takes for the cooking process.
     * @param outputCount The number of items produced from the cooking process.
     */
    fun createCampfireRecipe(
        key: NamespacedKey,
        input: ItemStack,
        output: ItemStack,
        experience: Float,
        cookingTime: Int,
        outputCount: Int? = null
    ): CampfireRecipe {
        val outputWithCount = output.clone().apply { amount = outputCount ?: 1 }
        return CampfireRecipe(key, outputWithCount, input.type, experience, cookingTime)
    }

    /**
     * Creates a smithing transformation recipe.
     *
     * This method allows you to create a smithing transformation recipe.
     *
     * @param key The unique key for this recipe.
     * @param output The resulting item after transformation.
     * @param template The template item used in the transformation.
     * @param input The input item to be transformed.
     * @param addition An additional item used in the transformation.
     */
    fun createSmithingTransformRecipe(
        key: NamespacedKey,
        output: ItemStack,
        template: ItemStack,
        input: ItemStack,
        addition: ItemStack,
        outputCount: Int? = null
    ): SmithingTransformRecipe {
        val outputWithCount = output.clone().apply { amount = outputCount ?: 1 }
        return SmithingTransformRecipe(
            key,
            outputWithCount,
            RecipeChoice.MaterialChoice(template.type),
            RecipeChoice.MaterialChoice(input.type),
            RecipeChoice.MaterialChoice(addition.type)
        )
    }

    /**
     * Creates a stonecutting recipe.
     *
     * This method allows you to create a stonecutting recipe.
     *
     * @param key The unique key for this recipe.
     * @param input The item to be cut.
     * @param output The resulting cut item.
     * @param outputCount The number of items produced from the stonecutting process.
     */
    fun createStonecuttingRecipe(
        key: NamespacedKey, input: ItemStack, output: ItemStack, outputCount: Int? = null
    ): StonecuttingRecipe {
        val outputWithCount = output.clone().apply { amount = outputCount ?: 1 }
        return StonecuttingRecipe(key, outputWithCount, input.type)
    }

    /**
     * Creates a custom recipe.
     *
     * This method allows you to create a custom recipe directly.
     *
     * @param recipe The custom recipe to be registered.
     */
    fun registerRecipe(recipe: Recipe) {
        Bukkit.addRecipe(recipe)
    }

    /**
     * Replaces an existing recipe with a new one.
     *
     * This method allows you to replace an existing recipe with a new one using the same key.
     *
     * @param key The unique key of the recipe to be replaced.
     * @param newRecipe The new recipe to replace the existing one.
     */
    fun replaceRecipe(key: NamespacedKey, newRecipe: Recipe) {
        removeRecipe(key)
        registerRecipe(newRecipe)
    }

    /**
     * Removes a recipe based on its key.
     *
     * This method allows you to remove a recipe from the server by its unique key.
     *
     * @param key The unique key of the recipe to be removed.
     */
    fun removeRecipe(key: NamespacedKey) {
        Bukkit.removeRecipe(key)
    }
}