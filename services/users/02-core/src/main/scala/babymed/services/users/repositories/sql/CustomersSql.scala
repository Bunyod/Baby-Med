package babymed.services.users.repositories.sql

import java.time.LocalDateTime

import skunk._
import skunk.codec.all._
import skunk.implicits._

import babymed.services.users.domain.CreateCustomer
import babymed.services.users.domain.Customer
import babymed.services.users.domain.CustomerWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.SearchFilters
import babymed.services.users.domain.Town
import babymed.services.users.domain.types.CustomerId
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId
import babymed.support.skunk.codecs._
import babymed.support.skunk.syntax.all.skunkSyntaxFragmentOps

object CustomersSql {
  val customerId: Codec[CustomerId] = identity[CustomerId]
  val regionId: Codec[RegionId] = identity[RegionId]
  val townId: Codec[TownId] = identity[TownId]

  private val Columns =
    customerId ~ timestamp ~ firstName ~ lastName ~ regionId ~ townId ~ address ~ date ~ phone

  val encoder: Encoder[CustomerId ~ LocalDateTime ~ CreateCustomer] = Columns.contramap {
    case id ~ createdAt ~ cc =>
      id ~ createdAt ~ cc.firstname ~ cc.lastname ~ cc.regionId ~ cc.townId ~ cc.address ~ cc.birthday ~ cc.phone
  }

  val decoder: Decoder[Customer] = Columns.map {
    case id ~ createdAt ~ firstName ~ lastName ~ regionId ~ townId ~ address ~ birthday ~ phone =>
      Customer(id, createdAt, firstName, lastName, regionId, townId, address, birthday, phone)
  }

  val decRegion: Decoder[Region] = (regionId ~ regionName).map {
    case id ~ name =>
      Region(id, name)
  }

  val decTown: Decoder[Town] = (townId ~ regionId ~ townName).map {
    case id ~ regionId ~ name =>
      Town(id, regionId, name)
  }

  val decCustomerWithAddress: Decoder[CustomerWithAddress] = (decoder ~ decRegion ~ decTown).map {
    case customer ~ region ~ town =>
      CustomerWithAddress(customer, region, town)
  }

  private def searchFilter(filters: SearchFilters): List[Option[AppliedFragment]] =
    List(
      filters.startDate.map(sql"created_at >= $zonedDateTime"),
      filters.endDate.map(sql"created_at <= $zonedDateTime"),
      filters.customerFirstName.map(sql"firstname like $firstName"),
      filters.customerLastName.map(sql"lastname like $lastName"),
      filters.regionId.map(sql"region_id = $regionId"),
      filters.townId.map(sql"town_id = $townId"),
      filters.address.map(sql"address like $address"),
      filters.birthday.map(sql"birthday = $date"),
      filters.phone.map(sql"phone like $phone"),
    )

  def select(filters: SearchFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] =
      sql"""SELECT
       customers.id,
       customers.created_at,
       customers.firstname,
       customers.lastname,
       customers.region_id,
       customers.town_id,
       customers.address,
       customers.birthday,
       customers.phone,
       regions.id,
       regions.name,
       towns.id,
       towns.region_id,
       towns.name
        FROM customers
        INNER JOIN regions ON customers.region_id = regions.id
        INNER JOIN towns ON customers.town_id = towns.id"""

    baseQuery(Void).whereAndOpt(searchFilter(filters): _*)
  }

  def total(filters: SearchFilters): AppliedFragment = {
    val baseQuery: Fragment[Void] = sql"""SELECT count(*) FROM customers"""
    baseQuery(Void).whereAndOpt(searchFilter(filters): _*)
  }

  val insert: Query[CustomerId ~ LocalDateTime ~ CreateCustomer, Customer] =
    sql"""INSERT INTO customers VALUES ($encoder)
         RETURNING id, created_at, firstname, lastname, region_id, town_id, address, birthday, phone"""
      .query(decoder)
}
