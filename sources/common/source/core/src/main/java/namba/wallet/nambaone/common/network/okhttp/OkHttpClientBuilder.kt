package namba.wallet.nambaone.common.network.okhttp

import okhttp3.OkHttpClient


interface OkHttpClientBuilder {
    fun builder() : OkHttpClient.Builder
    fun build(): OkHttpClient
}