package configuration.models

import pureconfig.ConfigReader
import pureconfig.generic.derivation.*

case class ServerConfig(host: String, port: Int) derives ConfigReader

case class IspecServerConfig(host: String, port: Int) derives ConfigReader

case class AppConfig(
                      serverConfig: ServerConfig,
                      ispecServerConfig: IspecServerConfig
                    ) derives ConfigReader
