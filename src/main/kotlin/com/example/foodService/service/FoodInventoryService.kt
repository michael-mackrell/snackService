package com.example.foodService.service

import com.example.foodService.document.toInventoryFood
import com.example.foodService.document.toInventoryFoodDocument
import com.example.foodService.model.InventoryFood
import com.example.foodService.repository.FoodCatalogRepository
import com.example.foodService.repository.InventoryFoodRepository
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class FoodInventoryService(
	private val inventoryFoodRepository: InventoryFoodRepository,
	private val foodCatalogRepository: FoodCatalogRepository,
) {

	fun getAllFoods(): List<InventoryFood> =
		inventoryFoodRepository.findAll().map { it.toInventoryFood() }

	fun getFood(uuid: UUID): InventoryFood =
		findInventoryFood(uuid).toInventoryFood()

	fun addFood(uuid: UUID): InventoryFood {
		val existing = inventoryFoodRepository.findById(uuid)
		val updated = if (existing.isPresent) {
			existing.get().copy(quantity = existing.get().quantity + 1)
		} else {
			val catalogFood = foodCatalogRepository.findById(uuid).orElseThrow {
				ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog entry not found")
			}
			catalogFood.toInventoryFoodDocument()
		}

		return inventoryFoodRepository.save(updated).toInventoryFood()
	}

	fun removeFood(uuid: UUID) {
		val existing = findInventoryFood(uuid)
		if (existing.quantity == 1) {
			inventoryFoodRepository.deleteById(uuid)
		} else {
			inventoryFoodRepository.save(existing.copy(quantity = existing.quantity - 1))
		}
	}

	private fun findInventoryFood(uuid: UUID) =
		inventoryFoodRepository.findById(uuid).orElseThrow {
			ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory food not found")
		}
}
