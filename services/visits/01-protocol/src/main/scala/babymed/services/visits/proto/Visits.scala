package babymed.services.visits.proto

import babymed.domain.ResponseData
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitReport
import babymed.services.visits.domain.types.ChequeId
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Visits[F[_]] {
  def create(createPatientVisit: CreatePatientVisit): F[Unit]
  def get(filters: PatientVisitFilters): F[ResponseData[PatientVisitReport]]
  def updatePaymentStatus(chequeId: ChequeId): F[List[PatientVisit]]
}

object Visits {}
