package com.example.foodService.document

import com.example.foodService.model.InventoryFood
import java.util.UUID
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

// Keep the legacy collection name so existing inventory remains available.
@Document(collection = "snack_inventory")
data class InventoryFoodDocument(
	@Id
	val uuid: UUID,
	val imageId: UUID,
	val name: String,
	val tasteRating: Int,
	val quantity: Int,
) {
	init {
		require(tasteRating in 1..5) { "tasteRating must be between 1 and 5" }
		require(quantity > 0) { "quantity must be greater than 0" }
	}
}

fun InventoryFoodDocument.toInventoryFood(): InventoryFood = InventoryFood(
	uuid = uuid,
	imageId = imageId,
	name = name,
	tasteRating = tasteRating,
	quantity = quantity,
)

fun FoodCatalogDocument.toInventoryFoodDocument(): InventoryFoodDocument =
	InventoryFoodDocument(
		uuid = uuid,
		imageId = imageId,
		name = name,
		tasteRating = tasteRating,
		quantity = 1,
	)
