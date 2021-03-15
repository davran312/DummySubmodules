package namba.wallet.nambaone.common.network.gson

import com.google.gson.GsonBuilder
import namba.wallet.nambaone.core.BuildConfig
import java.lang.reflect.Type

object GsonFactory {

    fun getInstance(typeAdapters: List<Pair<Type, Any>>): GsonBuilder {
        val builder = GsonBuilder()
        with(builder) {
            if (BuildConfig.DEBUG) setPrettyPrinting()
            typeAdapters.forEach {
                registerTypeAdapter(it.first, it.second)
            }
        }
        return builder
    }
}