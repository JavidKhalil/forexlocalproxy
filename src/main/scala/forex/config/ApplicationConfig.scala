package forex.config

import scala.concurrent.duration.FiniteDuration

case class ApplicationConfig(
    http: HttpConfig,
    token: String,
    oneFrameApiUrl: String)

case class HttpConfig(
    host: String,
    port: Int,
    timeout: FiniteDuration
)
