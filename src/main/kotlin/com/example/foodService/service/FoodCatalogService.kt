package com.example.foodService.service

import com.example.foodService.document.FoodCatalogDocument
import com.example.foodService.document.toFood
import com.example.foodService.dto.CreateFoodRequest
import com.example.foodService.dto.UpdateFoodRequest
import com.example.foodService.model.Food
import com.example.foodService.repository.FoodCatalogRepository
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class FoodCatalogService(
	private val foodCatalogRepository: FoodCatalogRepository,
	private val foodImageService: FoodImageService,
) {

	fun addEntry(request: CreateFoodRequest): Food {
		val entry = FoodCatalogDocument(
			name = request.name,
			category = request.category,
			tasteRating = request.tasteRating,
		)
		return foodCatalogRepository.save(entry).toFood()
	}

	fun getAllEntries(sort: String? = null): List<Food> {
		val entries = when (sort) {
			null -> foodCatalogRepository.findAll()
			"category" -> foodCatalogRepository.findAll(
				Sort.by(Sort.Order.asc("category").ignoreCase(), Sort.Order.asc("name").ignoreCase()),
			)
			else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported sort option: $sort")
		}
		return entries.map { it.toFood() }
	}

	fun updateEntry(uuid: UUID, request: UpdateFoodRequest): Food {
		val existing = foodCatalogRepository.findById(uuid).orElseThrow {
			ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog entry not found")
		}
		val updated = existing.copy(
			name = request.name,
			tasteRating = request.tasteRating,
		)
		return foodCatalogRepository.save(updated).toFood()
	}

	fun deleteEntry(uuid: UUID) {
		if (!foodCatalogRepository.existsById(uuid)) {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog entry not found")
		}
		foodImageService.delete(uuid)
		foodCatalogRepository.deleteById(uuid)
	}
}
