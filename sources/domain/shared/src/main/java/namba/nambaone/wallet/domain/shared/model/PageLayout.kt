package namba.nambaone.wallet.domain.shared.model

sealed class PageLayout {
    data class ActionPageLayout(
        val id: String,
        val detail: LayoutDetail,
        val items: List<LayoutItem>
    ) : PageLayout()

    data class CardPageLayout(
        val id: String,
        val detail: LayoutDetail,
        val items: List<LayoutItem>
    ) : PageLayout()

    data class BannerPageLayout(
        val id: String,
        val detail: LayoutDetail,
        val items: List<LayoutItem>
    ) : PageLayout()
}