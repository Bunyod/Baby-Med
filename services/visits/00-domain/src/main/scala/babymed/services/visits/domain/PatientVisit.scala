package babymed.services.visits.domain

import java.time.LocalDateTime

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import babymed.domain.PaymentStatus
import babymed.services.users.domain.types.PatientId
import babymed.services.users.domain.types.UserId
import babymed.services.visits.domain.types.ChequeId
import babymed.services.visits.domain.types.PatientVisitId
import babymed.services.visits.domain.types.ServiceId

@derive(decoder, encoder)
case class PatientVisit(
    id: PatientVisitId,
    createdAt: LocalDateTime,
    userId: UserId,
    patientId: PatientId,
    serviceId: ServiceId,
    chequeId: ChequeId,
    paymentStatus: PaymentStatus,
  )
