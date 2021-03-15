package namba.wallet.nambaone.common.utils.navigation

interface NavigatorsHolder {
    /**
     * Зарегистрирует [navigator] для [type]
     */
    fun registerNavigator(
        key: String,
        definition: Lazy<BaseNavigator>
    )

    /**
     * Вернет навигатор, зарегистрированный для [type]
     */
    fun <T : BaseNavigator> getNavigator(key: String): T
}
