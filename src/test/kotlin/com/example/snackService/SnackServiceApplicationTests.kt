package com.example.snackService

import com.example.snackService.repository.FoodCatalogRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest
class SnackServiceApplicationTests {

	@MockitoBean
	private lateinit var foodCatalogRepository: FoodCatalogRepository

	@Test
	fun contextLoads() {
	}

}
