package configuration.models

import pureconfig.ConfigReader
import pureconfig.generic.derivation.*

case class ServerConfig(host: String, port: Int) derives ConfigReader

case class PostgresqlConfig(
                             dbName: String,
                             host: String,
                             port: Int,
                             username: String,
                             password: String
                           )

case class IntegrationSpecConfig(
                                  host: String,
                                  port: Int,
                                  postgresqlConfig: PostgresqlConfig
                                ) derives ConfigReader

case class AppConfig(
                      serverConfig: ServerConfig,
                      postgresqlConfig: PostgresqlConfig,
                      integrationSpecConfig: IntegrationSpecConfig
                    ) derives ConfigReader
