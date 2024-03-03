package forex

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import forex.config.ApplicationConfig
import forex.httpClients.OneFrameApiClient
import forex.httpServer.RatesHttpRoutes
import forex.services.RateCache
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.middleware.{AutoSlash, Timeout}

class Module[F[_] : Timer : ConcurrentEffect: ContextShift](config: ApplicationConfig) {

  private val cache: F[RateCache[F]] = RateCache.create[F]

  private val oneFrameApiClient: OneFrameApiClient[F] = new OneFrameApiClient[F](config)

  private val oneFrameApiClient = new OneFrameApiClient(config, oneFrameApiClient, cache)

  private val ratesHttpRoutes: HttpRoutes[F] = new RatesHttpRoutes[F](oneFrameApiClient).routes

  type PartialMiddleware = HttpRoutes[F] => HttpRoutes[F]
  type TotalMiddleware = HttpApp[F] => HttpApp[F]

  private val routesMiddleware: PartialMiddleware = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    }
  }

  private val appMiddleware: TotalMiddleware = { http: HttpApp[F] =>
    Timeout(config.http.timeout)(http)
  }

  private val http: HttpRoutes[F] = ratesHttpRoutes

  val httpApp: HttpApp[F] = appMiddleware(routesMiddleware(http).orNotFound)

}
