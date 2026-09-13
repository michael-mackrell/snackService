package com.example.foodService.service

import com.example.foodService.document.FoodCatalogDocument
import com.example.foodService.document.InventoryFoodDocument
import com.example.foodService.repository.FoodCatalogRepository
import com.example.foodService.repository.InventoryFoodRepository
import java.util.Optional
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class FoodInventoryServiceTests {

	private val inventoryRepository = mock(InventoryFoodRepository::class.java)
	private val catalogRepository = mock(FoodCatalogRepository::class.java)
	private val service = FoodInventoryService(inventoryRepository, catalogRepository)
	private val foodId = UUID.randomUUID()
	private val imageId = UUID.randomUUID()

	@Test
	fun `adds a new catalog food with quantity one`() {
		val catalogFood = catalogFood()
		val inventoryFood = inventoryFood(quantity = 1)
		`when`(inventoryRepository.findById(foodId)).thenReturn(Optional.empty())
		`when`(catalogRepository.findById(foodId)).thenReturn(Optional.of(catalogFood))
		`when`(inventoryRepository.save(inventoryFood)).thenReturn(inventoryFood)

		val result = service.addFood(foodId)

		assertEquals(1, result.quantity)
		verify(inventoryRepository).save(inventoryFood)
	}

	@Test
	fun `increments an existing inventory food`() {
		val existing = inventoryFood(quantity = 2)
		val incremented = existing.copy(quantity = 3)
		`when`(inventoryRepository.findById(foodId)).thenReturn(Optional.of(existing))
		`when`(inventoryRepository.save(incremented)).thenReturn(incremented)

		val result = service.addFood(foodId)

		assertEquals(3, result.quantity)
	}

	@Test
	fun `decrements an inventory food with multiple units`() {
		val existing = inventoryFood(quantity = 2)
		val decremented = existing.copy(quantity = 1)
		`when`(inventoryRepository.findById(foodId)).thenReturn(Optional.of(existing))
		`when`(inventoryRepository.save(decremented)).thenReturn(decremented)

		service.removeFood(foodId)

		verify(inventoryRepository).save(decremented)
	}

	@Test
	fun `deletes an inventory food when its last unit is removed`() {
		`when`(inventoryRepository.findById(foodId))
			.thenReturn(Optional.of(inventoryFood(quantity = 1)))

		service.removeFood(foodId)

		verify(inventoryRepository).deleteById(foodId)
	}

	private fun catalogFood() = FoodCatalogDocument(
		uuid = foodId,
		imageId = imageId,
		name = "Almonds",
		calories = 160,
		protein = 6.0,
		carbs = 6.0,
		fat = 14.0,
		tasteRating = 4,
	)

	private fun inventoryFood(quantity: Int) = InventoryFoodDocument(
		uuid = foodId,
		imageId = imageId,
		name = "Almonds",
		calories = 160,
		protein = 6.0,
		carbs = 6.0,
		fat = 14.0,
		tasteRating = 4,
		quantity = quantity,
	)
}
