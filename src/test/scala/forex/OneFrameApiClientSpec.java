package forex;

import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AsyncFreeSpec
import forex.config.ApplicationConfig
import forex.httpClients.OneFrameApiClient
import forex.httpServer.Protocol.objects.GetApiResponse
import org.http4s.client.Client
import org.http4s.implicits._
import org.http4s.{HttpRoutes, Request, Status, Uri}
import cats.effect.{ContextShift, IO, Timer}
import fs2.Stream
import org.typelevel.ci.CIString
import forex.cache.RateCache
import org.http4s.headers.Authorization
import org.http4s.headers.{Accept, AuthScheme, Header}

import scala.concurrent.ExecutionContext

class OneFrameApiClientSpec extends AsyncFreeSpec with Matchers {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  "OneFrameApiClient" - {
    "should fetch exchange rate from OneFrame API" in {
      // Mock OneFrame API response
      val expectedResponse = GetApiResponse(...)

      // Create a mock HttpClient
      val httpClient = Client.fromHttpApp(HttpRoutes.of[IO] {
        case GET -> Root / "rates" :? Map("pair" -> pair) =>
          IO.pure(Response[IO](status = Status.Ok).withEntity(expectedResponse))
      }.orNotFound)

      // Create the OneFrameApiClient instance
      val oneFrameApiClient = new OneFrameApiClient[IO](ApplicationConfig(...), httpClient, RateCache.create[IO].unsafeRunSync())

      // Test the client
      val result: Option[GetApiResponse] = oneFrameApiClient.getStream("USD", "JPY").compile.last.unsafeRunSync()

      result shouldBe Some(expectedResponse)
    }
  }
}
