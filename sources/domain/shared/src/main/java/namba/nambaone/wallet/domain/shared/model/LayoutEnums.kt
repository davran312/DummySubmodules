package namba.nambaone.wallet.domain.shared.model

import com.google.gson.annotations.SerializedName

enum class LayoutType {
    @SerializedName("actions")
    ACTIONS,

    @SerializedName("card")
    CARD,

    @SerializedName("banners")
    BANNERS
}

enum class LayoutItemType {
    @SerializedName("action")
    ACTION,

    @SerializedName("service")
    SERVICE,

    @SerializedName("banner")
    BANNER,

    @SerializedName("utility")
    UTILITY,

    @SerializedName("merchant_tag")
    MERCHANT_TAG,

    @SerializedName("merchant")
    MERCHANT
}

enum class Orientation {
    @SerializedName("VERTICAL")
    VERTICAL,

    @SerializedName("HORIZONTAL")
    HORIZONTAL;
}

