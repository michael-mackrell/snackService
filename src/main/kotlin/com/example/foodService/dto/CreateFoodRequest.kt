package com.example.foodService.dto

data class CreateFoodRequest(
	val name: String,
	val category: String,
	val tasteRating: Int,
)
