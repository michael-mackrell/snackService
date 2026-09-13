package com.example.foodService.controller

import com.example.foodService.dto.CreateFoodRequest
import com.example.foodService.dto.UpdateFoodRequest
import com.example.foodService.model.Food
import com.example.foodService.service.FoodCatalogService
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/catalog/entries")
class FoodCatalogController(
	private val foodCatalogService: FoodCatalogService,
) {

	@PostMapping
	fun addEntry(@RequestBody request: CreateFoodRequest): Food =
		foodCatalogService.addEntry(request)

	@GetMapping
	fun getAllEntries(): List<Food> =
		foodCatalogService.getAllEntries()

	@PutMapping("/{uuid}")
	fun updateEntry(
		@PathVariable uuid: UUID,
		@RequestBody request: UpdateFoodRequest,
	): Food = foodCatalogService.updateEntry(uuid, request)

	@DeleteMapping("/{uuid}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun deleteEntry(@PathVariable uuid: UUID) =
		foodCatalogService.deleteEntry(uuid)
}
