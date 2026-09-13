package com.example.foodService.service

import com.example.foodService.document.FoodCatalogDocument
import com.example.foodService.document.InventoryFoodDocument
import com.example.foodService.repository.FoodCatalogRepository
import com.example.foodService.repository.InventoryFoodRepository
import java.io.InputStream
import java.util.Optional
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.server.ResponseStatusException

class FoodImageServiceTests {
	private val storage = FakeImageStorage()
	private val catalogRepository = mock(FoodCatalogRepository::class.java)
	private val inventoryRepository = mock(InventoryFoodRepository::class.java)
	private val service = FoodImageService(storage, catalogRepository, inventoryRepository)
	private val foodId = UUID.randomUUID()

	@Test
	fun `uploads a validated image and associates it with catalog and inventory`() {
		val food = food()
		val inventoryFood = inventoryFood()
		`when`(catalogRepository.findById(foodId)).thenReturn(Optional.of(food))
		`when`(inventoryRepository.findById(foodId)).thenReturn(Optional.of(inventoryFood))
		`when`(catalogRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer { it.arguments[0] }
		`when`(inventoryRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer { it.arguments[0] }

		val result = service.upload(foodId, jpeg())

		assertTrue(result.imageId != null)
		assertEquals("image/jpeg", storage.contentType)
		assertEquals("foods/$foodId/${result.imageId}", storage.key)
		val inventoryCaptor = ArgumentCaptor.forClass(InventoryFoodDocument::class.java)
		verify(inventoryRepository).save(inventoryCaptor.capture())
		assertEquals(result.imageId, inventoryCaptor.value.imageId)
	}

	@Test
	fun `rejects unsupported file contents without uploading`() {
		`when`(catalogRepository.findById(foodId)).thenReturn(Optional.of(food()))

		val exception = assertFailsWith<ResponseStatusException> {
			service.upload(
				foodId,
				MockMultipartFile("image", "food.txt", "image/jpeg", "not an image".toByteArray()),
			)
		}

		assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, exception.statusCode)
		assertNull(storage.key)
	}

	@Test
	fun `deletes the object and clears catalog and inventory image ids`() {
		val imageId = UUID.randomUUID()
		val food = food(imageId)
		val inventoryFood = inventoryFood(imageId)
		`when`(catalogRepository.findById(foodId)).thenReturn(Optional.of(food))
		`when`(inventoryRepository.findById(foodId)).thenReturn(Optional.of(inventoryFood))
		`when`(catalogRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer { it.arguments[0] }
		`when`(inventoryRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer { it.arguments[0] }

		service.delete(foodId)

		assertEquals("foods/$foodId/$imageId", storage.deletedKey)
		val catalogCaptor = ArgumentCaptor.forClass(FoodCatalogDocument::class.java)
		verify(catalogRepository).save(catalogCaptor.capture())
		assertNull(catalogCaptor.value.imageId)
		val inventoryCaptor = ArgumentCaptor.forClass(InventoryFoodDocument::class.java)
		verify(inventoryRepository).save(inventoryCaptor.capture())
		assertNull(inventoryCaptor.value.imageId)
	}

	private fun food(imageId: UUID? = null) = FoodCatalogDocument(
		uuid = foodId,
		imageId = imageId,
		name = "Almonds",
		tasteRating = 4,
	)

	private fun inventoryFood(imageId: UUID? = null) = InventoryFoodDocument(
		uuid = foodId,
		imageId = imageId,
		name = "Almonds",
		tasteRating = 4,
		quantity = 1,
	)

	private fun jpeg() = MockMultipartFile(
		"image",
		"food.jpg",
		"image/jpeg",
		byteArrayOf(0xff.toByte(), 0xd8.toByte(), 0xff.toByte(), 0x00),
	)

	private class FakeImageStorage : ImageStorage {
		var key: String? = null
		var contentType: String? = null
		var deletedKey: String? = null

		override fun put(objectKey: String, contentType: String, contentLength: Long, input: InputStream) {
			this.key = objectKey
			this.contentType = contentType
			input.readAllBytes()
		}

		override fun get(objectKey: String) = StoredImage(byteArrayOf(1), "image/jpeg")

		override fun delete(objectKey: String) {
			deletedKey = objectKey
		}
	}
}
