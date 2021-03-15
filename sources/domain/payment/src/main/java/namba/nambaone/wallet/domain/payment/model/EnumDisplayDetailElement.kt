package namba.nambaone.wallet.domain.payment.model

import com.google.gson.annotations.SerializedName

enum class EnumDisplayDetailElement {
    @SerializedName("TEXT")
    TEXT,
    @SerializedName("MONEY")
    MONEY,
    @SerializedName("TOTAL_MONEY")
    TOTAL_MONEY,
    @SerializedName("PERCENT")
    PERCENT,
    @SerializedName("DATE")
    DATE;
}