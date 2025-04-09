package com.thenolle.api.nollyapi.util.recipes

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

/**
 * DSL for building and registering recipes in a Minecraft plugin.
 *
 * This file contains a set of helper functions to make it easy to create and manage recipes using a DSL approach.
 * Recipes can be created, modified, and removed with a fluent, readable syntax.
 */

/**
 * Creates and returns a shaped recipe with a 3x3 grid.
 *
 * @param key The unique key for the recipe.
 * @param output The resulting item when the recipe is completed.
 * @param grid A list of 3 strings representing the crafting grid (rows).
 * @param outputCount The number of items produced from the crafting process.
 * @param block The lambda to define the ingredient map for the recipe.
 */
fun shaped(
    key: NamespacedKey,
    output: ItemStack,
    grid: List<String>,
    outputCount: Int? = null,
    block: RecipeIngredientBuilder.() -> Unit,
): ShapedRecipe {
    val builder = RecipeIngredientBuilder()
    builder.block()
    val recipe = RecipeManager.createShapedRecipe(key, output, grid, builder.ingredients, outputCount)
    return recipe
}

// Extension function to register the shaped recipe
fun ShapedRecipe.register() = registerRecipe(this)

/**
 * Creates and returns a shapeless recipe.
 *
 * @param key The unique key for the recipe.
 * @param output The resulting item when the recipe is completed.
 * @param ingredients A list of ingredients (items) used for crafting the output item.
 * @param outputCount The number of items produced from the crafting process.
 */
fun shapeless(
    key: NamespacedKey, output: ItemStack, ingredients: List<ItemStack>, outputCount: Int? = null
): ShapelessRecipe {
    val recipe = RecipeManager.createShapelessRecipe(key, output, ingredients, outputCount)
    return recipe
}

// Extension function to register the shapeless recipe
fun ShapelessRecipe.register() = registerRecipe(this)

/**
 * Creates and returns a furnace smelting recipe.
 *
 * @param key The unique key for the recipe.
 * @param input The item to be smelted.
 * @param output The resulting smelted item.
 * @param experience The experience awarded for this recipe.
 * @param cookingTime The time in ticks it takes for the smelting process.
 * @param outputCount The number of items produced from the smelting process.
 */
fun smelting(
    key: NamespacedKey,
    input: ItemStack,
    output: ItemStack,
    experience: Float,
    cookingTime: Int,
    outputCount: Int? = null
): FurnaceRecipe {
    val recipe = RecipeManager.createFurnaceRecipe(key, input, output, experience, cookingTime, outputCount)
    return recipe
}

// Extension function to register the furnace recipe
fun FurnaceRecipe.register() = registerRecipe(this)

/**
 * Creates and returns a blast furnace smelting recipe.
 *
 * @param key The unique key for the recipe.
 * @param input The item to be smelted.
 * @param output The resulting smelted item.
 * @param experience The experience awarded for this recipe.
 * @param cookingTime The time in ticks it takes for the smelting process.
 * @param outputCount The number of items produced from the smelting process.
 */
fun blastFurnace(
    key: NamespacedKey,
    input: ItemStack,
    output: ItemStack,
    experience: Float,
    cookingTime: Int,
    outputCount: Int? = null
): BlastingRecipe {
    val recipe = RecipeManager.createBlastingRecipe(key, input, output, experience, cookingTime, outputCount)
    return recipe
}

// Extension function to register the blast furnace recipe
fun BlastingRecipe.register() = registerRecipe(this)

/**
 * Creates and returns a smoker cooking recipe.
 *
 * @param key The unique key for the recipe.
 * @param input The item to be cooked.
 * @param output The resulting cooked item.
 * @param experience The experience awarded for this recipe.
 * @param cookingTime The time in ticks it takes for the cooking process.
 * @param outputCount The number of items produced from the cooking process.
 */
fun smoker(
    key: NamespacedKey,
    input: ItemStack,
    output: ItemStack,
    experience: Float,
    cookingTime: Int,
    outputCount: Int? = null
): SmokingRecipe {
    val recipe = RecipeManager.createSmokerRecipe(key, input, output, experience, cookingTime, outputCount)
    return recipe
}

// Extension function to register the smoker recipe
fun SmokingRecipe.register() = registerRecipe(this)

/**
 * Creates and returns a campfire cooking recipe.
 *
 * @param key The unique key for the recipe.
 * @param input The item to be cooked.
 * @param output The resulting cooked item.
 * @param experience The experience awarded for this recipe.
 * @param cookingTime The time in ticks it takes for the cooking process.
 * @param outputCount The number of items produced from the cooking process.
 */
fun campfire(
    key: NamespacedKey,
    input: ItemStack,
    output: ItemStack,
    experience: Float,
    cookingTime: Int,
    outputCount: Int? = null
): CampfireRecipe {
    val recipe = RecipeManager.createCampfireRecipe(key, input, output, experience, cookingTime, outputCount)
    return recipe
}

// Extension function to register the campfire recipe
fun CampfireRecipe.register() = registerRecipe(this)

/**
 * Creates and returns a smithing transformation recipe.
 *
 * @param key The unique key for the recipe.
 * @param output The resulting item after transformation.
 * @param template The template item used in the transformation.
 * @param input The input item to be transformed.
 * @param addition An additional item used in the transformation.
 * @param outputCount The number of items produced from the transformation process.
 */
fun smithingTransform(
    key: NamespacedKey,
    output: ItemStack,
    template: ItemStack,
    input: ItemStack,
    addition: ItemStack,
    outputCount: Int? = null
): SmithingTransformRecipe {
    val recipe = RecipeManager.createSmithingTransformRecipe(key, output, template, input, addition, outputCount)
    return recipe
}

// Extension function to register the smithing transformation recipe
fun SmithingTransformRecipe.register() = registerRecipe(this)

/**
 * Creates and returns a stonecutting recipe.
 *
 * @param key The unique key for the recipe.
 * @param input The item to be cut.
 * @param output The resulting cut item.
 * @param outputCount The number of items produced from the stonecutting process.
 */
fun stonecutting(
    key: NamespacedKey, input: ItemStack, output: ItemStack, outputCount: Int? = null
): StonecuttingRecipe {
    val recipe = RecipeManager.createStonecuttingRecipe(key, input, output, outputCount)
    return recipe
}

// Extension function to register the stonecutting recipe
fun StonecuttingRecipe.register() = registerRecipe(this)

/**
 * Retrieves a recipe based on its key.
 *
 * @param key The unique key of the recipe to retrieve.
 */
fun getRecipe(key: NamespacedKey): Recipe? = Bukkit.getRecipe(key)

/**
 * Registers a new recipe.
 *
 * @param recipe The new recipe to register.
 */
fun registerRecipe(recipe: Recipe) = RecipeManager.registerRecipe(recipe)

/**
 * Replaces an existing recipe with a new one using the same key.
 *
 * @param key The unique key for the recipe to be replaced.
 * @param newRecipe The new recipe to replace the existing one.
 */
fun replaceRecipe(key: NamespacedKey, newRecipe: Recipe) = RecipeManager.replaceRecipe(key, newRecipe)

/**
 * Removes a recipe based on its key.
 *
 * @param key The unique key of the recipe to be removed.
 */
fun removeRecipe(key: NamespacedKey) = RecipeManager.removeRecipe(key)

/**
 * A helper class to manage recipe ingredients.
 *
 * This class allows users to define the ingredients for shaped recipes in a flexible and readable way.
 */
class RecipeIngredientBuilder {
    val ingredients = mutableMapOf<String, ItemStack>()

    /**
     * Adds an ingredient to the recipe.
     *
     * @param position The position in the crafting grid (e.g., "A1").
     * @param item The item to use as an ingredient.
     */
    fun ingredient(position: String, item: ItemStack) = apply { ingredients[position] = item }
}