package configuration.models

final case class KafkaTopicConfig(
  userRegistered: String,
)

final case class KafkaConfig(
  bootstrapServers: String,
  clientId: String,
  acks: String,
  lingerMs: Int,
  retries: Int,
  topic: KafkaTopicConfig
)