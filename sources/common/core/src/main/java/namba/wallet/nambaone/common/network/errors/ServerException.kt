package namba.wallet.nambaone.common.network.errors

import android.content.Context
import java.io.IOException
import namba.wallet.nambaone.core.R

class ServerException(
    val statusCode: Int,
    private val serverError: ServerError
) : IOException("statusCode: $statusCode, errorMessage: $serverError") {
    val errorCode = serverError.errorDetail?.errorCode ?: ErrorCode.Error
    private val description: String? = serverError.errorDetail?.message
    val errorJson = serverError.errorDetail?.data
    fun getErrorMessage(context: Context) =
        serverError.errorDetail?.errorCode?.messageRes?.let {
            context.getString(
                it
            )
        }
            ?: description
            ?: context.getString(R.string.general_error_message)
}
