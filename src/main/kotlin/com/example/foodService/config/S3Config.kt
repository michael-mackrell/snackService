package com.example.foodService.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Config {
	@Bean
	fun s3Client(
		@Value("\${app.images.region:us-east-1}") region: String,
	): S3Client = S3Client.builder()
		.region(Region.of(region))
		.build()
}
