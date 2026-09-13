package com.example.foodService.model

import java.util.UUID

class InventoryFood(
	uuid: UUID,
	imageId: UUID,
	name: String,
	tasteRating: Int,
	val quantity: Int,
) : Food(
	uuid = uuid,
	imageId = imageId,
	name = name,
	tasteRating = tasteRating,
) {
	init {
		require(quantity > 0) { "quantity must be greater than 0" }
	}
}
