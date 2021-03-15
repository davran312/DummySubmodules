package namba.wallet.nambaone.common.network.environment

import android.content.SharedPreferences
import androidx.core.content.edit

private const val NAME = "NAME"
private const val ADDRESS_KEY = "ADDRESS_KEY"
private const val PORT_KEY = "PORT_KEY"
private const val API_VERSION = "API_VERSION"
private const val SSL_ENABLED_KEY = "SSL_ENABLED_KEY"
private const val WEB_ADDRESS_KEY = "WEB_ADDRESS_KEY"

class EnvironmentManager(
    private val environment: Environment,
    private val sharedPreferences: SharedPreferences
) {

    fun loadEnvironment() = with(sharedPreferences) {
        Environment(
            getString(NAME, environment.name)!!,
            getString(ADDRESS_KEY, environment.baseAddress)!!,
            getInt(PORT_KEY, environment.port ?: -1),
            getBoolean(SSL_ENABLED_KEY, environment.isSslEnabled),
            getInt(API_VERSION, environment.apiVersion)
        )
    }

    fun saveEnvironment(
        sharedPreferences: SharedPreferences,
        environment: Environment
    ) {
        with(environment) {
            sharedPreferences.edit {
                putString(NAME, name)
                putString(ADDRESS_KEY, baseAddress)
                putInt(PORT_KEY, port)
                putInt(API_VERSION, apiVersion)
                putBoolean(SSL_ENABLED_KEY, isSslEnabled)
            }
        }
    }
}
