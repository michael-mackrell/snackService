package com.example.foodService.service

import com.example.foodService.document.toFood
import com.example.foodService.model.Food
import com.example.foodService.repository.FoodCatalogRepository
import com.example.foodService.repository.InventoryFoodRepository
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@Service
class FoodImageService(
	private val imageStorage: ImageStorage,
	private val foodCatalogRepository: FoodCatalogRepository,
	private val inventoryFoodRepository: InventoryFoodRepository,
) {
	fun upload(foodId: UUID, file: MultipartFile): Food {
		val food = findFood(foodId)
		val imageType = detectImageType(file)
		val imageId = UUID.randomUUID()
		val objectKey = objectKey(foodId, imageId)

		file.inputStream.use { input ->
			imageStorage.put(objectKey, imageType.contentType, file.size, input)
		}

		val updated = try {
			foodCatalogRepository.save(food.copy(imageId = imageId))
		} catch (exception: Exception) {
			runCatching { imageStorage.delete(objectKey) }
			throw exception
		}

		inventoryFoodRepository.findById(foodId).ifPresent { inventoryFood ->
			inventoryFoodRepository.save(inventoryFood.copy(imageId = imageId))
		}

		food.imageId?.let { oldImageId ->
			runCatching { imageStorage.delete(objectKey(foodId, oldImageId)) }
		}

		return updated.toFood()
	}

	fun get(foodId: UUID): StoredImage {
		val food = findFood(foodId)
		val imageId = food.imageId ?: throw ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"Food image not found",
		)
		return imageStorage.get(objectKey(foodId, imageId))
	}

	fun delete(foodId: UUID) {
		val food = findFood(foodId)
		val imageId = food.imageId ?: return

		imageStorage.delete(objectKey(foodId, imageId))
		foodCatalogRepository.save(food.copy(imageId = null))
		inventoryFoodRepository.findById(foodId).ifPresent { inventoryFood ->
			inventoryFoodRepository.save(inventoryFood.copy(imageId = null))
		}
	}

	private fun findFood(foodId: UUID) =
		foodCatalogRepository.findById(foodId).orElseThrow {
			ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog entry not found")
		}

	private fun detectImageType(file: MultipartFile): ImageType {
		if (file.isEmpty) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is empty")
		}
		if (file.size > MAX_IMAGE_BYTES) {
			throw ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image must not exceed 5 MB")
		}

		val signature = file.inputStream.use { it.readNBytes(12) }
		return ImageType.entries.firstOrNull { it.matches(signature) }
			?: throw ResponseStatusException(
				HttpStatus.UNSUPPORTED_MEDIA_TYPE,
				"Only JPEG, PNG, and WebP images are supported",
			)
	}

	private fun objectKey(foodId: UUID, imageId: UUID): String =
		"foods/$foodId/$imageId"

	private enum class ImageType(val contentType: String) {
		JPEG("image/jpeg") {
			override fun matches(bytes: ByteArray) =
				bytes.size >= 3 && bytes[0] == 0xff.toByte() && bytes[1] == 0xd8.toByte() && bytes[2] == 0xff.toByte()
		},
		PNG("image/png") {
			override fun matches(bytes: ByteArray) = PNG_SIGNATURE.indices.all { index ->
				bytes.size > index && bytes[index] == PNG_SIGNATURE[index]
			}
		},
		WEBP("image/webp") {
			override fun matches(bytes: ByteArray) =
				bytes.size >= 12 && bytes.copyOfRange(0, 4).decodeToString() == "RIFF" &&
					bytes.copyOfRange(8, 12).decodeToString() == "WEBP"
		};

		abstract fun matches(bytes: ByteArray): Boolean
	}

	companion object {
		private const val MAX_IMAGE_BYTES = 5L * 1024 * 1024
		private val PNG_SIGNATURE = byteArrayOf(
			0x89.toByte(), 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a,
		)
	}
}
