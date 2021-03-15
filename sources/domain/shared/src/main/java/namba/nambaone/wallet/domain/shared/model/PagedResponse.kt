package namba.nambaone.wallet.domain.shared.model

import com.google.gson.annotations.SerializedName

data class PagedResponse<T>(
    @SerializedName("amount", alternate = ["count"])
    val count: Int,
    @SerializedName("items")
    val items: List<T>
)
