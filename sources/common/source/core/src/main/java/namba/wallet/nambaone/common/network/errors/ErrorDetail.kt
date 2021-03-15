package namba.wallet.nambaone.common.network.errors

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.threeten.bp.ZonedDateTime

data class ErrorDetail(

    @SerializedName("errorCode")
    val errorCode: ErrorCode?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: JsonObject?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("timestamp")
    val timestamp: ZonedDateTime?
)
