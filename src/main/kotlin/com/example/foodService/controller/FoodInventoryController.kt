package com.example.foodService.controller

import com.example.foodService.model.InventoryFood
import com.example.foodService.service.FoodInventoryService
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/inventory/foods")
class FoodInventoryController(
	private val foodInventoryService: FoodInventoryService,
) {

	@GetMapping
	fun getAllFoods(): List<InventoryFood> =
		foodInventoryService.getAllFoods()

	@GetMapping("/{uuid}")
	fun getFood(@PathVariable uuid: UUID): InventoryFood =
		foodInventoryService.getFood(uuid)

	@PostMapping("/{uuid}")
	fun addFood(@PathVariable uuid: UUID): InventoryFood =
		foodInventoryService.addFood(uuid)

	@DeleteMapping("/{uuid}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun removeFood(@PathVariable uuid: UUID) =
		foodInventoryService.removeFood(uuid)
}
