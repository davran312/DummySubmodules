package namba.nambaone.wallet.data.payment.model

import com.google.gson.annotations.SerializedName
import namba.nambaone.wallet.domain.payment.model.EnumDisplayDetailElement
import namba.nambaone.wallet.domain.payment.model.EnumPaymentCategory
import namba.nambaone.wallet.domain.payment.model.EnumPaymentPayload
import namba.nambaone.wallet.domain.shared.model.Amount
import org.threeten.bp.ZonedDateTime

abstract class PayloadResponse {
    abstract val type: EnumPaymentPayload
}

data class PaymentPayloadResponse(
    @SerializedName("amountCanBeChanged")
    val amountCanBeChanged: Boolean?,
    @SerializedName("amount")
    val amount: Amount?,
    @SerializedName("details")
    val details: PaymentPayloadDetailsResponse,
    @SerializedName("walletGuid")
    val walletGuid: String?,
    @SerializedName("oneTime")
    val oneTime: Boolean?,
    @SerializedName("displayDetails")
    val displayDetails: PayloadDisplayDetailResponse,
    @SerializedName("guid")
    val guid: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("type")
    override val type: EnumPaymentPayload,
    @SerializedName("expiresAt")
    val expiresAt: ZonedDateTime?,
    @SerializedName("createdAt")
    val createdAt: ZonedDateTime?
) : PayloadResponse()

data class CustomerPayloadResponse(
    @SerializedName("walletGuid")
    val walletGuid: String,
    @SerializedName("guid")
    val guid: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("type")
    override val type: EnumPaymentPayload,
    @SerializedName("expiresAt")
    val expiresAt: ZonedDateTime?,
    @SerializedName("createdAt")
    val createdAt: ZonedDateTime?
) : PayloadResponse()

data class PaymentPayloadDetailsResponse(
    @SerializedName("service")
    val service: PayloadServiceResponse,
    @SerializedName("merchant")
    val merchant: PayloadMerchantResponse,
    @SerializedName("correlationDetails")
    val correlationDetails: PayloadCorrelationResponse
)

data class PayloadServiceResponse(
    @SerializedName("feeRewardPercentage")
    val feeRewardPercentage: Float,
    @SerializedName("feeCalculationConfig")
    val feeCalculationConfig: FeeConfigResponse
)

data class PayloadMerchantResponse(
    @SerializedName("merchantName")
    val merchantName: String?,
    @SerializedName("merchantIcon")
    val merchantIcon: String?,
    @SerializedName("paymentLinkGuid")
    val paymentLinkGuid: String?,
    @SerializedName("merchantAccountGuid")
    val merchantAccountGuid: String?,
    @SerializedName("merchantProductImage")
    val merchantProductImage: String?,
    @SerializedName("merchantProductTitle")
    val merchantProductTitle: String?,
    @SerializedName("merchantProductSubtitle")
    val merchantProductSubtitle: String?
)

data class PayloadCorrelationResponse(
    @SerializedName("type")
    val type: EnumPaymentCategory?,
    @SerializedName("serviceId")
    val serviceId: String?,
    @SerializedName("identifier")
    val identifier: String?,
    @SerializedName("transactionId")
    val transactionId: Int,
    @SerializedName("initiationType")
    val initiationType: String?,
    @SerializedName("codesInPriority")
    val codesInPriority: List<String>?
)

data class PayloadDisplayDetailResponse(
    @SerializedName("elements")
    val elements: List<DisplayDetailElementResponse>,
    @SerializedName("commentable")
    val commentable: Boolean?,
    @SerializedName("payeeDetails")
    val payeeDetails: PayloadPayeeDetailResponse
)

data class DisplayDetailElementResponse(
    @SerializedName("type")
    val type: EnumDisplayDetailElement,
    @SerializedName("label")
    val label: String?,
    @SerializedName("value")
    val value: String?,
    @SerializedName("labelKey")
    val labelKey: String?,
    @SerializedName("showOnConsent")
    val showOnConsent: Boolean?,
    @SerializedName("showOnPrecheck")
    val showOnPrecheck: Boolean?
)

data class PayloadPayeeDetailResponse(
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("subtitle")
    val subtitle: String?
)

class FeeConfigResponse(
    @SerializedName("flat")
    val flat: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("maxFlat")
    val maxFlat: String?,
    @SerializedName("maxPercentage")
    val maxPercentage: String?
)