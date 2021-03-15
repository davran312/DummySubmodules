package namba.nambaone.wallet.domain.shared.model

import com.google.gson.annotations.SerializedName

data class LayoutItemResponse(
    @SerializedName("guid")
    val id: String,
    @SerializedName("url")
    val url: String?,
    @SerializedName("blockGuid")
    val blockId: String?,
    @SerializedName("displayOrder")
    val displayOrder: Int?,
    @SerializedName("isDisplayed")
    val isDisplayed: Boolean?,
    @SerializedName("isBlocked")
    val isBlocked: Boolean?,
    @SerializedName("type")
    val type: LayoutItemType,
    @SerializedName("details")
    val details: LayoutItemDetail?
)

data class LayoutItem(
    val id: String,
    val url: String,
    val blockId: String,
    val displayOrder: Int,
    val isDisplayed: Boolean,
    val isBlocked: Boolean,
    val type: LayoutItemType,
    val detail: LayoutItemDetail?
)