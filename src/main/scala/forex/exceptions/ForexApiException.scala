package forex.exceptions

import cats.Show
import io.circe.{Encoder, Json}

sealed trait ForexApiException extends Throwable {
  def getMessage: String
}

object ForexApiException {

  class ForexApiGenericException(msg: String) extends ForexApiException {
    override def getMessage = msg
  }

  class OneApiUnreachableException() extends ForexApiException {
    override def getMessage: String = "One api service is not reachable at the moment"
  }

  implicit val showForexErr: Show[ForexApiException] = Show.show(err => err.getMessage)

  implicit val forexGenericExpEncoder: Encoder[ForexApiGenericException] =
    Encoder.instance[ForexApiGenericException] {
      showForexErr.show _ andThen Json.fromString
    }

  implicit val forexUnreachableExpEncoder: Encoder[OneApiUnreachableException] =
    Encoder.instance[OneApiUnreachableException] {
      showForexErr.show _ andThen Json.fromString
    }

}
