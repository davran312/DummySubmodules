package namba.wallet.nambaone.common.network.errors

import com.google.gson.annotations.SerializedName

class ServerError(
    @SerializedName("status")
    val status: String?,
    @SerializedName("error")
    val errorDetail: ErrorDetail?
)
