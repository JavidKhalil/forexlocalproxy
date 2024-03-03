package forex.domain

case class Rate(
    pair: Rate.Pair,
    ask: BigDecimal,
    bid: BigDecimal,
    price: Price,
    timestamp: Timestamp
)

object Rate {
  final case class Pair(
      from: Currency,
      to: Currency
  )
}
