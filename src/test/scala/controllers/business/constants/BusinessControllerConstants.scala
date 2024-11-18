package controllers.business.constants

import models.business.Business

import java.time.LocalDateTime

object BusinessControllerConstants {

  val sampleBusiness_1: Business =
    Business(
      id = Some(1),
      businessId = "business_1",
      businessName = "Sample Business 1",
      contactNumber = "07402205071",
      contactEmail = "business_1@gmail.com",
      createdAt = LocalDateTime.of(2024, 10, 5, 15, 0)
    )
}