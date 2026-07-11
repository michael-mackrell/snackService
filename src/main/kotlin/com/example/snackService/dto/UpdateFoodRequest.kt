package com.example.snackService.dto

import java.util.UUID

data class UpdateFoodRequest(
	val name: String,
	val calories: Int,
	val protein: Double,
	val carbs: Double,
	val fat: Double,
	val tasteRating: Int,
	val imageId: UUID? = null,
)
