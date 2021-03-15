package namba.nambaone.wallet.domain.payment.model

import com.google.gson.annotations.SerializedName

enum class EnumPaymentPayload {
    @SerializedName("CUSTOMER")
    CUSTOMER,
    @SerializedName("MERCHANT")
    MERCHANT
}