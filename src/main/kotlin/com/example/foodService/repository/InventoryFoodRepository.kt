package com.example.foodService.repository

import com.example.foodService.document.InventoryFoodDocument
import java.util.UUID
import org.springframework.data.mongodb.repository.MongoRepository

interface InventoryFoodRepository : MongoRepository<InventoryFoodDocument, UUID>
