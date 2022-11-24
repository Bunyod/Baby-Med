package babymed.services.visits.proto

import babymed.domain.ResponseData
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitInfo
import babymed.services.visits.domain.types.PatientVisitId
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Visits[F[_]] {
  def create(createPatientVisit: CreatePatientVisit): F[PatientVisit]
  def get(filters: PatientVisitFilters): F[ResponseData[PatientVisitInfo]]
  def getTotal(filters: PatientVisitFilters): F[Long]
  def updatePaymentStatus(id: PatientVisitId): F[Unit]
}

object Visits {}