package controllers.fragments

import doobie.implicits.*
import doobie.util.fragment

object DeskListingControllerFragments {

  val resetDeskListingTable: fragment.Fragment =
    sql"TRUNCATE TABLE desk_listings RESTART IDENTITY"

  val createDeskListingTable: fragment.Fragment =
    sql"""
        CREATE TABLE IF NOT EXISTS desk_listings (
            id SERIAL PRIMARY KEY,
            business_id VARCHAR(255),
            workspace_id VARCHAR(255),
            title VARCHAR(50),
            description TEXT,
            desk_type VARCHAR(100),
            quantity INT NOT NULL CHECK (quantity >= 0),
            price_per_hour DECIMAL(10, 2) CHECK (price_per_day >= 0),
            price_per_day DECIMAL(10, 2) CHECK (price_per_day >= 0),
            features TEXT[],
            availability JSONB,
            rules TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
      """
}
