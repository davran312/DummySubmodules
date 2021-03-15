package namba.nambaone.wallet.data.payment.api

import namba.nambaone.wallet.data.payment.model.AddFavouriteRequest
import namba.nambaone.wallet.data.payment.model.PayloadPaymentRequest
import namba.nambaone.wallet.data.payment.model.PayloadResponse
import namba.nambaone.wallet.data.payment.model.PaymentRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface PayloadApi {

    @POST("payment/payment/me/calc_for_consent")
    suspend fun payloadPayment(@Body body: PayloadPaymentRequest): PayloadResponse

    @POST("payment/payment/me/give_consent")
    suspend fun pay(@Body body: PaymentRequest)

    @POST("utility/favorites")
    suspend fun saveFavourite(@Body body: AddFavouriteRequest)
}