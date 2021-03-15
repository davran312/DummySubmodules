package namba.nambaone.wallet.data.payment.model

import com.google.gson.annotations.SerializedName
import namba.nambaone.wallet.domain.shared.model.Amount

data class PaymentRequest(
    @SerializedName("paymentPinCode")
    val paymentPinCode: String,
    @SerializedName("paymentLinkToken")
    val paymentLinkToken: String,
    @SerializedName("paymentMethodGuid")
    val paymentMethodGuid: String,
    @SerializedName("amount")
    val amount: Amount?,
    @SerializedName("comment")
    val comment: String = ""
)
