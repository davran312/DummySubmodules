package namba.wallet.nambaone.common.network.errors

import com.google.gson.annotations.SerializedName
import namba.wallet.nambaone.core.R

private val DEFAULT_MESSAGE_RES = R.string.general_error_message

enum class ErrorCode(val messageRes: Int = DEFAULT_MESSAGE_RES) {
    @SerializedName("ERROR")
    Error,
    // Customer auth exceptions
    @SerializedName("CUSTOMER_NOT_FOUND")
    CUSTOMER_NOT_FOUND(R.string.error_customer_not_found),
    @SerializedName("CUSTOMER_ALREADY_EXISTS")
    CUSTOMER_ALREADY_EXISTS(R.string.error_customer_already_exists),
    @SerializedName("CUSTOMER_PINS_MATCH")
    CUSTOMER_PINS_MATCH(R.string.error_customer_pins_match),
    @SerializedName("CUSTOMER_WRONG_PIN")
    CUSTOMER_WRONG_PIN(R.string.error_customer_wrong_pin),
    @SerializedName("CUSTOMER_NOT_AUTHENTICATED")
    CUSTOMER_NOT_AUTHENTICATED(R.string.error_customer_not_authenticated),
    @SerializedName("CUSTOMER_NOT_AUTHORIZED")
    CUSTOMER_NOT_AUTHORIZED(R.string.error_customer_not_authenticated),
    @SerializedName("CUSTOMER_BLOCKED")
    CUSTOMER_BLOCKED(R.string.error_customer_blocked),
    @SerializedName("CUSTOMER_SUSPENDED")
    CUSTOMER_SUSPENDED(R.string.error_customer_suspend_blocked),
    @SerializedName("CUSTOMER_CANNOT_BE_RECOVERED")
    CUSTOMER_CANNOT_BE_RECOVERED(R.string.error_customer_cannot_be_recovered),
    @SerializedName("CUSTOMER_WRONG_OTP")
    CUSTOMER_WRONG_OTP(R.string.error_customer_wrong_otp),
    @SerializedName("CUSTOMER_PAYMENT_PIN_CANNOT_BE_SET")
    CUSTOMER_PAYMENT_PIN_CANNOT_BE_SET(R.string.error_customer_payment_pin_cannot_be_set),

    // Food order exceptions
    @SerializedName("FOOD_ORDER_WRONG_SUM")
    FOOD_ORDER_WRONG_SUM(R.string.error_food_order_wrong_sum),
    @SerializedName("FOOD_ORDER_CAFE_NOT_FOUND")
    FOOD_ORDER_CAFE_NOT_FOUND(R.string.error_food_order_cafe_not_found),
    @SerializedName("FOOD_ORDER_DISH_NOT_FOUND")
    FOOD_ORDER_DISH_NOT_FOUND(R.string.error_food_order_dish_not_found),

    // Payment order exceptions
    @SerializedName("PAYMENT_METHOD_NOT_FOUND")
    PAYMENT_METHOD_NOT_FOUND(R.string.error_payment_method_not_found),
    @SerializedName("PAYMENT_QR_NOT_FOUND")
    PAYMENT_QR_NOT_FOUND(R.string.error_payment_qr_not_found),
    @SerializedName("TOPUP_ORDER_NOT_FOUND")
    TOPUP_ORDER_NOT_FOUND(R.string.error_topup_order_not_found),
    @SerializedName("TOPUP_ORDER_CANNOT_BE_PROCESSED")
    TOPUP_ORDER(R.string.error_topup_order_cannot_be_processed),

    // Transfer exceptions
    @SerializedName("TRANSFER_SOURCE_NOT_FOUND")
    TRANSFER_SOURCE_NOT_FOUND(R.string.error_transfer_source_not_found),
    @SerializedName("TRANSFER_TARGET_NOT_FOUND")
    TRANSFER_TARGET_NOT_FOUND(R.string.error_transfer_target_not_found),
    @SerializedName("TRANSFER_TARGET_LIMIT_EXCEEDED")
    TRANSFER_TARGET_LIMIT_EXCEEDED(R.string.error_transfer_target_limit_exceeded),
    @SerializedName("TRANSFER_SOURCE_LIMIT_EXCEEDED")
    TRANSFER_SOURCE_LIMIT_EXCEEDED(R.string.error_transfer_source_limit_exceded),
    @SerializedName("TRANSFER_SOURCE_INSUFFICIENT_FUNDS")
    TRANSFER_SOURCE_INSUFFICIENT_FUNDS(R.string.error_transfer_source_insufficient_fund),
    @SerializedName("TRANSFER_QR_NOT_FOUND")
    TRANSFER_QR_NOT_FOUND(R.string.error_transfer_qr_not_found),
    @SerializedName("WALLET_LIMIT_TURNOVER_BALANCE_EXCEEDED_EXCEPTION")
    WALLET_LIMIT_TURNOVER_BALANCE_EXCEEDED_EXCEPTION(R.string.error_wallet_limit_turnover_balance_exceeded),
    @SerializedName("WALLET_LIMIT_NOT_ENOUGH_BALANCE_EXCEPTION")
    WALLET_LIMIT_NOT_ENOUGH_BALANCE_EXCEPTION(R.string.error_wallet_limit_not_enought_balance),
    @SerializedName("WALLET_UNAVAILABLE_FOR_TIER_EXCEPTION")
    WALLET_UNAVAILABLE_FOR_TIER_EXCEPTION(R.string.error_wallet_unavailable_for_tier),
    @SerializedName("WALLET_TRANSACTION_LIMIT_EXCEPTION")
    WALLET_TRANSACTION_LIMIT_EXCEPTION(R.string.error_wallet_transaction_limit),
    @SerializedName("UTILITY_SERVICE_NOT_FOUND")
    UTILITY_SERVICE_NOT_FOUND(R.string.error_utility_service_not_found),
    @SerializedName("UTILITY_UNKNOWN_PROVIDER")
    UTILITY_UNKNOWN_PROVIDER(R.string.error_utility_unknown_provider),
    @SerializedName("UTILITY_TRANSACTION_NOT_FOUND")
    UTILITY_TRANSACTION_NOT_FOUND(R.string.error_utility_transaction_not_found),
    @SerializedName("WALLET_LIMIT_BALANCE_EXCEEDED_EXCEPTION")
    WALLET_LIMIT_BALANCE_EXCEEDED_EXCEPTION(R.string.error_transfer_source_insufficient_fund),
    @SerializedName("VALIDATION_ERROR")
    VALIDATION_ERROR(R.string.error_wrong_phone_format),
    @SerializedName("PAYMENT_LINK_ALREADY_USED")
    PAYMENT_LINK_ALREADY_USED(R.string.error_payment_link_already_used)

}
