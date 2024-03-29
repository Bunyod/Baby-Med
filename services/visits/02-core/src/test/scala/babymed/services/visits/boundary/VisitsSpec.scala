package babymed.services.visits.boundary

import cats.effect.kernel.Sync

import babymed.services.visits.domain.CheckupExpense
import babymed.services.visits.domain.CheckupExpenseFilters
import babymed.services.visits.domain.CheckupExpenseInfo
import babymed.services.visits.domain.CreateCheckupExpense
import babymed.services.visits.domain.CreateDoctorShare
import babymed.services.visits.domain.CreatePatientVisit
import babymed.services.visits.domain.CreateService
import babymed.services.visits.domain.DoctorShare
import babymed.services.visits.domain.DoctorShareInfo
import babymed.services.visits.domain.PatientVisit
import babymed.services.visits.domain.PatientVisitFilters
import babymed.services.visits.domain.PatientVisitReport
import babymed.services.visits.domain.types.ChequeId
import babymed.services.visits.domain.types.DoctorShareId
import babymed.services.visits.generators.CheckupExpenseGenerators
import babymed.services.visits.generators.PatientVisitGenerators
import babymed.services.visits.repositories.CheckupExpensesRepository
import babymed.services.visits.repositories.VisitsRepository
import babymed.test.TestSuite

object VisitsSpec extends TestSuite with PatientVisitGenerators with CheckupExpenseGenerators {
  val visitRepo: VisitsRepository[F] = new VisitsRepository[F] {
    override def create(createPatientVisits: CreatePatientVisit): F[Unit] =
      Sync[F].unit
    override def get(filters: PatientVisitFilters): F[List[PatientVisitReport]] =
      Sync[F].delay(List(patientVisitReportGen.get))
    override def updatePaymentStatus(chequeId: ChequeId): F[List[PatientVisit]] =
      Sync[F].delay(List(patientVisit))
  }
  lazy val patientVisit: PatientVisit = patientVisitGen.get

  val checkupRepo: CheckupExpensesRepository[F] = new CheckupExpensesRepository[F] {
    override def createDoctorShare(createDoctorShare: CreateDoctorShare): F[DoctorShare] = ???
    override def get(filters: CheckupExpenseFilters): F[List[CheckupExpenseInfo]] = ???
    override def getTotal(filters: CheckupExpenseFilters): F[Long] = ???
    override def getDoctorShares: VisitsSpec.F[List[DoctorShareInfo]] = ???
    override def deleteDoctorShare(id: DoctorShareId): F[Unit] = ???
    override def create(
        createCheckupExpenses: List[CreateCheckupExpense]
      ): F[List[CheckupExpense]] =
      Sync[F].delay(List(checkupExpenseGen.get.copy(patientVisitId = patientVisit.id)))
  }

  val visits: Visits[F] = new Visits[F](visitRepo, checkupRepo)
  val createService: CreateService = createServiceGen().get

  loggedTest("Create Patient Visit") { logger =>
    visits
      .create(createPatientVisitGen().get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Get All Patient Visits") { logger =>
    visits
      .get(PatientVisitFilters.Empty)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }

  loggedTest("Update Payment Status") { logger =>
    visits
      .updatePaymentStatus(chequeIdGen.get)
      .as(success)
      .handleErrorWith { error =>
        logger
          .error("Error occurred!", cause = error)
          .as(failure("Test failed!"))
      }
  }
}
