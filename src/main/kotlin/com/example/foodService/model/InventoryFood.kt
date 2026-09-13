package com.example.foodService.model

import java.util.UUID

class InventoryFood(
	uuid: UUID,
	imageId: UUID,
	name: String,
	calories: Int,
	protein: Double,
	carbs: Double,
	fat: Double,
	tasteRating: Int,
	val quantity: Int,
) : Food(
	uuid = uuid,
	imageId = imageId,
	name = name,
	calories = calories,
	protein = protein,
	carbs = carbs,
	fat = fat,
	tasteRating = tasteRating,
) {
	init {
		require(quantity > 0) { "quantity must be greater than 0" }
	}
}
