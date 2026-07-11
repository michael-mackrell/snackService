package com.example.snackService.service

import com.example.snackService.dto.CreateFoodRequest
import com.example.snackService.dto.UpdateFoodRequest
import com.example.snackService.model.Food
import com.example.snackService.model.FoodCatalog
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class FoodCatalogService {

	private val catalog = FoodCatalog(name = "Default Catalog")

	fun addFood(request: CreateFoodRequest): Food {
		val food = Food(
			imageId = request.imageId ?: UUID.randomUUID(),
			name = request.name,
			calories = request.calories,
			protein = request.protein,
			carbs = request.carbs,
			fat = request.fat,
			tasteRating = request.tasteRating,
		)
		return catalog.addFood(food)
	}

	fun getAllFoods(): List<Food> = catalog.getAllFoods()

	fun updateFood(uuid: UUID, request: UpdateFoodRequest): Food {
		val existing = catalog.getFood(uuid)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found")
		val updated = Food(
			uuid = uuid,
			imageId = request.imageId ?: existing.imageId,
			name = request.name,
			calories = request.calories,
			protein = request.protein,
			carbs = request.carbs,
			fat = request.fat,
			tasteRating = request.tasteRating,
		)
		return catalog.updateFood(updated)
	}

	fun deleteFood(uuid: UUID) {
		if (!catalog.deleteFood(uuid)) {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found")
		}
	}
}
