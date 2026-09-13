package com.example.foodService.document

import com.example.foodService.model.Food
import java.util.UUID
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "food_catalog")
data class FoodCatalogDocument(
	@Id
	val uuid: UUID = UUID.randomUUID(),
	val imageId: UUID = UUID.randomUUID(),
	val name: String,
	val calories: Int,
	val protein: Double,
	val carbs: Double,
	val fat: Double,
	val tasteRating: Int,
) {
	init {
		require(tasteRating in 1..5) { "tasteRating must be between 1 and 5" }
	}
}

fun FoodCatalogDocument.toFood(): Food = Food(
	uuid = uuid,
	imageId = imageId,
	name = name,
	calories = calories,
	protein = protein,
	carbs = carbs,
	fat = fat,
	tasteRating = tasteRating,
)
