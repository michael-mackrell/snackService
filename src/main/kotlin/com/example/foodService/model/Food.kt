package com.example.foodService.model

import java.util.UUID

open class Food(
	uuid: UUID = UUID.randomUUID(),
	val imageId: UUID = UUID.randomUUID(),
	val name: String,
	val calories: Int,
	val protein: Double,
	val carbs: Double,
	val fat: Double,
	val tasteRating: Int,
) : Object(uuid) {
	init {
		require(tasteRating in 1..5) { "tasteRating must be between 1 and 5" }
	}
}
