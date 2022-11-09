package babymed.services.payments.repositories

import java.time.LocalDateTime
import java.util.UUID

import cats.effect.IO
import cats.effect.Resource
import cats.implicits.catsSyntaxOptionId
import cats.implicits.toFoldableOps
import org.scalacheck.Gen
import skunk.Session
import skunk.implicits.toIdOps

import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import babymed.services.users.generators.CustomerGenerators
import babymed.services.users.repositories.sql.CustomersSql
import babymed.support.skunk.syntax.all.skunkSyntaxQueryOps

object data extends CustomerGenerators {
  implicit private def gen2instance[T](gen: Gen[T]): T = gen.sample.get

  object regions {
    val id2: RegionId = RegionId(UUID.fromString("3b316182-e55c-4e03-8811-052fcd888236"))
  }

  object towns {
    val id2: TownId = TownId(UUID.fromString("b272f8fe-e0a1-4157-903f-91d1b22b6770"))
  }

  object customer {
    val id1: CustomerId = customerIdGen.get
    val data1: CreateCustomer = createCustomerGen(regions.id2.some, towns.id2.some)
    val values: Map[CustomerId, CreateCustomer] = Map(id1 -> data1)
  }

  def setup(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    setupCustomers

  private def setupCustomers(implicit session: Resource[IO, Session[IO]]): IO[Unit] =
    customer.values.toList.traverse_ {
      case id -> data =>
        CustomersSql.insert.queryUnique(id ~ LocalDateTime.now() ~ data)
    }
}
