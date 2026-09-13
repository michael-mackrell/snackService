package com.example.foodService.service

import com.example.foodService.document.FoodCatalogDocument
import com.example.foodService.document.toFood
import com.example.foodService.dto.CreateFoodRequest
import com.example.foodService.dto.UpdateFoodRequest
import com.example.foodService.model.Food
import com.example.foodService.repository.FoodCatalogRepository
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class FoodCatalogService(
	private val foodCatalogRepository: FoodCatalogRepository,
) {

	fun addEntry(request: CreateFoodRequest): Food {
		val entry = FoodCatalogDocument(
			imageId = request.imageId ?: UUID.randomUUID(),
			name = request.name,
			tasteRating = request.tasteRating,
		)
		return foodCatalogRepository.save(entry).toFood()
	}

	fun getAllEntries(): List<Food> =
		foodCatalogRepository.findAll().map { it.toFood() }

	fun updateEntry(uuid: UUID, request: UpdateFoodRequest): Food {
		val existing = foodCatalogRepository.findById(uuid).orElseThrow {
			ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog entry not found")
		}
		val updated = existing.copy(
			imageId = request.imageId ?: existing.imageId,
			name = request.name,
			tasteRating = request.tasteRating,
		)
		return foodCatalogRepository.save(updated).toFood()
	}

	fun deleteEntry(uuid: UUID) {
		if (!foodCatalogRepository.existsById(uuid)) {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog entry not found")
		}
		foodCatalogRepository.deleteById(uuid)
	}
}
