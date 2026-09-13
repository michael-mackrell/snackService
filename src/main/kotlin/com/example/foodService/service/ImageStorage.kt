package com.example.foodService.service

import java.io.InputStream

data class StoredImage(
	val bytes: ByteArray,
	val contentType: String,
)

interface ImageStorage {
	fun put(objectKey: String, contentType: String, contentLength: Long, input: InputStream)
	fun get(objectKey: String): StoredImage
	fun delete(objectKey: String)
}
