package forex.domain

case class RateResponse(
                         from: Currency,
                         to: Currency,
                         bid: BigDecimal,
                         ask: BigDecimal,
                         price: BigDecimal,
                         timeStamp: Timestamp
                       )
