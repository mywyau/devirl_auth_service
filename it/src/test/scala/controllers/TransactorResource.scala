package controllers

import cats.effect.IO
import doobie.Transactor
import org.http4s.client.Client
import org.http4s.server.Server

// Define a wrapper case class to help with runtime type issues
case class TransactorResource(xa: Transactor[IO])
case class HttpClientResource(client: Client[IO])
