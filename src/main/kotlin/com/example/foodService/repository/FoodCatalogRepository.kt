package com.example.foodService.repository

import com.example.foodService.document.FoodCatalogDocument
import java.util.UUID
import org.springframework.data.mongodb.repository.MongoRepository

interface FoodCatalogRepository : MongoRepository<FoodCatalogDocument, UUID>
