package forex.services

import cats.effect.{Concurrent, Ref, Sync, Timer}
import cats.implicits._
import forex.httpServer.Protocol.objects.GetApiResponse
import scala.concurrent.duration._

trait RateCache[F[_]] {
  def get(from: String, to: String): F[Option[GetApiResponse]]
  def put(from: String, to: String, rate: GetApiResponse): F[Unit]
}

object RateCache {
  def create[F[_]: Concurrent: Timer]: F[RateCache[F]] =
    Ref.of[F, Map[(String, String), (GetApiResponse, Long)]](Map.empty).map { state =>
      new RateCache[F] {
        override def get(from: String, to: String): F[Option[GetApiResponse]] =
          state.get.map(_.get((from, to)).collect {
            case (rate, timestamp) if !isStale(timestamp) => rate
          })

        override def put(from: String, to: String, rate: GetApiResponse): F[Unit] =
          state.update(_.updated((from, to), (rate, System.currentTimeMillis())))

        private def isStale(timestamp: Long): Boolean =
          System.currentTimeMillis() - timestamp > 5.minutes.toMillis
      }
    }
}
