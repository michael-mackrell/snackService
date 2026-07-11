package com.example.snackService.model

import java.util.UUID

class FoodCatalog(
	uuid: UUID = UUID.randomUUID(),
	val name: String,
	val foods: MutableMap<UUID, Food> = mutableMapOf(),
) : Object(uuid) {

	fun addFood(food: Food): Food {
		foods[food.uuid] = food
		return food
	}

	fun getFood(uuid: UUID): Food? = foods[uuid]

	fun getAllFoods(): List<Food> = foods.values.toList()

	fun updateFood(food: Food): Food {
		foods[food.uuid] = food
		return food
	}

	fun deleteFood(uuid: UUID): Boolean = foods.remove(uuid) != null
}
