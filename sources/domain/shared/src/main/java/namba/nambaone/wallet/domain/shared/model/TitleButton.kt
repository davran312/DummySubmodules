package namba.nambaone.wallet.domain.shared.model

import com.google.gson.annotations.SerializedName

data class TitleButton(
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String
)