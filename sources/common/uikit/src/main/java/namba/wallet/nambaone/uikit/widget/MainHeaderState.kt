package namba.wallet.nambaone.uikit.widget

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class MainHeaderState(
    private var isInitialized: Boolean = false,
    var scrollRange: Int = 0,
    var currentScroll: Int = 0,
    private var initialHeight: Int = 0,
    var buttonsInitialTopY: Int = 0,
    private var buttonsTravelDistanceY: Int = 0,
    private var maxElevation: Float = 0f
) : Parcelable {

    @IgnoredOnParcel
    var expandPercent: Float = 0f
        private set

    @IgnoredOnParcel
    var currentElevation: Float = 0f
        private set

    @IgnoredOnParcel
    var buttonsY: Int = 0
        private set

    fun getCurrentOffset() = initialHeight - scrollRange + currentScroll

    fun consumeScroll(dy: Int): Int {
        val newScroll = (currentScroll - dy).coerceIn(0, scrollRange)
        if (currentScroll == newScroll) return 0
        val consumed = currentScroll - newScroll
        currentScroll = newScroll

        computeState()

        return consumed
    }

    fun initialize(state: MainHeaderState) {
        if (isInitialized) return

        isInitialized = state.isInitialized
        scrollRange = state.scrollRange
        currentScroll = state.currentScroll
        initialHeight = state.initialHeight
        buttonsInitialTopY = state.buttonsInitialTopY
        buttonsY = state.buttonsY
        buttonsTravelDistanceY = state.buttonsTravelDistanceY
        maxElevation = state.maxElevation

        computeState()
    }

    fun initialize(view: IHeaderView) {
        if (isInitialized) return

        with(view) {
            isInitialized = true

            initialHeight = getViewHeight()
            buttonsInitialTopY = getButtonTop()
            buttonsTravelDistanceY =
                (getButtonTop() - getTitleBottom()).coerceAtLeast(0) + getTitleHeightDelta()
            scrollRange = buttonsTravelDistanceY + getButtonHeightDelta()
            currentScroll = scrollRange
            maxElevation = 8f
        }

        computeState()
    }

    fun computeState() {
        expandPercent = currentScroll / scrollRange.toFloat()
        val collapsePercent = 1 - expandPercent
        buttonsY = buttonsInitialTopY - (buttonsTravelDistanceY * collapsePercent).toInt()
        currentElevation = collapsePercent * maxElevation
    }
}

