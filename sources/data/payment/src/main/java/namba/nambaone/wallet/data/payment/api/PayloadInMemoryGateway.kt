package namba.nambaone.wallet.data.payment.api

import kotlinx.coroutines.flow.MutableStateFlow
import namba.nambaone.wallet.domain.payment.model.PaymentPayload

class PayloadInMemoryGateway : namba.nambaone.wallet.domain.payment.gateway.PayloadLocalGateway {

    private val lastPayload = MutableStateFlow<PaymentPayload>(PaymentPayload.Absent)

    override suspend fun cacheLastPayload(payload: PaymentPayload) {
        lastPayload.value = payload
    }

    override fun getLastPayload(): PaymentPayload = lastPayload.value
}