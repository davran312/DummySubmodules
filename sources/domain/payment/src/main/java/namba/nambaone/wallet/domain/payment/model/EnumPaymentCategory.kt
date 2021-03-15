package namba.nambaone.wallet.domain.payment.model

import com.google.gson.annotations.SerializedName

enum class EnumPaymentCategory {
    @SerializedName("QR")
    QR,
    @SerializedName("FOOD")
    FOOD,
    @SerializedName("UTILITY")
    UTILITY,
    @SerializedName("NONE")
    NONE;
}