package com.example.foodService.service

import com.example.foodService.document.FoodCatalogDocument
import com.example.foodService.dto.CreateFoodRequest
import com.example.foodService.repository.FoodCatalogRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class FoodCatalogServiceTests {
	private val repository = mock(FoodCatalogRepository::class.java)
	private val imageService = mock(FoodImageService::class.java)
	private val service = FoodCatalogService(repository, imageService)

	@Test
	fun `creates a catalog entry with its category`() {
		val request = CreateFoodRequest(name = "Apple", category = "Fruit", tasteRating = 5)
		`when`(repository.save(any(FoodCatalogDocument::class.java))).thenAnswer {
			it.arguments.first() as FoodCatalogDocument
		}

		val result = service.addEntry(request)

		assertEquals("Fruit", result.category)
		val captor = ArgumentCaptor.forClass(FoodCatalogDocument::class.java)
		verify(repository).save(captor.capture())
		assertEquals("Fruit", captor.value.category)
	}

	@Test
	fun `sorts catalog entries by category then name`() {
		val sort = Sort.by(
			Sort.Order.asc("category").ignoreCase(),
			Sort.Order.asc("name").ignoreCase(),
		)
		val entries = listOf(
			FoodCatalogDocument(name = "Apple", category = "Fruit", tasteRating = 5),
			FoodCatalogDocument(name = "Pretzels", category = "Snack", tasteRating = 4),
		)
		`when`(repository.findAll(sort)).thenReturn(entries)

		val result = service.getAllEntries("category")

		assertEquals(listOf("Fruit", "Snack"), result.map { it.category })
		verify(repository).findAll(sort)
	}

	@Test
	fun `rejects unsupported sort options`() {
		val exception = assertFailsWith<ResponseStatusException> {
			service.getAllEntries("taste")
		}

		assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
	}
}
