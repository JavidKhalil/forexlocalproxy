package forex.domain

import cats.Show

sealed trait Currency

object Currency {
  case object AUD extends Currency { override def toString: String = "AUD" }
  case object CAD extends Currency { override def toString: String = "CAD" }
  case object CHF extends Currency { override def toString: String = "CHF" }
  case object EUR extends Currency { override def toString: String = "EUR" }
  case object GBP extends Currency { override def toString: String = "GBR" }
  case object NZD extends Currency { override def toString: String = "NZD" }
  case object JPY extends Currency { override def toString: String = "JPY" }
  case object SGD extends Currency { override def toString: String = "SGD" }
  case object USD extends Currency { override def toString: String = "USD" }

  implicit val show: Show[Currency] = Show.show {
    case AUD => "AUD"
    case CAD => "CAD"
    case CHF => "CHF"
    case EUR => "EUR"
    case GBP => "GBP"
    case NZD => "NZD"
    case JPY => "JPY"
    case SGD => "SGD"
    case USD => "USD"
  }

  def fromString(s: String): Currency = s.toUpperCase match {
    case "AUD" => AUD
    case "CAD" => CAD
    case "CHF" => CHF
    case "EUR" => EUR
    case "GBP" => GBP
    case "NZD" => NZD
    case "JPY" => JPY
    case "SGD" => SGD
    case "USD" => USD
  }

}
