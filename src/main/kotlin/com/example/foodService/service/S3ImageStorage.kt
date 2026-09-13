package com.example.foodService.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception

@Component
class S3ImageStorage(
	private val s3Client: S3Client,
	@Value("\${app.images.bucket:}") private val bucket: String,
) : ImageStorage {
	override fun put(objectKey: String, contentType: String, contentLength: Long, input: java.io.InputStream) {
		requireConfigured()
		try {
			s3Client.putObject(
				PutObjectRequest.builder()
					.bucket(bucket)
					.key(objectKey)
					.contentType(contentType)
					.contentLength(contentLength)
					.build(),
				RequestBody.fromInputStream(input, contentLength),
			)
		} catch (exception: S3Exception) {
			throw unavailable("Could not store image", exception)
		}
	}

	override fun get(objectKey: String): StoredImage {
		requireConfigured()
		try {
			val response = s3Client.getObjectAsBytes(
				GetObjectRequest.builder().bucket(bucket).key(objectKey).build(),
			)
			return StoredImage(
				bytes = response.asByteArray(),
				contentType = response.response().contentType() ?: "application/octet-stream",
			)
		} catch (exception: NoSuchKeyException) {
			throw ResponseStatusException(HttpStatus.NOT_FOUND, "Food image not found", exception)
		} catch (exception: S3Exception) {
			throw unavailable("Could not load image", exception)
		}
	}

	override fun delete(objectKey: String) {
		requireConfigured()
		try {
			s3Client.deleteObject(
				DeleteObjectRequest.builder().bucket(bucket).key(objectKey).build(),
			)
		} catch (exception: S3Exception) {
			throw unavailable("Could not delete image", exception)
		}
	}

	private fun requireConfigured() {
		if (bucket.isBlank()) {
			throw ResponseStatusException(
				HttpStatus.SERVICE_UNAVAILABLE,
				"S3 image storage is not configured",
			)
		}
	}

	private fun unavailable(message: String, cause: Exception) =
		ResponseStatusException(HttpStatus.BAD_GATEWAY, message, cause)
}
