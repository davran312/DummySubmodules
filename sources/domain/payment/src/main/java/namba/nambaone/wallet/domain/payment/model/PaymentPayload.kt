package namba.nambaone.wallet.domain.payment.model

import namba.nambaone.wallet.domain.shared.model.Amount
import org.threeten.bp.ZonedDateTime

sealed class PaymentPayload {
    data class MerchantPayload(
        val walletId: String,
        val oneTime: Boolean,
        val amountCanBeChanged: Boolean,
        val displayDetail: PayloadDisplayDetail,
        val id: String,
        val comment: String,
        val token: String,
        val amount: Amount,
        val details: PaymentPayloadDetail,
        val expiresAt: ZonedDateTime,
        val createdAt: ZonedDateTime
    ) : PaymentPayload() {

        fun isAddableToFavourite(): Boolean =
            details.correlationDetail.type == EnumPaymentCategory.UTILITY
    }

    data class CustomerPayload(
        val id: String,
        val comment: String,
        val token: String,
        val walletId: String,
        val expiresAt: ZonedDateTime,
        val createdAt: ZonedDateTime
    ) : PaymentPayload()

    object Absent : PaymentPayload()
}

data class PaymentPayloadDetail(
    val service: PayloadService,
    val merchant: PayloadMerchant,
    val correlationDetail: PayloadCorrelation
)

data class PayloadService(
    val feeRewardPercentage: Float,
    val feeCalculationConfig: FeeConfig
)

data class PayloadMerchant(
    val merchantName: String,
    val merchantIcon: String,
    val paymentLinkId: String,
    val merchantAccountId: String,
    val merchantProductImage: String,
    val merchantProductTitle: String,
    val merchantProductSubtitle: String
)

data class PayloadCorrelation(
    val type: EnumPaymentCategory,
    val serviceId: String,
    val identifier: String,
    val transactionId: Int,
    val initiationType: String,
    val codesInPriority: List<String>
)

data class FeeConfig(
    val flat: String,
    val type: String,
    val maxFlat: String,
    val maxPercentage: String
)

data class PayloadDisplayDetail(
    val elements: List<DisplayDetailElement>,
    val commentable: Boolean,
    val payeeDetail: PayloadPayeeDetail
)

data class DisplayDetailElement(
    val type: EnumDisplayDetailElement,
    val label: String,
    val value: String,
    val labelKey: String,
    val showOnConsent: Boolean,
    val showOnPrecheck: Boolean
)

data class PayloadPayeeDetail(
    val icon: String,
    val title: String,
    val subtitle: String
)