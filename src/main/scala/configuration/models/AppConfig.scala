package configuration.models

import pureconfig.ConfigReader
import pureconfig.generic.derivation.*

case class ServerConfig(host: String, port: Int)derives ConfigReader

case class PostgresqlConfig(
                             dbName: String,
                             host: String,
                             port: Int,
                             username: String,
                             password: String
                           )derives ConfigReader

case class IntegrationSpecConfig(
                                  serverConfig: ServerConfig,
                                  postgresqlConfig: PostgresqlConfig
                                )derives ConfigReader

case class LocalConfig(
                        serverConfig: ServerConfig,
                        postgresqlConfig: PostgresqlConfig,
                      )derives ConfigReader

case class FeatureSwitches(
                            useDockerHost: Boolean
                          )derives ConfigReader

case class AppConfig(
                      featureSwitches: FeatureSwitches,
                      localConfig: LocalConfig,
                      integrationSpecConfig: IntegrationSpecConfig
                    )derives ConfigReader
