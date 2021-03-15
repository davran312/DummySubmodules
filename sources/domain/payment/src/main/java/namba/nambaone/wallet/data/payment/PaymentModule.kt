package namba.nambaone.wallet.data.payment

import namba.nambaone.wallet.data.payment.api.PayloadApi
import namba.nambaone.wallet.data.payment.api.PayloadInMemoryGateway
import namba.nambaone.wallet.data.payment.api.PayloadRemoteGateway
import namba.nambaone.wallet.domain.payment.gateway.PayloadInteractor
import namba.nambaone.wallet.domain.payment.gateway.PayloadLocalGateway
import namba.nambaone.wallet.domain.payment.gateway.PayloadRestGateway
import namba.wallet.nambaone.common.di.InjectionModule
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

object PaymentModule : InjectionModule {
    override fun create(): Module = module {
        single { get<Retrofit>().create(PayloadApi::class.java) } bind PayloadApi::class
        single { PayloadRemoteGateway(get()) } bind PayloadRestGateway::class
        single { PayloadInMemoryGateway() } bind PayloadLocalGateway::class
        single { PayloadInteractor(get(), get()) }
    }
}