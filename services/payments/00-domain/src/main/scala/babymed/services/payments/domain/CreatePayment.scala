package babymed.services.payments.domain

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive
import squants.Money

import babymed.services.users.domain.types.CustomerId

@derive(decoder, encoder)
case class CreatePayment(
    customerId: CustomerId,
    price: Money,
  )
