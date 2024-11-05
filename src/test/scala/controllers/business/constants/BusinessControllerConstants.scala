package controllers.business.constants

import models.business.Business

import java.time.LocalDateTime

object BusinessControllerConstants {

  val sampleBusiness_1: Business =
    Business(
      id = Some(1),
      business_id = "business_1",
      business_name = "Sample Business 1",
      contact_number = "07402205071",
      contact_email = "business_1@gmail.com",
      created_at = LocalDateTime.of(2024, 10, 5, 15, 0)
    )
}