package namba.nambaone.wallet.domain.payment.gateway

import namba.nambaone.wallet.domain.shared.model.Amount
import namba.nambaone.wallet.domain.payment.model.PaymentPayload

interface PayloadRestGateway {
   suspend fun payloadPayment(paymentLinkToken: String, paymentMethodId: String): PaymentPayload
   suspend fun pay(paymentPin: String, paymentLinkToken: String, paymentMethodId: String, amount: Amount? = null, comment: String = "")
   suspend fun saveFavourite(serviceId: String, identifier: String, name: String, amount: Amount?)

}