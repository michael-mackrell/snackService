package com.example.foodService.controller

import com.example.foodService.model.Food
import com.example.foodService.service.FoodImageService
import java.time.Duration
import java.util.UUID
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/catalog/entries/{foodId}/image")
class FoodImageController(
	private val foodImageService: FoodImageService,
) {
	@PutMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun upload(
		@PathVariable foodId: UUID,
		@RequestPart("image") image: MultipartFile,
	): Food = foodImageService.upload(foodId, image)

	@GetMapping
	fun get(@PathVariable foodId: UUID): ResponseEntity<ByteArray> {
		val image = foodImageService.get(foodId)
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(image.contentType))
			.cacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePrivate().immutable())
			.body(image.bytes)
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun delete(@PathVariable foodId: UUID) = foodImageService.delete(foodId)
}
