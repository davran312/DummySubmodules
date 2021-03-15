package namba.nambaone.wallet.domain.shared.model

import android.location.Location
import com.google.gson.annotations.SerializedName

sealed class LayoutItemDetail

data class ActionItemDetail(
    @SerializedName("title")
    val title: String,
    @SerializedName("iconUrl")
    val iconUrl: String?
) : LayoutItemDetail()

data class ServiceItemDetail(
    @SerializedName("title")
    val title: String,
    @SerializedName("iconUrl")
    val iconUrl: String?
) : LayoutItemDetail()

data class BannerItemDetail(
    @SerializedName("imageUrl")
    val imageUrl: String?
) : LayoutItemDetail()

data class UtilityItemDetail(
    @SerializedName("title")
    val title: String,
    @SerializedName("iconUrl")
    val iconUrl: String?
) : LayoutItemDetail()

data class MerchantTagItemDetail(
    @SerializedName("title")
    val title: String,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("backgroundColor")
    val backgroundColor: String?,
    @SerializedName("titleColor")
    val titleColor: String?
) : LayoutItemDetail()

data class MerchantItemDetail(
    @SerializedName("title")
    val title: String,
    @SerializedName("subtitle")
    val subtitle: String?,
    @SerializedName("logoUrl")
    val logoUrl: String?,
    @SerializedName("location")
    val location: Location?,
    @SerializedName("badgeIconUrls")
    val badgeIconUrls: List<String>?
) : LayoutItemDetail()