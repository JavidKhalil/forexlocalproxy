package forex;

import org.scalatest.matchers.should.Matchers
import org.scalatest.freespec.AsyncFreeSpec
import cats.effect.IO
import forex.cache.RateCache
import forex.httpServer.Protocol.objects.GetApiResponse

import scala.concurrent.ExecutionContext

class RateCacheSpec extends AsyncFreeSpec with Matchers {

    implicit val cs:ContextShift[IO]=IO.contextShift(ExecutionContext.global)

            "RateCache"-

    {
        "should store and retrieve exchange rates" in {
        val cache = RateCache.create[IO].unsafeRunSync()

        // Mock exchange rate
        val rate = GetApiResponse(...)

        // Store rate in the cache
        cache.put("USD", "JPY", rate).unsafeRunSync()

        // Retrieve rate from the cache
        val retrievedRate:Option[GetApiResponse] = cache.get("USD", "JPY").unsafeRunSync()

        retrievedRate shouldBe Some(rate)
    }

        "should not retrieve stale rates" in {
        val cache = RateCache.create[IO].unsafeRunSync()

        // Mock exchange rate
        val rate = GetApiResponse(...)

        // Store rate with an old timestamp in the cache
        val staleTimestamp = System.currentTimeMillis() - 10. minutes.toMillis
        cache.put("USD", "JPY", rate, staleTimestamp).unsafeRunSync()

        // Retrieve rate from the cache
        val retrievedRate:Option[GetApiResponse] = cache.get("USD", "JPY").unsafeRunSync()

        retrievedRate shouldBe None
    }
    }
}
