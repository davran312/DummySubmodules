package namba.wallet.nambaone.common.network.provider

import java.security.cert.X509Certificate

interface RootCertificateProvider {
    val rootCertificate: X509Certificate
}
