package namba.wallet.nambaone.common.network

import java.net.CookieManager
import java.net.CookiePolicy
import namba.wallet.nambaone.common.di.InjectionModule
import namba.wallet.nambaone.common.network.environment.Environment
import namba.wallet.nambaone.common.network.errors.ServerErrorInterceptor
import namba.wallet.nambaone.common.network.gson.GsonFactory
import namba.wallet.nambaone.common.network.interceptor.*
import namba.wallet.nambaone.common.network.okhttp.*
import namba.wallet.nambaone.common.network.provider.TokenProvider
import namba.wallet.nambaone.common.update.ConnectionStateProvider
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit

object NetworkModule : InjectionModule {

    const val MULTIPART_RETROFIT = "MODULE_MULTIPART_RETROFIT"
    const val TYPE_ADAPTERS = "MODULE_TYPE_ADAPTERS"
    const val CONVERTER_FACTORIES = "MODULE_CONVERTER_FACTORIES"

    override fun create() = module {
        single { GsonFactory.getInstance(get(named(NetworkModule.TYPE_ADAPTERS))).create() }
        single { EncryptDecryptInterceptor(get(), get(), get(), get()) }
        single { SSeInterceptor(get(), get(), get(), get()) }
        factory {
            BaseOkHttpClient(
                get(),
                TokenInterceptor(get()),
                ServerTimeInterceptor(),
                ServerErrorInterceptor(get(), get())
            )
        } bind OkHttpClientBuilder::class
        single {
            OkHttpClientFactory.getInstance(OkHttpClientFactory.Type.DEFAULT)
        }
        single(named(OkHttpClientFactory.Type.SSE)) {
            OkHttpClientFactory.getInstance(OkHttpClientFactory.Type.SSE)
        }
        single(named(OkHttpClientFactory.Type.MULTIPART)) {
            OkHttpClientFactory.getInstance(OkHttpClientFactory.Type.MULTIPART)
        }

        single { TokenProvider(get()) }
        single { CookieManager().apply { setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER) } }
        single { ConnectionStateProvider(get()) }
        single { createRetrofit(get()) }
        single(named(MULTIPART_RETROFIT)) {
            createRetrofit(get(named(OkHttpClientFactory.Type.MULTIPART)))
        }
    }
}

private fun Scope.createRetrofit(okHttpClient: OkHttpClient): Retrofit {
    val convertFactories = get<List<Converter.Factory>>(named(NetworkModule.CONVERTER_FACTORIES))
    val retrofit = Retrofit.Builder()
        .baseUrl("${get<Environment>().restAddress}/")
        .callFactory(okHttpClient)
    convertFactories.forEach {
        retrofit.addConverterFactory(it)
    }
    return retrofit.build()
}
