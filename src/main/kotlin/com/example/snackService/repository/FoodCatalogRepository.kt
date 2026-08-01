package com.example.snackService.repository

import com.example.snackService.document.FoodCatalogDocument
import java.util.UUID
import org.springframework.data.mongodb.repository.MongoRepository

interface FoodCatalogRepository : MongoRepository<FoodCatalogDocument, UUID>
