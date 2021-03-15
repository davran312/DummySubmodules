package namba.nambaone.wallet.domain.shared.model

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import namba.wallet.nambaone.wallet.domain.shared.R

enum class WalletTier(@StringRes val tierStringRes: Int) {
    @SerializedName("0")
    Guest(R.string.profile_guest),

    @SerializedName("1")
    Elementary(R.string.profile_elementary),

    @SerializedName("2")
    Advance(R.string.profile_advanced),

    @SerializedName("3")
    Pro(R.string.profile_pro);

    companion object {
        fun getValueOf(tierString: String): WalletTier {
            return when (tierString) {
                "1" -> Elementary
                "2" -> Advance
                "3" -> Pro
                else -> Guest
            }
        }
    }
}
