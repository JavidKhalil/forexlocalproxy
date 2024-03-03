package forex

import cats.effect.Sync
import forex.domain.Rate
import forex.httpServer.Protocol.objects.GetApiResponse
import io.circe.generic.extras.decoding.{EnumerationDecoder, UnwrappedDecoder}
import io.circe.generic.extras.encoding.{EnumerationEncoder, UnwrappedEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._

package object httpServer {

  implicit def valueClassEncoder[A: UnwrappedEncoder]: Encoder[A] = implicitly

  implicit def valueClassDecoder[A: UnwrappedDecoder]: Decoder[A] = implicitly

  implicit def enumEncoder[A: EnumerationEncoder]: Encoder[A] = implicitly

  implicit def enumDecoder[A: EnumerationDecoder]: Decoder[A] = implicitly

  implicit def jsonDecoder[A <: Product : Decoder, F[_] : Sync]: EntityDecoder[F, A] = jsonOf[F, A]

  implicit def jsonEncoder[A <: Product : Encoder, F[_]]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

  implicit class GetApiResponseOps(val rate: Rate) extends AnyVal {
    def asGetApiResponse: GetApiResponse =
      GetApiResponse(
        from = rate.pair.from,
        to = rate.pair.to,
        bid = rate.bid,
        ask = rate.ask,
        price = rate.price,
        timeStamp = rate.timestamp
      )
  }

}



