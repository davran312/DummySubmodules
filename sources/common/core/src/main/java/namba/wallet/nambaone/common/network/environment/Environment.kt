package namba.wallet.nambaone.common.network.environment

class Environment(
    val name: String,
    val baseAddress: String,
    val port: Int = -1,
    val isSslEnabled: Boolean,
    val apiVersion: Int
) {
    val restAddress: String =
        "${if (isSslEnabled) "https" else "http"}://$baseAddress${if (port != -1) ":$port" else ""}/api/v$apiVersion"

    val usageUrl =
        "${if (isSslEnabled) "https" else "http"}://${baseAddress.removePrefix("api.")}/politics/operating-rules"
    val agreementUrl =
        "${if (isSslEnabled) "https" else "http"}://${baseAddress.removePrefix("api.")}/politics/terms-of-use"
    val legacyUrl =
        "${if (isSslEnabled) "https" else "http"}://${baseAddress.removePrefix("api.")}/politics/personal-data"
    val supportUrl =
        "${if (isSslEnabled) "https" else "http"}://${baseAddress.removePrefix("api.")}"

    fun buildUrl(relativePath: String) =
        if (relativePath.isBlank()) {
            relativePath
        } else {
            "$restAddress/${relativePath.trimStart('/')}"
        }
}
