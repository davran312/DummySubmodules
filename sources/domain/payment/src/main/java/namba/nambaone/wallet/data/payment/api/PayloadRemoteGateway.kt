package namba.nambaone.wallet.data.payment.api

import namba.nambaone.wallet.data.payment.model.*
import namba.nambaone.wallet.domain.payment.gateway.PayloadRestGateway
import namba.nambaone.wallet.domain.payment.model.*
import namba.nambaone.wallet.domain.shared.model.Amount
import org.threeten.bp.ZonedDateTime

class PayloadRemoteGateway(
    private val api: PayloadApi
) : PayloadRestGateway {
    override suspend fun payloadPayment(paymentLinkToken: String, paymentMethodId: String) =
        api.payloadPayment(
            PayloadPaymentRequest(
                paymentLinkToken,
                paymentMethodId
            )
        ).toEntity()

    override suspend fun pay(
        paymentPin: String,
        paymentLinkToken: String,
        paymentMethodId: String,
        amount: Amount?,
        comment: String
    ) =
        api.pay(PaymentRequest(paymentPin, paymentLinkToken, paymentMethodId, amount, comment))

    override suspend fun saveFavourite(
        serviceId: String,
        identifier: String,
        name: String,
        amount: Amount?
    ) = api.saveFavourite(
        AddFavouriteRequest(serviceId, identifier, name, amount)
    )
}

private fun PayloadResponse.toEntity(): PaymentPayload {
    when (type) {
        EnumPaymentPayload.CUSTOMER -> {
            this as CustomerPayloadResponse
            return PaymentPayload.CustomerPayload(
                id = guid,
                comment = comment,
                token = token,
                expiresAt = expiresAt ?: ZonedDateTime.now(),
                createdAt = createdAt ?: ZonedDateTime.now(),
                walletId = walletGuid
            )
        }
        EnumPaymentPayload.MERCHANT -> {
            this as PaymentPayloadResponse
            return PaymentPayload.MerchantPayload(
                id = guid,
                comment = comment,
                token = token,
                expiresAt = expiresAt ?: ZonedDateTime.now(),
                createdAt = createdAt ?: ZonedDateTime.now(),
                amount = amount ?: Amount.ZERO,
                amountCanBeChanged = amountCanBeChanged ?: false,
                walletId = walletGuid ?: "",
                oneTime = oneTime ?: false,
                displayDetail = displayDetails.toEntity(),
                details = details.toEntity()
            )
        }
    }
}

private fun PayloadDisplayDetailResponse.toEntity(): PayloadDisplayDetail =
    PayloadDisplayDetail(
        elements = elements.map {
            DisplayDetailElement(
                type = it.type,
                label = it.label ?: "",
                value = it.value ?: "",
                labelKey = it.labelKey ?: "",
                showOnConsent = it.showOnConsent ?: false,
                showOnPrecheck = it.showOnPrecheck ?: false
            )
        },
        payeeDetail = payeeDetails.toEntity(),
        commentable = commentable ?: false
    )

private fun PayloadPayeeDetailResponse.toEntity(): PayloadPayeeDetail =
    PayloadPayeeDetail(
        icon = icon ?: "",
        title = title ?: "",
        subtitle = subtitle ?: ""
    )

private fun PaymentPayloadDetailsResponse.toEntity(): PaymentPayloadDetail =
    PaymentPayloadDetail(
        service = service.toEntity(),
        merchant = merchant.toEntity(),
        correlationDetail = correlationDetails.toEntity()
    )

private fun PayloadServiceResponse.toEntity(): PayloadService =
    PayloadService(
        feeRewardPercentage = feeRewardPercentage,
        feeCalculationConfig = feeCalculationConfig.toEntity()
    )

private fun FeeConfigResponse.toEntity(): FeeConfig =
    FeeConfig(
        flat = flat ?: "",
        type = type ?: "",
        maxFlat = maxFlat ?: "",
        maxPercentage = maxPercentage ?: ""
    )

private fun PayloadMerchantResponse.toEntity(): PayloadMerchant =
    PayloadMerchant(
        merchantName = merchantName ?: "",
        merchantAccountId = merchantAccountGuid ?: "",
        merchantIcon = merchantIcon ?: "",
        merchantProductImage = merchantProductImage ?: "",
        merchantProductSubtitle = merchantProductSubtitle ?: "",
        merchantProductTitle = merchantProductTitle ?: "",
        paymentLinkId = paymentLinkGuid ?: ""
    )

private fun PayloadCorrelationResponse.toEntity(): PayloadCorrelation =
    PayloadCorrelation(
        type = type ?: EnumPaymentCategory.NONE,
        serviceId = serviceId ?: "",
        identifier = identifier ?: "",
        transactionId = transactionId,
        initiationType = initiationType ?: "",
        codesInPriority = codesInPriority ?: emptyList()
    )