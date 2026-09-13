package com.example.foodService.dto

import java.util.UUID

data class CreateFoodRequest(
	val name: String,
	val tasteRating: Int,
	val imageId: UUID? = null,
)
