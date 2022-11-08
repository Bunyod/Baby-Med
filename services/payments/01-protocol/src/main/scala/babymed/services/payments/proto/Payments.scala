package babymed.services.payments.proto

import babymed.services.payments.domain.CreatePayment
import babymed.services.payments.domain.Payment
import babymed.services.payments.domain.PaymentFilters
import babymed.services.payments.domain.PaymentWithCustomer
import babymed.services.payments.domain.types.PaymentId
import babymed.support.services.service
import babymed.support.services.syntax.marshaller.codec

@service(Custom)
trait Payments[F[_]] {
  def create(createPayment: CreatePayment): F[Payment]
  def get(searchFilters: PaymentFilters): F[List[PaymentWithCustomer]]
  def getPaymentsTotal(searchFilters: PaymentFilters): F[Long]
  def delete(paymentId: PaymentId): F[Unit]
}

object Payments {}
