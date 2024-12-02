package controllers.fragments.business

import doobie.implicits.*
import doobie.util.fragment

object BusinessSpecsRepoFragments {

  val resetBusinessSpecsTable: fragment.Fragment = {
    sql"TRUNCATE TABLE business_specs RESTART IDENTITY"
  }

  val createBusinessSpecsTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS business_specs (
        id SERIAL PRIMARY KEY,
        user_id VARCHAR(255) NOT NULL UNIQUE,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        business_name VARCHAR(255) NOT NULL,
        description TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }

}
