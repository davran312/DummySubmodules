package namba.nambaone.wallet.domain.payment.gateway

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import namba.nambaone.wallet.domain.shared.model.Amount

class PayloadInteractor(
    private val payloadRestGateway: PayloadRestGateway,
    private val payloadLocalGateway: PayloadLocalGateway
) {
    suspend fun payloadPayment(paymentLinkToken: String, paymentMethodId: String) = withContext(Dispatchers.IO) {
        val result = payloadRestGateway.payloadPayment(paymentLinkToken, paymentMethodId)
        payloadLocalGateway.cacheLastPayload(result)
        return@withContext result
    }

    fun awaitPayload() = payloadLocalGateway.getLastPayload()

    suspend fun pay(
        paymentPin: String,
        paymentLinkToken: String,
        paymentMethodId:String,
        amount: Amount? = null,
        comment: String = "") =
        payloadRestGateway.pay(paymentPin,paymentLinkToken,paymentMethodId,amount,comment)

    suspend fun saveFavourite(
        serviceId: String,
        identifier: String,
        name: String,
        amount: Amount
    ) = payloadRestGateway.saveFavourite(serviceId, identifier, name, amount)
}