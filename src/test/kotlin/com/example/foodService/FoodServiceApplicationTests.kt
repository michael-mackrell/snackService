package com.example.foodService

import com.example.foodService.repository.FoodCatalogRepository
import com.example.foodService.repository.InventoryFoodRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest
class FoodServiceApplicationTests {

	@MockitoBean
	private lateinit var foodCatalogRepository: FoodCatalogRepository

	@MockitoBean
	private lateinit var inventoryFoodRepository: InventoryFoodRepository

	@Test
	fun contextLoads() {
	}

}
