package namba.nambaone.wallet.data.payment.model

import com.google.gson.annotations.SerializedName
import namba.nambaone.wallet.domain.shared.model.Amount

data class AddFavouriteRequest(
    @SerializedName("utilityServiceGuid")
    val utilityServiceGuid: String,
    @SerializedName("identifier")
    val identifier: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("amount")
    val amount: Amount?
)
