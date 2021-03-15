package namba.nambaone.wallet.domain.shared.model

import com.google.gson.annotations.SerializedName

enum class EnumCardAssociation {
    @SerializedName("VISA")
    VISA,
    @SerializedName("MASTER_CARD")
    MASTER_CARD,
    @SerializedName("ELCART")
    ELCART,
    @SerializedName("NONE")
    NONE
}
