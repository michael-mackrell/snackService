package com.example.foodService.model

import java.util.UUID

class InventoryFood(
	uuid: UUID,
	imageId: UUID?,
	name: String,
	category: String,
	tasteRating: Int,
	val quantity: Int,
) : Food(
	uuid = uuid,
	imageId = imageId,
	name = name,
	category = category,
	tasteRating = tasteRating,
) {
	init {
		require(quantity > 0) { "quantity must be greater than 0" }
	}
}
