package namba.nambaone.wallet.data.payment.model

import com.google.gson.annotations.SerializedName

class PayloadPaymentRequest(
    @SerializedName("paymentLinkToken")
    val paymentLinkToken: String,
    @SerializedName("paymentMethodGuid")
    val paymentMethodGuid: String
)