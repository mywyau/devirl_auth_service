package configuration

import cats.kernel.Eq
import configuration.models.*
import pureconfig.ConfigReader
import pureconfig.generic.derivation.*

case class AppConfig(
  featureSwitches: FeatureSwitches,
  kafka: KafkaConfig,
  devIrlFrontendConfig: DevIrlFrontendConfig,
  redisConfig: RedisConfig,
  postgresqlConfig: PostgresqlConfig,
  serverConfig: ServerConfig,
  stripeConfig: StripeConfig
) derives ConfigReader
