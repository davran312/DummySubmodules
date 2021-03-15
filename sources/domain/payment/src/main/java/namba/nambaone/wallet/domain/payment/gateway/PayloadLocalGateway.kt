package namba.nambaone.wallet.domain.payment.gateway

import namba.nambaone.wallet.domain.payment.model.PaymentPayload

interface PayloadLocalGateway {
    suspend fun cacheLastPayload(payload: PaymentPayload)
    fun getLastPayload(): PaymentPayload
}