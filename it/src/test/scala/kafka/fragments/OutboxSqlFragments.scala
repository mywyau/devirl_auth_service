package kafka.fragments

import doobie.implicits.*
import doobie.util.fragment

object OutboxSqlFragments {

  val resetOutboxTable: fragment.Fragment =
    sql"""TRUNCATE TABLE outbox_events RESTART IDENTITY"""

  val createOutboxTable: fragment.Fragment =
    sql"""
        CREATE TABLE IF NOT EXISTS outbox_events (
          event_id TEXT PRIMARY KEY,
          aggregate_type TEXT NOT NULL,
          aggregate_id TEXT NOT NULL,
          event_type TEXT NOT NULL,
          payload JSONB NOT NULL,
          published BOOLEAN NOT NULL DEFAULT FALSE,
          retry_count INT NOT NULL DEFAULT 0,
          created_at TIMESTAMP NOT NULL DEFAULT NOW()
        )
    """
}
