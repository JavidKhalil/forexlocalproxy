package forex.httpClients

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import forex.config.ApplicationConfig
import forex.httpServer.Protocol.objects.GetApiResponse
import io.circe.Json
import org.http4s.Method.GET
import org.http4s.Uri
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.circe.jsonOf
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.{Accept, Authorization}
import fs2.Stream
import org.typelevel.ci.CIString

class OneFrameApiClient[F[_]: ConcurrentEffect](applicationConfig: ApplicationConfig, httpClient: Client[F], cache: RateCache[F]) {
  // Existing code...

  def getStream(from: String, to: String): Stream[F, GetApiResponse] =
    Stream.eval(cache.get(from, to)).flatMap {
      case Some(rate) => Stream.emit(rate)
      case None       => fetchAndCache(from, to)
    }

  private def fetchAndCache(from: String, to: String): Stream[F, GetApiResponse] =
    Stream
      .eval(httpClient.run(req))
      .flatMap { response =>
        response.body.chunks.parseJsonStream.map(_.as[GetApiResponse]).collect {
          case Right(response) =>
            Stream.eval(cache.put(from, to, response)).flatMap(_ => Stream.emit(response))
        }
      }
}
