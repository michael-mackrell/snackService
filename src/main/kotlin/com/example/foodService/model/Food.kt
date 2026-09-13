package com.example.foodService.model

import java.util.UUID

open class Food(
	uuid: UUID = UUID.randomUUID(),
	val imageId: UUID? = null,
	val name: String,
	val category: String,
	val tasteRating: Int,
) : Object(uuid) {
	init {
		require(tasteRating in 1..5) { "tasteRating must be between 1 and 5" }
	}
}
