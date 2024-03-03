package forex.httpClients

import cats.effect.ConcurrentEffect
import fs2.Stream
import forex.config.ApplicationConfig
import forex.httpServer.Protocol.objects.GetApiResponse
import io.circe.Json
import org.http4s.{AuthScheme, Credentials, MediaType, Method, Request, Uri}
import org.http4s.client.Client
import org.typelevel.ci.CIString
import org.typelevel.jawn.Facade

class OneFrameApiClient[F[_]: ConcurrentEffect](applicationConfig: ApplicationConfig, httpClient: Client[F]) {

  implicit val f: Facade[Json] = new io.circe.jawn.CirceSupportParser(None, false).facade

  def getStream(from: String, to: String): Stream[F, GetApiResponse] = {
    val uriWithParams = Uri.fromString(s"${applicationConfig.oneFrameApiUrl}?pair=$from$to").getOrElse(uri"/")
    val req = Request[F](
      method = Method.GET,
      uri = uriWithParams,
      headers = Headers(
        Header.Raw(CIString("token"), applicationConfig.token),
        Accept(MediaType.application.json)
      )
    )

    Stream
      .eval(httpClient.run(req))
      .flatMap { response =>
        response.body.chunks.parseJsonStream.map(_.as[GetApiResponse]).collect {
          case Right(response) => response
        }
      }
  }
}
