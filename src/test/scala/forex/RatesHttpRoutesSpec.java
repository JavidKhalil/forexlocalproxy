package forex;

import org.http4s._
import org.http4s.implicits._
import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AsyncFreeSpec

class RatesHttpRoutesSpec extends AsyncFreeSpec with Matchers {

  "RatesHttpRoutes" - {
    "should return exchange rate" in {
      // Mock OneFrameApiClient and provide a stubbed response
      val oneFrameApiClient = new OneFrameApiClientStub(GetApiResponse(""))

      // Create Routes with the mock OneFrameApiClient
      val routes = new RatesHttpRoutes[IO](oneFrameApiClient).routes

      // Test the route
      val request = Request[IO](method = Method.GET, uri = uri"/rates?pair=USDJPY")
      val response = routes.orNotFound(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[String].unsafeRunSync() shouldBe """{"from":"USD","to":"JPY","bid":0.61,"ask":0.82,"price":0.71,"time_stamp":"2019-01-01T00:00:00.000"}"""
    }
  }

  // Stub for OneFrameApiClient
  class OneFrameApiClientStub(response: GetApiResponse) extends OneFrameApiClient[IO](applicationConfig, httpClient, cache) {
    override def getStream(from: String, to: String): Stream[IO, GetApiResponse] =
      Stream.emit(response)
  }
}
